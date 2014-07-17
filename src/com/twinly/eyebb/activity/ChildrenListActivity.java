package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ChildrenListActivity extends Activity {
	private LinearLayout item1;
	private LinearLayout item2;
	private LinearLayout item3;
	private TextView location1;
	private TextView location2;
	private TextView location3;
	private TextView phone1;
	private TextView phone2;
	private TextView phone3;
	private LinearLayout maindialogBeepBtn1;
	private LinearLayout maindialogBeepBtn2;
	private LinearLayout maindialogBeepBtn3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_children_list);
		setTitle(getString(R.string.text_kids_list));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		item1 = (LinearLayout) findViewById(R.id.item1);
		item2 = (LinearLayout) findViewById(R.id.item2);
		item3 = (LinearLayout) findViewById(R.id.item3);
		location1 = (TextView) findViewById(R.id.location1);
		location2 = (TextView) findViewById(R.id.location2);
		location3 = (TextView) findViewById(R.id.location3);
		phone1 = (TextView) findViewById(R.id.phone_1);
		phone2 = (TextView) findViewById(R.id.phone_2);
		phone3 = (TextView) findViewById(R.id.phone_3);
		maindialogBeepBtn1 = (LinearLayout) findViewById(R.id.maindialog_beep_btn_1);
		maindialogBeepBtn2 = (LinearLayout) findViewById(R.id.maindialog_beep_btn_2);
		maindialogBeepBtn3 = (LinearLayout) findViewById(R.id.maindialog_beep_btn_3);

		if (SharePrefsUtils.getRole(this)) {

		} else {
			item1.setVisibility(View.GONE);
			item2.setVisibility(View.GONE);
		}
		location3.setText("@" + getIntent().getStringExtra("location"));

		phone1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri telUri = Uri.parse("tel:" + phone1.getText());
				Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
				startActivity(intent);
			}
		});
		phone2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri telUri = Uri.parse("tel:" + phone2.getText());
				Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
				startActivity(intent);
			}
		});
		phone3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri telUri = Uri.parse("tel:" + phone3.getText());
				Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
				startActivity(intent);
			}
		});

		Beep beep = new Beep();
		maindialogBeepBtn1.setOnClickListener(beep);
		maindialogBeepBtn2.setOnClickListener(beep);
		maindialogBeepBtn3.setOnClickListener(beep);

		item1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", 0);
				setResult(Constants.RESULT_RESULT_OK, data);
				finish();
			}
		});

		item2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", 1);
				setResult(Constants.RESULT_RESULT_OK, data);
				finish();
			}
		});

		item3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", 2);
				setResult(Constants.RESULT_RESULT_OK, data);
				finish();
			}
		});

		if (getIntent().getIntExtra("from", 0) == 2) {
			location1.setVisibility(View.GONE);
			location2.setVisibility(View.GONE);
			location3.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class Beep implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ChildrenListActivity.this,
					BeepDialog.class);
			startActivity(intent);
		}

	}
}
