package com.twinly.eyebb.activity;

import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewAdapter;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.SerializableChildrenMap;

public class KidsListActivity extends Activity {
	private ListView listView;
	private Map<String, Child> childrenMap;
	private KidsListViewAdapter adapter;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_kids_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_kids_list);

		// DeviceListAcitivity dla = new DeviceListAcitivity();
		// dla.scanLeDevice(false);

		Bundle bundle = getIntent().getExtras();
		SerializableChildrenMap serializableMap = (SerializableChildrenMap) bundle
				.get("childrenMap");
		if (serializableMap != null) {
			childrenMap = serializableMap.getMap();
			adapter = new KidsListViewAdapter(this, childrenMap);
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
