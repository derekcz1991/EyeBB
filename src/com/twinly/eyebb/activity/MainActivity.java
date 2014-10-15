package com.twinly.eyebb.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.TabsAdapter;
import com.twinly.eyebb.adapter.TabsAdapter.TabsAdapterCallback;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.fragment.IndoorLocatorFragment;
import com.twinly.eyebb.fragment.ProfileFragment;
import com.twinly.eyebb.fragment.RadarTrackingFragment;
import com.twinly.eyebb.fragment.ReportFragment;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		ReportFragment.CallbackInterface,
		IndoorLocatorFragment.CallbackInterface,
		ProfileFragment.CallbackInterface, TabsAdapterCallback {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private IndoorLocatorFragment indoorLocatorFragment;
	private RadarTrackingFragment radarTrackingFragment;
	private ReportFragment reportFragment;
	private ProfileFragment profileFragment;
	private TextView noticeNum;

	private SmoothProgressBar progressBar;
	private SmoothProgressBar bar;
	private boolean isRefreshing;
	private boolean autoUpdateFlag;
	private AutoUpdateTask autoUpdateTask;
	private KeepSessionAliveTask keepSessionAliveTask;
	private int timeoutCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setUpTab(savedInstanceState);
		setUpProgressBar();
		// checkBluetooth();
		SystemUtils.initImageLoader(getApplicationContext());

		if (SharePrefsUtils.isAutoUpdate(this)) {
			autoUpdateFlag = true;
			autoUpdateTask = new AutoUpdateTask();
			autoUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			indoorLocatorFragment.updateView();
		}
		reportFragment.updateView();
		keepSessionAliveTask = new KeepSessionAliveTask();
		keepSessionAliveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	protected void onResume() {
		super.onResume();
		bar.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		autoUpdateFlag = false;
		if (autoUpdateTask != null) {
			autoUpdateTask.cancel(true);
		}
		if (keepSessionAliveTask != null) {
			keepSessionAliveTask.cancel(true);
		}
	}

	@SuppressLint("InflateParams")
	private void setUpTab(Bundle savedInstanceState) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
		mTabsAdapter.setCallback(this);

		indoorLocatorFragment = new IndoorLocatorFragment();
		indoorLocatorFragment.setCallbackInterface(this);
		// main
		View mainLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		mainLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_home_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Main").setIndicator(mainLabel),
				indoorLocatorFragment);
		//		mainLabel.findViewById(R.id.notification_number).setVisibility(
		//				View.GONE);

		// radar
		radarTrackingFragment = new RadarTrackingFragment();
		View trackingLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		trackingLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_tracking_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Radar").setIndicator(trackingLabel),
				radarTrackingFragment);
		//		trackingLabel.findViewById(R.id.notification_number).setVisibility(
		//				View.GONE);

		// report
		reportFragment = new ReportFragment();
		reportFragment.setCallbackInterface(this);
		View reportLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		reportLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_report_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Report").setIndicator(reportLabel),
				reportFragment);
		//		reportLabel.findViewById(R.id.notification_number).setVisibility(
		//				View.GONE);

		// profile
		profileFragment = new ProfileFragment();
		profileFragment.setCallbackInterface(this);
		View profileLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		profileLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_profile_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Profile").setIndicator(profileLabel),
				profileFragment);
		//		noticeNum = (TextView) profileLabel
		//				.findViewById(R.id.notification_number);
		//		noticeNum.setVisibility(View.VISIBLE);

		mTabHost.setCurrentTab(0);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

	}

	private void setUpProgressBar() {
		bar = (SmoothProgressBar) findViewById(R.id.bar);
		bar.setVisibility(View.INVISIBLE);

		progressBar = (SmoothProgressBar) findViewById(R.id.progressBar);
		ShapeDrawable shape = new ShapeDrawable();
		shape.setShape(new RectShape());
		shape.getPaint().setColor(
				getResources().getColor(R.color.spb_default_color));
		ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER,
				ClipDrawable.HORIZONTAL);
		progressBar.setProgressDrawable(clipDrawable);

		progressBar.setVisibility(View.INVISIBLE);
		progressBar.setProgress(0);
		progressBar.setIndeterminate(false);
	}

	private void checkBluetooth() {
		// TODO Auto-generated method stub
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.text_ble_not_supported,
					Toast.LENGTH_SHORT).show();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.text_error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void updateProgressBarForIndoorLocator(int value) {
		updateProgressBar(value, 0);
	}

	@Override
	public void updateProgressBarForReport(int value) {
		updateProgressBar(value, 1);
	}

	private void updateProgressBar(int value, int fragmentId) {
		if (isRefreshing == false) {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(progressBar.getProgress() + value);
			if (progressBar.getProgress() >= 100) {
				isRefreshing = true;
				bar.setVisibility(View.VISIBLE);
				bar.progressiveStart();
				switch (fragmentId) {
				case 0:
					indoorLocatorFragment.updateView();
					break;
				case 1:
					reportFragment.updateView();
					break;
				}
			}
		}
	}

	@Override
	public void cancelProgressBar() {
		if (isRefreshing == false) {
			progressBar.setProgress(0);
		}
	}

	@Override
	public void resetProgressBar() {
		isRefreshing = false;
		progressBar.setProgress(0);
		bar.progressiveStop();
		//reportFragment.setRefreshing(false);
	}

	@Override
	public void onProfileTabClicked() {
		//noticeNum.setVisibility(View.INVISIBLE);
	}

	@Override
	public void startAutoRefresh() {
		autoUpdateFlag = true;
		indoorLocatorFragment.lockListViewToPull(true);
		autoUpdateTask = new AutoUpdateTask();
		autoUpdateTask.execute();
	}

	@Override
	public void stopAutoRefresh() {
		autoUpdateFlag = false;
		indoorLocatorFragment.lockListViewToPull(false);
		if (autoUpdateTask != null)
			autoUpdateTask.cancel(true);
	}

	private class AutoUpdateTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (autoUpdateFlag) {
				if (indoorLocatorFragment != null) {
					indoorLocatorFragment.updateView();
				}
				try {
					// refresh time
					long refreshTime = 5;
					try {
						if (Long.parseLong(SharePrefsUtils
								.refreshTime(MainActivity.this)) > 0) {
							refreshTime = Long.parseLong(SharePrefsUtils
									.refreshTime(MainActivity.this)) * 1000;
						} else {
							SharePrefsUtils.setRefreshTime(MainActivity.this,
									refreshTime + "");
							refreshTime = refreshTime * 1000;
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						SharePrefsUtils.setRefreshTime(MainActivity.this,
								refreshTime + "");
						refreshTime = refreshTime * 1000;
						e.printStackTrace();
					}
					Thread.sleep(refreshTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	private class KeepSessionAliveTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				try {
					Thread.sleep(600000); //10min = 600000s
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("KeepSessionAliveTask runs once --->>>");
				this.publishProgress(HttpRequestUtils.get(
						"reportService/api/refreshSession", null));
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			if (values[0].equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
				timeoutCounter++;
				if (timeoutCounter == 2) {
					finish();
				}
			} else {
				timeoutCounter = 0;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY) {
			if (resultCode != ActivityConstants.RESULT_RESULT_OK) {
				finish();
			}
		}
	}

}
