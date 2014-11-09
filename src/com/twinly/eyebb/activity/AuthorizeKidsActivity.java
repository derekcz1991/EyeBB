package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;

public class AuthorizeKidsActivity extends Activity {
	private ListView listView;
	private ChangeKidsListViewAdapter adapter;
	private Button btnAddNewGuest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_authorize_guest_list);

		btnAddNewGuest = (Button) findViewById(R.id.btn_add_new_guest);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new ChangeKidsListViewAdapter(this,
				DBChildren.getChildrenList(this), false);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("child", adapter.getItem(position));
				data.putExtras(bundle);
				setResult(ActivityConstants.RESULT_RESULT_OK, data);
				finish();
			}
		});

		btnAddNewGuest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AuthorizeKidsActivity.this,
						SearchGuestActivity.class);
				startActivity(intent);
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
