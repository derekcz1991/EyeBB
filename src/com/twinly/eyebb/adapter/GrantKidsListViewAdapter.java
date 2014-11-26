package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class GrantKidsListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Child> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	public static ArrayList<String> grantkidId;
	public static ArrayList<String> noAccessGrantkidId;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView selected;
		public RelativeLayout layout;
	}

	public GrantKidsListViewAdapter(Context context, List<Child> data,
			boolean isSortByName) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;
		grantkidId = new ArrayList<String>();
		noAccessGrantkidId = new ArrayList<String>();
		if (isSortByName) {
			Collections.sort(this.data, new Comparator<Child>() {

				@Override
				public int compare(Child lhs, Child rhs) {
					return lhs.getName().charAt(0) - rhs.getName().charAt(0);
				}
			});
		}

		imageLoader = ImageLoader.getInstance();

		// init select
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isWithAccess()) {
				System.out.println(data.get(i).getChildId() + "");
				grantkidId.add(data.get(i).getChildId() + "");
			} else {
				noAccessGrantkidId.add(data.get(i).getChildId() + "");
			}
		}

	}

	@Override
	public int getCount() {

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
			convertView = inflater.inflate(R.layout.list_item_grant_kid,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);

			viewHolder.layout = (RelativeLayout) convertView
					.findViewById(R.id.layout);
			viewHolder.selected = (TextView) convertView
					.findViewById(R.id.selected);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		final Child child = data.get(position);
		// init select
		for (int i = 0; i < data.size(); i++) {
			if (child.isWithAccess()) {
				viewHolder.selected.setBackground(context.getResources()
						.getDrawable(R.drawable.ic_selected));

			} else {
				viewHolder.selected.setBackground(context.getResources()
						.getDrawable(R.drawable.ic_selected_off));

			}
		}

		viewHolder.layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// first click
				if (child.isWithAccess()) {
					viewHolder.selected.setBackground(context.getResources()
							.getDrawable(R.drawable.ic_selected_off));
					child.setWithAccess(false);
					grantkidId.remove(position);
					noAccessGrantkidId.add(child.getChildId() + "");
				} else {
					viewHolder.selected.setBackground(context.getResources()
							.getDrawable(R.drawable.ic_selected));
					grantkidId.add(child.getChildId() + "");
					noAccessGrantkidId.remove(position);
					child.setWithAccess(true);
				}

			}
		});

		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					CommonUtils.getDisplayImageOptions(), null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.ic_stub));
		}
		viewHolder.name.setText(child.getName());
	}
}
