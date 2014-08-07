package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.database.DBChildren;

public class ChangeKidsActivity extends Activity {
	private ListView listView;
	private ChangeKidsListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_change_kids));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		setContentView(R.layout.activity_kids_list);

		listView = (ListView) findViewById(R.id.listView);
		adapter = new ChangeKidsListViewAdapter(this,
				DBChildren.getChildrenList(this));
		listView.setAdapter(adapter);
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
