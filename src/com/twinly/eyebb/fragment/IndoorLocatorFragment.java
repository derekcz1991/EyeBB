package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.activity.ChildrenListActivity;
import com.twinly.eyebb.activity.MainDialog;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class IndoorLocatorFragment extends Fragment {

	Handler mHandler = new Handler();
	private CircleImageView messHallChild;
	private CircleImageView playgroundChild;
	private CircleImageView sleepChild1;
	private CircleImageView sleepChild2;
	private CircleImageView sleepChild3;
	private TextView messHallNum;
	private TextView playgroundNum;
	private TextView sleepNum;
	private int position;
	private boolean flag = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_indoor_locator, container,
				false);
		setUpView(v);
		setUpListener(v);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		flag = true;
		mProgressRunner.run();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mProgressRunner);
		flag = false;
	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									BeepDialog.class);
							startActivity(intent);
						}

					}
				});

		v.findViewById(R.id.btn_shcool_bus).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SchoolBusTrackingActivity.class);
						startActivity(intent);

					}
				});

		/*v.findViewById(R.id.sleep_child_1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									MainDialog.class);
							startActivity(intent);
						}
					}
				});*/

		v.findViewById(R.id.btn_kidslist).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								ChildrenListActivity.class);
						intent.putExtra("from", 1);
						switch (position) {
						case 0:
							intent.putExtra("location",
									getString(R.string.text_mess_hall));
							break;
						case 1:
							intent.putExtra("location",
									getString(R.string.text_playground));
							break;
						case 2:
							intent.putExtra("location",
									getString(R.string.text_sleeping));
							break;
						}
						startActivity(intent);
					}
				});
	}

	private void setUpView(View v) {
		messHallChild = (CircleImageView) v.findViewById(R.id.mess_hall_child);
		playgroundChild = (CircleImageView) v
				.findViewById(R.id.playground_child);
		sleepChild1 = (CircleImageView) v.findViewById(R.id.sleep_child_1);
		sleepChild2 = (CircleImageView) v.findViewById(R.id.sleep_child_2);
		sleepChild3 = (CircleImageView) v.findViewById(R.id.sleep_child_3);
		messHallNum = (TextView) v.findViewById(R.id.mess_hall_num);
		playgroundNum = (TextView) v.findViewById(R.id.playground_num);
		sleepNum = (TextView) v.findViewById(R.id.sleep_num);

		//refreshView();
	}

	class AvatarClicked implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), MainDialog.class);
			startActivity(intent);
		}

	}

	private void refreshView() {
		if (getActivity() == null) {
			return;
		}
		position = (int) (Math.random() * 3);
		switch (position) {
		case 0:
			messHallChild.setVisibility(View.VISIBLE);
			playgroundChild.setVisibility(View.GONE);
			sleepChild3.setVisibility(View.GONE);
			messHallNum.setText("1");
			playgroundNum.setText("0");
			sleepNum.setText("0");
			if (SharePrefsUtils.getRole(getActivity())) {
				sleepNum.setText("2");
			} else {
				sleepChild1.setVisibility(View.GONE);
				sleepChild2.setVisibility(View.GONE);
			}
			break;
		case 1:
			messHallChild.setVisibility(View.GONE);
			playgroundChild.setVisibility(View.VISIBLE);
			sleepChild3.setVisibility(View.GONE);
			messHallNum.setText("0");
			playgroundNum.setText("1");
			sleepNum.setText("0");
			if (SharePrefsUtils.getRole(getActivity())) {
				sleepNum.setText("2");
			} else {
				sleepChild1.setVisibility(View.GONE);
				sleepChild2.setVisibility(View.GONE);
			}
			break;
		case 2:
			messHallChild.setVisibility(View.GONE);
			playgroundChild.setVisibility(View.GONE);
			sleepChild3.setVisibility(View.VISIBLE);
			messHallNum.setText("0");
			playgroundNum.setText("0");
			sleepNum.setText("1");
			if (SharePrefsUtils.getRole(getActivity())) {
				sleepChild1.setVisibility(View.VISIBLE);
				sleepChild2.setVisibility(View.VISIBLE);
				sleepNum.setText("3");
			} else {
				sleepChild1.setVisibility(View.GONE);
				sleepChild2.setVisibility(View.GONE);
			}
			break;
		}
	}

	Runnable mProgressRunner = new Runnable() {
		@Override
		public void run() {
			if (flag) {
				System.out.println("----->>>");
				refreshView();
				mHandler.postDelayed(mProgressRunner, 10000);
			}
		}
	};
}
