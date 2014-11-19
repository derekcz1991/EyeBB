package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class UpdatePasswordActivity extends Activity {

	private EditText ed_oldPassword;
	private EditText ed_newPassword;
	private EditText ed_newRepeatPassword;

	private Button btn_confrim;

	private String oldPassword;
	private String newPassword;
	private String newRepeatPassword;
	Toast toast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_update_password));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_update_password);

		ed_oldPassword = (EditText) findViewById(R.id.ed_old_password);
		ed_newPassword = (EditText) findViewById(R.id.ed_new_password);
		ed_newRepeatPassword = (EditText) findViewById(R.id.ed_repeat_new_password);
		btn_confrim = (Button) findViewById(R.id.btn_confirm);

		btn_confrim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(postUpdatePasswordToServerRunnable).start();
			}
		});
	}

	Runnable postUpdatePasswordToServerRunnable = new Runnable() {
		@Override
		public void run() {
			oldPassword = ed_oldPassword.getText().toString();
			newPassword = ed_newPassword.getText().toString();
			newRepeatPassword = ed_newRepeatPassword.getText().toString();

			if (oldPassword.length() > 0 && newPassword.length() > 0
					&& newRepeatPassword.length() > 0) {
				if (isPassword(newPassword)) {
					if (isPassword(newRepeatPassword))
						if (newPassword.equals(newRepeatPassword)) {
							postUpdatePasswordToServer();
						} else {
							Message msg = handler.obtainMessage();
							msg.what = BleDeviceConstants.TWO_DIFFERENT_PASSWORD_SUCCESS;
							handler.sendMessage(msg);
						}
					else {
						Message msg = handler.obtainMessage();
						msg.what = BleDeviceConstants.PASSWORD_FORMAT_ERROR;
						handler.sendMessage(msg);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.PASSWORD_FORMAT_ERROR;
					handler.sendMessage(msg);
				}

			} else {
				Message msg = handler.obtainMessage();
				msg.what = BleDeviceConstants.NULL_FEEDBAKC_CONTENT;
				handler.sendMessage(msg);
			}

		}
	};

	@SuppressLint("ShowToast")
	private void postUpdatePasswordToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + oldPassword + " " + newPassword + " "
				+ newRepeatPassword);

		map.put("oldPassword", CommonUtils.getSHAHashValue(oldPassword));
		map.put("newPassword", CommonUtils.getSHAHashValue(newPassword));

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(
					UpdatePasswordActivity.this, HttpConstants.UPDATE_PASSWORD,
					map);
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
					msg.what = BleDeviceConstants.UPDATE_PASSWORD_SUCCESS;
					handler.sendMessage(msg);

					finish();
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.OLD_PASSWORD_ERROR;
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
			case BleDeviceConstants.UPDATE_PASSWORD_SUCCESS:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_update_password_successful,
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

			case BleDeviceConstants.TWO_DIFFERENT_PASSWORD_SUCCESS:
				toast = Toast
						.makeText(getApplicationContext(),
								R.string.text_two_password_different,
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case BleDeviceConstants.OLD_PASSWORD_ERROR:
				Intent intent = new Intent(UpdatePasswordActivity.this,
						OldPasswordIncorrectDialog.class);
				startActivity(intent);

				break;

			case BleDeviceConstants.PASSWORD_FORMAT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_error_password, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				break;
			}

		}
	};

	public static boolean isPassword(String password) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,20}$");
		Matcher m = p.matcher(password);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
