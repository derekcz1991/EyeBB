package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.GrantKidsActivity;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.ChildForGrant;
import com.twinly.eyebb.model.User;

public class GuestListViewAdapter extends BaseAdapter {
	private Context context;
	private List<User> data;
	private LayoutInflater inflater;
	private ViewGroup childItem;

	private List<User> master_data;

	private ArrayList<ChildForGrant> authFromMasterChildrenData;
	private ArrayList<ChildForGrant> newChildrenData;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView nameLeft;
		public TextView phoneLeft;

		public TextView nameRight;
		public TextView phoneRight;

		public LinearLayout reLayoutLeft;
		public LinearLayout reLayoutRight;

		public ViewGroup avatarContainer;
		public TextView authToChildrenNum;
		public TextView tvAuthorizedToOthers;

	}

	public GuestListViewAdapter(Context context, List<User> data,
			List<User> master_data,
			ArrayList<ChildForGrant> auth_from_master_children_data) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;

		this.master_data = master_data;
		this.authFromMasterChildrenData = auth_from_master_children_data;
	}

	/**
	 * we just need two list
	 */
	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public User getItem(int position) {
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

			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_grant_kid_new,
					parent, false);

			viewHolder.avatarContainer = (ViewGroup) convertView
					.findViewById(R.id.avatarContainer);
			viewHolder.authToChildrenNum = (TextView) convertView
					.findViewById(R.id.auth_to_children_num);
			viewHolder.tvAuthorizedToOthers = (TextView) convertView
					.findViewById(R.id.tv_authorized_to_others);
			if (position == 1) {
				convertView.findViewById(R.id.liner_g_to_m)
						.setBackgroundResource(R.drawable.bg_home_blue01);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {

		viewHolder.avatarContainer.removeAllViews();

		/*
		 * return 2 list , 1 is for master list, 0 is for guest list
		 * 
		 */
		if (position == 1) {

			viewHolder.tvAuthorizedToOthers.setText(context
					.getString(R.string.text_authorization_from_others));

			newChildrenData = new ArrayList<ChildForGrant>();

			for (int i = 0; i < master_data.size(); i++) {

				for (int y = 0; y < authFromMasterChildrenData.size(); y++) {
					if (master_data
							.get(i)
							.getGuardianId()
							.equals(authFromMasterChildrenData.get(y)
									.getPhone())) {
						newChildrenData.add(authFromMasterChildrenData.get(y));
						break;
					}
				}

			}
			for (int i = 0; i < master_data.size();) {

				// this view has two sides (left and right) and we get two data at once, so i = 1 + 2
				childItem = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.list_item_grant_kid_new_child_item,
						viewHolder.avatarContainer, false);

				viewHolder.nameLeft = (TextView) childItem
						.findViewById(R.id.auth_nick_name_left);
				viewHolder.phoneLeft = (TextView) childItem
						.findViewById(R.id.auth_user_name_left);
				viewHolder.reLayoutLeft = (LinearLayout) childItem
						.findViewById(R.id.re_layout_left);

				viewHolder.nameRight = (TextView) childItem
						.findViewById(R.id.auth_nick_name_right);
				viewHolder.phoneRight = (TextView) childItem
						.findViewById(R.id.auth_user_name_right);
				viewHolder.reLayoutRight = (LinearLayout) childItem
						.findViewById(R.id.re_layout_right);

				viewHolder.nameLeft.setText(master_data.get(i).getName());
				viewHolder.phoneLeft.setText(master_data.get(i)
						.getPhoneNumber());
				final User guest_left = master_data.get(i);

				viewHolder.reLayoutLeft
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(context,
										GrantKidsActivity.class);
								intent.putExtra("guestId",
										guest_left.getGuardianId());
								intent.putExtra("guestName",
										guest_left.getName());
								intent.putExtra("from_where", "master");
								intent.putExtra("child_data", newChildrenData);
								context.startActivity(intent);
								((Activity) context).finish();

							}
						});

				if (i + 1 > master_data.size() - 1) {
					viewHolder.reLayoutRight.setVisibility(View.INVISIBLE);
				} else {
					viewHolder.nameRight.setText(master_data.get(i + 1)
							.getName());
					viewHolder.phoneRight.setText(master_data.get(i + 1)
							.getPhoneNumber());
					final User guest_right = master_data.get(i + 1);

					viewHolder.reLayoutRight
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {

									Intent intent = new Intent(context,
											GrantKidsActivity.class);
									intent.putExtra("guestId",
											guest_right.getGuardianId());
									intent.putExtra("guestName",
											guest_right.getName());
									intent.putExtra("from_where", "master");
									intent.putExtra("child_data",
											newChildrenData);
									context.startActivity(intent);
									((Activity) context).finish();

								}
							});

				}

				i = i + 2;
				viewHolder.avatarContainer.addView(childItem, 0);
			}
			viewHolder.authToChildrenNum.setText(master_data.size() + "");
		} else {

			for (int i = 0; i < data.size();) {

				childItem = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.list_item_grant_kid_new_child_item,
						viewHolder.avatarContainer, false);

				viewHolder.nameLeft = (TextView) childItem
						.findViewById(R.id.auth_nick_name_left);
				viewHolder.phoneLeft = (TextView) childItem
						.findViewById(R.id.auth_user_name_left);
				viewHolder.reLayoutLeft = (LinearLayout) childItem
						.findViewById(R.id.re_layout_left);

				viewHolder.nameRight = (TextView) childItem
						.findViewById(R.id.auth_nick_name_right);
				viewHolder.phoneRight = (TextView) childItem
						.findViewById(R.id.auth_user_name_right);
				viewHolder.reLayoutRight = (LinearLayout) childItem
						.findViewById(R.id.re_layout_right);

				viewHolder.nameLeft.setText(data.get(i).getName());
				viewHolder.phoneLeft.setText(data.get(i).getPhoneNumber());
				final User guest_left = data.get(i);

				viewHolder.reLayoutLeft
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(context,
										GrantKidsActivity.class);
								intent.putExtra("guestId",
										guest_left.getGuardianId());
								intent.putExtra("guestName",
										guest_left.getName());
								intent.putExtra("from_where", "guest");
								context.startActivity(intent);
								((Activity) context).finish();
							}
						});

				if (i + 1 > data.size() - 1) {
					viewHolder.reLayoutRight.setVisibility(View.INVISIBLE);
				} else {
					viewHolder.nameRight.setText(data.get(i + 1).getName());
					viewHolder.phoneRight.setText(data.get(i + 1)
							.getPhoneNumber());
					final User guestRight = data.get(i + 1);

					viewHolder.reLayoutRight
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(context,
											GrantKidsActivity.class);
									intent.putExtra("guestId",
											guestRight.getGuardianId());
									intent.putExtra("guestName",
											guestRight.getName());
									intent.putExtra("from_where", "guest");
									context.startActivity(intent);
									((Activity) context).finish();
								}
							});
				}
				i = i + 2;
				viewHolder.avatarContainer.addView(childItem, 0);
			}
			viewHolder.authToChildrenNum.setText(data.size() + "");
		}
	}

}
