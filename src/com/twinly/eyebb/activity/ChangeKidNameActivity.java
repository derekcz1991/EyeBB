package com.twinly.eyebb.activity;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.utils.CommonUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class ChangeKidNameActivity extends Activity {
	private EditText etName;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_kid_name);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		name = getIntent().getStringExtra(ActivityConstants.EXTRA_CHILD_NAME);
		setTitle(name);

		etName = (EditText) findViewById(R.id.et_name);
		etName.setText(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_change_kid_name, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_options) {
			DBChildren.updateNameByChildId(
					this,
					getIntent().getLongExtra(ActivityConstants.EXTRA_CHILD_ID,
							-1), CommonUtils
							.isNull(etName.getText().toString()) ? name
							: etName.getText().toString());
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
