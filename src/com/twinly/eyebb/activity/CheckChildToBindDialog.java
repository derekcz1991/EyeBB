package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eyebb.R;
import com.google.android.gms.internal.it;
import com.google.android.gms.internal.nw;
import com.twinly.eyebb.adapter.CheckChildToBindAdapter;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class CheckChildToBindDialog extends Activity {

	private ListView listView;
	private CheckChildToBindAdapter adapter;
	private String getData;

	private String childId;
	private String name;
	private String icon;
	// private String kindergarten;
	private String cls;

	private String childIdToPost;
	private ArrayList<Child> child_data;


	public static final int CHILD_EXIST = 2;
	public static final int ALREADY_RELATIONSHIP = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_check_child_to_bind_list);
		Intent intent = getIntent();
		child_data = new ArrayList<Child>();

		getData = intent.getStringExtra("child_data");

		listView = (ListView) findViewById(R.id.listView);
		adapter = new CheckChildToBindAdapter(CheckChildToBindDialog.this,
				parseJson(getData));
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				childIdToPost = parseJson(getData).get(position).getChildId()
						+ "";

				new Thread(postRelationToServerRunnable).start();

				// Intent data = new Intent(CheckChildToBindDialog.this,
				// CheckBeaconActivity.class);
				// data.putExtra("name", parseJson(getData).get(position)
				//
				//
				// .getName());
				// data.putExtra("id", parseJson(getData).get(position)
				// .getChildId());
				// startActivity(data);
				// finish();
			}
		});

	}

	private ArrayList<Child> parseJson(String getData) {
		// TODO Auto-generated method stub
		// System.out.println("getData=>" + getData);

		Child child = new Child();
		try {

			JSONArray arr = new JSONArray(getData);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				childId = temp
						.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_ID);
				name = temp
						.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_NAME);
				icon = temp
						.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_ICON);
				HashMap<String, Object> map = new HashMap<String, Object>();

				System.out.println("childId>" + childId + " " + name + " "
						+ icon);
				// map.put("childId", childId);
				// map.put("name", name);
				// map.put("icon", icon);

				child.setChildId(Long.parseLong(childId));
				child.setName(name);
				child.setIcon(icon);
				child_data.add(child);
				
				//adapter.notifyDataSetChanged();
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return child_data;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	Runnable postRelationToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postRelationToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postRelationToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out
				.println("info=>"
						+ childIdToPost
						+ " "
						+ SharePrefsUtils
								.signUpGuardianId(CheckChildToBindDialog.this));

		map.put("childId", childIdToPost);
		map.put("guardianId",
				SharePrefsUtils.signUpGuardianId(CheckChildToBindDialog.this));

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(
					CheckChildToBindDialog.this, HttpConstants.CHILD_GUA_REL,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("true")) {
					Intent data = new Intent(CheckChildToBindDialog.this,
							CheckBeaconActivity.class);

					SharePrefsUtils.setSignUpChildId(
							CheckChildToBindDialog.this, childIdToPost);

					startActivity(data);
					finish();
				} else if (retStr.equals("false")) {
					Message msg = handler.obtainMessage();
					msg.what = ALREADY_RELATIONSHIP;
					handler.sendMessage(msg);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG)
						.show();

				break;

			case ALREADY_RELATIONSHIP:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_already_relationship, Toast.LENGTH_LONG)
						.show();
				break;

			}

		}
	};

}