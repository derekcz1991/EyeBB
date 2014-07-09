package com.eyebb.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyebb.R;

public class ProfileFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("---->>> ProfileFragment");
		View v = inflater.inflate(R.layout.fragment_profile, container, false);
		return v;
	}

}
