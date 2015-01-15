package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewSimpleAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.woozzu.android.widget.IndexableListView;

public class ChangeKidsActivity extends Activity {
	private IndexableListView listView;
	private EditText etSearch;
	private KidsListViewSimpleAdapter adapter;
	private ArrayList<Child> list;
	private ArrayList<Child> searchList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_change_kids));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_kids_list);

		etSearch = (EditText) findViewById(R.id.et_search);
		listView = (IndexableListView) findViewById(R.id.listView);
		list = DBChildren.getChildrenListWithAddress(this);
		searchList = new ArrayList<Child>();

		adapter = new KidsListViewSimpleAdapter(this, list, false);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);

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

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				search(etSearch.getText().toString());
			}
		});

	}

	private void search(String keyword) {
		if (!TextUtils.isEmpty(keyword)) {
			searchList.clear();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getName().contains(keyword)) {
					searchList.add(list.get(i));
				}
			}
			adapter = new KidsListViewSimpleAdapter(ChangeKidsActivity.this,
					searchList, false);
			listView.setAdapter(adapter);
		} else {
			adapter = new KidsListViewSimpleAdapter(ChangeKidsActivity.this,
					list, false);
			listView.setAdapter(adapter);
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
}
