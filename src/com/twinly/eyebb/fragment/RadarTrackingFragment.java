package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class RadarTrackingFragment extends Fragment {
	private SimpleAdapter mkidsListAdapter;
	ArrayList<HashMap<String, Object>> mKidsData;
	ImageView radar_rotate;
	private View radarBeepAllBtn;
	private View radarBeepBtn;
	private View radarBeepBtn1;
	private View radarBeepBtn2;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_radar_tracking, container,
				false);
		radarBeepBtn1 = v.findViewById(R.id.radar_beep_btn1);
		radarBeepBtn2 = v.findViewById(R.id.radar_beep_btn2);
		if (SharePrefsUtils.getRole(getActivity()) == false) {
			radarBeepBtn1.setVisibility(View.GONE);
			radarBeepBtn2.setVisibility(View.GONE);
			v.findViewById(R.id.avatar1).setVisibility(View.GONE);
			v.findViewById(R.id.avatar2).setVisibility(View.GONE);
			((TextView) v.findViewById(R.id.radar_text_missed_number))
					.setText("1");
			v.findViewById(R.id.newslist_item_layout_1)
					.setVisibility(View.GONE);
			v.findViewById(R.id.newslist_item_layout2)
					.setVisibility(View.GONE);
		} else {
			((TextView) v.findViewById(R.id.radar_text_missed_number))
					.setText("3");
		}
		radarBeepBtn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BeepDialog.class);
				startActivity(intent);
			}
		});
		radarBeepBtn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BeepDialog.class);
				startActivity(intent);
			}
		});
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
