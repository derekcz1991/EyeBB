package com.twinly.eyebb.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eyebb.R;

public class SettingsActivity extends Activity {

	private View tittlebarBackBtn;
	private TextView englishSelected;
	private TextView chineseSelected;
	private TextView enableSoundSelected;
	private TextView enableVibrationSelected;
	final static int ENGLISH = 1;
	final static int CHINESE = 2;
	// the default is true
	private Boolean isEnableSoundSelected;
	private Boolean isEnableVibrationSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

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

		Intent intent = getIntent();
		isEnableSoundSelected = intent.getBooleanExtra("isEnableSoundSelected",
				true);
		isEnableVibrationSelected = intent.getBooleanExtra(
				"isEnableVibrationSelected", true);

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

		enableSoundSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isEnableSoundSelected) {
					enableSoundSelected
							.setBackgroundResource(R.drawable.ic_selected_off);

					isEnableSoundSelected = false;
				} else if (!isEnableSoundSelected) {
					enableSoundSelected
							.setBackgroundResource(R.drawable.ic_selected);

					isEnableSoundSelected = true;
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
				} else if (!isEnableVibrationSelected) {
					enableVibrationSelected
							.setBackgroundResource(R.drawable.ic_selected);

					isEnableVibrationSelected = true;
				}
			}
		});
		checkAppLanguage();
	}

	// change the language
	public void changeAppLanguage(int language) {
		// 搴���ㄥ�����缃�璇�瑷�
		Resources resources = getResources();// ��峰��res璧�婧�瀵硅薄
		Configuration config = resources.getConfiguration();// ��峰��璁剧疆瀵硅薄
		DisplayMetrics dm = resources.getDisplayMetrics();// ��峰��灞�骞������帮��涓昏��������杈ㄧ��锛����绱�绛����

		if (language == 1) {
			config.locale = Locale.ENGLISH;
			resources.updateConfiguration(config, dm);
		} else if (language == 2) {
			config.locale = Locale.TRADITIONAL_CHINESE;
			resources.updateConfiguration(config, dm);

		}
		Intent intent = new Intent();
		intent.setClass(SettingsActivity.this, SettingsActivity.class);
		intent.putExtra("isEnableVibrationSelected", isEnableVibrationSelected);
		intent.putExtra("isEnableSoundSelected", isEnableSoundSelected);
		startActivity(intent);
		finish();
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

}
