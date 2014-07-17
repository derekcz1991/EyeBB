package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.ActivityDetailsActivity;
import com.twinly.eyebb.customview.CircleImageView;

public class ReportActivitiesFragment extends Fragment {
	private CircleImageView img;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_report_activities,
				container, false);
		img = (CircleImageView) v.findViewById(R.id.img);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		View detailsBtn = getActivity().findViewById(R.id.details_btn);

		detailsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ActivityDetailsActivity.class);
				startActivity(intent);

			}
		});

	}

	public void updateAvatar(int i) {
		img.setImageResource(i);
	}
}
