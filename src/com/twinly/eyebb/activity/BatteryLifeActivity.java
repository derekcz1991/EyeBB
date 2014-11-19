package com.twinly.eyebb.activity;

import com.eyebb.R;
import com.twinly.eyebb.bluetooth.ServicesActivity;
import com.twinly.eyebb.constant.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class BatteryLifeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_authorize_guest_list);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Constans.exit_ask(this);
			System.out.println("onKeyDown<----------");
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
