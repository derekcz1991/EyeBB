package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.utils.CommonUtils;

public class ChildDialog extends Activity {

	private TextView phone;
	private TextView name;
	private TextView areaName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_child);

		phone = (TextView) findViewById(R.id.phone);
		name = (TextView) findViewById(R.id.name);
		areaName = (TextView) findViewById(R.id.area_name);

		phone.setText(getIntent().getStringExtra("phone"));
		name.setText(getIntent().getStringExtra("name"));
		areaName.setText(getIntent().getStringExtra("area_name"));

		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.phone).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phone.getText().toString().trim().length() != 0) {

					Uri telUri = Uri.parse("tel:" + phone.getText());
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
							Intent intent = new Intent(ChildDialog.this,
									BeepDialog.class);
							startActivity(intent);
						}
					}
				});

	}
}
