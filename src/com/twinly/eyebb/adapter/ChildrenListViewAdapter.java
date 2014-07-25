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
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.IndoorAera;
import com.twinly.eyebb.utils.CommonUtils;

public class ChildrenListViewAdapter extends BaseAdapter {
	private Context context;
	private Map<String, Child> childrenMap;
	private String[] childrenId;
	private Map<String, IndoorAera> indoorAeraMap;
	private LayoutInflater inflater;
	private boolean showMoreInfo;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView areaName;
		public TextView phone;
		public LinearLayout btnBeep;
		public LinearLayout moreInfo;
	}

	public ChildrenListViewAdapter(Context context,
			Map<String, Child> childrenMap,
			Map<String, IndoorAera> indoorAeraMap, boolean showMoreInfo) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.showMoreInfo = showMoreInfo;
		this.indoorAeraMap = indoorAeraMap;
		this.childrenMap = childrenMap;
		this.childrenId = (String[]) childrenMap.keySet().toArray();
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
			viewHolder.areaName = (TextView) convertView
					.findViewById(R.id.area_name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
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
		viewHolder.avatar.setImageDrawable(context.getResources().getDrawable(
				R.drawable.hugh));
		viewHolder.name.setText(child.getName());
		if (showMoreInfo) {
			viewHolder.moreInfo.setVisibility(View.VISIBLE);
			viewHolder.areaName.setText(indoorAeraMap.get(
					child.getIndoorAreaId()).getName());
			viewHolder.phone.setText(child.getPhone());
			viewHolder.phone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (TextUtils.isEmpty(child.getPhone()) != false) {
						Uri telUri = Uri.parse("tel:" + child.getPhone());
						Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
						context.startActivity(intent);
					}

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
		} else {
			viewHolder.moreInfo.setVisibility(View.GONE);
		}

	}
}
