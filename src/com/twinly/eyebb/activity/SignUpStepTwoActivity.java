package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.JPushUtils;
import com.twinly.eyebb.utils.RegularExpression;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

/**
 * @author eyebb team
 * 
 * @category SignUpActivity
 * 
 *           SignUpActivity will let the user register their own information.
 *           this information will post to the server.
 */
public class SignUpStepTwoActivity extends Activity {
	public static final String EXTRA_PHONE_COUNTRY = "phone_country";
	public static final String EXTRA_PHONE_NUM = "phone_num";

	private Button signupBtn;

	private TextView countryText;
	private TextView phoneNumText;

	private EditText emailText;
	private EditText passwordText;
	private EditText nicknameText;

	private String phoneCountry;
	private String phoneNum;
	private String email;
	private String password;
	private String nickName;

	private TextView emailIcon;
	private TextView passwordIcon;
	private TextView nicknameIcon;

	private String hashPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_step_two);
		setTitle(getString(R.string.btn_sign_up));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		phoneCountry = getIntent().getStringExtra(EXTRA_PHONE_COUNTRY);
		phoneNum = getIntent().getStringExtra(EXTRA_PHONE_NUM);

		countryText = (TextView) findViewById(R.id.country);
		phoneNumText = (TextView) findViewById(R.id.et_phone_number);

		emailText = (EditText) findViewById(R.id.et_email);
		passwordText = (EditText) findViewById(R.id.et_password);
		nicknameText = (EditText) findViewById(R.id.et_nickname);

		emailIcon = (TextView) findViewById(R.id.ic_signup_email);
		passwordIcon = (TextView) findViewById(R.id.ic_signup_pw);
		nicknameIcon = (TextView) findViewById(R.id.ic_signup_nickname);

		signupBtn = (Button) findViewById(R.id.btn_signup);

		countryText.setText(phoneCountry);
		phoneNumText.setText(phoneNum);

		signupBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSignUpClicked();
			}
		});
	}

	private void onSignUpClicked() {
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		nickName = nicknameText.getText().toString();

		if (nickName != null && nickName.length() > 0) {
			if (RegularExpression.isPassword(password)) {
				new SignUpTask().execute();
			} else {
				Toast.makeText(this, R.string.text_error_password,
						Toast.LENGTH_SHORT).show();
				passwordIcon.setBackgroundResource(R.drawable.ic_cross);
			}
		} else {
			Toast.makeText(this, R.string.text_error_nickname,
					Toast.LENGTH_SHORT).show();
			nicknameIcon.setBackgroundResource(R.drawable.ic_cross);
		}
	}

	private class SignUpTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			hashPassword = CommonUtils.getSHAHashValue(password);
			map.put("accName", phoneNum);
			map.put("name", nickName);
			map.put("password", hashPassword);
			map.put("email", email);
			map.put("phoneNum", phoneNum);
			map.put("areaCode", phoneCountry);
			return HttpRequestUtils.post(HttpConstants.REG_PARENTS, map);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				System.out.println("signUp======>" + result);
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
						|| result.equals("") || result.length() == 0) {
					System.out.println("connect error");
					Toast.makeText(SignUpStepTwoActivity.this,
							R.string.text_network_error, Toast.LENGTH_SHORT)
							.show();
				} else {
					if (!result.equals(HttpConstants.SERVER_RETURN_F)
							&& result.length() > 0 && result.length() < 20) {
						SystemUtils.clearData(SignUpStepTwoActivity.this);
						SharePrefsUtils.setLogin(SignUpStepTwoActivity.this,
								true);
						SharePrefsUtils.setLoginAccount(
								SignUpStepTwoActivity.this, phoneNum);
						SharePrefsUtils.setPassowrd(SignUpStepTwoActivity.this,
								hashPassword);

						// register to GCM server
						//new GCMUtils().GCMRegistration(SignUpActivity.this, "");
						JPushUtils
								.updateRegistrationId(SignUpStepTwoActivity.this);

						Intent intent = new Intent(SignUpStepTwoActivity.this,
								SignUpAskToBindDialog.class);
						intent.putExtra(ActivityConstants.EXTRA_REGION_CODE,
								countryText.getText().toString());
						intent.putExtra(ActivityConstants.EXTRA_USER_NAME,
								phoneNum);
						intent.putExtra(ActivityConstants.EXTRA_HASH_PASSWORD,
								hashPassword);
						intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID,
								Long.parseLong(result));
						startActivityForResult(
								intent,
								ActivityConstants.REQUEST_GO_TO_SIGNUP_ASK_TO_BIND_DIALOG);

					} else {
						Toast.makeText(SignUpStepTwoActivity.this,
								R.string.text_network_error, Toast.LENGTH_SHORT)
								.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(SignUpStepTwoActivity.this,
						R.string.text_network_error, Toast.LENGTH_SHORT).show();
			}
		}
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
