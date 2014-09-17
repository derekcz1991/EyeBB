package com.twinly.eyebb.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class SettingsActivity extends Activity {

	private View tittlebarBackBtn;
	private TextView englishSelected;
	private TextView chineseSelected;
	private TextView enableSoundSelected;
	private TextView enableVibrationSelected;
	public final static int ENGLISH = 1;
	public final static int CHINESE = 2;
	private View aboutBtn;
	// the default is true
	private Boolean isEnableSoundSelected;
	private Boolean isEnableVibrationSelected;

	// sharedPreferences
	private SharedPreferences SandVpreferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		SandVpreferences = getSharedPreferences("soundAndVibrate", MODE_PRIVATE);
		editor = SandVpreferences.edit();

		tittlebarBackBtn = this.findViewById(R.id.tittlebar_back_btn);
		tittlebarBackBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		chineseSelected = (TextView) findViewById(R.id.chinese_selected);
		englishSelected = (TextView) findViewById(R.id.english_selected);

		// get the language location
		englishSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				englishSelected.setBackgroundResource(R.drawable.ic_selected);
				chineseSelected
						.setBackgroundResource(R.drawable.ic_selected_off);

				changeAppLanguage(ENGLISH);
			}
		});

		chineseSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				englishSelected
						.setBackgroundResource(R.drawable.ic_selected_off);
				chineseSelected.setBackgroundResource(R.drawable.ic_selected);

				changeAppLanguage(CHINESE);
			}
		});

		enableSoundSelected = (TextView) findViewById(R.id.enable_sound_selected);
		enableVibrationSelected = (TextView) findViewById(R.id.enable_vibration_selected);

		// check

		// 设定默认值
		isEnableSoundSelected = SandVpreferences.getBoolean("sound", true);
		isEnableVibrationSelected = SandVpreferences
				.getBoolean("vibrate", true);
		checkSoundAndVibrate(isEnableSoundSelected, isEnableVibrationSelected);
		// 判断是否点击sound vibration

		enableSoundSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isEnableSoundSelected) {
					enableSoundSelected
							.setBackgroundResource(R.drawable.ic_selected_off);

					isEnableSoundSelected = false;
					// 设置总的控制 传递数据
					editor.putBoolean("sound", isEnableSoundSelected);
					editor.commit();
				} else if (!isEnableSoundSelected) {
					enableSoundSelected
							.setBackgroundResource(R.drawable.ic_selected);

					isEnableSoundSelected = true;
					editor.putBoolean("sound", isEnableSoundSelected);
					editor.commit();
				}

			}
		});

		enableVibrationSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isEnableVibrationSelected) {
					enableVibrationSelected
							.setBackgroundResource(R.drawable.ic_selected_off);

					isEnableVibrationSelected = false;
					editor.putBoolean("vibrate", isEnableVibrationSelected);
					editor.commit();
				} else if (!isEnableVibrationSelected) {
					enableVibrationSelected
							.setBackgroundResource(R.drawable.ic_selected);

					isEnableVibrationSelected = true;
					editor.putBoolean("vibrate", isEnableVibrationSelected);
					editor.commit();
				}
			}
		});

		//about activity

		aboutBtn = findViewById(R.id.about_btn);

		aboutBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});

		checkAppLanguage();

	}

	// change the language
	public void changeAppLanguage(int language) {

		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();

		if (language == 1) {
			config.locale = Locale.ENGLISH;
			resources.updateConfiguration(config, dm);

		} else if (language == 2) {
			config.locale = Locale.TRADITIONAL_CHINESE;
			resources.updateConfiguration(config, dm);

		}
		editor.putInt("language", language);
		editor.commit();
		// Intent intent = new Intent();
		// intent.setClass(SettingsActivity.this, SettingsActivity.class);
		// startActivity(intent);
		// finish();
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	private void checkAppLanguage() {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();

		System.out.println("config.locale====>" + config.locale);
		if (config.locale.toString().equals("en_GB")
				|| config.locale.toString().equals("en")) {
			englishSelected.setBackgroundResource(R.drawable.ic_selected);
			chineseSelected.setBackgroundResource(R.drawable.ic_selected_off);
			englishSelected.setEnabled(false);
		} else if (config.locale.toString().equals("zh_TW")
				|| config.locale.toString().equals("zh")) {
			englishSelected.setBackgroundResource(R.drawable.ic_selected_off);
			chineseSelected.setBackgroundResource(R.drawable.ic_selected);
			chineseSelected.setEnabled(false);
		} else if (config.locale.toString().equals("zh_HK")
				|| config.locale.toString().equals("zh")) {
			englishSelected.setBackgroundResource(R.drawable.ic_selected_off);
			chineseSelected.setBackgroundResource(R.drawable.ic_selected);
			chineseSelected.setEnabled(false);
		}

	}

	private void checkSoundAndVibrate(Boolean isEnableSoundSelected,
			Boolean isEnableVibrationSelected) {

		if (isEnableVibrationSelected) {
			enableVibrationSelected
					.setBackgroundResource(R.drawable.ic_selected);
		} else if (!isEnableVibrationSelected) {
			enableVibrationSelected
					.setBackgroundResource(R.drawable.ic_selected_off);
		}

		if (isEnableSoundSelected) {
			enableSoundSelected.setBackgroundResource(R.drawable.ic_selected);
		} else if (!isEnableSoundSelected) {
			enableSoundSelected
					.setBackgroundResource(R.drawable.ic_selected_off);
		}

	}

	public void onLogoutClicked(View view) {
		SharePrefsUtils.setLogin(this, false);
		setResult(ActivityConstants.RESULT_LOGOUT);
		finish();
	}
}
