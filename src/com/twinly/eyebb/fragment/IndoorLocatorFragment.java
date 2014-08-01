package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.activity.ChildrenListActivity;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.adapter.IndoorLocatorAdapter;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

public class IndoorLocatorFragment extends Fragment implements
		PullToRefreshListener {
	private PullToRefreshListView listView;
	private CallbackInterface callback;
	private Map<String, Child> childrenMap;

	public interface CallbackInterface {
		public void updateProgressBar(int value);

		public void cancelProgressBar();
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_indoor_locator, container,
				false);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);

		childrenMap = DBChildren.getChildren(getActivity());
		setUpListener(v);
		return v;
	}

	public void updateListView(Map<String, ArrayList<String>> indoorLocatorData) {
		IndoorLocatorAdapter adapter = new IndoorLocatorAdapter(getActivity(),
				indoorLocatorData, childrenMap);
		listView.setAdapter(adapter);
	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									BeepDialog.class);
							startActivity(intent);
						}

					}
				});

		v.findViewById(R.id.btn_shcool_bus).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SchoolBusTrackingActivity.class);
						startActivity(intent);

					}
				});

		v.findViewById(R.id.btn_kidslist).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								ChildrenListActivity.class);
						//startActivity(intent);
					}
				});
	}

	@Override
	public void updateProgressBar(int value) {
		callback.updateProgressBar(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	public void setRefreshing(boolean isRefreshing) {
		listView.setRefreshing(isRefreshing);
	}
}
