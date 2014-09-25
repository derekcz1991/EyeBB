package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class LancherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setLanguage();
		new HttpRequestUtils();

		if (SharePrefsUtils.isLogin(this)) {
			new loginBackground().execute();
		} else {
			Intent intent = new Intent(this, WelcomeActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY);
			finish();
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

	private class loginBackground extends AsyncTask<Void, Void, String> {

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
			try {
				new JSONObject(result);
				Intent intent = new Intent(LancherActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} catch (JSONException e) {

			}
		}
	}
}
