package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.customview.AvatarView;
import com.twinly.eyebb.model.Child;

public class IndoorLocatorAdapter extends BaseAdapter {
	private Context context;
	private Map<String, ArrayList<String>> data;
	private String[] locationNames;
	private Map<String, Child> childrenMap;
	private LayoutInflater inflater;

	public final class ViewHolder {
		public TextView icon;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public IndoorLocatorAdapter(Context context,
			Map<String, ArrayList<String>> data, Map<String, Child> childrenMap) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;
		this.childrenMap = childrenMap;
		this.locationNames = (String[]) data.keySet().toArray(
				new String[data.keySet().size()]);
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

		String locationName = locationNames[position];
		// set the area name
		viewHolder.areaName.setText(locationName);
		ArrayList<String> childrenIds = data.get(locationName);
		// set the the number of children
		viewHolder.childrenNum.setText(String.valueOf(childrenIds.size()));
		for (int i = 0; i < childrenIds.size(); i++) {
			// add the avatar to flowlayout
			AvatarView avatarView = new AvatarView(context,
					childrenMap.get(childrenIds.get(i)),
					viewHolder.avatarContainer);
			viewHolder.avatarContainer.addView(avatarView.getInstance(), 0);

			// update the child's area id
			childrenMap.get(childrenIds.get(i)).setLocationName(locationName);
		}
	}

}
