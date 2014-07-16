package com.twinly.eyebb.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twinly.eyebb.R;

public class ReportFragment extends Fragment {

	private ReportPerformanceFragment performanceFragment;
	private ReportActivitiesFragment activitiesFragment;
	private TextView tvPerformance;
	private TextView redDividerPerformance;
	private TextView blackDividerPerformance;
	private TextView tvActivities;
	private TextView redDividerActivities;
	private TextView blackDividerActivities;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_report, container, false);
		setUpView(v);
		setUpListener(v);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.out.println("onDestroyView");
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		View reportChangeBtn = getActivity().findViewById(
				R.id.report_change_btn);

		reportChangeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

	}
}
