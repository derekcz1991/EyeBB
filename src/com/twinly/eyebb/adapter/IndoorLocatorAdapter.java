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
import com.twinly.eyebb.customview.AvatarView;
import com.twinly.eyebb.model.Child;

public class IndoorLocatorAdapter extends BaseAdapter {
	private Context context;
	private List<Map.Entry<String, ArrayList<String>>> data;
	private Map<String, Child> childrenMap;
	private LayoutInflater inflater;

	public final class ViewHolder {
		public LinearLayout rootLayout;
		public TextView icon;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public IndoorLocatorAdapter(Context context,
			Map<String, ArrayList<String>> data, Map<String, Child> childrenMap) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.childrenMap = childrenMap;

		this.data = new ArrayList<Map.Entry<String, ArrayList<String>>>(
				data.entrySet());

		Collections.sort(this.data,
				new Comparator<Map.Entry<String, ArrayList<String>>>() {

					@Override
					public int compare(Entry<String, ArrayList<String>> lhs,
							Entry<String, ArrayList<String>> rhs) {
						return rhs.getValue().size() - lhs.getValue().size();
					}
				});
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

		String locationName = data.get(position).getKey();
		// set the area name
		viewHolder.areaName.setText(locationName);
		ArrayList<String> childrenIds = data.get(position).getValue();
		// set the the number of children
		viewHolder.childrenNum.setText(String.valueOf(childrenIds.size()));
		for (int i = 0; i < childrenIds.size(); i++) {
			// add the avatar to flowlayout
			AvatarView avatarView = new AvatarView(context,
					childrenMap.get(childrenIds.get(i)),
					viewHolder.avatarContainer);
			viewHolder.avatarContainer.addView(avatarView.getInstance(), 0);

			// update the child's location
			childrenMap.get(childrenIds.get(i)).setLocationName(locationName);
		}

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
		} else if (locationName.contains("Class")) {
			viewHolder.icon.setBackgroundResource(R.drawable.ic_home_classroom);
			viewHolder.rootLayout
					.setBackgroundResource(R.drawable.bg_home_yellow02);
		}
	}

}
