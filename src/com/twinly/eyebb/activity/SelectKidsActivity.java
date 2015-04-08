package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewSimpleAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.ChildSelectable;
import com.twinly.eyebb.model.SerializableChildrenList;
import com.woozzu.android.widget.IndexableListView;

/**
 * @author eyebb team
 * 
 * @category SelectKidsActivity
 * 
 *           this activity is used to select the child in the
 *           RadarFragment(Activity). it main function is supported to anti-thief.
 * 
 */
public class SelectKidsActivity extends Activity {
	public static final String EXTRA_CHILDREN_LIST = "children_list";
	public static final String EXTRA_SCANNED_DEVICE = "scanned_device";
	private IndexableListView mListView;
	private KidsListViewSimpleAdapter adapter;
	private ArrayList<ChildSelectable> mList;
	private String scannedDevice;
	private boolean isSelectAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_change_kids));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_select_kids);

		scannedDevice = getIntent().getStringExtra(EXTRA_SCANNED_DEVICE);
		mListView = (IndexableListView) findViewById(R.id.listview);

		mList = DBChildren.getChildrenListWithAddress(this);
		for (int i = 0; i < mList.size(); i++) {
			if (scannedDevice.contains(mList.get(i).getMacAddress())) {
				mList.get(i).setSelected(true);
			}
		}
		adapter = new KidsListViewSimpleAdapter(this, mList, true);
		mListView.setAdapter(adapter);
		mListView.setFastScrollEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 10, R.string.btn_confirm).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0, 1, 1, R.string.btn_select_all).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == 0) {
			boolean isValid = false;
			for (ChildSelectable childSelectable : mList) {
				if (childSelectable.isSelected()) {
					isValid = true;
					break;
				}
			}
			if (isValid) {
				Intent data = new Intent();
				SerializableChildrenList serializableChildrenList = new SerializableChildrenList();
				serializableChildrenList.setList(mList);
				Bundle bundle = new Bundle();
				bundle.putSerializable(EXTRA_CHILDREN_LIST,
						serializableChildrenList);
				data.putExtras(bundle);
				setResult(ActivityConstants.RESULT_RESULT_OK, data);
				finish();
			}
		} else if (item.getItemId() == 1) {
			if (isSelectAll) {
				isSelectAll = false;
				for (ChildSelectable childSelectable : mList) {
					childSelectable.setSelected(false);
				}
			} else {
				isSelectAll = true;
				for (ChildSelectable childSelectable : mList) {
					childSelectable.setSelected(true);
				}
			}
			adapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}
}
