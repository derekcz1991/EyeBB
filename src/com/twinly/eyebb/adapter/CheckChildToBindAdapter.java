package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twinly.twinly.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.ImageUtils;

public class CheckChildToBindAdapter extends BaseAdapter {

	private ArrayList<Child> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
	}

	public CheckChildToBindAdapter(Context context,
			ArrayList<Child> childrenList) {
		inflater = LayoutInflater.from(context);
		this.data = childrenList;

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}

	@Override
	public int getCount() {
		// System.out.println("data.size()>" + data.size());
		return data.size();
	}

	@Override
	public Child getItem(int position) {
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
			convertView = inflater.inflate(
					R.layout.dialog_check_child_to_bind_list_listitem, parent,
					false);

			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		final Child child = data.get(position);
		if (ImageUtils.isLocalImage(child.getLocalIcon())) {
			viewHolder.avatar.setImageBitmap(ImageUtils
					.getBitmapFromLocal(child.getLocalIcon()));
		} else {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					ImageUtils.avatarOpitons, null);
		}
		viewHolder.name.setText(child.getName());
	}
}
