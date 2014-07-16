package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.twinly.eyebb.R;
import com.twinly.eyebb.utils.CommonUtils;

public class MainDialog extends Activity {

	private String phoneNumber = "1234567890";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_bb);

		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.phone).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phoneNumber.trim().length() != 0) {

					Uri telUri = Uri.parse("tel:" + phoneNumber);
					Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
					startActivity(intent);
				}
			}
		});

		findViewById(R.id.maindialog_beep_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(MainDialog.this,
									BeepDialog.class);
							startActivity(intent);
						}
					}
				});

	}
}
