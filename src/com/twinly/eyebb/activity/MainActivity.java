package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.TabsAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.fragment.IndoorLocatorFragment;
import com.twinly.eyebb.fragment.ProfileFragment;
import com.twinly.eyebb.fragment.RadarTrackingFragment;
import com.twinly.eyebb.fragment.ReportFragment;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		ReportFragment.CallbackInterface,
		IndoorLocatorFragment.CallbackInterface {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private IndoorLocatorFragment indoorLocatorFragment;
	private RadarTrackingFragment radarTrackingFragment;
	private ReportFragment reportFragment;
	private ProfileFragment profileFragment;

	private Map<String, ArrayList<String>> indoorLocatorData;

	private SmoothProgressBar progressBar;
	private SmoothProgressBar bar;

	private boolean isRefreshing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkLogin();
		setContentView(R.layout.activity_main);

		setUpTab(savedInstanceState);
		setUpProgressBar();
		checkBluetooth();
		indoorLocatorData = new HashMap<String, ArrayList<String>>();
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

	private void setUpTab(Bundle savedInstanceState) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		indoorLocatorFragment = new IndoorLocatorFragment();
		indoorLocatorFragment.setCallbackInterface(this);
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
		reportFragment.setCallbackInterface(this);
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
		super.onResume();
		bar.setVisibility(View.INVISIBLE);
	}

	class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bar.setVisibility(View.VISIBLE);
			bar.progressiveStart();
			indoorLocatorData.clear();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return HttpRequestUtils.get("reportService/api/childrenList", null);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("childrenList = " + result);
			try {
				JSONObject json = new JSONObject(result);
				JSONArray list = json
						.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_LIST);
				for (int i = 0; i < list.length(); i++) {
					JSONObject object = (JSONObject) list.get(i);
					String childId = object
							.getString(HttpConstants.JSON_KEY_CHILD_ID);
					String locationName = object.getJSONObject(
							HttpConstants.JSON_KEY_LOCATION).getString(
							HttpConstants.JSON_KEY_LOCATION_NAME);
					updateLocationData(childId, locationName);
					indoorLocatorFragment.updateListView(indoorLocatorData);
				}

			} catch (JSONException e) {
				/*Toast.makeText(MainActivity.this,
						getString(R.string.toast_invalid_username_or_password),
						Toast.LENGTH_SHORT).show();*/
				System.out.println("reportService/api/childrenList ---->> "
						+ e.getMessage());
			}
			isRefreshing = false;
			progressBar.setProgress(0);
			bar.progressiveStop();
			reportFragment.setRefreshing(false);
			indoorLocatorFragment.setRefreshing(false);
		}

	}

	private void updateLocationData(String childId, String locationName) {
		ArrayList<String> childrenIdList = null;
		if (indoorLocatorData.keySet().contains(locationName)) {
			childrenIdList = indoorLocatorData.get(locationName);
		} else {
			childrenIdList = new ArrayList<String>();
		}
		childrenIdList.add(childId);
		indoorLocatorData.put(locationName, childrenIdList);
	}

	private void updateProgress(int value) {
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setProgress(progressBar.getProgress() + value);
		if (progressBar.getProgress() >= 100) {
			isRefreshing = true;
			reportFragment.setRefreshing(true);
			indoorLocatorFragment.setRefreshing(true);

			new UpdateView().execute();
		}
	}

	@Override
	public void updateProgressBar(int value) {
		updateProgress(value);
	}

	@Override
	public void cancelProgressBar() {
		if (isRefreshing == false) {
			progressBar.setProgress(0);
		}
	}
}
