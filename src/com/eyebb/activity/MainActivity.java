package com.eyebb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

import com.eyebb.R;
import com.eyebb.adapter.TabsAdapter;
import com.eyebb.constant.Constants;
import com.eyebb.fragment.IndoorLocatorFragment;
import com.eyebb.fragment.ProfileFragment;
import com.eyebb.fragment.RadarTrackingFragment;
import com.eyebb.fragment.ReportFragment;
import com.eyebb.utils.SharePrefsUtils;

public class MainActivity extends FragmentActivity {
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

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
					Constants.REQUEST_GO_TO_WELCOME_ACTIVITY);
		}
	}

	private void setUpTab() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(2);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		View mainLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		mainLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_home_selector);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("Main").setIndicator(mainLabel),
				IndoorLocatorFragment.class, null);

		View trackingLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		trackingLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_tracking_selector);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("Radar").setIndicator(trackingLabel),
				RadarTrackingFragment.class, null);

		View reportLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		reportLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_report_selector);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("Report").setIndicator(reportLabel),
				ReportFragment.class, null);

		View profileLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		profileLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_profile_selector);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("Profile").setIndicator(profileLabel),
				ProfileFragment.class, null);
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
		if (requestCode == Constants.REQUEST_GO_TO_WELCOME_ACTIVITY) {
			if (resultCode != Constants.RESULT_RESULT_OK) {
				finish();
			}
		}
	}

}
