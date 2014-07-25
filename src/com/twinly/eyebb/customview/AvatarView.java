package com.twinly.eyebb.customview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.eyebb.R;
import com.twinly.eyebb.activity.ChildDialog;
import com.twinly.eyebb.model.Child;

public class AvatarView {
	private Context context;
	private Child child;
	private String areaName;
	private CircleImageView avatar;
	private ViewGroup avatarViewItem;

	public AvatarView(Context context, Child child, ViewGroup viewGroup) {
		this.context = context;
		this.child = child;

		setUpView(viewGroup);
	}

	public ViewGroup getInstance() {
		return avatarViewItem;
	}

	private void setUpView(ViewGroup viewGroup) {
		avatarViewItem = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.item_avatar, viewGroup, false);
		avatar = (CircleImageView) avatarViewItem.findViewById(R.id.avatar);
		avatar.setImageDrawable(context.getResources().getDrawable(
				R.drawable.hugh));
		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChildDialog.class);
				intent.putExtra("phone", child.getPhone());
				intent.putExtra("area_name", areaName);
				intent.putExtra("name", child.getName());
				context.startActivity(intent);
			}
		});
	}

}
