package com.twinly.eyebb.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.TabsAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.fragment.IndoorLocatorFragment;
import com.twinly.eyebb.fragment.ProfileFragment;
import com.twinly.eyebb.fragment.RadarTrackingFragment;
import com.twinly.eyebb.fragment.ReportFragment;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private IndoorLocatorFragment indoorLocatorFragment;
	private RadarTrackingFragment radarTrackingFragment;
	private ReportFragment reportFragment;
	private ProfileFragment profileFragment;
	private boolean flag = true;
	private UpdateView updateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkLogin();

		setContentView(R.layout.activity_main);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		setUpTab();
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		mTabHost.setCurrentTab(0);

		checkBluetooth();
		updateView = new UpdateView();
		//updateView.execute();
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	private void checkLogin() {
		if (SharePrefsUtils.isLogin(this) == false) {
			Intent intent = new Intent(this, WelcomeActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY);
		}
	}

	private void setUpTab() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		//mViewPager.setOffscreenPageLimit(2);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		indoorLocatorFragment = new IndoorLocatorFragment();
		View mainLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		mainLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_home_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Main").setIndicator(mainLabel),
				indoorLocatorFragment);

		radarTrackingFragment = new RadarTrackingFragment();
		View trackingLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		trackingLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_tracking_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Radar").setIndicator(trackingLabel),
				radarTrackingFragment);

		reportFragment = new ReportFragment();
		View reportLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		reportLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_report_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Report").setIndicator(reportLabel),
				reportFragment);

		profileFragment = new ProfileFragment();
		View profileLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		profileLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_profile_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Profile").setIndicator(profileLabel),
				profileFragment);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY) {
			if (resultCode != ActivityConstants.RESULT_RESULT_OK) {
				finish();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return HttpRequestUtils.get("reportService/api/childrenList", null);
		}

		@Override
		protected void onPostExecute(String result) {

		}

	}

}
