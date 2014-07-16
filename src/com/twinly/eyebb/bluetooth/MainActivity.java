package com.twinly.eyebb.bluetooth;

import com.eyebb.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnClickListener {
	public static String TAB_TAG_CALL = "scan";
	public static String TAB_TAG_CONTACT = "contact";
	public static String TAB_TAG_HISTORY = "history";
	public static String TAB_TAG_ACCOUNT = "account";
	public static String TAB_TAB_MORE = "more";
	public static TabHost mTabHost;
	public static final int COLOR1 = Color.parseColor("#787878");
	public static final int COLOR2 = Color.parseColor("#ffffff");
	public static ImageView mBut1, mBut2, mBut3, mBut4, mBut5;
	public static TextView mCateText1, mCateText2, mCateText3, mCateText4, mCateText5;

	Intent mCallItent, mContactIntent, mHistoryIntent, mAccountIntent, mMoreIntent;

//	int mCurTabId = R.id.channel1;

	// Animation
	private Animation left_in, left_out;
	private Animation right_in, right_out;
	 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//if(BaseApp.getInstance().mAccount == null || BaseApp.getInstance().mAccount.length()==0){
		//	Constans.logout(MainActivity.this);
		//	return;
		//}
		setContentView(R.layout.ble_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		BaseApp.getInstance().addActivity(this);
						
		prepareAnim();
		prepareIntent();
		setupIntent();
		prepareView();
	}

	private void prepareAnim() {
//		left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
//		left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
//
//		right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
//		right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);
	}

	private void prepareView() {
//		mBut1 = (ImageView) findViewById(R.id.imageView1);
//		mBut2 = (ImageView) findViewById(R.id.imageView2);
//		mBut3 = (ImageView) findViewById(R.id.imageView3);
//		mBut4 = (ImageView) findViewById(R.id.imageView4);
//		mBut5 = (ImageView) findViewById(R.id.imageView5);
//		findViewById(R.id.channel1).setOnClickListener(this);
//		findViewById(R.id.channel2).setOnClickListener(this);
//		findViewById(R.id.channel3).setOnClickListener(this);
//		findViewById(R.id.channel4).setOnClickListener(this);
//		findViewById(R.id.channel5).setOnClickListener(this);
//		mCateText1 = (TextView) findViewById(R.id.textView1);
//		mCateText2 = (TextView) findViewById(R.id.textView2);
//		mCateText3 = (TextView) findViewById(R.id.textView3);
//		mCateText4 = (TextView) findViewById(R.id.textView4);
//		mCateText5 = (TextView) findViewById(R.id.textView5);
	}

	private void prepareIntent() {
		mCallItent = new Intent(this, PeripheralActivity.class);
		mContactIntent = new Intent(this, PeripheralActivity.class);
		mHistoryIntent = new Intent(this, PeripheralActivity.class);
		mAccountIntent = new Intent(this, PeripheralActivity.class);
		mMoreIntent = new Intent(this, PeripheralActivity.class);		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {			
			mBut1.performClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setupIntent() {
		mTabHost = getTabHost();
//		mTabHost.addTab(buildTabSpec(TAB_TAG_CALL, R.string.category_call,R.drawable.icon_1_n, mCallItent));
//		mTabHost.addTab(buildTabSpec(TAB_TAG_CONTACT,R.string.category_contact, R.drawable.icon_2_n, mContactIntent));
//		mTabHost.addTab(buildTabSpec(TAB_TAG_HISTORY, R.string.category_history,R.drawable.icon_3_n, mHistoryIntent));
//		mTabHost.addTab(buildTabSpec(TAB_TAG_ACCOUNT,R.string.category_account, R.drawable.icon_4_n, mAccountIntent));
//		mTabHost.addTab(buildTabSpec(TAB_TAB_MORE, R.string.category_more,R.drawable.icon_5_n, mMoreIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,final Intent content) {
		return mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),getResources().getDrawable(resIcon)).setContent(content);
	}

	public static void setCurrentTabByTag(String tab) {
		mTabHost.setCurrentTabByTag(tab);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		if (mCurTabId == v.getId()) {
//			return;
//		}
//		mBut1.setImageResource(R.drawable.icon_1_n);
//		mBut2.setImageResource(R.drawable.icon_2_n);
//		mBut3.setImageResource(R.drawable.icon_3_n);
//		mBut4.setImageResource(R.drawable.icon_4_n);
//		mBut5.setImageResource(R.drawable.icon_5_n);
//		mCateText1.setTextColor(COLOR1);
//		mCateText2.setTextColor(COLOR1);
//		mCateText3.setTextColor(COLOR1);
//		mCateText4.setTextColor(COLOR1);
//		mCateText5.setTextColor(COLOR1);
//		int checkedId = v.getId();
//		final boolean o;
//		if (mCurTabId < checkedId)
//			o = true;
//		else
//			o = false;
//		if (o)
//			mTabHost.getCurrentView().startAnimation(left_out);
//		else
//			mTabHost.getCurrentView().startAnimation(right_out);
//		switch (checkedId) {
//		case R.id.channel1:
//			mTabHost.setCurrentTabByTag(TAB_TAG_CALL);
//			mBut1.setImageResource(R.drawable.icon_1_c);
//			mCateText1.setTextColor(COLOR2);
//			break;
//		case R.id.channel2:
//			mTabHost.setCurrentTabByTag(TAB_TAG_CONTACT);
//			mBut2.setImageResource(R.drawable.icon_2_c);
//			mCateText2.setTextColor(COLOR2);
//			break;
//		case R.id.channel3:
//			mTabHost.setCurrentTabByTag(TAB_TAG_HISTORY);
//			mBut3.setImageResource(R.drawable.icon_3_c);
//			mCateText3.setTextColor(COLOR2);
//			break;
//		case R.id.channel4:
//			mTabHost.setCurrentTabByTag(TAB_TAG_ACCOUNT);
//			mBut4.setImageResource(R.drawable.icon_4_c);
//			mCateText4.setTextColor(COLOR2);
//			break;
//		case R.id.channel5:
//			mTabHost.setCurrentTabByTag(TAB_TAB_MORE);
//			mBut5.setImageResource(R.drawable.icon_5_c);
//			mCateText5.setTextColor(COLOR2);
//			break;
//		default:
//			break;
//		}
//
//		if (o)
//			mTabHost.getCurrentView().startAnimation(left_in);
//		else
//			mTabHost.getCurrentView().startAnimation(right_in);
//		mCurTabId = checkedId;
//	}	

}