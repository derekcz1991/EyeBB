package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.bluetooth.AntiLostService;
import com.twinly.eyebb.bluetooth.RadarTrackingService;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.model.SerializableDeviceMap;
import com.twinly.eyebb.utils.BroadcastUtils;

@SuppressLint("NewApi")
public class RadarTrackingFragment extends Fragment {

	private ListView listView;
	private RelativeLayout btnSuperised;
	private RelativeLayout btnMissed;
	private TextView redDividerSuperised;
	private TextView blackDividerSuperised;
	private TextView tvSuperised;
	private TextView redDividerMissed;
	private TextView blackDividerMissed;
	private TextView tvMissed;

	private TextView tvSupervisedNumber;
	private TextView tvMissedNumber;
	private RadarViewFragment radarViewFragment;

	private SerializableDeviceMap serializableMacaronMap;
	private HashMap<String, Device> deviceHashMap;
	private ArrayList<Device> displayDeviceList;
	private ArrayList<Device> scannedDeviceList;
	private ArrayList<Device> missedDeviceList;
	private RadarKidsListViewAdapter mAdapter;

	private boolean isSuperisedSection = true;
	private boolean isRadarTrackingOn = false;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (RadarTrackingService.ACTION_DATA_CHANGED.equals(action)) {
				Bundle bundle = intent.getExtras();
				serializableMacaronMap = (SerializableDeviceMap) bundle
						.get(RadarTrackingService.EXTRA_DEVICE_LIST);
				updateView();
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_radar_tracking, container,
				false);
		listView = (ListView) v.findViewById(R.id.listView);
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
		tvSupervisedNumber = (TextView) v
				.findViewById(R.id.tv_supervised_number);
		tvMissedNumber = (TextView) v.findViewById(R.id.tv_missed_number);

		radarViewFragment = (RadarViewFragment) getChildFragmentManager()
				.findFragmentByTag("radarView");
		if (radarViewFragment == null) {
			radarViewFragment = new RadarViewFragment();
			getChildFragmentManager().beginTransaction()
					.add(R.id.radar_view, radarViewFragment, "radarView")
					.commit();
		}

		getActivity().registerReceiver(mReceiver,
				new IntentFilter(RadarTrackingService.ACTION_DATA_CHANGED));
		setupListener();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		displayDeviceList = new ArrayList<Device>();
		scannedDeviceList = new ArrayList<Device>();
		missedDeviceList = new ArrayList<Device>();
		mAdapter = new RadarKidsListViewAdapter(getActivity(),
				displayDeviceList);
		listView.setAdapter(mAdapter);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private void setupListener() {
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
	}

	public void start() {
		isRadarTrackingOn = true;
		radarViewFragment.startAnimation();
		BroadcastUtils.opeanRadar(getActivity());

		// start service
		Intent radarTrackingServiceIntent = new Intent();
		radarTrackingServiceIntent.setClass(getActivity(),
				RadarTrackingService.class);
		getActivity().startService(radarTrackingServiceIntent);
	}

	public void resume() {
		isRadarTrackingOn = true;
	}

	public void stop() {
		isRadarTrackingOn = false;
		radarViewFragment.stopAnimation();
		BroadcastUtils.closeRadar(getActivity());

		Intent action = new Intent(RadarTrackingService.ACTION_STOP_SERVICE);
		getActivity().sendBroadcast(action);
	}

	public void updateView() {
		if (isRadarTrackingOn) {
			String macAddress;

			scannedDeviceList.clear();
			missedDeviceList.clear();

			deviceHashMap = serializableMacaronMap.getMap();
			Iterator<String> it = deviceHashMap.keySet().iterator();
			while (it.hasNext()) {
				macAddress = it.next();
				if (System.currentTimeMillis()
						- deviceHashMap.get(macAddress).getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
					deviceHashMap.get(macAddress).setMissed(false);
					scannedDeviceList.add(deviceHashMap.get(macAddress));
				} else {
					deviceHashMap.get(macAddress).setMissed(true);
					missedDeviceList.add(deviceHashMap.get(macAddress));
				}
			}

			radarViewFragment.updateView(scannedDeviceList);
			updateListView();
		}
	}

	private void updateListView() {
		displayDeviceList.clear();
		if (isSuperisedSection) {
			displayDeviceList.addAll(scannedDeviceList);
		} else {
			displayDeviceList.addAll(missedDeviceList);
		}

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvSupervisedNumber.setText(scannedDeviceList.size() + "");
				tvMissedNumber.setText(missedDeviceList.size() + "");

				mAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(listView);
			}
		});
	}

	public ArrayList<Device> getScannedDeviceList() {
		return scannedDeviceList;
	}

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

		listView.setLayoutParams(params);

	}

}
