package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.twinly.R;
import com.twinly.eyebb.activity.KidProfileActivity;
import com.twinly.eyebb.activity.MyKidsListActivity;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.ImageUtils;

public class MykIdsListAdapter extends BaseAdapter {
	private final int[] backgrouds = new int[] { R.drawable.bg_home_green01,
			R.drawable.bg_home_blue01, R.drawable.bg_home_yellow01 };

	MyKidsListActivity myKidsListActivity;
	private Context context;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	ArrayList<Child> childrenWithAddress;
	ArrayList<Child> childrenWithoutAddress;
	ArrayList<Child> chidrenGuest;

	private ViewGroup children_item;

	public MykIdsListAdapter(MyKidsListActivity myKidsListActivity,
			ArrayList<Child> childrenWithAddress,
			ArrayList<Child> childrenWithoutAddress,
			ArrayList<Child> chidrenGuest) {
		inflater = LayoutInflater.from(myKidsListActivity);
		this.myKidsListActivity = myKidsListActivity;
		this.context = myKidsListActivity;
		this.childrenWithAddress = childrenWithAddress;
		this.childrenWithoutAddress = childrenWithoutAddress;
		this.chidrenGuest = chidrenGuest;

		imageLoader = ImageLoader.getInstance();
	}

	private final class ViewHolder {
		public TextView tv_title;
		public ViewGroup avatarContainer;

		public CircleImageView avatar;
	}

	/*
	 * this view we just need 3 list
	 */
	@Override
	public int getCount() {
		return 3;
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

			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.list_item_my_kids_list_first_layer, parent, false);

			viewHolder.avatarContainer = (ViewGroup) convertView
					.findViewById(R.id.avatarContainer);
			viewHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);

			switch (position) {
			case 0:
				viewHolder.tv_title.setText(context
						.getString(R.string.text_bind_child));
				convertView.findViewById(R.id.liner_g_to_m)
						.setBackgroundResource(backgrouds[0]);
				break;
			case 1:
				viewHolder.tv_title.setText(context
						.getString(R.string.text_unbind_child));
				convertView.findViewById(R.id.liner_g_to_m)
						.setBackgroundResource(backgrouds[1]);
				break;
			case 2:
				viewHolder.tv_title.setText(context
						.getString(R.string.text_granted_child));
				convertView.findViewById(R.id.liner_g_to_m)
						.setBackgroundResource(backgrouds[2]);
				break;
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
		switch (position) {
		case 0:
			for (int i = 0; i < childrenWithAddress.size(); i++) {
				children_item = (ViewGroup) LayoutInflater.from(context)
						.inflate(R.layout.item_avatar,
								viewHolder.avatarContainer, false);
				viewHolder.avatar = (CircleImageView) children_item
						.findViewById(R.id.avatar);

				final Child child = childrenWithAddress.get(i);

				if (ImageUtils.isLocalImage(child.getLocalIcon())) {
					viewHolder.avatar.setImageBitmap(ImageUtils
							.getBitmapFromLocal(child.getLocalIcon()));
				} else {
					imageLoader.displayImage(child.getIcon(),
							viewHolder.avatar, ImageUtils.avatarOpitons, null);
				}

				viewHolder.avatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent_rewrite = new Intent(context,
								KidProfileActivity.class);
						intent_rewrite.putExtra(
								ActivityConstants.EXTRA_CHILD_ID,
								child.getChildId());

						myKidsListActivity
								.startActivityForResult(
										intent_rewrite,
										ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY);
					}
				});

				viewHolder.avatarContainer.addView(children_item, 0);

			}

			break;

		case 1:
			for (int i = 0; i < childrenWithoutAddress.size(); i++) {
				children_item = (ViewGroup) LayoutInflater.from(context)
						.inflate(R.layout.item_avatar,
								viewHolder.avatarContainer, false);
				viewHolder.avatar = (CircleImageView) children_item
						.findViewById(R.id.avatar);

				if (TextUtils.isEmpty(childrenWithoutAddress.get(i).getIcon()) == false) {
					imageLoader.displayImage(childrenWithoutAddress.get(i)
							.getIcon(), viewHolder.avatar,
							ImageUtils.avatarOpitons, null);
				} else {
					viewHolder.avatar.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.icon_avatar_dark));
				}

				final Child child = childrenWithoutAddress.get(i);

				viewHolder.avatar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent_rewrite = new Intent(context,
								KidProfileActivity.class);
						intent_rewrite.putExtra(
								ActivityConstants.EXTRA_CHILD_ID,
								child.getChildId());

						myKidsListActivity
								.startActivityForResult(
										intent_rewrite,
										ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY);

					}
				});

				viewHolder.avatarContainer.addView(children_item, 0);
			}

			break;
		case 2:
			for (int i = 0; i < chidrenGuest.size(); i++) {
				children_item = (ViewGroup) LayoutInflater.from(context)
						.inflate(R.layout.item_avatar,
								viewHolder.avatarContainer, false);
				viewHolder.avatar = (CircleImageView) children_item
						.findViewById(R.id.avatar);

				if (TextUtils.isEmpty(chidrenGuest.get(i).getIcon()) == false) {
					imageLoader.displayImage(chidrenGuest.get(i).getIcon(),
							viewHolder.avatar, ImageUtils.avatarOpitons, null);
				} else {
					viewHolder.avatar.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.icon_avatar_dark));
				}

				final Child child = chidrenGuest.get(i);

				viewHolder.avatar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// myKidsListActivity.startActivityForResult(child);

						Intent intent_rewrite = new Intent(context,
								KidProfileActivity.class);
						intent_rewrite.putExtra(
								ActivityConstants.EXTRA_CHILD_ID,
								child.getChildId());

						myKidsListActivity
								.startActivityForResult(
										intent_rewrite,
										ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY);

					}
				});

				viewHolder.avatarContainer.addView(children_item, 0);
			}

			break;
		}
	}

}
