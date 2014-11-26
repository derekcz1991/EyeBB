package com.twinly.eyebb.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class SettingsActivity extends Activity {

	private View tittlebarBackBtn;
	private LinearLayout refreshTimeView;
	private TextView englishSelected;
	private TextView chineseSelected;
	private TextView enableSoundSelected;
	private TextView enableVibrationSelected;
	private View aboutBtn;
	private LinearLayout bindingBtn;
	private LinearLayout authorizationBtn;
	private LinearLayout updatePswBtn;
	private LinearLayout updateNicknameBtn;
	// private LinearLayout batteryLifeBtn;

	private boolean isAutoUpdate;
	private TextView refreshTimeNumber;
	public static SettingsActivity instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		instance = this;
		tittlebarBackBtn = this.findViewById(R.id.tittlebar_back_btn);
		enableSoundSelected = (TextView) findViewById(R.id.enable_sound_selected);
		enableVibrationSelected = (TextView) findViewById(R.id.enable_vibration_selected);
		chineseSelected = (TextView) findViewById(R.id.chinese_selected);
		englishSelected = (TextView) findViewById(R.id.english_selected);
		aboutBtn = findViewById(R.id.about_btn);
		refreshTimeView = (LinearLayout) findViewById(R.id.refresh_time_view);
		refreshTimeNumber = (TextView) findViewById(R.id.refresh_time_number);
		bindingBtn = (LinearLayout) findViewById(R.id.binding_btn);
		authorizationBtn = (LinearLayout) findViewById(R.id.authorization_btn);
		updatePswBtn = (LinearLayout) findViewById(R.id.update_psw_btn);
		updateNicknameBtn = (LinearLayout) findViewById(R.id.update_nickname_btn);
		// batteryLifeBtn = (LinearLayout) findViewById(R.id.battery_life_btn);

		setupView();

		tittlebarBackBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finishActivity();
			}
		});

		// get the language location
		englishSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				englishSelected.setBackgroundResource(R.drawable.ic_selected);
				chineseSelected
						.setBackgroundResource(R.drawable.ic_selected_off);

				setAppLanguage(BleDeviceConstants.LOCALE_EN);
			}
		});

		chineseSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				englishSelected
						.setBackgroundResource(R.drawable.ic_selected_off);
				chineseSelected.setBackgroundResource(R.drawable.ic_selected);

				setAppLanguage(BleDeviceConstants.LOCALE_HK);
			}
		});

		enableSoundSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SharePrefsUtils.isSoundOn(SettingsActivity.this)) {
					enableSoundSelected
							.setBackgroundResource(R.drawable.ic_selected_off);
					SharePrefsUtils.setSoundOn(SettingsActivity.this, false);
				} else {
					enableSoundSelected
							.setBackgroundResource(R.drawable.ic_selected);
					SharePrefsUtils.setSoundOn(SettingsActivity.this, true);
				}

			}
		});

		enableVibrationSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (SharePrefsUtils.isVibrateOn(SettingsActivity.this)) {
					enableVibrationSelected
							.setBackgroundResource(R.drawable.ic_selected_off);
					SharePrefsUtils.setVibrateOn(SettingsActivity.this, false);
				} else {
					enableVibrationSelected
							.setBackgroundResource(R.drawable.ic_selected);
					SharePrefsUtils.setVibrateOn(SettingsActivity.this, true);
				}
			}
		});

		aboutBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});

		refreshTimeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,
						RefreshTimeDialog.class);
				startActivity(intent);
			}
		});

		bindingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,
						MyKidsListActivity.class);
				startActivity(intent);
			}
		});

		authorizationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);

			}
		});

		updatePswBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,
						UpdatePasswordActivity.class);
				startActivity(intent);
			}
		});
		
		updateNicknameBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,
						UpdateNicknameActivity.class);
				startActivity(intent);
			}
		});

		// batteryLifeBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// Intent intent = new Intent(SettingsActivity.this,
		// BatteryLifeActivity.class);
		// startActivity(intent);
		// }
		// });

	}

	private void setupView() {
		// auto refresh time
		refreshTimeNumber
				.setText(SharePrefsUtils.refreshTime(this) == null ? SharePrefsUtils
						.refreshTime(this) : "5");

		// sound
		if (SharePrefsUtils.isSoundOn(this)) {
			enableSoundSelected.setBackgroundResource(R.drawable.ic_selected);
		} else {
			enableSoundSelected
					.setBackgroundResource(R.drawable.ic_selected_off);
		}

		// vibrate
		if (SharePrefsUtils.isVibrateOn(this)) {
			enableVibrationSelected
					.setBackgroundResource(R.drawable.ic_selected);
		} else {
			enableVibrationSelected
					.setBackgroundResource(R.drawable.ic_selected_off);
		}

		// language
		switch (SharePrefsUtils.getLanguage(this)) {
		case BleDeviceConstants.LOCALE_TW:
		case BleDeviceConstants.LOCALE_HK:
		case BleDeviceConstants.LOCALE_CN:
			englishSelected.setBackgroundResource(R.drawable.ic_selected_off);
			chineseSelected.setBackgroundResource(R.drawable.ic_selected);
			break;
		default:
			englishSelected.setBackgroundResource(R.drawable.ic_selected);
			chineseSelected.setBackgroundResource(R.drawable.ic_selected_off);
			break;
		}

		// device item
		if (SharePrefsUtils.getUserType(this).equals("P")) {
			findViewById(R.id.device_item).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.device_item).setVisibility(View.GONE);
		}
	}

	// change the language
	public void setAppLanguage(int language) {

		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();

		switch (language) {
		case BleDeviceConstants.LOCALE_TW:
		case BleDeviceConstants.LOCALE_HK:
		case BleDeviceConstants.LOCALE_CN:
			config.locale = Locale.TRADITIONAL_CHINESE;
			resources.updateConfiguration(config, dm);
			break;
		default:
			config.locale = Locale.ENGLISH;
			resources.updateConfiguration(config, dm);
			break;
		}
		SharePrefsUtils.setLanguage(this, language);

		Intent intent = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void onLogoutClicked(View view) {
		SharePrefsUtils.setLogin(this, false);
		setResult(ActivityConstants.RESULT_LOGOUT);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void finishActivity() {
		if (isAutoUpdate && SharePrefsUtils.isAutoUpdate(this) == false) {
			setResult(ActivityConstants.RESULT_AUTO_UPDATE_OFF);
		} else if (isAutoUpdate == false && SharePrefsUtils.isAutoUpdate(this)) {
			setResult(ActivityConstants.RESULT_AUTO_UPDATE_ON);
		}
		finish();
	}

}
