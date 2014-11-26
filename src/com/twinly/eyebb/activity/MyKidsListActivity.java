package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidExpandableListviewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

public class MyKidsListActivity extends Activity {
	private ExpandableListView listView;
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

		allChildren = DBChildren.getChildrenList(this);
		childrenWithAddress = new ArrayList<Child>();
		childrenWithoutAddress = new ArrayList<Child>();
		chidrenGuest = new ArrayList<Child>();

		Child child;
		for (int i = 0; i < allChildren.size(); i++) {
			child = allChildren.get(i);
			System.out.println(child);
			if (child.getRelationWithUser().equals("P")) {
				if (CommonUtils.isNull(child.getMacAddress())) {
					System.out.println(child);
					System.out.println(allChildren.get(i));
					childrenWithoutAddress.add(child);
				} else {
					childrenWithAddress.add(child);
				}
			} else {
				chidrenGuest.add(child);
			}
		}

		childrenList = new ArrayList<ArrayList<Child>>();
		childrenList.add(childrenWithAddress);
		childrenList.add(childrenWithoutAddress);
		childrenList.add(chidrenGuest);

		groupList = new ArrayList<String>();
		groupList.add(getString(R.string.text_bind_child));
		groupList.add(getString(R.string.text_unbind_child));
		groupList.add(getString(R.string.text_granted_child));

		listView.setAdapter(new KidExpandableListviewAdapter(this, groupList,
				childrenList));
		/*listView1 = (ListView) findViewById(R.id.listView1);
		listView2 = (ListView) findViewById(R.id.listView2);

		new GetMyKidsTask().execute();

		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(MyKidsListActivity.this,
						KidProfileActivity.class);
				intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
						childWithDevice.get(position).getChildId());
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY);

			}
		});

		listView2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MyKidsListActivity.this,
						KidProfileActivity.class);
				intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
						childWithoutDevice.get(position).getChildId());
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY);
			}
		});*/

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
