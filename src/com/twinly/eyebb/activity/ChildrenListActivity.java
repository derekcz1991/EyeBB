package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.eyebb.R;

public class ChildrenListActivity extends Activity {
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_children_list);
		if (getIntent().getIntExtra("from", 0) == 2) {
			setTitle(getString(R.string.text_change_kids));
		} else if (getIntent().getIntExtra("from", 0) == 1) {
			setTitle(getString(R.string.text_kids_list));
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

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
