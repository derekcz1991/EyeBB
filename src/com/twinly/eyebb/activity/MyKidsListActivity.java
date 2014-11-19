package com.twinly.eyebb.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewSimpleAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class MyKidsListActivity extends Activity {
	private ListView listView1;
	private ListView listView2;
	private ArrayList<Child> childWithDevice;
	private ArrayList<Child> childWithoutDevice;

	public static MyKidsListActivity instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.btn_my_child));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_my_kids);
		instance = this;
		listView1 = (ListView) findViewById(R.id.listView1);
		listView2 = (ListView) findViewById(R.id.listView2);

		new GetMyKidsTask().execute();

		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(MyKidsListActivity.this,
						KidProfileActivity.class);
				intent.putExtra("child_id", childWithDevice.get(position)
						.getChildId());
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY);
				/*if (childWithDevice.get(position).getMacAddress().length() > 0) {
					Intent data = new Intent(MyKidsListActivity.this,
							UnbindDeviceDialog.class);

					SharePrefsUtils.setSignUpChildId(MyKidsListActivity.this,
							childWithDevice.get(position).getChildId() + "");

					startActivity(data);

				} else {
					Intent data = new Intent(MyKidsListActivity.this,
							CheckBeaconActivity.class);
					SharePrefsUtils.setSignUpChildId(MyKidsListActivity.this,
							childWithDevice.get(position).getChildId() + "");
					// bundle.putSerializable("child",
					// adapter.getItem(position));
					// data.putExtras(bundle);
					// setResult(ActivityConstants.RESULT_RESULT_OK, data);
					startActivity(data);
					//finish();
				}*/

			}
		});

		listView2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MyKidsListActivity.this,
						KidProfileActivity.class);
				SharePrefsUtils.setSignUpChildId(MyKidsListActivity.this,
						childWithoutDevice.get(position).getChildId() + "");
				intent.putExtra("child_id", childWithoutDevice.get(position)
						.getChildId());
				startActivity(intent);

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
			Intent intent = new Intent(MyKidsListActivity.this,
					ChildInformationMatchingActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetMyKidsTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return HttpRequestUtils
					.get(HttpConstants.GET_MASTER_CHILDREN, null);
		}

		@Override
		protected void onPostExecute(String result) {
			childWithDevice = new ArrayList<Child>();
			childWithoutDevice = new ArrayList<Child>();

			System.out.println("master children = " + result);
			try {
				JSONObject json = new JSONObject(result);
				JSONArray childJSONList = json
						.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_LIST);
				for (int i = 0; i < childJSONList.length(); i++) {
					JSONObject object = (JSONObject) childJSONList.get(i);
					Child child = new Child(
							object.getInt(HttpConstants.JSON_KEY_CHILD_ID),
							object.getString(HttpConstants.JSON_KEY_CHILD_NAME),
							object.getString(HttpConstants.JSON_KEY_CHILD_ICON));
					if (CommonUtils.isNull(object
							.getString(HttpConstants.JSON_KEY_CHILD_BEACON))) {
						childWithoutDevice.add(child);
					} else {
						childWithDevice.add(child);
					}
				}
			} catch (JSONException e) {
				System.out.println(HttpConstants.GET_MASTER_CHILDREN
						+ e.getMessage());
			}
			listView1.setAdapter(new KidsListViewSimpleAdapter(
					MyKidsListActivity.this, childWithDevice, false));
			listView2.setAdapter(new KidsListViewSimpleAdapter(
					MyKidsListActivity.this, childWithoutDevice, false));
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_UNBIND_SUCCESS) {
				new GetMyKidsTask().execute();
			}
		}
	}
}
