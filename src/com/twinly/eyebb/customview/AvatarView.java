package com.twinly.eyebb.customview;

import android.content.Context;
import android.view.ViewGroup;

import com.twinly.eyebb.model.Child;

public class AvatarView {
	private Context context;
	private Child child;
	private CircleImageView avatar;
	private ViewGroup avatarViewItem;

	public AvatarView(Context context, Child child) {
		this.context = context;
		this.child = child;
	}

	public ViewGroup getInstance() {
		return avatarViewItem;
	}
}
