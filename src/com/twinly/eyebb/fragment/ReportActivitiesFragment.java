package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.eyebb.R;
import com.twinly.eyebb.activity.ActivityDetailsActivity;
import com.twinly.eyebb.adapter.ActivitiesListViewAdapter;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.model.ActivityInfo;

public class ReportActivitiesFragment extends Fragment implements
		PullToRefreshListener {

	private ArrayList<ActivityInfo> list;
	private PullToRefreshListView listView;
	private ActivitiesListViewAdapter adapter;
	private CallbackInterface callback;

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
		View v = inflater.inflate(R.layout.fragment_report_activities,
				container, false);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);
		updateView(getArguments().getLong("childId"));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						ActivityDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("activityInfo",
						(ActivityInfo) adapter.getItem(position));
				startActivity(intent);
			}
		});
		return v;
	}

	public void updateView(long childId) {
		list = DBActivityInfo.getActivityInfoByChildId(getActivity(), childId);
		adapter = new ActivitiesListViewAdapter(getActivity(), list);
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
		listView.setRefreshing(isRefreshing);
	}
}
