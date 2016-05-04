package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.AvatarView;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.Group;

public class GroupLocatorAdapter extends BaseAdapter {
	private final int[] backgrouds = new int[] { R.drawable.bg_home_blue01,
			R.drawable.bg_home_green01, R.drawable.bg_home_yellow01,
			R.drawable.bg_home_orange, R.drawable.bg_home_pink,
			R.drawable.bg_home_purple, R.drawable.bg_home_blue02,
			R.drawable.bg_home_green02, R.drawable.bg_home_yellow02 };

	private final int[] icons = new int[] { R.drawable.ic_home_art,
			R.drawable.ic_home_classroom, R.drawable.ic_home_food,
			R.drawable.ic_home_music, R.drawable.ic_home_pc,
			R.drawable.ic_home_play, R.drawable.ic_home_sleep };

	private Context context;
	private ArrayList<Group> groupList;
	private boolean isViewAllRooms;

	private final class ViewHolder {
		public LinearLayout contentLayout;
		public CircleImageView icon;
		public CheckedTextView monitorSwitch;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public GroupLocatorAdapter(Context context, ArrayList<Group> groupList) {
		this.context = context;
		this.groupList = groupList;
		processData();
	}

	private void processData() {
		if (!isViewAllRooms) {
			// remove empty group
			for (int i = 0; i < groupList.size(); i++) {
				if (groupList.get(i).getChildList() == null
						&& groupList.get(i).getChildList().size() == 0) {
					groupList.remove(i);
					i--;
				}
			}
		}
	}

	@Override
	public int getCount() {
		return groupList == null ? 0 : groupList.size();
	}

	@Override
	public Object getItem(int position) {
		return groupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_indoor_locator, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.contentLayout = (LinearLayout) convertView
					.findViewById(R.id.content);
			viewHolder.icon = (CircleImageView) convertView
					.findViewById(R.id.loc_icon);
			viewHolder.monitorSwitch = (CheckedTextView) convertView
					.findViewById(R.id.btn_monitor_switch);
			viewHolder.areaName = (TextView) convertView
					.findViewById(R.id.areaNameText);
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

	@Override
	public void notifyDataSetChanged() {
		processData();
		super.notifyDataSetChanged();
	}

	public void setViewAllRooms(boolean isViewAllRooms) {
		this.isViewAllRooms = isViewAllRooms;
		notifyDataSetChanged();
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		// clear the view
		viewHolder.avatarContainer.removeAllViews();
		Group group = groupList.get(position);
		viewHolder.areaName.setText(group.getGroupName());
		// set the the number of children
		viewHolder.childrenNum.setText(group.getChildList().size() + "");
		// add the avatar to flowlayout
		if (group.getChildList() != null) {
			ChildForLocator child;
			for (int i = 0; i < group.getChildList().size(); i++) {
				child = group.getChildList().get(i);
				AvatarView avatarView;
				if (System.currentTimeMillis() - child.getLastAppearTime() < Constants.validTimeDuration) {
					avatarView = new AvatarView(context, child,
							viewHolder.avatarContainer, true);
				} else {
					avatarView = new AvatarView(context, child,
							viewHolder.avatarContainer, false);
				}
				viewHolder.avatarContainer.addView(avatarView.getInstance());
			}
		}
		viewHolder.contentLayout.setBackgroundResource(backgrouds[position
				% backgrouds.length]);
		viewHolder.icon.setImageResource(icons[position % icons.length]);
		viewHolder.monitorSwitch.setVisibility(View.GONE);
	}
}
