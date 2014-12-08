package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.GCMUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

public class LoginActivity extends Activity {
	private TextView forgetPasswordBtn;

	private EditText loginAccount;
	private EditText password;
	private String hashPassword;
	private Dialog loginDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.btn_login));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		loginAccount = (EditText) findViewById(R.id.login_account);
		loginAccount.setText(SharePrefsUtils
				.getLoginAccount(LoginActivity.this));

		password = (EditText) findViewById(R.id.password);

		forgetPasswordBtn = (TextView) findViewById(R.id.forget_password_btn);

		forgetPasswordBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// build forget password dialog
				Intent intent = new Intent(LoginActivity.this,
						ForgetPasswordDialog.class);
				startActivity(intent);

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void OnLoginClicked(View view) {

		if (TextUtils.isEmpty(loginAccount.getText().toString())) {
			return;
		} else if (TextUtils.isEmpty(password.getText().toString())) {
			return;
		}

		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				hashPassword = CommonUtils.getSHAHashValue(password.getText()
						.toString());
				// System.out.println("hashPassword = " + hashPassword);
				loginDialog = LoadingDialog.createLoadingDialog(
						LoginActivity.this, getString(R.string.toast_login));
				loginDialog.show();
			}

			@Override
			protected String doInBackground(Void... params) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("j_username", loginAccount.getText().toString());
				map.put("j_password", hashPassword);

				return HttpRequestUtils.post(HttpConstants.LOGIN, map);
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println("login result = " + result);
				try {
					JSONObject json = new JSONObject(result);
					json = json.getJSONObject(HttpConstants.JSON_KEY_USER);

					SystemUtils.clearData(LoginActivity.this);

					SharePrefsUtils.setUserId(LoginActivity.this,
							json.getLong(HttpConstants.JSON_KEY_USER_ID));
					SharePrefsUtils.setUserName(LoginActivity.this,
							json.getString(HttpConstants.JSON_KEY_USER_NAME));
					SharePrefsUtils.setUserPhone(LoginActivity.this,
							json.getString(HttpConstants.JSON_KEY_USER_PHONE));
					SharePrefsUtils.setUserType(LoginActivity.this,
							json.getString(HttpConstants.JSON_KEY_USER_TYPE));
					SharePrefsUtils.setDeviceId(LoginActivity.this,
							HttpConstants.JSON_KEY_REGISTRATION_ID);

					SharePrefsUtils.setLogin(LoginActivity.this, true);
					SharePrefsUtils.setLoginAccount(LoginActivity.this,
							loginAccount.getText().toString());
					SharePrefsUtils.setPassowrd(LoginActivity.this,
							hashPassword);

					new GCMUtils().GCMRegistration(LoginActivity.this, false);
					setResult(ActivityConstants.RESULT_RESULT_OK);

					if (loginDialog.isShowing() && loginDialog != null) {
						loginDialog.dismiss();
					}
					finish();
				} catch (JSONException e) {
					loginDialog.dismiss();
					Toast.makeText(
							LoginActivity.this,
							getString(R.string.toast_invalid_username_or_password),
							Toast.LENGTH_SHORT).show();
					System.out.println("login ---->> " + e.getMessage());
				}
			}
		}.execute();
	}
}
