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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.Test;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapterTemp;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Macaron;
import com.twinly.eyebb.utils.BluetoothUtils;

@SuppressLint("NewApi")
public class RadarTrackingFragmentTemp extends Fragment implements
		BluetoothUtils.BleConnectCallback {

	private BluetoothUtils mBluetoothUtils;
	private ListView listView;
	private ScrollView radarScrollView;
	//private ToggleButton confirmRadarBtn;
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
	private boolean isAntiLostOpen = false;
	private boolean isRadarOpen = false;
	private boolean isSuperisedSection = true;

	private HashMap<String, Macaron> macaronHashMap;
	private ArrayList<Macaron> displayDeviceList;
	private ArrayList<Macaron> scannedDeviceList;
	private ArrayList<Macaron> missedDeviceList;
	private RadarKidsListViewAdapterTemp mAdapter;
	private Handler mHandler;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mBluetoothUtils = new BluetoothUtils(getActivity(),
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
		tv_supervised_number = (TextView) v.findViewById(R.id.tv_supervised_number);
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
		macaronHashMap = DBChildren.getMacaronMap(getActivity());
		displayDeviceList = new ArrayList<Macaron>();
		scannedDeviceList = new ArrayList<Macaron>();
		missedDeviceList = new ArrayList<Macaron>();
		mHandler = new Handler();
		mAdapter = new RadarKidsListViewAdapterTemp(getActivity(),
				displayDeviceList, true);
		listView.setAdapter(mAdapter);
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
				getActivity().startActivity(
						new Intent(getActivity(), Test.class));
				/*if (isAntiLostOpen) {
					isAntiLostOpen = false;
					openAntiTheft
							.setBackgroundResource(R.drawable.ic_selected_off);
					mAdapter.setAntiLostOpen(false);
				} else {
					isAntiLostOpen = true;
					openAntiTheft.setBackgroundResource(R.drawable.ic_selected);
					for (int i = 0; i < scannedDeviceList.size(); i++) {
						scannedDeviceList.get(i).setAntiLostOpen(true);
					}
					mAdapter.setAntiLostOpen(true);
				}
				updateListView();*/
			}
		});
	}

	private void startRadar() {
		isRadarOpen = true;
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_on);
		radarScrollView.setAlpha(1F);
		mBluetoothUtils.startLeScan(leScanCallback, 500);
		radarViewFragment.startAnimation();
		mHandler.postDelayed(updateViewRunnable, 2000);
	}

	private void stopRadar() {
		isRadarOpen = false;
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_off);
		radarScrollView.setAlpha(0.3F);
		mHandler.removeCallbacks(updateViewRunnable);
		mBluetoothUtils.stopLeScan();
		radarViewFragment.stopAnimation();
	}

	Runnable updateViewRunnable = new Runnable() {

		@Override
		public void run() {
			String macAddress;

			scannedDeviceList.clear();
			missedDeviceList.clear();

			Iterator<String> it = macaronHashMap.keySet().iterator();
			while (it.hasNext()) {
				macAddress = it.next();
				if (System.currentTimeMillis()
						- macaronHashMap.get(macAddress).getLastAppearTime() < 15000) {
					scannedDeviceList.add(macaronHashMap.get(macAddress));
				} else {
					missedDeviceList.add(macaronHashMap.get(macAddress));
				}
			}

			radarViewFragment.updateView(scannedDeviceList);
			updateListView();

			if (isRadarOpen) {
				mHandler.postDelayed(updateViewRunnable, 5000);
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
				/*if (device.getAddress().contains("04:F5")) {
					System.out.println("Wanan");
				} else if (device.getAddress().contains("04:EB")) {
					System.out.println("Peter");
				} else if (device.getAddress().contains("04:DD")) {
					System.out.println("轩仔");
				} else if (device.getAddress().contains("04:EC")) {
					System.out.println("Terry");
				}*/
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
	public void onPreConnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectCanceled() {

	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub

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
	public void onResult(boolean result) {
		// TODO Auto-generated method stub

	}

}
