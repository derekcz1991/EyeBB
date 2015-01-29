package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.SelectKidsActivity;
import com.twinly.eyebb.bluetooth.BluetoothUtils;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.SerializableChildrenList;

@SuppressLint("NewApi")
public class RadarFragment extends Fragment {
	public final static int LOST_TIMEOUT = 15000;
	private final int MESSAGE_WHAT_UPDATE_VIEW = 0;
	private final int MESSAGE_WHAT_REMOVE_CALLBACK = 1;

	private RadarTrackingFragment radarTrackingFragment;
	private AntiLostFragment antiLostFragment;
	private TextView btnRadarSwitch;
	private RelativeLayout container;
	private CheckedTextView tvRadarTracking;
	private CheckedTextView tvAntiLost;

	private BluetoothUtils mBluetoothUtils;
	public static boolean isRadarOpen = false;
	private boolean isRadarTrackingOn = false;
	private boolean isAntiLostOn = false;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
				case BluetoothAdapter.STATE_OFF:
					break;
				case BluetoothAdapter.STATE_ON:
					start();
					break;
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActivity().registerReceiver(mReceiver,
				new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");
		mBluetoothUtils = new BluetoothUtils(getActivity(),
				getFragmentManager());

		View v = inflater.inflate(R.layout.fragment_radar, container, false);
		setUpView(v);
		setupListener();
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		System.out.println("TempFragment ==>> onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private void setUpView(View v) {
		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();
		radarTrackingFragment = (RadarTrackingFragment) getChildFragmentManager()
				.findFragmentByTag("radar");
		if (radarTrackingFragment == null) {
			radarTrackingFragment = new RadarTrackingFragment();

			fragmentTransaction.add(R.id.container, radarTrackingFragment,
					"radar");
		} else {
			fragmentTransaction.show(radarTrackingFragment);
		}

		antiLostFragment = new AntiLostFragment();
		antiLostFragment = (AntiLostFragment) getChildFragmentManager()
				.findFragmentByTag("antiLost");
		if (antiLostFragment == null) {
			antiLostFragment = new AntiLostFragment();
			fragmentTransaction.add(R.id.container, antiLostFragment,
					"antiLost");
			fragmentTransaction.hide(antiLostFragment);
		} else {
			fragmentTransaction.hide(antiLostFragment);
		}
		fragmentTransaction.commit();

		btnRadarSwitch = (TextView) v.findViewById(R.id.btn_radar_switch);
		container = (RelativeLayout) v.findViewById(R.id.container);
		tvRadarTracking = (CheckedTextView) v.findViewById(R.id.radar_tracking);
		tvAntiLost = (CheckedTextView) v.findViewById(R.id.anti_lost);
	}

	private void setupListener() {
		btnRadarSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRadarOpen) {
					stop();
				} else {
					if (mBluetoothUtils.initialize()) {
						start();
					}
				}
			}
		});
		tvRadarTracking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startRadar();
			}
		});
		tvAntiLost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAntiLostOn == false) {
					Intent intent = new Intent(getActivity(),
							SelectKidsActivity.class);
					startActivityForResult(intent, 1);
				}
			}
		});
	}

	private void start() {
		isRadarOpen = true;
		container.setAlpha(1F);
		container.setEnabled(true);
		tvRadarTracking.setEnabled(true);
		tvAntiLost.setEnabled(true);
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_on);
		startRadar();

		mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UPDATE_VIEW, 2000);
	}

	private void stop() {
		isRadarOpen = false;
		container.setAlpha(0.3F);
		container.setEnabled(false);
		tvRadarTracking.setEnabled(false);
		tvAntiLost.setEnabled(false);
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_off);
		stopRadar();
		stopAntiLost();
	}

	private void startRadar() {
		if (isRadarTrackingOn == false) {
			stopAntiLost();
			isRadarTrackingOn = true;
			tvRadarTracking.setChecked(true);
			radarTrackingFragment.start();
			getChildFragmentManager().beginTransaction()
					.show(radarTrackingFragment).hide(antiLostFragment)
					.commit();
		}
	}

	private void stopRadar() {
		if (isRadarTrackingOn == true) {
			isRadarTrackingOn = false;
			tvRadarTracking.setChecked(false);
			radarTrackingFragment.stop();
		}
	}

	private void startAntiLost(ArrayList<String> antiLostDeviceList) {
		if (isAntiLostOn == false) {
			stopRadar();
			isAntiLostOn = true;
			tvAntiLost.setChecked(true);
			antiLostFragment.start(antiLostDeviceList);
			getChildFragmentManager().beginTransaction().show(antiLostFragment)
					.hide(radarTrackingFragment).commit();
		}
	}

	private void stopAntiLost() {
		if (isAntiLostOn == true) {
			isAntiLostOn = false;
			tvAntiLost.setChecked(false);
			antiLostFragment.stop();
		}
	}

	Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_WHAT_UPDATE_VIEW:
				mHandler.post(updateViewRunnable);
				break;
			case MESSAGE_WHAT_REMOVE_CALLBACK:
				mHandler.removeCallbacks(updateViewRunnable);
				break;
			}
		}
	};

	Runnable updateViewRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRadarOpen) {
				radarTrackingFragment.updateView();
				antiLostFragment.updateView();
				mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UPDATE_VIEW, 5000);
			}

		}

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
			mBluetoothUtils.stopLeScan();

			SerializableChildrenList serializableChildrenList = (SerializableChildrenList) data
					.getExtras().getSerializable(
							SelectKidsActivity.EXTRA_CHILDREN_LIST);
			ArrayList<Child> childrenList = serializableChildrenList.getList();
			ArrayList<String> antiLostDeviceList = new ArrayList<String>();
			for (int i = 0; i < childrenList.size(); i++) {
				if (childrenList.get(i).isWithAccess()) {
					antiLostDeviceList.add(childrenList.get(i).getMacAddress());
				}
			}
			startAntiLost(antiLostDeviceList);
		}
	}
}
