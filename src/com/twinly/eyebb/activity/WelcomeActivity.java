package com.twinly.eyebb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;

public class WelcomeActivity extends Activity {
	private ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		findViewById(R.id.sign_up).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						SignUpActivity.class);
				startActivityForResult(intent,
						Constants.REQUEST_GO_TO_SIGN_UP_ACTIVITY);
			}
		});

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivityForResult(intent,
						Constants.REQUEST_GO_TO_LOGIN_ACTIVITY);
			}
		});

		logo = (ImageView) findViewById(R.id.icon);
		logo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String[] names = { "Enter", "Leave", "School" };
				new AlertDialog.Builder(WelcomeActivity.this).setTitle("列表对话框")// 对话框标题
						.setItems(names, new DialogInterface.OnClickListener() {// 每一条的名称
									public void onClick(DialogInterface dialog,
											int which) {
										//on click
										if (which == 0) {
											Intent intent = new Intent(
													WelcomeActivity.this,
													BusEnterDialog.class);
											startActivity(intent);
											finish();
										} else if (which == 1) {
											Intent intent = new Intent(
													WelcomeActivity.this,
													BusLeaveDialog.class);
											startActivity(intent);
											finish();
										} else if (which == 2) {
											Intent intent = new Intent(
													WelcomeActivity.this,
													BusSchoolDialog.class);
											startActivity(intent);
											finish();
										}
									}
								})

						.show();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == Constants.REQUEST_GO_TO_SIGN_UP_ACTIVITY
				|| requestCode == Constants.REQUEST_GO_TO_LOGIN_ACTIVITY) {
			if (resultCode == Constants.RESULT_RESULT_OK) {
				setResult(Constants.RESULT_RESULT_OK);
				finish();
			}
		}
	}
}
