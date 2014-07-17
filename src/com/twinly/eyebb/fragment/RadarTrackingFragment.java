package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.utils.CommonUtils;

public class RadarTrackingFragment extends Fragment {
	private SimpleAdapter mkidsListAdapter;
	ArrayList<HashMap<String, Object>> mKidsData;
	ImageView radar_rotate;
	private View radarBeepAllBtn;
	private View radarBeepBtn;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_radar_tracking, container,
				false);
		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		radarAnim();
		radarBeepBtn = getActivity().findViewById(R.id.radar_beep_btn);

		radarBeepBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					Intent intent = new Intent(getActivity(), BeepDialog.class);
					startActivity(intent);
				}
			}
		});

		radarBeepAllBtn = getActivity().findViewById(R.id.radar_beep_all_btn);

		radarBeepAllBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					Intent intent = new Intent(getActivity(), BeepDialog.class);
					startActivity(intent);
				}
			}
		});

	}

	private void radarAnim() {

		radar_rotate = (ImageView) getActivity().findViewById(
				R.id.bg_radar_rotate_img);
		final Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_anim);
		anim.setFillAfter(true);
		radar_rotate.startAnimation(anim);
	}

}
