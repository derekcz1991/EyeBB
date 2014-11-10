package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.AvatarView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Location;

public class IndoorLocatorAdapter extends BaseAdapter {
	private Context context;
	private List<Map.Entry<Location, ArrayList<String>>> data;
	private Map<String, Child> childrenMap;
	private LayoutInflater inflater;
	private boolean isSort;

	public final class ViewHolder {
		public LinearLayout rootLayout;
		public TextView icon;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public IndoorLocatorAdapter(Context context,
			Map<Location, ArrayList<String>> data,
			Map<String, Child> childrenMap, boolean isSort) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.childrenMap = childrenMap;
		this.isSort = isSort;
		this.data = new ArrayList<Map.Entry<Location, ArrayList<String>>>(
				data.entrySet());

		if (this.isSort) {
			// remove the empty location
			for (int i = 0; i < this.data.size(); i++) {
				if (this.data.get(i).getValue().size() == 0) {
					this.data.remove(i);
					i--;
				}
			}
			// sort the location by the children number
			Collections.sort(this.data,
					new Comparator<Map.Entry<Location, ArrayList<String>>>() {

						@Override
						public int compare(
								Entry<Location, ArrayList<String>> lhs,
								Entry<Location, ArrayList<String>> rhs) {
							if (lhs.getKey().getType().equals("X")
									|| lhs.getKey().getType().equals("N")) {
								return 1;
							} else if (rhs.getKey().getType().equals("X")
									|| rhs.getKey().getType().equals("N")) {
								return -1;
							}

							if (rhs.getValue().size() - lhs.getValue().size() == 0) {
								return (int) (lhs.getKey().getId() - rhs
										.getKey().getId());
							} else {
								return rhs.getValue().size()
										- lhs.getValue().size();
							}
						}
					});
		} else {
			// sort the location by location id
			Collections.sort(this.data,
					new Comparator<Map.Entry<Location, ArrayList<String>>>() {

						@Override
						public int compare(
								Entry<Location, ArrayList<String>> lhs,
								Entry<Location, ArrayList<String>> rhs) {
							if (lhs.getKey().getType().equals("X")
									|| lhs.getKey().getType().equals("N")) {
								return 1;
							} else if (rhs.getKey().getType().equals("X")
									|| rhs.getKey().getType().equals("N")) {
								return -1;
							}
							return (int) (lhs.getKey().getId() - rhs.getKey()
									.getId());
						}
					});
		}
	}

	@Override
	public int getCount() {
		return data.size();
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
			viewHolder.rootLayout = (LinearLayout) convertView
					.findViewById(R.id.root);
			viewHolder.icon = (TextView) convertView.findViewById(R.id.icon);
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

		String locationType = data.get(position).getKey().getType();
		String locationName = data.get(position).getKey().getName();
		// set the area name
		viewHolder.areaName.setText(locationName);

		ArrayList<String> childrenIds = data.get(position).getValue();
		// remove the child if he showed in the EXIT area and stay more than 10 mins.
		if (locationType.equals("X")) {
			for (int i = 0; i < childrenIds.size(); i++) {
				if (System.currentTimeMillis()
						- childrenMap.get(childrenIds.get(i))
								.getLastAppearTime() > Constants.validTimeDuration) {
					childrenIds.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < childrenIds.size(); i++) {
			// add the avatar to flowlayout
			AvatarView avatarView;
			if (System.currentTimeMillis()
					- childrenMap.get(childrenIds.get(i)).getLastAppearTime() < Constants.validTimeDuration) {
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

		if (locationName.contains("Sleeping")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_sleep);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_yellow01);
		} else if (locationName.contains("Playground")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_play);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_blue01);
		} else if (locationName.contains("Computer")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_pc);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_pink);
		} else if (locationName.contains("Art")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_art);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_purple);
		} else if (locationName.contains("Music")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_music);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_green02);
		} else if (locationName.contains("Mess")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_food);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_green01);
		} else if (locationName.contains("Study")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_classroom);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_yellow02);
		} else if (locationName.contains("Entrance")) {
			viewHolder.icon.setBackground(null);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_blue02);
		} else if (locationName.contains("Exit")) {
			viewHolder.icon.setBackground(null);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_blue02);
		}
	}

}
