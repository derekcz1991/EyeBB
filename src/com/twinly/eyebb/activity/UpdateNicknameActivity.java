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

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class UpdateNicknameActivity extends Activity {

	private EditText ed_new_nickname;
	private EditText ed_password;

	private Button btn_confrim;

	private String new_nickname = "";
	private String password = "";

	Toast toast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_update_nickname));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_update_nickname);

		ed_new_nickname = (EditText) findViewById(R.id.ed_new_nickname);
		ed_password = (EditText) findViewById(R.id.ed_password);

		btn_confrim = (Button) findViewById(R.id.btn_confirm);

		btn_confrim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(postUpdateNicknameToServerRunnable).start();
			}
		});
	}

	Runnable postUpdateNicknameToServerRunnable = new Runnable() {
		@Override
		public void run() {
			new_nickname = ed_new_nickname.getText().toString();
			password = ed_password.getText().toString();

			if (new_nickname.length() > 0 && password.length() > 0) {
				if (isPassword(password)) {
					postUpdateNicknameToServer();
				} else {
					Message msg = handler.obtainMessage();
					msg.what = Constants.PASSWORD_FORMAT_ERROR;
					handler.sendMessage(msg);
				}
			} else if (new_nickname.length() > 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_PASSWORD;
				handler.sendMessage(msg);
			} else if (password.length() > 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_NICKNAME;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_NICKNAME;
				handler.sendMessage(msg);
			}

		}
	};

	@SuppressLint("ShowToast")
	private void postUpdateNicknameToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + new_nickname + " " + password);

		map.put("password", CommonUtils.getSHAHashValue(password));
		map.put("newNickname", new_nickname);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(
					UpdateNicknameActivity.this, HttpConstants.CHANGE_NICKNAME,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals(HttpConstants.SERVER_RETURN_T)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.UPDATE_NICKNAME_SUCCESS;
					handler.sendMessage(msg);
					SharePrefsUtils.setUserName(UpdateNicknameActivity.this,
							new_nickname);
					setResult(ActivityConstants.RESULT_UPDATE_NICKNAME_SUCCESS);
					finish();
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.UPDATE_NICKNAME_FAIL_WRONG_PASSWORD;
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

			case Constants.CONNECT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_network_error, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case Constants.UPDATE_NICKNAME_SUCCESS:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_update_nickname_successful,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_CONTENT:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_something, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_PASSWORD:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_password, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_NICKNAME:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_nickname, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.UPDATE_NICKNAME_FAIL_WRONG_PASSWORD:
				Intent intent = new Intent(UpdateNicknameActivity.this,
						OldPasswordIncorrectDialog.class);
				startActivity(intent);

				break;

			case Constants.PASSWORD_FORMAT_ERROR:
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
