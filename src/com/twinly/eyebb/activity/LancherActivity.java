package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class LancherActivity extends Activity {
	private ImageView logo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancher);
		
		logo = (ImageView) findViewById(R.id.icon);
		checkLogo();
		
		setLanguage();
		new HttpRequestUtils();

		if (SharePrefsUtils.isLogin(this)) {
			new AutoLoginTask().execute();
		} else {
			Intent intent = new Intent(this, WelcomeActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY);
			finish();
		}
	}
	
	private void checkLogo() {
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_cht));
			break;
		default:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_en));
			break;
		}
	}

	private void setLanguage() {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			config.locale = Locale.TRADITIONAL_CHINESE;
			resources.updateConfiguration(config, dm);
			break;
		default:
			config.locale = Locale.ENGLISH;
			resources.updateConfiguration(config, dm);
			break;
		}
	}

	private class AutoLoginTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("j_username",
					SharePrefsUtils.getLoginAccount(LancherActivity.this));
			map.put("j_password",
					SharePrefsUtils.getPassword(LancherActivity.this));
			map.put("kId", String.valueOf(SharePrefsUtils
					.getKindergartenId(LancherActivity.this)));

			return HttpRequestUtils.post(HttpConstants.LOGIN, map);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
				goBackToLogin();
				return;
			}
			try {
				new JSONObject(result);
				Intent intent = new Intent(LancherActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} catch (JSONException e) {
				goBackToLogin();
			}
		}
	}

	private void goBackToLogin() {
		Toast.makeText(this, getString(R.string.toast_login_failed),
				Toast.LENGTH_LONG).show();
		Intent intent = new Intent(LancherActivity.this, WelcomeActivity.class);
		startActivityForResult(intent,
				ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY);
		finish();
	}
}
