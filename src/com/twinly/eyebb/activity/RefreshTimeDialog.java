package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class RefreshTimeDialog extends Activity {

	private EditText enterMail;
	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_refresh_time);

		enterMail = (EditText) findViewById(R.id.enter_mail);
		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// keyboard
				if (enterMail.getText().toString() != null
						&& enterMail.getText().toString().length() > 0) {
					if (Integer.parseInt(enterMail.getText().toString()) > 0) {
						SharePrefsUtils.setRefreshTime(RefreshTimeDialog.this,
								enterMail.getText().toString());
						Intent intent = new Intent(RefreshTimeDialog.this,
								SettingsActivity.class);

						startActivity(intent);
						if (SettingsActivity.instance != null) {
							SettingsActivity.instance.finish();
						}
						finish();

					}
				} else {
					enterMail.setHint(getResources().getString(
							R.string.text_fill_in_something));
					enterMail.setHintTextColor(getResources().getColor(
							R.color.red));
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
