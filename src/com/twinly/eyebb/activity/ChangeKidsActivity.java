package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.adapter.KidsListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

public class ChangeKidsActivity extends Activity {
	private ListView listView;
	private ChangeKidsListViewAdapter adapter;
	private boolean isSortByName;
	private ArrayList<Child> list;
	private ArrayList<Child> searchList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_change_kids));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_kids_list);

		listView = (ListView) findViewById(R.id.listView);
		list = DBChildren.getChildrenList(this);
		searchList = new ArrayList<Child>();

		adapter = new ChangeKidsListViewAdapter(this, list, isSortByName);
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

		findViewById(R.id.sort_locator).setVisibility(View.GONE);

		findViewById(R.id.sort_name).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSortByName = !isSortByName;
				adapter = new ChangeKidsListViewAdapter(
						ChangeKidsActivity.this, DBChildren
								.getChildrenList(ChangeKidsActivity.this),
						isSortByName);
				listView.setAdapter(adapter);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem search = menu.add(0, 1, 0, getString(R.string.btn_search));
		search.setIcon(R.drawable.ic_search)
				.setActionView(R.layout.actionbar_search)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		final EditText etSearch = (EditText) search.getActionView()
				.findViewById(R.id.search_addr);

		search.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				etSearch.requestFocus();
				CommonUtils.switchSoftKeyboardstate(ChangeKidsActivity.this);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				etSearch.clearFocus();
				CommonUtils.hideSoftKeyboard(etSearch, ChangeKidsActivity.this);
				adapter = new ChangeKidsListViewAdapter(
						ChangeKidsActivity.this, list, isSortByName);
				listView.setAdapter(adapter);
				return true;
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
		//search.collapseActionView();
		return super.onCreateOptionsMenu(menu);
	}

	private void search(String keyword) {
		if (!TextUtils.isEmpty(keyword)) {
			searchList.clear();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getName().contains(keyword)) {
					searchList.add(list.get(i));
				}
			}
			adapter = new ChangeKidsListViewAdapter(ChangeKidsActivity.this,
					searchList, isSortByName);
			listView.setAdapter(adapter);
		} else {
			adapter = new ChangeKidsListViewAdapter(ChangeKidsActivity.this,
					list, isSortByName);
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
