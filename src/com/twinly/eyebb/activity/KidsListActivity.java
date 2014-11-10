package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewAdapter;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.SerializableChildrenMap;
import com.twinly.eyebb.utils.CommonUtils;

public class KidsListActivity extends Activity {
	private ListView listView;
	private List<Map.Entry<String, Child>> list;
	private List<Map.Entry<String, Child>> searchList;
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

		searchList = new ArrayList<Map.Entry<String, Child>>();
		Bundle bundle = getIntent().getExtras();
		SerializableChildrenMap serializableMap = (SerializableChildrenMap) bundle
				.get("childrenMap");
		if (serializableMap != null) {
			list = new ArrayList<Map.Entry<String, Child>>(serializableMap
					.getMap().entrySet());
			adapter = new KidsListViewAdapter(this, list, isSortByName,
					isSortByLocator);
		}
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		findViewById(R.id.sort_name).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSortByName = !isSortByName;
				adapter = new KidsListViewAdapter(KidsListActivity.this, list,
						isSortByName, isSortByLocator);
				listView.setAdapter(adapter);
			}
		});

		findViewById(R.id.sort_locator).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						isSortByLocator = !isSortByLocator;
						adapter = new KidsListViewAdapter(
								KidsListActivity.this, list, isSortByName,
								isSortByLocator);
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
				CommonUtils.switchSoftKeyboardstate(KidsListActivity.this);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				etSearch.clearFocus();
				CommonUtils.hideSoftKeyboard(etSearch, KidsListActivity.this);
				adapter = new KidsListViewAdapter(KidsListActivity.this, list,
						isSortByName, isSortByLocator);
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
				if (list.get(i).getValue().getName().contains(keyword)) {
					searchList.add(list.get(i));
				}
			}
			adapter = new KidsListViewAdapter(KidsListActivity.this,
					searchList, isSortByName, isSortByLocator);
			listView.setAdapter(adapter);
		} else {
			adapter = new KidsListViewAdapter(KidsListActivity.this, list,
					isSortByName, isSortByLocator);
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
