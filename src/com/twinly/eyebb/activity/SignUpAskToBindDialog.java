package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class SignUpAskToBindDialog extends Activity {
	private LinearLayout btn_skip;
	private LinearLayout btn_bind;

	private String userName;
	private String hashPassword;
	private String guardianId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_sign_up_ask_bind);

		btn_skip = (LinearLayout) findViewById(R.id.btn_cancel);
		btn_bind = (LinearLayout) findViewById(R.id.btn_confirm);

		Intent intent = getIntent();
		userName = intent.getStringExtra(ActivityConstants.EXTRA_USER_NAME);
		hashPassword = intent
				.getStringExtra(ActivityConstants.EXTRA_HASH_PASSWORD);
		guardianId = intent.getStringExtra(ActivityConstants.EXTRA_GUARDIAN_ID);

		btn_skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(SignUpAskToBindDialog.this,
						LancherActivity.class);
				// use to login
				SharePrefsUtils.setLogin(SignUpAskToBindDialog.this, true);
				SharePrefsUtils.setLoginAccount(SignUpAskToBindDialog.this,
						userName);
				SharePrefsUtils.setPassowrd(SignUpAskToBindDialog.this,
						hashPassword);
				startActivity(intent);
				finish();
			}
		});

		btn_bind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignUpAskToBindDialog.this,
						ChildInformationMatchingActivity.class);
				intent.putExtra(ActivityConstants.EXTRA_USER_NAME, userName);
				intent.putExtra(ActivityConstants.EXTRA_HASH_PASSWORD,
						hashPassword);
				intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID, guardianId);
				startActivity(intent);
				finish();

			}
		});

	}
}
