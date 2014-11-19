package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class WelcomeActivity extends Activity {
	private ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		logo = (ImageView) findViewById(R.id.icon);
		checkLogo();
		findViewById(R.id.sign_up).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(WelcomeActivity.this,
//						ChooseUsrRoleActivity.class);
//				startActivityForResult(intent,
//						ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY);
				
				Intent intent = new Intent(WelcomeActivity.this,
						SignUpActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY);
			}
		});

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				// Intent intent = new Intent(WelcomeActivity.this,
				// CheckBeaconActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_LOGIN_ACTIVITY);
			}
		});

	}

	private void checkLogo() {
		switch (SharePrefsUtils.getLanguage(this)) {
		case BleDeviceConstants.LOCALE_TW:
		case BleDeviceConstants.LOCALE_HK:
		case BleDeviceConstants.LOCALE_CN:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_cht));
			break;
		default:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_en));
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY
				|| requestCode == ActivityConstants.REQUEST_GO_TO_LOGIN_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}

}
