package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.twinly.eyebb.adapter.TabsAdapter.TabsAdapterCallback;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.DensityUtil;

public class MissRadarKidsListViewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Child> data;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	// private RadarKidsListViewAdapterCallback callback;
	//
	// public interface RadarKidsListViewAdapterCallback {
	// public void onStartToBeepClicked(int position);
	//
	// // public void onStartToBeepClicked(int rssi, String getAddress,
	// // String getName);
	// }
	//
	// public void setCallback(RadarKidsListViewAdapterCallback callback) {
	// this.callback = callback;
	// }

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public View beepBtn;
		public TextView status;
	}

	public MissRadarKidsListViewAdapter(Context context, ArrayList<Child> data) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		ViewHolder viewHolderavatarRadar = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.fragment_radar_tracking_kids_listitem, parent,
					false);

			// View radarLayout = inflater.inflate(
			// R.layout.fragment_radar_tracking, null);
			// RelativeLayout mainLayout = (RelativeLayout) radarLayout
			// .findViewById(R.id.layout_circle);
			// RelativeLayout.LayoutParams layoutParams = new
			// RelativeLayout.LayoutParams(
			// DensityUtil.dip2px(context, 32),
			// DensityUtil.dip2px(context, 32));

			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.radar_child_head_img);
			// // 設置item圖片為白色
			// viewHolder.avatar.setBorderColor(convertView.getResources()
			// .getColor(R.color.white));
			viewHolderavatarRadar = new ViewHolder();

			viewHolder.beepBtn = convertView
					.findViewById(R.id.radar_item_beep_btn);

			viewHolder.name = (TextView) convertView
					.findViewById(R.id.radar_list_kids_name);

			viewHolder.status = (TextView) convertView
					.findViewById(R.id.radar_list_kids_missd);
			// viewHolder.status.setVisibility(View.GONE);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, final int position) {
		if (data.size() > position) {
			System.out.println("POITION MISS = >" + position);

			Child child = null;
			try {
				child = data.get(position);
			} catch (Exception e) {
				setUpView(viewHolder, position);
				e.printStackTrace();
			}
			if (child != null) {
				if (TextUtils.isEmpty(child.getIcon()) == false) {

					imageLoader.displayImage(child.getIcon(),
							viewHolder.avatar, options, null);

				} else {
					viewHolder.avatar.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.hugh));
				}
				viewHolder.name.setText(child.getName());
			}

		}
	}

}