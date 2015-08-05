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
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

/**
 * @author eyebb team
 * 
 * @category SettingsActivity
 * 
 *           setting activity shows the all options, which position is on top
 *           right conner of ProfileFragment class.
 * 
 */
public class SettingsActivity extends Activity {

	private View tittlebarBackBtn;
	private LinearLayout refreshTimeView;
	private CheckedTextView englishSelected;
	private CheckedTextView chineseSelected;
	private CheckedTextView simplifiedChineseSelected;
	private TextView enableSoundSelected;
	private TextView enableVibrationSelected;
	private View aboutBtn;
	private LinearLayout deviceItem;
	private LinearLayout bindingBtn;
	private LinearLayout authorizationBtn;
	private LinearLayout updatePswBtn;
	private LinearLayout updateNicknameBtn;
	private LinearLayout termsOfServiceBtn;
	private TextView termsOfService;
	private LinearLayout privacyPolicyBtn;
	private TextView privacyPolicy;

	private boolean isAutoUpdate;
	private TextView refreshTimeNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		tittlebarBackBtn = this.findViewById(R.id.tittlebar_back_btn);
		enableSoundSelected = (TextView) findViewById(R.id.enable_sound_selected);
		enableVibrationSelected = (TextView) findViewById(R.id.enable_vibration_selected);
		chineseSelected = (CheckedTextView) findViewById(R.id.chinese_selected);
		englishSelected = (CheckedTextView) findViewById(R.id.english_selected);
		simplifiedChineseSelected = (CheckedTextView) findViewById(R.id.simplified_chinese_selected);
		aboutBtn = findViewById(R.id.about_btn);
		refreshTimeView = (LinearLayout) findViewById(R.id.refresh_time_view);
		refreshTimeNumber = (TextView) findViewById(R.id.refresh_time_number);
		deviceItem = (LinearLayout) findViewById(R.id.device_item);
		bindingBtn = (LinearLayout) findViewById(R.id.binding_btn);
		authorizationBtn = (LinearLayout) findViewById(R.id.authorization_btn);
		updatePswBtn = (LinearLayout) findViewById(R.id.update_psw_btn);
		updateNicknameBtn = (LinearLayout) findViewById(R.id.update_nickname_btn);
		termsOfServiceBtn = (LinearLayout) findViewById(R.id.termsOfService_btn);
		termsOfService = (TextView) findViewById(R.id.termsOfService);
		privacyPolicyBtn = (LinearLayout) findViewById(R.id.privacyPolicy_btn);
		privacyPolicy = (TextView) findViewById(R.id.privacyPolicy);

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
				englishSelected.setChecked(true);
				chineseSelected.setChecked(false);
				simplifiedChineseSelected.setChecked(false);

				setAppLanguage(Constants.LOCALE_EN);
			}
		});

		chineseSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				englishSelected.setChecked(false);
				chineseSelected.setChecked(true);
				simplifiedChineseSelected.setChecked(false);

				setAppLanguage(Constants.LOCALE_HK);
			}
		});

		simplifiedChineseSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				englishSelected.setChecked(false);
				chineseSelected.setChecked(false);
				simplifiedChineseSelected.setChecked(true);

				setAppLanguage(Constants.LOCALE_CN);
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
				Intent intent = new Intent(SettingsActivity.this,
						RefreshTimeDialog.class);
				startActivity(intent);
			}
		});

		bindingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,
						MyKidsListActivity.class);
				startActivity(intent);
			}
		});

		authorizationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);

			}
		});

		updatePswBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,
						UpdatePasswordActivity.class);
				startActivity(intent);
			}
		});

		updateNicknameBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,
						UpdateNicknameActivity.class);
				startActivityForResult(
						intent,
						ActivityConstants.REQUEST_GO_TO_UPDATE_NICKNAME_ACTIVITY);
			}
		});

		termsOfServiceBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,
						WebViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("from", ActivityConstants.ACTIVITY_SETTING);
				bundle.putString("url", HttpConstants.SERVER_URL + "disclaimer");
				bundle.putString("title", termsOfService.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		privacyPolicyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,
						WebViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("from", ActivityConstants.ACTIVITY_SETTING);
				bundle.putString("url", HttpConstants.SERVER_URL + "privacy");
				bundle.putString("title", privacyPolicy.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		// auto refresh time
		refreshTimeNumber.setText(String.valueOf(SharePrefsUtils
				.getAutoUpdateTime(this, 5)));
		super.onResume();
	}

	private void setupView() {
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

		englishSelected.setChecked(false);
		chineseSelected.setChecked(false);
		simplifiedChineseSelected.setChecked(false);
		// language
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
			chineseSelected.setChecked(true);
			break;
		case Constants.LOCALE_CN:
			simplifiedChineseSelected.setChecked(true);
			break;
		default:
			englishSelected.setChecked(true);
			break;
		}

		if (SharePrefsUtils.getUserType(this).equals("T")) {
			authorizationBtn.setVisibility(View.GONE);
		}
	}

	// change the language
	public void setAppLanguage(int language) {

		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();

		switch (language) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
			config.locale = Locale.TRADITIONAL_CHINESE;
			resources.updateConfiguration(config, dm);
			break;
		case Constants.LOCALE_CN:
			config.locale = Locale.SIMPLIFIED_CHINESE;
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
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_UPDATE_NICKNAME_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_UPDATE_NICKNAME_SUCCESS) {
				setResult(ActivityConstants.RESULT_UPDATE_NICKNAME_SUCCESS);
			}
		}
	}

}
