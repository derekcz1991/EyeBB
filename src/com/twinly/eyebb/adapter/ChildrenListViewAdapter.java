package com.twinly.eyebb.adapter;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

public class ChildrenListViewAdapter extends BaseAdapter {
	private Context context;
	private Map<String, Child> childrenMap;
	private String[] childrenId;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView locationName;
		public TextView phone;
		public LinearLayout phoneBtn;
		public LinearLayout btnBeep;
		public LinearLayout moreInfo;
	}

	public ChildrenListViewAdapter(Context context,
			Map<String, Child> childrenMap) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.childrenMap = childrenMap;
		this.childrenId = (String[]) childrenMap.keySet().toArray(
				new String[childrenMap.size()]);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		return childrenMap.size();
	}

	@Override
	public Object getItem(int position) {
		return childrenMap.get(childrenId[position]);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_child, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.locationName = (TextView) convertView
					.findViewById(R.id.area_name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
			viewHolder.phoneBtn = (LinearLayout) convertView
					.findViewById(R.id.phone_btn);
			viewHolder.btnBeep = (LinearLayout) convertView
					.findViewById(R.id.btn_beep);
			viewHolder.moreInfo = (LinearLayout) convertView
					.findViewById(R.id.more_info);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		final Child child = childrenMap.get(childrenId[position]);
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					options, null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.hugh));
		}
		viewHolder.name.setText(child.getName());
		viewHolder.moreInfo.setVisibility(View.VISIBLE);
		viewHolder.locationName.setText("@ " + child.getLocationName());
		viewHolder.phone.setText(child.getPhone());
		if (viewHolder.phone.getText().toString().trim().length() == 0) {
			viewHolder.phoneBtn.setVisibility(View.GONE);
		}
		viewHolder.phoneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri telUri = Uri.parse("tel:" + child.getPhone());
				Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
				context.startActivity(intent);
			}
		});
		viewHolder.btnBeep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					Intent intent = new Intent(context, BeepDialog.class);
					context.startActivity(intent);
				}
			}
		});

	}
}