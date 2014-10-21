package com.twinly.eyebb.activity;

import com.eyebb.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class ChildInformationMatchingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_information_matching);
		setTitle(getString(R.string.text_child_information_matching));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
