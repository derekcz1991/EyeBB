package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidExpandableListviewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

public class MyKidsListActivity extends Activity {
	private ExpandableListView listView;
	private KidExpandableListviewAdapter adapter;

	private ArrayList<ArrayList<Child>> childrenList;
	private ArrayList<Child> allChildren;
	private ArrayList<Child> childrenWithAddress;
	private ArrayList<Child> childrenWithoutAddress;
	private ArrayList<Child> chidrenGuest;
	private ArrayList<String> groupList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.btn_children_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_my_kids);

		listView = (ExpandableListView) findViewById(R.id.expandableListView);
		listView.setGroupIndicator(null);

		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});

		groupList = new ArrayList<String>();
		childrenList = new ArrayList<ArrayList<Child>>();

		adapter = new KidExpandableListviewAdapter(this, groupList,
				childrenList);
		listView.setAdapter(adapter);

		childrenWithAddress = new ArrayList<Child>();
		childrenWithoutAddress = new ArrayList<Child>();
		chidrenGuest = new ArrayList<Child>();

		updateListView();
	}

	private void updateListView() {
		groupList.clear();
		childrenList.clear();
		childrenWithAddress.clear();
		childrenWithoutAddress.clear();
		chidrenGuest.clear();

		allChildren = DBChildren.getChildrenList(this);

		Child child;
		for (int i = 0; i < allChildren.size(); i++) {
			child = allChildren.get(i);
			if (child.getRelationWithUser().equals("P")) {
				if (CommonUtils.isNull(child.getMacAddress())) {
					childrenWithoutAddress.add(child);
				} else {
					childrenWithAddress.add(child);
				}
			} else {
				chidrenGuest.add(child);
			}
		}
		if (childrenWithAddress.size() > 0) {
			groupList.add(getString(R.string.text_bind_child));
		}
		if (childrenWithoutAddress.size() > 0) {
			groupList.add(getString(R.string.text_unbind_child));
		}
		if (chidrenGuest.size() > 0) {
			groupList.add(getString(R.string.text_granted_child));
		}

		childrenList.add(childrenWithAddress);
		childrenList.add(childrenWithoutAddress);
		childrenList.add(chidrenGuest);

		adapter.notifyDataSetChanged();

		for (int i = 0; i < groupList.size(); i++) {
			listView.expandGroup(i);
		}
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
			Intent intent = new Intent(MyKidsListActivity.this,
					ChildInformationMatchingActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY) {
			System.out.println("========>>>>>>>" + resultCode);
			if (resultCode == ActivityConstants.RESULT_UNBIND_SUCCESS) {
				//new GetMyKidsTask().execute();
			} else if (resultCode == ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS) {
				System.out.println("========>>>>>>>");
				//new GetMyKidsTask().execute();
			}
		}
	}
}
