package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.ImageUtils;

public class KidsListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map.Entry<Long, Child>> list;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView locationName;
		public TextView phone;
		public LinearLayout phoneBtn;
		public LinearLayout btnBeep;
	}

	public KidsListViewAdapter(Context context,
			List<Map.Entry<Long, Child>> data, boolean isSortByName,
			boolean isSortByLocator) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = new ArrayList<Map.Entry<Long, Child>>();
		this.list.addAll(data);
		if (isSortByName) {
			Collections.sort(list, new Comparator<Map.Entry<Long, Child>>() {

				@Override
				public int compare(Entry<Long, Child> lhs,
						Entry<Long, Child> rhs) {
					return lhs.getValue().getName().charAt(0)
							- rhs.getValue().getName().charAt(0);
				}
			});
		}

		if (isSortByLocator) {
			Collections.sort(list, new Comparator<Map.Entry<Long, Child>>() {

				@Override
				public int compare(Entry<Long, Child> lhs,
						Entry<Long, Child> rhs) {
					if (lhs.getValue().getLocationName().length() == 0) {
						return 1;
					} else if (rhs.getValue().getLocationName().length() == 0) {
						return -1;
					}
					return lhs.getValue().getLocationName().charAt(0)
							- rhs.getValue().getLocationName().charAt(0);
				}
			});
		}

		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_kid, parent,
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		final Child child = list.get(position).getValue();
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					ImageUtils.avatarOpitons, null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.icon_avatar_dark));
		}
		viewHolder.name.setText(child.getName());
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
					if (child.getMacAddress().length() > 0) {
						System.out.println("THIS CHILD ID:"
								+ child.getChildId() + " MACADDRESS:"
								+ child.getMacAddress());
					} else {
					}

				}
			}
		});

	}
}
