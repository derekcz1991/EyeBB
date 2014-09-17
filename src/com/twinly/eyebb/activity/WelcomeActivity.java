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
import android.widget.ImageView;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;

public class WelcomeActivity extends Activity {
	private ImageView logo;
	// sharedPreferences
	SharedPreferences languagePreferences;
	private int language;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		languagePreferences = getSharedPreferences("soundAndVibrate",
				MODE_PRIVATE);
		language = languagePreferences.getInt("language",
				SettingsActivity.ENGLISH);

		checkLanguage();
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		logo = (ImageView) findViewById(R.id.icon);
		checkLogo();
		findViewById(R.id.sign_up).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						SignUpActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY);
			}
		});

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_LOGIN_ACTIVITY);
			}
		});

	}

	private void checkLogo() {
		if (language == 1) {
			logo.setBackground(getResources().getDrawable(R.drawable.logo_en));
		} else if (language == 2) {
			logo.setBackground(getResources().getDrawable(R.drawable.logo_cht));
		}
	}

	private void checkLanguage() {
		// TODO Auto-generated method stub
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY
				|| requestCode == ActivityConstants.REQUEST_GO_TO_LOGIN_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				//setResult(ActivityConstants.RESULT_RESULT_OK);
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
