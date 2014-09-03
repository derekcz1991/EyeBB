package com.twinly.eyebb.adapter;

import java.util.ArrayList;

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

import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.DensityUtil;

public class RadarKidsListViewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Child> data;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;


	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public View beepBtn;
	}

	public RadarKidsListViewAdapter(Context context, ArrayList<Child> data) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		ViewHolder viewHolderavatarRadar = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.fragment_radar_tracking_kids_listitem, parent,false
					);
			
//			View radarLayout = inflater.inflate(
//					R.layout.fragment_radar_tracking, null);			
//			RelativeLayout mainLayout = (RelativeLayout) radarLayout
//					.findViewById(R.id.layout_circle);
//			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
//					DensityUtil.dip2px(context, 32),
//					DensityUtil.dip2px(context, 32));
			
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.radar_child_head_img);
			viewHolderavatarRadar = new ViewHolder();

			viewHolder.beepBtn = convertView.findViewById(R.id.radar_item_beep_btn);

			viewHolder.name = (TextView) convertView
					.findViewById(R.id.radar_list_kids_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, final int position) {
		final Child child = data.get(position);
		if (TextUtils.isEmpty(child.getIcon()) == false) {

			
			viewHolder.beepBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("positionposition = > " + position + "");
				}
			});
			
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					options, null);

		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.hugh));
		}
		viewHolder.name.setText(child.getName());
	}

}