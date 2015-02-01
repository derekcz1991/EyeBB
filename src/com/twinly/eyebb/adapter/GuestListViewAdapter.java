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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.GrantKidsActivity;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.User;

public class GuestListViewAdapter extends BaseAdapter {
	private Context context;
	private List<User> data;
	private LayoutInflater inflater;
	private ViewGroup child_item;

	private List<User> master_data;

	private ArrayList<Child> auth_from_master_children_data;
	private ArrayList<Child> new_children_data;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView phone;
		public RelativeLayout btn_guest_view;
		public ViewGroup avatarContainer;

		public TextView auth_to_children_num;

	}

	public GuestListViewAdapter(Context context, List<User> data,
			List<User> master_data,
			ArrayList<Child> auth_from_master_children_data) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;

		this.master_data = master_data;
		this.auth_from_master_children_data = auth_from_master_children_data;
	}

	@Override
	public int getCount() {
		System.out.println("data.size()-->" + data.size());
		// start from 0
		return 2;
	}

	@Override
	public User getItem(int position) {
		// return data.get(position);
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
			viewHolder.auth_to_children_num = (TextView) convertView
					.findViewById(R.id.auth_to_children_num);

			if (position == 1) {
				convertView.findViewById(R.id.liner_g_to_m).setBackground(
						context.getResources().getDrawable(
								R.drawable.bg_home_blue01));
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

		if (position == 1) {
			new_children_data = new ArrayList<Child>();

			for (int i = 0; i < master_data.size(); i++) {

				for (int y = 0; y < auth_from_master_children_data.size(); y++) {
					if (master_data
							.get(i)
							.getGuardianId()
							.equals(auth_from_master_children_data.get(y)
									.getPhone())) {
						new_children_data.add(auth_from_master_children_data
								.get(y));
						break;
					}
				}

			}
			for (int i = 0; i < master_data.size(); i++) {

				child_item = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.list_item_grant_kid_new_child_item,
						viewHolder.avatarContainer, false);

				viewHolder.name = (TextView) child_item
						.findViewById(R.id.auth_nick_name);
				viewHolder.phone = (TextView) child_item
						.findViewById(R.id.auth_user_name);
				viewHolder.btn_guest_view = (RelativeLayout) child_item
						.findViewById(R.id.btn_guest_view);

				viewHolder.name.setText(master_data.get(i).getName());
				viewHolder.phone.setText(master_data.get(i).getPhoneNumber());
				final User guest = master_data.get(i);

				System.out.println("data.size()-->"
						+ master_data.get(i).getName());
				viewHolder.btn_guest_view
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								System.out.println("--->guest name: "
										+ guest.getName());
								System.out.println("--->guest id: "
										+ guest.getGuardianId());

								Intent intent = new Intent(context,
										GrantKidsActivity.class);
								intent.putExtra("guestId",
										guest.getGuardianId());
								intent.putExtra("guestName", guest.getName());
								intent.putExtra("from_where", "master");
								intent.putExtra("child_data", new_children_data);
								context.startActivity(intent);
								((Activity) context).finish();

							}
						});

				viewHolder.avatarContainer.addView(child_item, 0);
			}
			viewHolder.auth_to_children_num.setText(master_data.size() + "");
		} else {

			for (int i = 0; i < data.size(); i++) {

				child_item = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.list_item_grant_kid_new_child_item,
						viewHolder.avatarContainer, false);

				viewHolder.name = (TextView) child_item
						.findViewById(R.id.auth_nick_name);
				viewHolder.phone = (TextView) child_item
						.findViewById(R.id.auth_user_name);
				viewHolder.btn_guest_view = (RelativeLayout) child_item
						.findViewById(R.id.btn_guest_view);

				viewHolder.name.setText(data.get(i).getName());
				viewHolder.phone.setText(data.get(i).getPhoneNumber());
				final User guest = data.get(i);

				System.out.println("data.size()-->" + data.get(i).getName());
				viewHolder.btn_guest_view
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								System.out.println("--->guest name: "
										+ guest.getName());
								System.out.println("--->guest id: "
										+ guest.getGuardianId());

								Intent intent = new Intent(context,
										GrantKidsActivity.class);
								intent.putExtra("guestId",
										guest.getGuardianId());
								intent.putExtra("guestName", guest.getName());
								intent.putExtra("from_where", "guest");
								context.startActivity(intent);
								((Activity) context).finish();
							}
						});

				viewHolder.avatarContainer.addView(child_item, 0);

			}

			viewHolder.auth_to_children_num.setText(data.size() + "");
		}

		// final User guest = data.get(position);

		// viewHolder.phone.setText(guest.getPhoneNumber());
		// viewHolder.name.setText(guest.getName());
	}

}
