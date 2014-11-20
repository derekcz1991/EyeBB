package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.RadarOutOfRssiBeepDialog;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;

public class MissRadarKidsListViewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Child> data;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ArrayList<Child> Antidata;
	public static long checkAntidata;
	private int autiDelayTimeFlag = 0;
	private int antiChildMissId = 0;
	private static Timer autidelayTimer;
	private static TimerTask autidelayTask;

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
		public TextView ChildStatus;
		public TextView DeviceConnectStatus;
		public RelativeLayout allView;
	}

	public MissRadarKidsListViewAdapter(Context context, ArrayList<Child> data,
			ArrayList<Child> openAntiData2) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;
		if (openAntiData2 != null)
			this.Antidata = openAntiData2;
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
			viewHolder.allView = (RelativeLayout) convertView
					.findViewById(R.id.newslist_item_layout);
			viewHolder.allView.setAlpha((float) 0.3);

			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.radar_child_head_img);
			// // 設置item圖片為白色
			// viewHolder.avatar.setBorderColor(convertView.getResources()
			// .getColor(R.color.white));

			viewHolder.beepBtn = convertView
					.findViewById(R.id.radar_item_beep_btn);
			viewHolder.beepBtn.setVisibility(View.GONE);

			viewHolder.name = (TextView) convertView
					.findViewById(R.id.radar_list_kids_name);

			viewHolder.ChildStatus = (TextView) convertView
					.findViewById(R.id.radar_list_kids_missd);
			// viewHolder.status.setVisibility(View.GONE);
			viewHolder.DeviceConnectStatus = (TextView) convertView
					.findViewById(R.id.device_connect_status);
			viewHolder.DeviceConnectStatus.setVisibility(View.GONE);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	public static void stopTimer() {
		if (autidelayTimer != null) {
			autidelayTimer.cancel();
			autidelayTimer.purge();
			autidelayTimer = null;
		}

		if (autidelayTask != null) {
			autidelayTask.cancel();
			autidelayTask = null;
		}
	}

	private void setUpView(ViewHolder viewHolder, final int position) {
		if (data.size() > position) {
			// System.out.println("POITION MISS = >" + position);

			Child child = null;
			try {
				child = data.get(position);
			} catch (Exception e) {
				setUpView(viewHolder, position);
				e.printStackTrace();
			}

			if (Antidata != null) {
				// System.out.println("Antidata.size()=>" + Antidata.size());
				for (int i = 0; i < Antidata.size(); i++) {
					if (Antidata.get(i).getChildId() == child.getChildId()) {
						viewHolder.avatar.setBorderColor(context.getResources()
								.getColor(R.color.red));
						viewHolder.ChildStatus.setVisibility(View.VISIBLE);
						viewHolder.ChildStatus.setText(context.getResources()
								.getString(R.string.text_anti_lost_mode_miss));

						checkAntidata = Antidata.get(i).getChildId();
						stopTimer();
						antiChildMissId = i;
						autidelayTimer = new Timer();
						autidelayTask = new TimerTask() {

							public void run() {
								System.out.println("autiDelayTimeFlag====>"
										+ autiDelayTimeFlag);
								autiDelayTimeFlag++;
								if (autiDelayTimeFlag == 5) {
									autiDelayTimeFlag = 6;
									Intent beepForAntiIntent = new Intent();

									beepForAntiIntent.setClass(context,
											RadarOutOfRssiBeepDialog.class);
									beepForAntiIntent.putExtra(
											"child_information",
											Antidata.get(antiChildMissId));

									context.startActivity(beepForAntiIntent);

									stopTimer();
								}
							}

						};
						autiDelayTimeFlag = 0;

						autidelayTimer.schedule(autidelayTask, 0, 1000);

						break;
					}
				}
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

	// from small to bigf
	public void sort(int[] targetArr) {

		int temp = 0;
		for (int i = 0; i < targetArr.length; i++) {
			for (int j = i; j < targetArr.length; j++) {
				if (targetArr[i] > targetArr[j]) {

					// 方法一：
					temp = targetArr[i];
					targetArr[i] = targetArr[j];
					targetArr[j] = temp;

					// //方法二:
					// targetArr[i] = targetArr[i] + targetArr[j];
					// targetArr[j] = targetArr[i] - targetArr[j];
					// targetArr[i] = targetArr[i] - targetArr[j];
				}

			}
		}
	}

}