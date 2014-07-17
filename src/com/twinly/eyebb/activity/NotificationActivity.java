package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.twinly.eyebb.R;

public class NotificationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_detials);
		setTitle(getString(R.string.text_notificationDetails));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
