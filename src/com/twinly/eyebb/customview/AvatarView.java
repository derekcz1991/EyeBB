package com.twinly.eyebb.customview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.twinly.R;
import com.twinly.eyebb.dialog.ChildDialog;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.utils.ImageUtils;

public class AvatarView {
	private Context context;
	private ChildForLocator childForLocator;
	private CircleImageView avatar;
	private ViewGroup avatarViewItem;
	private boolean isOnline;
	private ImageLoader imageLoader;

	public AvatarView(Context context, ChildForLocator childForLocator,
			ViewGroup viewGroup, boolean isOnline) {
		this.context = context;
		this.childForLocator = childForLocator;
		this.isOnline = isOnline;

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
		if (isOnline == false) {
			avatar.setAlpha(0.4f);
		}

		if (ImageUtils.isLocalImage(childForLocator.getLocalIcon())) {
			avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(childForLocator
					.getLocalIcon()));
		} else {
			imageLoader.displayImage(childForLocator.getIcon(), avatar,
					ImageUtils.avatarOpitons, null);
		}

		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChildDialog.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ChildDialog.EXTRA_CHILD, childForLocator);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

}
