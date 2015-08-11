package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.twinly.eyebb.R;

public class SelectRegionActivity extends Activity {
	public static final int RESULT_CODE_CHINA = 1;
	public static final int RESULT_CODE_HK = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_country));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_select_region);

		findViewById(R.id.tv_country_china).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						setResult(RESULT_CODE_CHINA);
						finish();
					}
				});

		findViewById(R.id.tv_country_hk).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						setResult(RESULT_CODE_HK);
						finish();
					}
				});
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
