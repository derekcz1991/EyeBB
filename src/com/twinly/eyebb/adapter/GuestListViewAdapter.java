package com.twinly.eyebb.adapter;

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

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.GrantKidsActivity;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.User;

public class GuestListViewAdapter extends BaseAdapter {
	private Context context;
	private List<User> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView phone;
		public RelativeLayout btn_guest_view;
	}

	public GuestListViewAdapter(Context context, List<User> data) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;

		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		System.out.println("data.size()-->" + data.size());
		return data.size();

	}

	@Override
	public User getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_guest, parent,
					false);
			viewHolder = new ViewHolder();
			// viewHolder.avatar = (CircleImageView) convertView
			// .findViewById(R.id.avatar);
			//
			// //NO NEED
			// viewHolder.avatar.setVisibility(View.GONE);

			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
			viewHolder.btn_guest_view = (RelativeLayout) convertView
					.findViewById(R.id.btn_guest_view);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		final User guest = data.get(position);
		// if (TextUtils.isEmpty(guest.getIcon()) == false) {
		// imageLoader.displayImage(guest.getIcon(), viewHolder.avatar,
		// CommonUtils.getDisplayImageOptions(), null);
		// } else {
		// viewHolder.avatar.setImageDrawable(context.getResources()
		// .getDrawable(R.drawable.ic_stub));
		// }
		viewHolder.btn_guest_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("--->guest name: " + guest.getName());
				System.out.println("--->guest id: " + guest.getGuardianId());

				Intent intent = new Intent(context, GrantKidsActivity.class);
				intent.putExtra("guestId", guest.getGuardianId());
				intent.putExtra("guestName", guest.getName());
				intent.putExtra("from_where", "guest");
				context.startActivity(intent);
				((Activity) context).finish();
			}
		});

		viewHolder.phone.setText(guest.getPhoneNumber());
		viewHolder.name.setText(guest.getName());
	}
}
