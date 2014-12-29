package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Macaron;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.CommonUtils;

public class RadarKidsListViewAdapterTemp extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private ArrayList<Macaron> deviceList;
	private HashMap<String, Child> childMap;
	private boolean isMiss;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public View beepBtn;
		public TextView deviceConnectStatus;
		public TextView deviceRssi;
	}

	public RadarKidsListViewAdapterTemp(Context context,
			ArrayList<Macaron> deviceList, boolean isMiss) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.deviceList = deviceList;
		this.isMiss = isMiss;
		childMap = DBChildren.getChildrenMap(context);
		imageLoader = ImageLoader.getInstance();

		Collections.sort(this.deviceList, new Comparator<Macaron>() {

			@Override
			public int compare(Macaron lhs, Macaron rhs) {
				long left = childMap.get(lhs.getMacAddress()).getChildId();
				long right = childMap.get(rhs.getMacAddress()).getChildId();
				return (int) (left - right);
			}
		});
	}

	public void setMiss(boolean isMiss) {
		this.isMiss = isMiss;
	}

	@Override
	public int getCount() {
		return deviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.list_item_radar_tracking_kid, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.deviceRssi = (TextView) convertView
					.findViewById(R.id.rssi);
			viewHolder.beepBtn = convertView.findViewById(R.id.btn_beep);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.deviceConnectStatus = (TextView) convertView
					.findViewById(R.id.device_connect_status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		if (isMiss) {
			viewHolder.avatar.setBorderColor(context.getResources().getColor(
					R.color.red));
			viewHolder.beepBtn.setVisibility(View.INVISIBLE);
			viewHolder.deviceRssi.setVisibility(View.GONE);
		} else {
			viewHolder.avatar.setBorderColor(context.getResources().getColor(
					R.color.white));
			viewHolder.beepBtn.setVisibility(View.VISIBLE);
			viewHolder.deviceRssi.setVisibility(View.VISIBLE);
		}
		Child child = childMap.get(deviceList.get(position).getMacAddress());
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					CommonUtils.getDisplayImageOptions(), null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.icon_avatar_dark));
		}
		viewHolder.name.setText(child.getName());

		int rssi = deviceList.get(position).getRssi();
		switch (BLEUtils.getRssiLevel(rssi)) {
		case BleDeviceConstants.RSSI_STRONG:
			viewHolder.deviceRssi.setText(context.getResources().getString(
					R.string.text_rssi_strong)
					+ "(" + rssi + ")");
			viewHolder.deviceRssi.setTextColor(context.getResources().getColor(
					R.color.sky_blue));
			break;
		case BleDeviceConstants.RSSI_GOOD:
			viewHolder.deviceRssi.setText(context.getResources().getString(
					R.string.text_rssi_good)
					+ "(" + rssi + ")");
			viewHolder.deviceRssi.setTextColor(context.getResources().getColor(
					R.color.dark_grey));
			break;
		case BleDeviceConstants.RSSI_WEEK:
			viewHolder.deviceRssi.setText(context.getResources().getString(
					R.string.text_rssi_weak)
					+ "(" + rssi + ")");
			viewHolder.deviceRssi.setTextColor(context.getResources().getColor(
					R.color.red));
			break;
		}
	}

}