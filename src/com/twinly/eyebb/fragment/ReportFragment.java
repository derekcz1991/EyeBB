package com.twinly.eyebb.fragment;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.ChangeKidsActivity;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.database.DBPerformance;
import com.twinly.eyebb.model.ActivityInfo;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Performance;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ReportFragment extends Fragment implements
		ReportPerformanceFragment.CallbackInterface,
		ReportActivitiesFragment.CallbackInterface {

	private ReportPerformanceFragment performanceFragment;
	private ReportActivitiesFragment activitiesFragment;
	private TextView tvPerformance;
	private TextView redDividerPerformance;
	private TextView blackDividerPerformance;
	private TextView tvActivities;
	private TextView redDividerActivities;
	private TextView blackDividerActivities;
	private CircleImageView avatar;
	private CallbackInterface callback;
	private ImageLoader imageLoader;
	private Child child;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * @param value current progress
		 */
		public void updateProgressBarForReport(int value);

		/**
		 * Cancel update the progressBar when release the listView  
		 */
		public void cancelProgressBar();

		/**
		 * Reset the progressBar when finishing to update listView  
		 */
		public void resetProgressBar();
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_report, container, false);
		imageLoader = ImageLoader.getInstance();
		setUpView(v);
		setUpListener(v);
		return v;
	}

	private void setUpView(View v) {
		// set the current child
		child = DBChildren.getChildById(getActivity(),
				SharePrefsUtils.getReportChildId(getActivity()));

		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();

		Bundle bundle = new Bundle();
		if (child != null) {
			bundle.putLong("childId", child.getChildId());
		} else {
			bundle.putLong("childId", -1);
		}

		// setup ReportPerformanceFragment
		performanceFragment = (ReportPerformanceFragment) getChildFragmentManager()
				.findFragmentByTag("performance");
		if (performanceFragment == null) {
			performanceFragment = new ReportPerformanceFragment();

			performanceFragment.setArguments(bundle);
			fragmentTransaction.add(R.id.container, performanceFragment,
					"performance");
			performanceFragment.setCallbackInterface(this);
		} else {
			fragmentTransaction.show(performanceFragment);
		}

		// setup ReportActivitiesFragment
		activitiesFragment = (ReportActivitiesFragment) getChildFragmentManager()
				.findFragmentByTag("activities");
		if (activitiesFragment == null) {
			activitiesFragment = new ReportActivitiesFragment();
			activitiesFragment.setArguments(bundle);
			fragmentTransaction.add(R.id.container, activitiesFragment,
					"activities");
			activitiesFragment.setCallbackInterface(this);
			fragmentTransaction.hide(activitiesFragment);
		} else {
			fragmentTransaction.hide(activitiesFragment);
		}
		fragmentTransaction.commit();

		tvPerformance = (TextView) v.findViewById(R.id.tv_performance);
		redDividerPerformance = (TextView) v
				.findViewById(R.id.red_divider_performance);
		blackDividerPerformance = (TextView) v
				.findViewById(R.id.black_divider_performance);
		tvActivities = (TextView) v.findViewById(R.id.tv_activities);
		redDividerActivities = (TextView) v
				.findViewById(R.id.red_divider_activities);
		blackDividerActivities = (TextView) v
				.findViewById(R.id.black_divider_activities);
		avatar = (CircleImageView) v.findViewById(R.id.avatar);

		if (child != null) {
			imageLoader.displayImage(child.getIcon(), avatar,
					CommonUtils.getDisplayImageOptions(), null);
		}

	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_performance).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (performanceFragment.isHidden()) {
							tvActivities.setTextAppearance(getActivity(),
									R.style.LightGreyText_18);
							redDividerActivities.setVisibility(View.INVISIBLE);
							blackDividerActivities.setVisibility(View.VISIBLE);

							tvPerformance.setTextAppearance(getActivity(),
									R.style.RedText_18);
							redDividerPerformance.setVisibility(View.VISIBLE);
							blackDividerPerformance
									.setVisibility(View.INVISIBLE);

							getChildFragmentManager().beginTransaction()
									.show(performanceFragment)
									.hide(activitiesFragment).commit();
						}

					}
				});

		v.findViewById(R.id.btn_activities).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (activitiesFragment.isHidden()) {
							tvPerformance.setTextAppearance(getActivity(),
									R.style.LightGreyText_18);
							redDividerPerformance.setVisibility(View.INVISIBLE);
							blackDividerPerformance.setVisibility(View.VISIBLE);

							tvActivities.setTextAppearance(getActivity(),
									R.style.RedText_18);
							redDividerActivities.setVisibility(View.VISIBLE);
							blackDividerActivities
									.setVisibility(View.INVISIBLE);

							getChildFragmentManager().beginTransaction()
									.show(activitiesFragment)
									.hide(performanceFragment).commit();
						}

					}
				});

		v.findViewById(R.id.report_change_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								ChangeKidsActivity.class);
						startActivityForResult(
								intent,
								ActivityConstants.REQUEST_GO_TO_CHANGE_KIDS_ACTIVITY);
					}
				});
	}

	/**
	 * when switch tab to Report, reload the performance to show the progressbar animation.
	 */
	public void refreshPerformanceFragment() {
		if (performanceFragment != null) {
			//performanceFragment.updateAdapter();
		}
	}

	@Override
	public void updateProgressBar(int value) {
		callback.updateProgressBarForReport(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	/**
	 * Get newest the data from server and update the view 
	 */
	public void updateView() {
		if (child != null) {
			new UpdateView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setRefreshing(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(child.getChildId()));
			map.put("avgDays", "5");
			return HttpRequestUtils.get("reportService/api/stat", map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("report = " + result);
			try {
				JSONObject json = new JSONObject(result);
				updatePerformance(json);
				updateActivity(json);

			} catch (JSONException e) {
				reInitView();
				System.out.println("reportService/api/stat ---->> "
						+ e.getMessage());
			}
			setRefreshing(false);
			callback.resetProgressBar();
		}
	}

	/**
	 * Re-initialize the view when change a child to display
	 */
	private void reInitView() {
		performanceFragment.updateView(DBPerformance.getPerformanceByChildId(
				getActivity(), child.getChildId()));
		activitiesFragment.updateView(child.getChildId());
	}

	/**
	 * Set the listView state. The list cannot scroll when is refreshing, 
	 * @param isRefreshing whether requesting server to update data
	 */
	private void setRefreshing(boolean isRefreshing) {
		if (performanceFragment != null) {
			performanceFragment.setRefreshing(isRefreshing);
		}
		if (activitiesFragment != null) {
			activitiesFragment.setRefreshing(isRefreshing);
		}
	}

	/**
	 * Update the performance fragment
	 * @param json
	 * @throws JSONException
	 */
	private void updatePerformance(JSONObject json) throws JSONException {
		Performance performance = new Performance();
		performance.setChildId(child.getChildId());
		performance.setDaily(json
				.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_DAILY));
		performance.setWeekly(json
				.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_WEEKLY));
		performance.setAverage(json
				.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_AVERAGE));
		performance
				.setLastUpdateTime(json
						.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_LAST_UPDATE_TIME));
		DBPerformance.insert(getActivity(), performance);
		performanceFragment.updateView(performance);
	}

	/**
	 * Update the activity fragment
	 * @param json
	 * @throws JSONException
	 */
	private void updateActivity(JSONObject json) throws JSONException {
		if (CommonUtils.isNotNull(json
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO))) {
			// delete activityInfo saved before of this child
			DBActivityInfo.deleteByChildId(getActivity(), child.getChildId());

			JSONArray activityInfolist = json
					.getJSONArray(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO);
			// save new activityInfo
			for (int i = 0; i < activityInfolist.length(); i++) {
				JSONObject item = (JSONObject) activityInfolist.get(i);
				ActivityInfo activityInfo = new ActivityInfo();
				activityInfo.setChildId(SharePrefsUtils
						.getReportChildId(getActivity()));
				activityInfo
						.setTitle(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE));
				activityInfo
						.setTitleSc(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC));
				activityInfo
						.setTitleTc(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC));
				activityInfo
						.setUrl(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL));
				activityInfo
						.setUrlSc(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC));
				activityInfo
						.setUrlTc(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC));
				activityInfo
						.setDate(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_DATE));
				activityInfo
						.setIcon(item
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_ICON));
				DBActivityInfo.insert(getActivity(), activityInfo);
			}
			activitiesFragment.updateView(child.getChildId());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_CHANGE_KIDS_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK
					&& data != null) {
				// change a child to display
				child = (Child) data.getSerializableExtra("child");
				imageLoader.displayImage(child.getIcon(), avatar,
						CommonUtils.getDisplayImageOptions(), null);
				SharePrefsUtils.setReportChildId(getActivity(),
						child.getChildId());
				//reInitView();
				updateView();
			}
		}
	}
}
