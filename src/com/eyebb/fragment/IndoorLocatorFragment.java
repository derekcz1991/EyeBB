package com.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.eyebb.R;
import com.eyebb.activity.MainDialog;

public class IndoorLocatorFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_indoor_locator, container,
				false);
		setUpListener(v);
		return v;
	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								MainDialog.class);
						startActivity(intent);
					}
				});
	}
}
