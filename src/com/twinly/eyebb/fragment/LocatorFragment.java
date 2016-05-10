package com.twinly.eyebb.fragment;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twinly.twinly.R;
import com.twinly.eyebb.activity.KidsListActivity;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.SerializableChildrenMap;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public abstract class LocatorFragment extends Fragment implements
		PullToRefreshListener {

	protected int layoutRes;
	protected String requestUrl;
	protected PullToRefreshListView listView;
	private ProgressBar progressBar;
	protected TextView hintText;

	protected boolean isAutoUpdateOn;
	protected AutoUpdateTask autoUpdateTask;
	private boolean isFirstUpdate = true;
	protected boolean isInTheFront;

	// <child_id, Child>
	protected HashMap<Long, ChildForLocator> childrenMap;
	private SerializableChildrenMap myMap;
	protected String userType;

	protected CallbackInterface callback;
	protected String tag;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * 
		 * @param value
		 *            current progress
		 */
		public void updateProgressBarForLocator(int value);

		/**
		 * Cancel update the progressBar when release the listView
		 */
		public void cancelProgressBar();

		/**
		 * Reset the progressBar when finishing to update listView
		 */
		public void resetProgressBar();

		public void switchFragment();
	}

	abstract void setResource();

	abstract void setUpSecondMenu(View v);

	abstract protected void handleResponse(String response)
			throws JSONException;

	abstract protected void handlePostResult(boolean result);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setResource();

		View v = inflater.inflate(layoutRes, container, false);
		userType = SharePrefsUtils.getUserType(getActivity());

		setUpView(v);
		setUpSecondMenu(v);

		return v;
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		childrenMap = new HashMap<Long, ChildForLocator>();
		updateView();
		/*if (SharePrefsUtils.isAutoUpdate(getActivity())) {
			isAutoUpdateOn = true;
			listView.setLockPullAction(true);
			autoUpdateTask = new AutoUpdateTask();
			autoUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			updateView();
		}*/
	}

	protected void setUpView(View v) {
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		hintText = (TextView) v.findViewById(R.id.hintText);
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	public void updateView() {
		if (isInTheFront) {
			new UpdateViewTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void updateProgressBar(int value) {
		if (callback != null)
			callback.updateProgressBarForLocator(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	public boolean isInTheFront() {
		return isInTheFront;
	}

	public void setInTheFront(boolean isInTheFront) {
		this.isInTheFront = isInTheFront;
	}

	public void showKidsListActivity() {
		if (childrenMap.size() != 0) {
			Intent intent = new Intent(getActivity(), KidsListActivity.class);
			if (myMap == null) {
				myMap = new SerializableChildrenMap();
			}
			myMap.setMap(childrenMap);
			Bundle bundle = new Bundle();
			bundle.putSerializable("childrenMap", myMap);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	public void switchAutoUpdate(boolean isAutoUpdateOn) {
		this.isAutoUpdateOn = isAutoUpdateOn;
		if (isAutoUpdateOn) {
			listView.setLockPullAction(true);
			autoUpdateTask = new AutoUpdateTask();
			autoUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			listView.setLockPullAction(false);
			if (autoUpdateTask != null)
				autoUpdateTask.cancel(true);
		}
	}

	protected class AutoUpdateTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (isAutoUpdateOn) {
				try {
					Thread.sleep(SharePrefsUtils.getAutoUpdateTime(
							getActivity(), 5) * 1000);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
				updateView();
			}
			return null;
		}
	}

	protected class UpdateViewTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isAutoUpdateOn == false && isFirstUpdate) {
				progressBar.setVisibility(View.VISIBLE);
				hintText.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String response = HttpRequestUtils.get(requestUrl, null);
			if (isAutoUpdateOn == false) {
				try {
					new JSONObject(response);
				} catch (JSONException e) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					response = HttpRequestUtils.get(requestUrl, null);
				}
			}
			System.out.println(requestUrl + " ---->> " + response);
			try {
				handleResponse(response);
			} catch (JSONException e) {
				System.out.println(requestUrl + " ---->> " + e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressBar.setVisibility(View.INVISIBLE);
			callback.resetProgressBar();
			isFirstUpdate = false;
			handlePostResult(result);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isAutoUpdateOn = false;
		if (autoUpdateTask != null) {
			autoUpdateTask.cancel(true);
		}
	}
}
