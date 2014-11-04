package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.eyebb.R;

public class FeedbackDialog extends Activity {

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_feedback);

		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
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
