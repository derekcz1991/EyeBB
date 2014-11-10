package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class LoginAuthKidsActivity extends Activity {
	private ListView listView;
	private ChangeKidsListViewAdapter adapter;
	private boolean isSortByName;
	private ArrayList<Child> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_change_kids));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_login_auth_kids);

		listView = (ListView) findViewById(R.id.listView);
		list = DBChildren.getChildrenList(this);

		adapter = new ChangeKidsListViewAdapter(this, list, isSortByName);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent data = new Intent(LoginAuthKidsActivity.this,
						CheckBeaconActivity.class);
				SharePrefsUtils.setSignUpChildId(LoginAuthKidsActivity.this,
						list.get(position).getChildId() + "");
				// bundle.putSerializable("child", adapter.getItem(position));
				// data.putExtras(bundle);
				// setResult(ActivityConstants.RESULT_RESULT_OK, data);
				startActivity(data);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.btn_add).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			// Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(LoginAuthKidsActivity.this,
					ChildInformationMatchingActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
