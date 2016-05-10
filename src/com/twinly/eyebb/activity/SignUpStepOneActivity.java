package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.twinly.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.RegularExpression;

public class SignUpStepOneActivity extends Activity implements OnClickListener {
	private static final int SMS_COUNT_DOWN_TOTAL_TIME = 60 * 1000; //短信定时60s
	private static final int COUNT_DOWN_INTERVAL_TIME = 1000; //倒数间隔1s

	private LinearLayout countryLayout;
	private TextView tvCountry;
	private TextView phoneCountryText;
	private int phoneLength = 100;

	private EditText phoneNumText;
	private EditText vCodeText;
	private Button vCodeBtn;
	private TextView phoneNumIcon;

	private String vCode = "";

	private boolean isCountrySelect = false;
	private boolean isPhoneNumValid = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_step_one);
		setTitle(getString(R.string.btn_sign_up));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		countryLayout = (LinearLayout) findViewById(R.id.ll_country);
		tvCountry = (TextView) findViewById(R.id.tv_country);
		phoneCountryText = (TextView) findViewById(R.id.country);

		phoneNumIcon = (TextView) findViewById(R.id.ic_signup_phone);
		phoneNumText = (EditText) findViewById(R.id.et_phone_number);
		phoneNumText.setFocusable(false);
		vCodeText = (EditText) findViewById(R.id.et_vcode);
		vCodeBtn = (Button) findViewById(R.id.btn_vcode);

		phoneNumText.setFocusable(false);
		countryLayout.setOnClickListener(this);
		phoneNumText.setOnClickListener(this);
		vCodeBtn.setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);

		phoneNumText.setInputType(InputType.TYPE_CLASS_PHONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_country: {
			Intent intent = new Intent(this, SelectRegionActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_SELECT_REGION);
			break;
		}

		case R.id.et_phone_number:
			if (isCountrySelect == false)
				Toast.makeText(this, getString(R.string.toast_select_country),
						Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_vcode:
			if (RegularExpression.isUsername(phoneNumText.getText().toString(),
					phoneLength)) {
				new CheckAccoutTask().execute();
			} else {
				Toast.makeText(this, R.string.text_error_username,
						Toast.LENGTH_SHORT).show();
				phoneNumIcon.setBackgroundResource(R.drawable.ic_verify_cross);
			}
			break;
		case R.id.btn_next: {
			if (isPhoneNumValid == false) {
				Toast.makeText(this, getString(R.string.text_invalid_username),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (vCodeText.getText().toString().equals(vCode) == false) {
				Toast.makeText(this, getString(R.string.text_invalid_vcode),
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(this, SignUpStepTwoActivity.class);
			intent.putExtra(SignUpStepTwoActivity.EXTRA_PHONE_COUNTRY,
					phoneCountryText.getText().toString());
			intent.putExtra(SignUpStepTwoActivity.EXTRA_PHONE_NUM, phoneNumText
					.getText().toString());
			startActivity(intent);
			break;
		}

		}
	}

	private class CheckAccoutTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("accName", phoneNumText.getText().toString());
			return HttpRequestUtils.post(HttpConstants.ACC_NAME_CHECK, map);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				System.out.println("retStrpost======>" + result);
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
						|| result.equals("") || result.length() == 0) {
					System.out.println("connect error");

					Toast.makeText(SignUpStepOneActivity.this,
							R.string.text_network_error, Toast.LENGTH_SHORT)
							.show();
				} else {
					if (result.equals("true")) {
						phoneNumIcon
								.setBackgroundResource(R.drawable.ic_selected);
						isPhoneNumValid = true;
						new GetVcodeTask().execute();
					} else if (result.equals("false")) {
						Toast.makeText(SignUpStepOneActivity.this,
								R.string.text_username_is_used,
								Toast.LENGTH_SHORT).show();
						phoneNumIcon
								.setBackgroundResource(R.drawable.ic_verify_cross);
						isPhoneNumValid = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private class GetVcodeTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			vCodeBtn.setEnabled(false);
			vCodeBtn.setText(R.string.sending_verify_code);
			getSMSCountDownTimer(SMS_COUNT_DOWN_TOTAL_TIME).start();
		}

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("accName", phoneNumText.getText().toString());
			return HttpRequestUtils.post(HttpConstants.GET_VCODE, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("getVcodeTask result = " + result);
			vCode = result;
		}
	}

	private CountDownTimer getSMSCountDownTimer(long millisInFuture) {
		return new CountDownTimer(millisInFuture, COUNT_DOWN_INTERVAL_TIME) {
			@Override
			public void onTick(long millisUntilFinished) {
				vCodeBtn.setText(getString(R.string.text_sign_up_retry,
						(int) millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				vCodeBtn.setText(getString(R.string.btn_verify_code));
				vCodeBtn.setEnabled(true);
			}
		};
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ActivityConstants.REQUEST_GO_TO_SIGNUP_ASK_TO_BIND_DIALOG:
			setResult(ActivityConstants.RESULT_RESULT_OK);
			finish();
			break;
		case ActivityConstants.REQUEST_GO_TO_SELECT_REGION:
			switch (resultCode) {
			case SelectRegionActivity.RESULT_CODE_CHINA:
				tvCountry.setText(getString(R.string.text_china));
				phoneCountryText.setText("+86");
				phoneLength = 11;
				isCountrySelect = true;
				phoneNumText.setFocusableInTouchMode(true);
				phoneNumText.setFocusable(true);
				phoneNumText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
				break;
			case SelectRegionActivity.RESULT_CODE_HK:
				tvCountry.setText(getString(R.string.text_hk));
				phoneCountryText.setText("+852");
				phoneLength = 8;
				isCountrySelect = true;
				phoneNumText.setFocusableInTouchMode(true);
				phoneNumText.setFocusable(true);
				phoneNumText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
				break;
			}
			break;
		}
	}
}
