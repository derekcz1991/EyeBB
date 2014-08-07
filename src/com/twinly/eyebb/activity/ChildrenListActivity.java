package com.twinly.eyebb.activity;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChildrenListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.SerializableChildrenMap;

public class ChildrenListActivity extends Activity {
	private ListView listView;
	private Map<String, Child> childrenMap;
	private ChildrenListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_children_list);
		if (getIntent().getIntExtra("from", 0) == ActivityConstants.REPORT_FRAGMENT) {
			setTitle(getString(R.string.text_change_kids));
		} else if (getIntent().getIntExtra("from", 0) == ActivityConstants.INDOOR_FRAGMENT) {
			setTitle(getString(R.string.text_kids_list));
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			SerializableChildrenMap serializableMap = (SerializableChildrenMap) bundle
					.get("childrenMap");
			childrenMap = serializableMap.getMap();
			adapter = new ChildrenListViewAdapter(this, childrenMap);
		}
		listView = (ListView) findViewById(R.id.listView);
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
