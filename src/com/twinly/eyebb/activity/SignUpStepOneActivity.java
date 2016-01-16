package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.RegularExpression;

public class SignUpStepOneActivity extends Activity implements OnClickListener {

	private LinearLayout llCountry;
	private TextView tvCountry;
	private TextView country;
	private int phoneLength = 100;

	private EditText etUsername;
	private EditText etVcode;
	private String phone;
	private String userName;

	private TextView tvUsername;
	private boolean isCountrySelect = false;
	private boolean userNameFlag = false;

	public static final int CHECK_ACC_SUCCESS = 1;
	public static final int CHECK_ACC_FALSE = 2;
	public static final int CHECK_ACC_ERROR = 4;
	public static final int CONNECT_ERROR = 3;
	public static final int REG_SUCCESSFULLY = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_step_one);
		setTitle(getString(R.string.btn_sign_up));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		llCountry = (LinearLayout) findViewById(R.id.ll_country);
		tvCountry = (TextView) findViewById(R.id.tv_country);
		country = (TextView) findViewById(R.id.country);

		tvUsername = (TextView) findViewById(R.id.ic_signup_phone);
		etUsername = (EditText) findViewById(R.id.et_phone_number);
		etUsername.setFocusable(false);
		etVcode = (EditText) findViewById(R.id.et_vcode);

		etUsername.setFocusable(false);
		llCountry.setOnClickListener(this);
		etUsername.setOnClickListener(this);

		etUsername.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (etUsername.hasFocus() == false) {
					userName = etUsername.getText().toString();
					if (RegularExpression.isUsername(userName, phoneLength)) {
						new Thread(postAccNameCheckToServerRunnable).start();
					} else {
						Message msg = handler.obtainMessage();
						msg.what = CHECK_ACC_ERROR;
						handler.sendMessage(msg);
					}
				}

			}
		});

		etUsername.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				editStart = etUsername.getSelectionStart();
				editEnd = etUsername.getSelectionEnd();
				if (temp.length() > phoneLength) {
					s.delete(editStart - 1, editEnd);
					int tempSelection = editStart;
					etUsername.setText(s);
					etUsername.setSelection(tempSelection);
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_country:
			Intent intent = new Intent(this, SelectRegionActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_SELECT_REGION);
			break;
		case R.id.et_phone_number:
			if (isCountrySelect == false)
				Toast.makeText(this, getString(R.string.toast_select_country),
						Toast.LENGTH_SHORT).show();
			break;
		}
	}

	Runnable postAccNameCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postCheckAccToServer();
		}
	};

	private void postCheckAccToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("accName", userName);
		try {
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
					userNameFlag = true;
				} else if (retStr.equals("false")) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_FALSE;
					handler.sendMessage(msg);
					userNameFlag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHECK_ACC_SUCCESS:
				tvUsername.setBackgroundResource(R.drawable.ic_selected);
				break;

			case CHECK_ACC_FALSE:
				Toast.makeText(SignUpStepOneActivity.this,
						R.string.text_username_is_used, Toast.LENGTH_SHORT)
						.show();
				tvUsername.setBackgroundResource(R.drawable.ic_verify_cross);
				break;

			case CHECK_ACC_ERROR:
				Toast.makeText(SignUpStepOneActivity.this,
						R.string.text_error_username, Toast.LENGTH_SHORT)
						.show();
				tvUsername.setBackgroundResource(R.drawable.ic_verify_cross);
				break;

			case CONNECT_ERROR:
				Toast.makeText(SignUpStepOneActivity.this,
						R.string.text_network_error, Toast.LENGTH_SHORT).show();

				break;

			case REG_SUCCESSFULLY:
				Toast.makeText(SignUpStepOneActivity.this,
						R.string.text_register_successfully, Toast.LENGTH_SHORT)
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
				country.setText("+86");
				phoneLength = 11;
				isCountrySelect = true;
				etUsername.setFocusableInTouchMode(true);
				etUsername.setFocusable(true);
				break;
			case SelectRegionActivity.RESULT_CODE_HK:
				tvCountry.setText(getString(R.string.text_hk));
				country.setText("+852");
				phoneLength = 8;
				isCountrySelect = true;
				etUsername.setFocusableInTouchMode(true);
				etUsername.setFocusable(true);
				break;
			}
			break;
		}
	}
}
