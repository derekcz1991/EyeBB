package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.NotificationActivity;
import com.twinly.eyebb.activity.SettingsActivity;
import com.twinly.eyebb.constant.Constants;

public class ProfileFragment extends Fragment {

	private TextView settingBtn;
	private View notificationDetailsBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		settingBtn = (TextView) getActivity().findViewById(R.id.options_btn);
		settingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						SettingsActivity.class);
				startActivityForResult(intent,
						Constants.REQUEST_GO_TO_SETTING_ACTIVITY);

			}
		});

		notificationDetailsBtn = getActivity().findViewById(
				R.id.notification_details_btn);

		notificationDetailsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), NotificationActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constants.REQUEST_GO_TO_SETTING_ACTIVITY) {
			if(resultCode == Constants.RESULT_LOGOUT) {
				
			}
		}
	}

}
