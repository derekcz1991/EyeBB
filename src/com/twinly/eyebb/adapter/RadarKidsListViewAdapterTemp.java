package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
	private boolean isSuperisedSection;
	private boolean isAntiLostOpen;
	private ViewHolder viewHolder;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public View btnBeep;
		public TextView btnSelected;
		public TextView deviceConnectStatus;
		public TextView deviceRssi;
	}

	public RadarKidsListViewAdapterTemp(Context context,
			ArrayList<Macaron> deviceList, boolean isMissSection) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.deviceList = deviceList;
		this.isSuperisedSection = isMissSection;
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

	public void setSuperisedSection(boolean isSuperisedSection) {
		this.isSuperisedSection = isSuperisedSection;
	}

	public void setAntiLostOpen(boolean isAntiLostOpen) {
		this.isAntiLostOpen = isAntiLostOpen;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.list_item_radar_tracking_kid, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.deviceRssi = (TextView) convertView
					.findViewById(R.id.rssi);
			viewHolder.btnBeep = convertView.findViewById(R.id.btn_beep);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.deviceConnectStatus = (TextView) convertView
					.findViewById(R.id.device_connect_status);
			viewHolder.btnSelected = (TextView) convertView
					.findViewById(R.id.btn_selected);
			viewHolder.btnSelected.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					System.out.println(deviceList.size() + "  " + position);
					if (position < deviceList.size()) {
						Toast.makeText(
								context,
								position
										+ "  "
										+ childMap.get(
												deviceList.get(position)
														.getMacAddress())
												.getName(), Toast.LENGTH_SHORT)
								.show();
						if (deviceList.get(position).isAntiLostOpen()) {
							deviceList.get(position).setAntiLostOpen(false);
						} else {
							deviceList.get(position).setAntiLostOpen(true);
						}
						notifyDataSetChanged();
					}
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		if (isSuperisedSection) {
			viewHolder.avatar.setBorderColor(context.getResources().getColor(
					R.color.white));
			viewHolder.deviceRssi.setVisibility(View.VISIBLE);
			if (isAntiLostOpen) {
				viewHolder.btnBeep.setVisibility(View.INVISIBLE);
				viewHolder.btnSelected.setVisibility(View.VISIBLE);
			} else {
				viewHolder.btnBeep.setVisibility(View.VISIBLE);
				viewHolder.btnSelected.setVisibility(View.INVISIBLE);
			}
		} else {
			viewHolder.avatar.setBorderColor(context.getResources().getColor(
					R.color.red));
			viewHolder.btnBeep.setVisibility(View.INVISIBLE);
			viewHolder.deviceRssi.setVisibility(View.GONE);
			viewHolder.btnSelected.setVisibility(View.INVISIBLE);
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
		case BleDeviceConstants.RSSI_WEAK:
			viewHolder.deviceRssi.setText(context.getResources().getString(
					R.string.text_rssi_weak)
					+ "(" + rssi + ")");
			viewHolder.deviceRssi.setTextColor(context.getResources().getColor(
					R.color.red));
			break;
		}

		if (deviceList.get(position).isAntiLostOpen()) {
			viewHolder.btnSelected
					.setBackgroundResource(R.drawable.ic_selected);
		} else {
			viewHolder.btnSelected
					.setBackgroundResource(R.drawable.ic_selected_off);
		}
	}

}