package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.activity.ChildrenListActivity;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.fragment.ReportPerformanceFragment.CallbackInterface;

public class ReportFragment extends Fragment implements CallbackInterface {

	private ReportPerformanceFragment performanceFragment;
	private ReportActivitiesFragment activitiesFragment;
	private TextView tvPerformance;
	private TextView redDividerPerformance;
	private TextView blackDividerPerformance;
	private TextView tvActivities;
	private TextView redDividerActivities;
	private TextView blackDividerActivities;
	private View reportChangeBtn;
	private CircleImageView img;
	private CallbackInterface callback;

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
		View v = inflater.inflate(R.layout.fragment_report, container, false);
		img = (CircleImageView) v.findViewById(R.id.img);
		setUpView(v);
		setUpListener(v);
		return v;
	}

	private void setUpView(View v) {

		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();

		performanceFragment = (ReportPerformanceFragment) getChildFragmentManager()
				.findFragmentByTag("performance");
		if (performanceFragment == null) {
			performanceFragment = new ReportPerformanceFragment();
			fragmentTransaction.add(R.id.container, performanceFragment,
					"performance");
			performanceFragment.setCallbackInterface(this);
		} else {
			fragmentTransaction.show(performanceFragment);
		}

		activitiesFragment = (ReportActivitiesFragment) getChildFragmentManager()
				.findFragmentByTag("activities");
		if (activitiesFragment == null) {
			activitiesFragment = new ReportActivitiesFragment();
			fragmentTransaction.add(R.id.container, activitiesFragment,
					"activities");
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
	}

	public void refreshPerformanceFragment() {
		if (performanceFragment != null)
			performanceFragment.updateView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		reportChangeBtn = getActivity().findViewById(R.id.report_change_btn);

		reportChangeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ChildrenListActivity.class);
				intent.putExtra("from", ActivityConstants.REPORT_FRAGMENT);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_CHILDREN_LIST_ACTIVITY);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_CHILDREN_LIST_ACTIVITY) {
			if (data == null) {
				return;
			}
			int index = data.getIntExtra("index", 0);
			switch (index) {
			case 2:
				img.setImageResource(R.drawable.hugh);
				activitiesFragment.updateAvatar((R.drawable.hugh));
				performanceFragment.updateIndex(0);
				break;
			case 0:
				img.setImageResource(R.drawable.head_img2);
				activitiesFragment.updateAvatar((R.drawable.head_img2));
				performanceFragment.updateIndex(1);
				break;
			case 1:
				img.setImageResource(R.drawable.head_img3);
				activitiesFragment.updateAvatar((R.drawable.head_img3));
				performanceFragment.updateIndex(2);
				break;
			}
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

	public void setRefreshing(boolean isRefreshing) {
		if (performanceFragment != null) {
			performanceFragment.setRefreshing(isRefreshing);
		}
	}

}
