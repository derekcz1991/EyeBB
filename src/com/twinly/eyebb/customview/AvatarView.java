package com.twinly.eyebb.customview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.ChildDialog;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

public class AvatarView {
	private Context context;
	private Child child;
	private CircleImageView avatar;
	private ViewGroup avatarViewItem;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public AvatarView(Context context, Child child, ViewGroup viewGroup) {
		this.context = context;
		this.child = child;

		imageLoader = ImageLoader.getInstance();

		setUpView(viewGroup);
	}

	public ViewGroup getInstance() {
		return avatarViewItem;
	}

	private void setUpView(ViewGroup viewGroup) {
		avatarViewItem = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.item_avatar, viewGroup, false);
		avatar = (CircleImageView) avatarViewItem.findViewById(R.id.avatar);
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), avatar,
					CommonUtils.getDisplayImageOptions(), null);
		} else {
			avatar.setImageDrawable(context.getResources().getDrawable(
					R.drawable.hugh));
		}

		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChildDialog.class);
				intent.putExtra("phone", child.getPhone());
				intent.putExtra("location", child.getLocationName());
				intent.putExtra("name", child.getName());
				intent.putExtra("icon", child.getIcon());
				context.startActivity(intent);
			}
		});
	}

}
