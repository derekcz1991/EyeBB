package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class LancherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
