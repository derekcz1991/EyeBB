package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.database.DBPerformance;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

@SuppressLint("InflateParams")
public class LoginActivity extends Activity {
	private TextView forgetPasswordBtn;
	private LayoutInflater inflater;
	private TextView backBtn;
	private AlertDialog passwordDialog;
	private EditText edEmail;
	private EditText loginAccount;
	private EditText password;
	private String hashPassword;
	private Dialog loginDialog;

	private String userAccout;

	private String kindergartenNameEn;
	private String kindergartenNameSc;
	private String kindergartenNameTc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.btn_login));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		// when login clear the guardian id
		SharePrefsUtils.setSignUpGuardianId(LoginActivity.this, "");

		loginAccount = (EditText) findViewById(R.id.login_account);
		loginAccount.setText(SharePrefsUtils
				.getLoginAccount(LoginActivity.this));

		password = (EditText) findViewById(R.id.password);

		forgetPasswordBtn = (TextView) findViewById(R.id.forget_password_btn);
		inflater = LayoutInflater.from(this);
		forgetPasswordBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// build forget password dialog
				AlertDialog.Builder forgetPassword = new AlertDialog.Builder(
						LoginActivity.this);
				View layout = inflater.inflate(R.layout.dialog_forget_password,
						null);

				edEmail = (EditText) layout.findViewById(R.id.enter_mail);
				edEmail.setFocusable(true);
				edEmail.setFocusableInTouchMode(true);
				edEmail.requestFocus();
				openKeyBoard();

				forgetPassword.setView(layout);

				backBtn = (TextView) layout.findViewById(R.id.back_confirm);

				backBtn.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						closeKeyBoard();
						passwordDialog.dismiss();

						new Thread(postResetPasswordToServerRunnable).start();
					}
				});

				passwordDialog = forgetPassword.create();
				passwordDialog.show();
			}
		});

	}

	Runnable postResetPasswordToServerRunnable = new Runnable() {
		@Override
		public void run() {
			userAccout = edEmail.getText().toString();

			if (userAccout.length() > 0) {

				postResetPasswordToServer();

			} else {
				Message msg = handler.obtainMessage();
				msg.what = BleDeviceConstants.NULL_FEEDBAKC_CONTENT;
				handler.sendMessage(msg);
			}

		}

	};

	@SuppressLint("ShowToast")
	private void postResetPasswordToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + userAccout);

		map.put("accName", userAccout);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(LoginActivity.this,
					HttpConstants.RESET_PASSWORD, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = BleDeviceConstants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals(HttpConstants.SERVER_RETURN_T)) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.PASSWORD_RESET_SUCCESS;
					handler.sendMessage(msg);

					
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.ACCOUNT_NOT_EXIST;
					handler.sendMessage(msg);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			Toast toast = null;
			switch (msg.what) {

			case BleDeviceConstants.CONNECT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_network_error, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case BleDeviceConstants.PASSWORD_RESET_SUCCESS:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_feed_back_successful,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case BleDeviceConstants.NULL_FEEDBAKC_CONTENT:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_something, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case BleDeviceConstants.ACCOUNT_NOT_EXIST:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_user_do_not_exist, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				break;
			}

		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void openKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edEmail, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public void closeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edEmail.getWindowToken(), 0);
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
				System.out.println("hashPassword = " + hashPassword);
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
					new GetUserTypeTask(json).execute();
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

	private class GetUserTypeTask extends AsyncTask<Void, Void, String> {

		private JSONObject loginJSON;

		public GetUserTypeTask(JSONObject loginJSON) {
			this.loginJSON = loginJSON;
		}

		@Override
		protected String doInBackground(Void... params) {
			return HttpRequestUtils.get(HttpConstants.LOGIN_INFO, null);
		}

		@Override
		protected void onPostExecute(String result) {
			loginDialog.dismiss();
			System.out.println("login info result = " + result);
			try {
				JSONArray childJSONList = loginJSON
						.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_LIST);
				// is a new account
				if (loginAccount
						.getText()
						.toString()
						.equals(SharePrefsUtils
								.getLoginAccount(LoginActivity.this)) == false) {
					DBActivityInfo.deleteTable(LoginActivity.this);
					DBChildren.deleteTable(LoginActivity.this);
					DBPerformance.deleteTable(LoginActivity.this);
					DBNotifications.deleteTable(LoginActivity.this);
				}
				getAllChildren(childJSONList);

				JSONObject json = new JSONObject(result);
				SharePrefsUtils.setUserType(LoginActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_TYPE));
				SharePrefsUtils.setUserName(LoginActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_NAME));
				SharePrefsUtils.setUserPhone(LoginActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_PHONE));

				SharePrefsUtils.setLogin(LoginActivity.this, true);
				SharePrefsUtils.setLoginAccount(LoginActivity.this,
						loginAccount.getText().toString());
				SharePrefsUtils.setPassowrd(LoginActivity.this, hashPassword);
				SharePrefsUtils.setKindergartenNameEn(LoginActivity.this,
						kindergartenNameEn);
				SharePrefsUtils.setKindergartenNameSc(LoginActivity.this,
						kindergartenNameSc);
				SharePrefsUtils.setKindergartenNameTc(LoginActivity.this,
						kindergartenNameTc);

				setResult(ActivityConstants.RESULT_RESULT_OK);
				finish();
			} catch (JSONException e) {
				Toast.makeText(LoginActivity.this,
						getString(R.string.toast_invalid_username_or_password),
						Toast.LENGTH_SHORT).show();
				System.out.println("login info---->> " + e.getMessage());
			}
		}

	}

	private void getAllChildren(JSONArray childJSONList) throws JSONException {
		for (int i = 0; i < childJSONList.length(); i++) {
			JSONObject object = (JSONObject) childJSONList.get(i);
			Child child = new Child(
					object.getInt(HttpConstants.JSON_KEY_CHILD_ID),
					object.getString(HttpConstants.JSON_KEY_CHILD_NAME),
					object.getString(HttpConstants.JSON_KEY_CHILD_ICON));
			child.setMacAddress(object
					.getString(HttpConstants.JSON_KEY_CHILD_MAC_ADDRESS));
			// get parents' phone
			if (CommonUtils.isNotNull(object
					.getString(HttpConstants.JSON_KEY_PARENTS))) {
				JSONArray parents = object
						.getJSONArray(HttpConstants.JSON_KEY_PARENTS);
				if (parents != null) {
					JSONObject parent = (JSONObject) parents.get(0);
					child.setPhone(parent
							.getString(HttpConstants.JSON_KEY_PARENTS_PHONE));
				}
			}

			DBChildren.insert(LoginActivity.this, child);
			// set the first child as the current reporting child
			if (i == 0) {
				SharePrefsUtils.setReportChildId(LoginActivity.this,
						child.getChildId());
			}
		}
	}

}
