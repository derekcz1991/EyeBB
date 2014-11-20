package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.eyebb.R;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class SignUpAskToBindDialog extends Activity {
	private LinearLayout btn_skip;
	private LinearLayout btn_bind;

	private String username;
	private String hashPassword;
	private String guardianId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_sign_up_ask_bind);

		btn_skip = (LinearLayout) findViewById(R.id.btn_cancel);
		btn_bind = (LinearLayout) findViewById(R.id.btn_confirm);

		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		hashPassword = intent.getStringExtra("hashPassword");
		guardianId = intent.getStringExtra("guardianId");

		btn_skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(SignUpAskToBindDialog.this,
						LancherActivity.class);

				// use to login
				SharePrefsUtils.setLogin(SignUpAskToBindDialog.this, true);
				SharePrefsUtils.setLoginAccount(SignUpAskToBindDialog.this,
						username);
				SharePrefsUtils.setPassowrd(SignUpAskToBindDialog.this,
						hashPassword);

				startActivity(intent);
				
				finish();
			}
		});

		btn_bind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SignUpAskToBindDialog.this,
						ChildInformationMatchingActivity.class);

				startActivity(intent);
				
				finish();

			}
		});

	}
}
