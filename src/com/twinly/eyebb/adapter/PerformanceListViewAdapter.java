package com.twinly.eyebb.adapter;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.model.PerformanceListItem;

public class PerformanceListViewAdapter extends BaseAdapter {

	private Context context;
	private List<PerformanceListItem> list;
	private LayoutInflater mInflater;

	private final class ViewHolder {
		private TextView title;
		private RelativeLayout content;
		private TextView subTitle;
		private ProgressBar progressBar;
		private TextView time;
	}

	public PerformanceListViewAdapter(Context context,
			List<PerformanceListItem> list) {
		this.context = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
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
		View v = convertView;
		ViewHolder viewHolder;
		if (convertView == null) {
			/* This is where you initialize new rows, by:
			 *  - Inflating the layout,
			 *  - Instantiating the ViewHolder,
			 *  - And defining any characteristics that are consistent for every row */
			v = mInflater
					.inflate(R.layout.list_item_performance, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) v.findViewById(R.id.title);
			viewHolder.content = (RelativeLayout) v.findViewById(R.id.content);
			viewHolder.subTitle = (TextView) v.findViewById(R.id.subtitle);
			viewHolder.progressBar = (ProgressBar) v
					.findViewById(R.id.progressBar);
			viewHolder.time = (TextView) v.findViewById(R.id.time);
			v.setTag(viewHolder);
		} else {
			/* Fetch data already in the row layout, 
			 * primarily you only use this to get a copy of the ViewHolder */
			viewHolder = (ViewHolder) v.getTag();
		}
		/* Set the data that changes in each row, like `title` and `size`
		 *  This is where you give rows there unique values. */
		setUpView(viewHolder, position);
		return v;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		viewHolder.content.setVisibility(View.GONE);
		viewHolder.title.setVisibility(View.GONE);
		if (list.get(position).getTitle().equals("")) {
			viewHolder.content.setVisibility(View.VISIBLE);
			viewHolder.subTitle.setText(list.get(position).getSubTitle());
			viewHolder.time.setText(list.get(position).getTime() + " min.");
			viewHolder.progressBar.setMax(list.get(position).getMaxProgress());
			viewHolder.progressBar.setProgressDrawable(context.getResources()
					.getDrawable(list.get(position).getProgressBarstyle()));

			// progress bar animation
			if (list.get(position).isFlag() == false) {
				list.get(position).setFlag(true);
				new AsyncTask<Void, Integer, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						int progress = 0;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						while (progress < list.get(position).getProgress()) {
							progress += 2;
							publishProgress(progress);
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						return null;
					}

					@Override
					protected void onProgressUpdate(Integer... values) {
						viewHolder.progressBar.setProgress(values[0]);
					}

				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		} else {
			viewHolder.title.setVisibility(View.VISIBLE);
			viewHolder.title.setBackgroundResource(list.get(position)
					.getTitleBackground());
			viewHolder.title.setText(list.get(position).getTitle());
		}
	}
}