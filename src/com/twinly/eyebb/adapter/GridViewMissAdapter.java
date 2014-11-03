package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter.ViewHolder;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridViewMissAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Child> data;
	private ArrayList<Child> Antidata;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public GridViewMissAdapter(Context context, ArrayList<Child> data,
			ArrayList<Child> antiData) {
		// TODO Auto-generated constructor stub
		this.inflater = LayoutInflater.from(context);
		this.data = data;
		this.context = context;
		this.Antidata = antiData;

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

	}

	private class ViewHolder {
		CircleImageView civ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {

			// convertView.setLayoutParams(new LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			convertView = inflater.inflate(
					R.layout.dialog_radar_all_child_image_item, null);
			viewHolder = new ViewHolder();
			viewHolder.civ = (CircleImageView) convertView
					.findViewById(R.id.radar_child_all_head_img);
			viewHolder.civ.setBorderColor(context.getResources().getColor(
					R.color.dark_grey));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, final int position) {
		Child child = null;
		try {
			child = data.get(position);
		} catch (Exception e) {
			setUpView(viewHolder, position);
			e.printStackTrace();
		}

		if (Antidata != null) {
			System.out.println("Antidata.size()=>" + Antidata.size());
			for (int i = 0; i < Antidata.size(); i++) {
				if (Antidata.get(i).getChildId() == child.getChildId()) {
					viewHolder.civ.setBorderColor(context.getResources()
							.getColor(R.color.red));
					break;
				}
			}
		}

		if (TextUtils.isEmpty(child.getIcon()) == false) {

			imageLoader.displayImage(child.getIcon(), viewHolder.civ, options,
					null);

		} else {
			viewHolder.civ.setImageDrawable(context.getResources().getDrawable(
					R.drawable.hugh));
		}

	}

}
