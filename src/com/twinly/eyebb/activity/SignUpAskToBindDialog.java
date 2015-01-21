package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.bluetooth.BLEUtils;
import com.twinly.eyebb.constant.ActivityConstants;

public class SignUpAskToBindDialog extends Activity {
	private LinearLayout btn_skip;
	private LinearLayout btn_bind;

	private long guardianId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_sign_up_ask_bind);

		btn_skip = (LinearLayout) findViewById(R.id.btn_cancel);
		btn_bind = (LinearLayout) findViewById(R.id.btn_confirm);

		Intent intent = getIntent();
		guardianId = intent
				.getLongExtra(ActivityConstants.EXTRA_GUARDIAN_ID, 0);

		btn_skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignUpAskToBindDialog.this,
						LancherActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});

		btn_bind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (BLEUtils.isSupportBle(SignUpAskToBindDialog.this)) {
					Intent intent = new Intent(SignUpAskToBindDialog.this,
							ChildInformationMatchingActivity.class);
					intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID,
							guardianId);
					startActivityForResult(
							intent,
							ActivityConstants.REQUEST_GO_TO_CHILD_INFO_MATCHING_ACTIVITY);
					finish();
				} else {
					Toast.makeText(SignUpAskToBindDialog.this,
							R.string.text_ble_not_supported, Toast.LENGTH_LONG)
							.show();
				}

			}
		});

	}
}
