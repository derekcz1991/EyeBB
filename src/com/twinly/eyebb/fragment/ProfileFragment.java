package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyebb.R;

import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.activity.NotificationActivity;
import com.twinly.eyebb.activity.SettingsActivity;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ProfileFragment extends Fragment {

	private TextView settingBtn;
	private View notificationDetailsBtn;
	private View notificationDetailsBtn2;
	private View notificationDetailsBtn3;
	private CallbackInterface callback;
	//没用的东西
	int child;
	TextView v1;
	TextView v2;
	TextView v3;

	public interface CallbackInterface {
		public void stopAutoRefresh();

		public void startAutoRefresh();
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, container, false);
		((TextView) v.findViewById(R.id.username)).setText(SharePrefsUtils
				.getLoginAccount(getActivity()));

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
						ActivityConstants.REQUEST_GO_TO_SETTING_ACTIVITY);

			}
		});

		notificationDetailsBtn = getActivity().findViewById(
				R.id.notification_details_btn);

		v1 = (TextView) getActivity().findViewById(R.id.vi_1);
		v2 = (TextView) getActivity().findViewById(R.id.vi_2);
		v3 = (TextView) getActivity().findViewById(R.id.vi_3);
		notificationDetailsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), NotificationActivity.class);
				v1.setVisibility(0);
				v2.setVisibility(8);
				v3.setVisibility(8);
				child = 1;
				intent.putExtra("child", child);
				startActivity(intent);
			}
		});

		notificationDetailsBtn2 = getActivity().findViewById(
				R.id.notification_details_btn2);

		notificationDetailsBtn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), NotificationActivity.class);
				v1.setVisibility(8);
				v2.setVisibility(0);
				v3.setVisibility(8);
				child = 2;
				intent.putExtra("child", child);
				startActivity(intent);
			}
		});

		notificationDetailsBtn3 = getActivity().findViewById(
				R.id.notification_details_btn3);

		notificationDetailsBtn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), NotificationActivity.class);
				v1.setVisibility(8);
				v2.setVisibility(8);
				v3.setVisibility(0);
				child = 3;
				intent.putExtra("child", child);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SETTING_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_LOGOUT) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LancherActivity.class);
				startActivity(intent);
				getActivity().finish();
			} else if (resultCode == ActivityConstants.RESULT_AUTO_UPDATE_ON) {
				callback.startAutoRefresh();
			} else if (resultCode == ActivityConstants.RESULT_AUTO_UPDATE_OFF) {
				callback.stopAutoRefresh();
			}
		}
	}
}
