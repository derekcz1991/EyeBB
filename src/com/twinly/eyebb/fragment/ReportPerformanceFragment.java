package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyebb.R;
import com.twinly.eyebb.adapter.PerformanceListViewAdapter;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBPerformance;
import com.twinly.eyebb.model.Performance;
import com.twinly.eyebb.model.PerformanceListItem;

public class ReportPerformanceFragment extends Fragment implements
		PullToRefreshListener {

	private PullToRefreshListView listView;
	private PerformanceListViewAdapter adapter;
	private List<PerformanceListItem> list;
	private CallbackInterface callback;
	private long childId;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * @param value current progress
		 */
		public void updateProgressBar(int value);

		/**
		 * Cancel update the progressBar when release the listView  
		 */
		public void cancelProgressBar();
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_report_performance,
				container, false);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);

		list = new ArrayList<PerformanceListItem>();
		childId = getArguments().getLong("childId");
		if (childId != -1) {
			updateView(DBPerformance.getPerformanceByChildId(getActivity(),
					childId));
		}

		return v;
	}

	public void updateView(Performance performance) {
		if (performance != null) {
			list.clear();

			Iterator<Entry<String, Integer>> iter;
			int index;

			PerformanceListItem dailyTitle = new PerformanceListItem(
					getResources().getString(R.string.text_daily), "",
					R.drawable.bg_report_daily, -1, 0, 0, 0);
			list.add(dailyTitle);
			if (performance.getDailyMap() != null) {
				iter = performance.getDailyMap().entrySet().iterator();
				index = 0;
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter
							.next();
					PerformanceListItem item = new PerformanceListItem("",
							entry.getKey(), -1,
							Constants.progressBarStyleSet[index],
							entry.getValue(), entry.getValue(), 1440);
					list.add(item);
					index++;
				}
			} else {
				PerformanceListItem item = new PerformanceListItem("",
						getString(R.string.text_no_record), -1,
						Constants.progressBarStyleSet[0], 0, 0, 0);
				list.add(item);
			}

			PerformanceListItem weeklyTitle = new PerformanceListItem(
					getResources().getString(R.string.text_weekly), "",
					R.drawable.bg_report_weekly, -1, 0, 0, 0);
			list.add(weeklyTitle);
			if (performance.getWeeklyMap() != null) {
				iter = performance.getWeeklyMap().entrySet().iterator();
				index = 0;
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter
							.next();
					PerformanceListItem item = new PerformanceListItem("",
							entry.getKey(), -1,
							Constants.progressBarStyleSet[index],
							entry.getValue(), entry.getValue(), 1440);
					list.add(item);
					index++;
				}
			} else {
				PerformanceListItem item = new PerformanceListItem("",
						getString(R.string.text_no_record), -1,
						Constants.progressBarStyleSet[0], 0, 0, 0);
				list.add(item);
			}
			
			PerformanceListItem averageTitle = new PerformanceListItem(
					getResources().getString(R.string.text_average), "",
					R.drawable.bg_report_average, -1, 0, 0, 0);
			list.add(averageTitle);
			if (performance.getAverageMap() != null) {
				iter = performance.getAverageMap().entrySet().iterator();
				index = 0;
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter
							.next();
					PerformanceListItem item = new PerformanceListItem("",
							entry.getKey(), -1,
							Constants.progressBarStyleSet[index],
							entry.getValue(), entry.getValue(), 1440);
					list.add(item);
					index++;
				}
			} else {
				PerformanceListItem item = new PerformanceListItem("",
						getString(R.string.text_no_record), -1,
						Constants.progressBarStyleSet[0], 0, 0, 0);
				list.add(item);
			}
			updateAdapter();
		}

	}

	public void updateAdapter() {
		adapter = new PerformanceListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	@Override
	public void updateProgressBar(int value) {
		callback.updateProgressBar(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();

	}

	/**
	 * Set the listView state. The list cannot scroll when is refreshing, 
	 * @param isRefreshing whether requesting server to update data
	 */
	public void setRefreshing(boolean isRefreshing) {
		if(listView != null) {
			listView.setRefreshing(isRefreshing);
		}
	}
}
