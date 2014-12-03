package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.model.Device;

public class LeDeviceListAdapter extends BaseAdapter {
	private ArrayList<Device> mLeDevices;
	private LayoutInflater mInflator;

	private final class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	public LeDeviceListAdapter(Context context) {
		super();
		mLeDevices = new ArrayList<Device>();
		mInflator = LayoutInflater.from(context);
	}

	public ArrayList<Device> getDeviceList() {
		return mLeDevices;
	}

	public void setDeviceList(ArrayList<Device> mLeDevices) {
		this.mLeDevices = mLeDevices;
	}

	public void addDevice(Device device) {
		if (!mLeDevices.contains(device)) {
			mLeDevices.add(device);
		}
	}

	public Device getDevice(int position) {
		return mLeDevices.get(position);
	}

	public void clear() {
		mLeDevices.clear();
	}

	@Override
	public int getCount() {
		return mLeDevices.size();
	}

	@Override
	public Device getItem(int i) {
		return mLeDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (view == null) {
			view = mInflator.inflate(R.layout.ble_listview, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceAddress = (TextView) view
					.findViewById(R.id.device_address);
			viewHolder.deviceName = (TextView) view
					.findViewById(R.id.device_name);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		Device device = mLeDevices.get(i);
		final String deviceName = device.getName();
		if (deviceName != null && deviceName.length() > 0)
			viewHolder.deviceName.setText(deviceName);
		//else
		//viewHolder.deviceName.setText(R.string.unknown_device);
		viewHolder.deviceAddress.setText(device.getAddress());

		return view;
	}
}
