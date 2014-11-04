package com.twinly.eyebb.activity;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewAdapter;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.SerializableChildrenMap;

public class KidsListActivity extends Activity {
	private ListView listView;
	private Map<String, Child> childrenMap;
	private KidsListViewAdapter adapter;
	private boolean isSortByName;
	private boolean isSortByLocator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_kids_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_kids_list);

		Bundle bundle = getIntent().getExtras();
		SerializableChildrenMap serializableMap = (SerializableChildrenMap) bundle
				.get("childrenMap");
		if (serializableMap != null) {
			childrenMap = serializableMap.getMap();
			adapter = new KidsListViewAdapter(this, childrenMap, isSortByName,
					isSortByLocator);
		}
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		findViewById(R.id.sort_name).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSortByName = !isSortByName;
				adapter = new KidsListViewAdapter(KidsListActivity.this,
						childrenMap, isSortByName, isSortByLocator);
				listView.setAdapter(adapter);
			}
		});

		findViewById(R.id.sort_locator).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						isSortByLocator = !isSortByLocator;
						adapter = new KidsListViewAdapter(
								KidsListActivity.this, childrenMap,
								isSortByName, isSortByLocator);
						listView.setAdapter(adapter);
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
