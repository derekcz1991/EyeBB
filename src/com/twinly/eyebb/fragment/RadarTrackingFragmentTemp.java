package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.SelectKidsActivity;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapterTemp;
import com.twinly.eyebb.bluetooth.BluetoothUtils;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Macaron;
import com.twinly.eyebb.model.SerializableChildrenList;
import com.twinly.eyebb.utils.BroadcastUtils;

@SuppressLint("NewApi")
public class RadarTrackingFragmentTemp extends Fragment implements
		BluetoothUtils.BleConnectCallback {
	private final int LOST_TIMEOUT = 15000;
	private final int WRITE_ANTI_LOST_TIMEOUT = 10000;
	private final int CONNECT_TIMEOUT = 4000;

	private final int MESSAGE_WHAT_UPDATE_VIEW = 0;
	private final int MESSAGE_WHAT_REMOVE_CALLBACK = 1;
	private final int MESSAGE_WHAT_CONNECT_DEVICE = 2;

	private BluetoothUtils mBluetoothUtilsA;
	private ListView listView;
	private ScrollView radarScrollView;
	private TextView btnRadarSwitch;
	private RelativeLayout btnSuperised;
	private RelativeLayout btnMissed;
	private TextView redDividerSuperised;
	private TextView blackDividerSuperised;
	private TextView tvSuperised;
	private TextView redDividerMissed;
	private TextView blackDividerMissed;
	private TextView tvMissed;

	private TextView tv_supervised_number;
	private TextView tv_missed_number;
	private RadarViewFragment radarViewFragment;
	// 開啟防丟器
	private TextView openAntiTheft;
	public static boolean isRadarOpen = false;
	private boolean isAntiLostOpen = false;
	private int index;
	private boolean isSuperisedSection = true;

	private HashMap<String, Macaron> macaronHashMap;
	private ArrayList<String> antiLostDeviceList;
	private ArrayList<Macaron> displayDeviceList;
	private ArrayList<Macaron> scannedDeviceList;
	private ArrayList<Macaron> missedDeviceList;
	private RadarKidsListViewAdapterTemp mAdapter;
	private Handler mHandler;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mBluetoothUtilsA = new BluetoothUtils(getActivity(),
				getFragmentManager(), this);

		View v = inflater.inflate(R.layout.fragment_radar_tracking_temp,
				container, false);
		listView = (ListView) v.findViewById(R.id.listView);
		radarScrollView = (ScrollView) v.findViewById(R.id.radar_scrollview);
		radarScrollView.setAlpha(0.3F);
		radarScrollView.smoothScrollTo(0, 0);

		btnRadarSwitch = (TextView) v.findViewById(R.id.btn_radar_switch);
		openAntiTheft = (TextView) v.findViewById(R.id.confirm_anti_lost_btn);
		btnSuperised = (RelativeLayout) v.findViewById(R.id.btn_supervised);
		tvSuperised = (TextView) v.findViewById(R.id.tv_supervised);
		redDividerSuperised = (TextView) v
				.findViewById(R.id.red_divider_supervised);
		blackDividerSuperised = (TextView) v
				.findViewById(R.id.black_divider_supervised);
		tvMissed = (TextView) v.findViewById(R.id.tv_missed);
		redDividerMissed = (TextView) v.findViewById(R.id.red_divider_missed);
		blackDividerMissed = (TextView) v
				.findViewById(R.id.black_divider_missed);
		btnMissed = (RelativeLayout) v.findViewById(R.id.btn_missed);
		tv_supervised_number = (TextView) v
				.findViewById(R.id.tv_supervised_number);
		tv_missed_number = (TextView) v.findViewById(R.id.tv_missed_number);

		radarViewFragment = (RadarViewFragment) getChildFragmentManager()
				.findFragmentByTag("radarView");
		if (radarViewFragment == null) {
			radarViewFragment = new RadarViewFragment();
			getChildFragmentManager().beginTransaction()
					.add(R.id.radar_view, radarViewFragment, "radarView")
					.commit();
		}

		setupListener();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		macaronHashMap = DBChildren.getChildrenMapWithAddress(getActivity());
		displayDeviceList = new ArrayList<Macaron>();
		scannedDeviceList = new ArrayList<Macaron>();
		missedDeviceList = new ArrayList<Macaron>();
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_WHAT_UPDATE_VIEW:
					mHandler.post(updateViewRunnable);
					break;
				case MESSAGE_WHAT_REMOVE_CALLBACK:
					mHandler.removeCallbacks(updateViewRunnable);
					break;
				case MESSAGE_WHAT_CONNECT_DEVICE:
					if (macaronHashMap.get(antiLostDeviceList.get(index))
							.isAntiLostWriten()) {
						mBluetoothUtilsA.close();
						mBluetoothUtilsA.connectOnly(
								antiLostDeviceList.get(index), CONNECT_TIMEOUT);
					} else {
						mBluetoothUtilsA.close();
						mBluetoothUtilsA.writeAntiLostPeroid(
								antiLostDeviceList.get(index),
								WRITE_ANTI_LOST_TIMEOUT, "FFFF");
					}
					index++;
					index = index % antiLostDeviceList.size();
					break;
				}
			}
		};
		mAdapter = new RadarKidsListViewAdapterTemp(getActivity(),
				displayDeviceList, true);
		listView.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mBluetoothUtilsA != null) {
			mBluetoothUtilsA.registerReceiver();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mBluetoothUtilsA != null) {
			mBluetoothUtilsA.unregisterReceiver();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBluetoothUtilsA != null) {
			mBluetoothUtilsA.disconnect();
		}
	}

	private void setupListener() {
		btnRadarSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRadarOpen) {
					stopRadar();
				} else {
					startRadar();
				}

			}
		});
		btnSuperised.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSuperisedSection = true;
				tvMissed.setTextAppearance(getActivity(),
						R.style.LightGreyText_18);
				redDividerMissed.setVisibility(View.INVISIBLE);
				blackDividerMissed.setVisibility(View.VISIBLE);

				tvSuperised
						.setTextAppearance(getActivity(), R.style.RedText_18);
				redDividerSuperised.setVisibility(View.VISIBLE);
				blackDividerSuperised.setVisibility(View.INVISIBLE);

				updateListView();
			}
		});
		btnMissed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSuperisedSection = false;
				tvSuperised.setTextAppearance(getActivity(),
						R.style.LightGreyText_18);
				redDividerSuperised.setVisibility(View.INVISIBLE);
				blackDividerSuperised.setVisibility(View.VISIBLE);

				tvMissed.setTextAppearance(getActivity(), R.style.RedText_18);
				redDividerMissed.setVisibility(View.VISIBLE);
				blackDividerMissed.setVisibility(View.INVISIBLE);

				updateListView();
			}
		});
		openAntiTheft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAntiLostOpen) {
					isAntiLostOpen = false;
					openAntiTheft
							.setBackgroundResource(R.drawable.ic_selected_off);
					mAdapter.setAntiLostOpen(false);
					mBluetoothUtilsA.startLeScan(leScanCallback, 500);
				} else {
					Intent intent = new Intent(getActivity(),
							SelectKidsActivity.class);
					startActivityForResult(intent, 1);
				}
			}
		});
	}

	private void startRadar() {
		isRadarOpen = true;
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_on);
		radarScrollView.setAlpha(1F);
		mBluetoothUtilsA.startLeScan(leScanCallback, 500);
		radarViewFragment.startAnimation();
		mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UPDATE_VIEW, 2000);
		BroadcastUtils.opeanRadar(getActivity());
	}

	private void stopRadar() {
		isRadarOpen = false;
		isAntiLostOpen = false;
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_off);
		radarScrollView.setAlpha(0.3F);
		mHandler.sendEmptyMessage(MESSAGE_WHAT_REMOVE_CALLBACK);
		mBluetoothUtilsA.stopLeScan();
		radarViewFragment.stopAnimation();
		BroadcastUtils.closeRadar(getActivity());
	}

	Runnable updateViewRunnable = new Runnable() {

		@Override
		public void run() {
			String macAddress;

			scannedDeviceList.clear();
			missedDeviceList.clear();

			Iterator<String> it = macaronHashMap.keySet().iterator();
			if (isAntiLostOpen) {
				while (it.hasNext()) {
					macAddress = it.next();
					if (macaronHashMap.get(macAddress).isAntiLostWriten()) {
						if (System.currentTimeMillis()
								- macaronHashMap.get(macAddress)
										.getLastAppearTime() < LOST_TIMEOUT) {
							scannedDeviceList.add(macaronHashMap
									.get(macAddress));
						} else {
							missedDeviceList
									.add(macaronHashMap.get(macAddress));
							Toast.makeText(getActivity(),
									macAddress + " Alert", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						missedDeviceList.add(macaronHashMap.get(macAddress));
					}
				}
			} else {
				while (it.hasNext()) {
					macAddress = it.next();
					if (System.currentTimeMillis()
							- macaronHashMap.get(macAddress)
									.getLastAppearTime() < LOST_TIMEOUT) {
						scannedDeviceList.add(macaronHashMap.get(macAddress));
					} else {
						missedDeviceList.add(macaronHashMap.get(macAddress));
					}
				}
			}

			radarViewFragment.updateView(scannedDeviceList);
			updateListView();

			if (isRadarOpen) {
				mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UPDATE_VIEW, 5000);
			}
		}

	};

	private void updateListView() {
		displayDeviceList.clear();
		if (isSuperisedSection) {
			displayDeviceList.addAll(scannedDeviceList);
			mAdapter.setSuperisedSection(true);

		} else {
			displayDeviceList.addAll(missedDeviceList);
			mAdapter.setSuperisedSection(false);

		}

		tv_supervised_number.setText(scannedDeviceList.size() + "");
		tv_missed_number.setText(missedDeviceList.size() + "");

		mAdapter.notifyDataSetChanged();
		setListViewHeightBasedOnChildren(listView);
	}

	LeScanCallback leScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (macaronHashMap.get(device.getAddress()) != null) {
				macaronHashMap.get(device.getAddress()).setPreRssi(
						macaronHashMap.get(device.getAddress()).getRssi());
				macaronHashMap.get(device.getAddress()).setRssi(rssi);
				macaronHashMap.get(device.getAddress()).setLastAppearTime(
						System.currentTimeMillis());
			}
		}
	};

	/**
	 * 动态设置ListView的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(final ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition 
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		final ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listView.setLayoutParams(params);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
			mBluetoothUtilsA.stopLeScan();

			SerializableChildrenList serializableChildrenList = (SerializableChildrenList) data
					.getExtras().getSerializable(
							SelectKidsActivity.EXTRA_CHILDREN_LIST);
			ArrayList<Child> childrenList = serializableChildrenList.getList();
			antiLostDeviceList = new ArrayList<String>();
			for (int i = 0; i < childrenList.size(); i++) {
				if (childrenList.get(i).isWithAccess()) {
					macaronHashMap.get(childrenList.get(i).getMacAddress())
							.setAntiLostOpen(true);
					antiLostDeviceList.add(childrenList.get(i).getMacAddress());
				} else {
					macaronHashMap.get(childrenList.get(i).getMacAddress())
							.setAntiLostOpen(false);
				}
			}
			System.out.println(macaronHashMap);
			isAntiLostOpen = true;
			openAntiTheft.setBackgroundResource(R.drawable.ic_selected);
			mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_CONNECT_DEVICE, 1000);
		}
	}

	@Override
	public void onPreConnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectCanceled(String mDeviceAddress) {
		System.out.println("onConnectCanceled ==>> " + mDeviceAddress);
		System.out.println("   ");
		mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_CONNECT_DEVICE, 1000);
	}

	@Override
	public void onConnected(String mDeviceAddress) {
		if (macaronHashMap.get(mDeviceAddress).isAntiLostWriten()) {
			System.out.println("onConnected  ==>> " + mDeviceAddress);
			System.out.println("   ");
			macaronHashMap.get(mDeviceAddress).setLastAppearTime(
					System.currentTimeMillis());
			mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_CONNECT_DEVICE, 1000);
		}
	}

	@Override
	public void onDisConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDiscovered() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataAvailable(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResult(boolean result, String mDeviceAddress) {
		System.out.println("onResult ==>> " + result + " ==>> "
				+ mDeviceAddress);
		System.out.println("   ");
		if (result) {
			macaronHashMap.get(mDeviceAddress).setLastAppearTime(
					System.currentTimeMillis());
			macaronHashMap.get(mDeviceAddress).setAntiLostWriten(true);
		}
		mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_CONNECT_DEVICE, 1000);
	}

}
