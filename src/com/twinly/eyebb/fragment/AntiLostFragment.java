package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.model.SerializableDeviceMap;
import com.twinly.eyebb.service.AntiLostService;

public class AntiLostFragment extends Fragment {
	private HashMap<String, Device> macaronHashMap;
	private ArrayList<Device> deviceList;
	private SerializableDeviceMap serializableMacaronMap;
	private ListView listView;
	private RadarKidsListViewAdapter mAdapter;
	private boolean isAntiLostOn = false;
	private boolean isSingleMode;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_anti_lost, container, false);
		listView = (ListView) v.findViewById(R.id.listView);
		deviceList = new ArrayList<Device>();
		mAdapter = new RadarKidsListViewAdapter(getActivity(), deviceList);
		listView.setAdapter(mAdapter);
		return v;
	}

	public void start(ArrayList<String> antiLostDeviceList) {
		if (antiLostDeviceList.size() > AntiLostService.MAX_DUAL_MODE_SIZE) {
			isSingleMode = true;
		} else {
			isSingleMode = false;
		}
		Intent antiLostServiceIntent = new Intent();
		antiLostServiceIntent.setClass(getActivity(), AntiLostService.class);
		antiLostServiceIntent.putStringArrayListExtra(
				AntiLostService.EXTRA_DEVICE_LIST, antiLostDeviceList);
		getActivity().startService(antiLostServiceIntent);
		isAntiLostOn = true;
	}

	public void stop() {
		isAntiLostOn = false;
	}

	private void updateView(Message msg) {
		if (isAntiLostOn) {
			serializableMacaronMap = (SerializableDeviceMap) msg.getData().get(
					AntiLostService.EXTRA_DEVICE_LIST);
			macaronHashMap = serializableMacaronMap.getMap();
			String macAddress;
			Iterator<String> it = macaronHashMap.keySet().iterator();
			deviceList.clear();
			while (it.hasNext()) {
				macAddress = it.next();
				if (isSingleMode) {
					if (System.currentTimeMillis()
							- macaronHashMap.get(macAddress)
									.getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
						macaronHashMap.get(macAddress).setMissed(false);
					} else {
						macaronHashMap.get(macAddress).setMissed(true);
					}
				}
				deviceList.add(macaronHashMap.get(macAddress));
			}
			mAdapter.notifyDataSetChanged();
		}
	}

}
