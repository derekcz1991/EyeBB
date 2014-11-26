package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.AvatarView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Location;
import com.twinly.eyebb.utils.CommonUtils;

public class IndoorLocatorAdapter extends BaseAdapter {
	private Context context;
	private HashMap<Long, Location> locationMap;
	private HashMap<Long, Child> childrenMap;
	private List<HashMap.Entry<Long, ArrayList<Long>>> list;

	private LayoutInflater inflater;
	private boolean isSort;
	private ImageLoader imageLoader;

	private final class ViewHolder {
		public LinearLayout contentLayout;
		public ImageView icon;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public IndoorLocatorAdapter(Context context,
			HashMap<Long, ArrayList<Long>> data,
			HashMap<Long, Location> locationMap,
			HashMap<Long, Child> childrenMap, boolean isSort) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.locationMap = locationMap;
		this.childrenMap = childrenMap;
		this.isSort = isSort;
		this.list = new ArrayList<Map.Entry<Long, ArrayList<Long>>>(
				data.entrySet());

		imageLoader = ImageLoader.getInstance();
		sort();
	}

	private void sort() {
		// move the entry and exit location to the bottom
		Collections.sort(list,
				new Comparator<HashMap.Entry<Long, ArrayList<Long>>>() {

					@Override
					public int compare(Entry<Long, ArrayList<Long>> lhs,
							Entry<Long, ArrayList<Long>> rhs) {
						if (locationMap.get(lhs.getKey()).getType().equals("X")
								|| locationMap.get(lhs.getKey()).getType()
										.equals("N")) {
							return 1;
						} else if (locationMap.get(rhs.getKey()).getType()
								.equals("X")
								|| locationMap.get(rhs.getKey()).getType()
										.equals("N")) {
							return -1;
						} else {
							return 0;
						}
					}

				});

		if (isSort) {
			// remove the empty location
			for (int i = 0; i < this.list.size(); i++) {
				if (this.list.get(i).getValue().size() == 0) {
					this.list.remove(i);
					i--;
				}
			}
			// sort the location by the children number
			Collections.sort(list,
					new Comparator<HashMap.Entry<Long, ArrayList<Long>>>() {

						@Override
						public int compare(Entry<Long, ArrayList<Long>> lhs,
								Entry<Long, ArrayList<Long>> rhs) {
							if (lhs.getValue().size() == rhs.getValue().size()) {
								return 0;
							} else {
								return rhs.getValue().size()
										- lhs.getValue().size();
							}
						}

					});
		} else {
			// sort the location by location id
			Collections.sort(list,
					new Comparator<HashMap.Entry<Long, ArrayList<Long>>>() {

						@Override
						public int compare(Entry<Long, ArrayList<Long>> lhs,
								Entry<Long, ArrayList<Long>> rhs) {
							return (int) (locationMap.get(lhs.getKey()).getId() - locationMap
									.get(rhs.getKey()).getId());
						}

					});
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_indoor_locator,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.contentLayout = (LinearLayout) convertView
					.findViewById(R.id.content);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.areaName = (TextView) convertView
					.findViewById(R.id.area_name);
			viewHolder.childrenNum = (TextView) convertView
					.findViewById(R.id.children_num);
			viewHolder.avatarContainer = (ViewGroup) convertView
					.findViewById(R.id.avatarContainer);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		// clear the view
		viewHolder.avatarContainer.removeAllViews();

		String locationType = locationMap.get(list.get(position).getKey())
				.getType();
		String locationName = locationMap.get(list.get(position).getKey())
				.getName();
		String locationIcon = locationMap.get(list.get(position).getKey())
				.getIcon();

		// set the area name
		viewHolder.areaName.setText(locationName);

		ArrayList<Long> childrenIds = list.get(position).getValue();
		// remove the child if he showed in the EXIT area and stay more than 10 mins.
		if (locationType.equals("X")) {
			for (int i = 0; i < childrenIds.size(); i++) {
				if (System.currentTimeMillis()
						- childrenMap.get(childrenIds.get(i))
								.getLastAppearTime() > BleDeviceConstants.validTimeDuration) {
					childrenIds.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < childrenIds.size(); i++) {
			childrenMap.get(childrenIds.get(i)).setLocationName(locationName);

			// add the avatar to flowlayout
			AvatarView avatarView;
			if (System.currentTimeMillis()
					- childrenMap.get(childrenIds.get(i)).getLastAppearTime() < BleDeviceConstants.validTimeDuration) {
				avatarView = new AvatarView(context,
						childrenMap.get(childrenIds.get(i)),
						viewHolder.avatarContainer, true);
			} else {
				avatarView = new AvatarView(context,
						childrenMap.get(childrenIds.get(i)),
						viewHolder.avatarContainer, false);
			}
			viewHolder.avatarContainer.addView(avatarView.getInstance(), 0);
		}

		// set the the number of children
		viewHolder.childrenNum.setText(String.valueOf(childrenIds.size()));

		if (locationType.equals("X") || locationType.equals("N")) {
			viewHolder.icon.setBackground(null);
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_blue02);
		} else {
			imageLoader.displayImage(locationIcon, viewHolder.icon,
					CommonUtils.getDisplayImageOptions(), null);
		}

		if (locationName.contains("Sleeping")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_yellow01);
		} else if (locationName.contains("Playground")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_blue01);
		} else if (locationName.contains("Computer")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_pink);
		} else if (locationName.contains("Study")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_purple);
		} else if (locationName.contains("Music")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_green02);
		} else if (locationName.contains("Mess")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_green01);
		} else if (locationName.contains("Reading")) {
			viewHolder.contentLayout
					.setBackgroundResource(R.drawable.bg_home_yellow02);
		}
	}

}
