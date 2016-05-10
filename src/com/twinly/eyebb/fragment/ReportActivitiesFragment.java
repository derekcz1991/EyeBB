package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.twinly.R;
import com.twinly.eyebb.activity.WebViewActivity;
import com.twinly.eyebb.adapter.ActivitiesListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.model.ActivityInfo;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ReportActivitiesFragment extends Fragment implements
		PullToRefreshListener, OnClickListener {

	private ArrayList<ActivityInfo> list;
	private PullToRefreshListView listView;
	private ActivitiesListViewAdapter adapter;
	private CallbackInterface callback;
	private TextView listIsNull;
	private WebView webview;
	private RelativeLayout webviewLayout;
	private long childId;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * 
		 * @param value
		 *            current progress
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
		webview = (WebView) v.findViewById(R.id.webview);
		webviewLayout = (RelativeLayout) v.findViewById(R.id.webviewLayout);
		listIsNull = (TextView) v.findViewById(R.id.list_is_null);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);
		updateView(getArguments().getLong("childId"));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				childId = ((ActivityInfo) adapter.getItem(position))
						.getChildId();
				switch (SharePrefsUtils.getLanguage(getActivity())) {
				case Constants.LOCALE_TW:
				case Constants.LOCALE_HK:
				case Constants.LOCALE_CN:
					webviewLayout.setVisibility(View.VISIBLE);
					webview.loadUrl(((ActivityInfo) adapter.getItem(position))
							.getUrlTc());
					break;
				default:
					webviewLayout.setVisibility(View.VISIBLE);
					webview.loadUrl(((ActivityInfo) adapter.getItem(position))
							.getUrl());
					break;
				}

				/*Intent intent = new Intent(getActivity(), WebViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("from",
						ActivityConstants.FRAGMENT_REPORT_ACTIVITY);
				bundle.putSerializable("activityInfo",
						(ActivityInfo) adapter.getItem(position));
				intent.putExtras(bundle);
				startActivity(intent);*/
			}
		});
		v.findViewById(R.id.reject).setOnClickListener(this);
		v.findViewById(R.id.accept).setOnClickListener(this);
		return v;
	}

	public void updateView(long childId) {
		list = DBActivityInfo.getActivityInfoByChildId(getActivity(), childId);
		adapter = new ActivitiesListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);

		if (list.size() == 0 || list == null) {
			listIsNull.setVisibility(View.VISIBLE);
		} else {
			listIsNull.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void updateProgressBar(int value) {
		callback.updateProgressBar(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reject:
			webviewLayout.setVisibility(View.INVISIBLE);
			new Upload("I").execute();
			break;
		case R.id.accept:
			webviewLayout.setVisibility(View.INVISIBLE);
			new Upload("A").execute();
			break;
		}

	}

	private class Upload extends AsyncTask<Void, Void, String> {

		String type;

		public Upload(String type) {
			this.type = type;
		}

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("result", type);

			return HttpRequestUtils.post(HttpConstants.ActivityReceipt, map);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("result = " + result);
		}

	}

	/**
	 * Set the listView state. The list cannot scroll when is refreshing,
	 * 
	 * @param isRefreshing
	 *            whether requesting server to update data
	 */
	/*public void setRefreshing(boolean isRefreshing) {
		if (listView != null) {
			listView.setRefreshing(isRefreshing);
		}
	}*/
}
