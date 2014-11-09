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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class SignUpActivity extends Activity {
	private Button btnContinue;
	private Button btnSkip;

	private EditText ed_username;
	private EditText ed_email;
	private EditText ed_password;
	private EditText ed_nickname;
	private EditText ed_phone;
	private String username;
	private String email;
	private String password;
	private String nickname;
	private String phone;
	private String regType;

	private TextView tv_username;
	private TextView tv_email;
	private TextView tv_password;
	private TextView tv_nickname;
	private TextView tv_phone;

	private boolean usernameFlag = false;
	private boolean regSuccessFlag = false;

	public static final int CHECK_ACC_SUCCESS = 1;
	public static final int CHECK_ACC_FALSE = 2;
	public static final int CHECK_ACC_ERROR = 4;
	public static final int CONNECT_ERROR = 3;
	public static final int REG_SUCCESSFULLY = 5;

	private String hashPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		setTitle(getString(R.string.btn_sign_up));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		// username password email
		ed_username = (EditText) findViewById(R.id.ed_username);
		ed_email = (EditText) findViewById(R.id.ed_email);
		ed_password = (EditText) findViewById(R.id.ed_password);
		ed_nickname = (EditText) findViewById(R.id.ed_nickname);
		ed_phone = (EditText) findViewById(R.id.ed_phone_number);

		tv_username = (TextView) findViewById(R.id.ic_signup_name);
		tv_email = (TextView) findViewById(R.id.ic_signup_email);
		tv_password = (TextView) findViewById(R.id.ic_signup_pw);
		tv_nickname = (TextView) findViewById(R.id.ic_signup_nickname);
		tv_phone = (TextView) findViewById(R.id.ic_signup_phone);

		btnContinue = (Button) findViewById(R.id.btn_signup_continue);
		btnSkip = (Button) findViewById(R.id.btn_signup_skip);

		ed_username.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (ed_username.hasFocus() == false) {
					username = ed_username.getText().toString();
					if (isUsername(username)) {
						new Thread(postAccNameCheckToServerRunnable).start();

					} else {
						Message msg = handler.obtainMessage();
						msg.what = CHECK_ACC_ERROR;
						handler.sendMessage(msg);
					}
				}

			}
		});

		btnContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Regex for username password username
				username = ed_username.getText().toString();
				email = ed_email.getText().toString();
				password = ed_password.getText().toString();
				nickname = ed_nickname.getText().toString();
				phone = ed_phone.getText().toString();
				regType = "P";

				System.out.println("username + email + password" + username
						+ " " + email + " " + password);
				if (username != null && username.length() > 0) {
					if (nickname != null && nickname.length() > 0) {

						if (isPassword(password)) {
							// editor.putString("usrname", username);
							// editor.putString("email", email);
							// editor.putString("password", password);
							// editor.commit();
							if (isEmail(email) || phone.length() > 0) {
								if (phone != null || phone.length() > 0) {
									SharePrefsUtils.setSignUpEmail(
											SignUpActivity.this, email);
									SharePrefsUtils.setSignUpNickname(
											SignUpActivity.this, nickname);
									SharePrefsUtils.setSignUpPassword(
											SignUpActivity.this, password);
									SharePrefsUtils.setSignUpPhoneNumber(
											SignUpActivity.this, phone);
									SharePrefsUtils.setSignUpUsername(
											SignUpActivity.this, username);

									if (usernameFlag) {
										new Thread(
												postRegParentsCheckToServerRunnable)
												.start();

									} else {
										new Thread(
												postAccNameCheckToServerRunnable)
												.start();
									}

								}
							} else {

								Toast.makeText(SignUpActivity.this,
										R.string.text_fill_email_or_phone,
										Toast.LENGTH_LONG).show();
								tv_email.setBackground(getResources()
										.getDrawable(R.drawable.ic_radar_missed));
								tv_phone.setBackground(getResources()
										.getDrawable(R.drawable.ic_radar_missed));

							}
						} else {
							Toast.makeText(SignUpActivity.this,
									R.string.text_error_password,
									Toast.LENGTH_LONG).show();
							tv_password.setBackground(getResources()
									.getDrawable(R.drawable.ic_radar_missed));
						}

					} else {
						Toast.makeText(SignUpActivity.this,
								R.string.text_error_nickname, Toast.LENGTH_LONG)
								.show();
						tv_nickname.setBackground(getResources().getDrawable(
								R.drawable.ic_radar_missed));
					}
				} else {
					Toast.makeText(SignUpActivity.this,
							R.string.text_error_username, Toast.LENGTH_LONG)
							.show();
					tv_username.setBackground(getResources().getDrawable(
							R.drawable.ic_radar_missed));
				}

			}
		});

		btnSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username = ed_username.getText().toString();
				email = ed_email.getText().toString();
				password = ed_password.getText().toString();
				nickname = ed_nickname.getText().toString();
				phone = ed_phone.getText().toString();
				regType = "G";

				System.out.println("username + email + password" + username
						+ " " + email + " " + password);

				if (username != null && username.length() > 0) {

					if (nickname != null && nickname.length() > 0) {

						if (isPassword(password)) {
							// editor.putString("usrname", username);
							// editor.putString("email", email);
							// editor.putString("password", password);
							// editor.commit();
							if (isEmail(email) || phone.length() > 0) {
								if (phone != null || phone.length() > 0) {
									SharePrefsUtils.setSignUpEmail(
											SignUpActivity.this, email);
									SharePrefsUtils.setSignUpNickname(
											SignUpActivity.this, nickname);
									SharePrefsUtils.setSignUpPassword(
											SignUpActivity.this, password);
									SharePrefsUtils.setSignUpPhoneNumber(
											SignUpActivity.this, phone);
									SharePrefsUtils.setSignUpUsername(
											SignUpActivity.this, username);

									if (usernameFlag) {
										new Thread(
												postRegParentsCheckToServerRunnable)
												.start();

									} else {
										new Thread(
												postAccNameCheckToServerRunnable)
												.start();
									}
								}
							} else {

								Toast.makeText(SignUpActivity.this,
										R.string.text_fill_email_or_phone,
										Toast.LENGTH_LONG).show();
								tv_email.setBackground(getResources()
										.getDrawable(R.drawable.ic_radar_missed));
								tv_phone.setBackground(getResources()
										.getDrawable(R.drawable.ic_radar_missed));

							}
						} else {
							Toast.makeText(SignUpActivity.this,
									R.string.text_error_password,
									Toast.LENGTH_LONG).show();
							tv_password.setBackground(getResources()
									.getDrawable(R.drawable.ic_radar_missed));
						}

					} else {
						Toast.makeText(SignUpActivity.this,
								R.string.text_error_nickname, Toast.LENGTH_LONG)
								.show();
						tv_nickname.setBackground(getResources().getDrawable(
								R.drawable.ic_radar_missed));
					}

				} else {
					Toast.makeText(SignUpActivity.this,
							R.string.text_error_username, Toast.LENGTH_LONG)
							.show();
					tv_username.setBackground(getResources().getDrawable(
							R.drawable.ic_radar_missed));
				}
			}
		});

	}

	Runnable postAccNameCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postCheckAccToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postCheckAccToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("username=>" + username);
		map.put("accName", username);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(HttpConstants.ACC_NAME_CHECK,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {

				if (retStr.equals("true")) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_SUCCESS;
					handler.sendMessage(msg);
					usernameFlag = true;
				} else if (retStr.equals("false")) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_FALSE;
					handler.sendMessage(msg);
					usernameFlag = false;

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	Runnable postRegParentsCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postRegParentsToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postRegParentsToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("username=>" + username + " " + nickname + " "
				+ password + " " + email + " " + phone + " " + regType);

		hashPassword = CommonUtils.getSHAHashValue(password);
		map.put("accName", username);
		map.put("name", nickname);
		map.put("password", hashPassword);
		map.put("email", email);
		map.put("phoneNum", phone);
		map.put("regType", regType);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(HttpConstants.REG_PARENTS,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {

				if (!retStr.equals("false") && retStr.length() > 0) {
					Message msg = handler.obtainMessage();
					msg.what = REG_SUCCESSFULLY;
					handler.sendMessage(msg);
					regSuccessFlag = true;

					if (regType.equals("P")) {
						// binding
						Intent intent = new Intent(SignUpActivity.this,
								ChildInformationMatchingActivity.class);
						//intent.putExtra("guardianId", retStr);
						SharePrefsUtils.setSignUpGuardianId(SignUpActivity.this, retStr);
						startActivity(intent);
						
						SharePrefsUtils.setSignUpUsername(SignUpActivity.this, username);
						SharePrefsUtils.setSignUpPassword(SignUpActivity.this, hashPassword);
	
						
					} else if (regType.equals("G")) {
						// login
						Intent intent = new Intent(SignUpActivity.this,
								LancherActivity.class);

						//use to login
						SharePrefsUtils.setLogin(SignUpActivity.this, true);
						SharePrefsUtils.setLoginAccount(SignUpActivity.this,
								username);
						SharePrefsUtils.setPassowrd(SignUpActivity.this,
								hashPassword);
						// intent.putExtra("username", username);
						// intent.putExtra("password", password);
						startActivity(intent);
					}

				} else if (retStr.equals("false")) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_FALSE;
					handler.sendMessage(msg);
					regSuccessFlag = false;

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHECK_ACC_SUCCESS:
				tv_username.setBackground(getResources().getDrawable(
						R.drawable.ic_selected));
				break;

			case CHECK_ACC_FALSE:
				Toast.makeText(SignUpActivity.this,
						R.string.text_username_is_used, Toast.LENGTH_LONG)
						.show();
				tv_username.setBackground(getResources().getDrawable(
						R.drawable.ic_radar_missed));
				break;

			case CHECK_ACC_ERROR:
				Toast.makeText(SignUpActivity.this,
						R.string.text_error_username, Toast.LENGTH_LONG).show();
				tv_username.setBackground(getResources().getDrawable(
						R.drawable.ic_radar_missed));
				break;

			case CONNECT_ERROR:
				Toast.makeText(SignUpActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG)
						.show();

				break;

			case REG_SUCCESSFULLY:
				Toast.makeText(SignUpActivity.this,
						R.string.text_register_successfully, Toast.LENGTH_LONG)
						.show();
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

	public static boolean isUsername(String usrname) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
		Matcher m = p.matcher(usrname);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static boolean isPassword(String password) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{8,30}$");
		Matcher m = p.matcher(password);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static boolean isEmail(String email) {
		Pattern p = Pattern
				.compile("^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
		Matcher m = p.matcher(email);
		System.out.println(m.matches() + "---");
		return m.matches();
	}
}
