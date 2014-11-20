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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyebb.R;
import com.google.android.gms.internal.hr;
import com.twinly.eyebb.adapter.CheckChildToBindAdapter;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Child;
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

				new Thread(postCheckChildIsBindToServerRunnable).start();

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

		try {

			child_data.clear();
			JSONArray arr = new JSONArray(getData);
			for (int i = 0; i < arr.length(); i++) {
				Child child = new Child();
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

				// adapter.notifyDataSetChanged();
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

	Runnable postCheckChildIsBindToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postCheckChildIsBindToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postCheckChildIsBindToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out
				.println("info=>"
						+ childIdToPost
						+ "  +guardianid==>"
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
				msg.what = BleDeviceConstants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals(HttpConstants.SERVER_RETURN_T)) {

					Intent data = new Intent(CheckChildToBindDialog.this,
							CheckBeaconActivity.class);

					SharePrefsUtils.setSignUpChildId(
							CheckChildToBindDialog.this, childIdToPost);

					startActivity(data);
					finish();

				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {

					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.MASTER_OF_CHILD_ALREAD_EXIST;
					handler.sendMessage(msg);
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_WG)) {

					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.WRONG_LOGIN;
					handler.sendMessage(msg);
				} else if (retStr.substring(0, 1).equals(
						HttpConstants.SERVER_RETURN_E)) {

					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.ALREADY_RELATIONSHIP;
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

			case BleDeviceConstants.CONNECT_ERROR:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case BleDeviceConstants.ALREADY_RELATIONSHIP:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_already_relationship, Toast.LENGTH_LONG)
						.show();
				adapter.notifyDataSetChanged();
				// parseJson(getData).clear();
				break;

			case BleDeviceConstants.WRONG_LOGIN:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_wrong_login_for_binding,
						Toast.LENGTH_LONG).show();
				adapter.notifyDataSetChanged();
				// parseJson(getData).clear();
				break;

			case BleDeviceConstants.MASTER_OF_CHILD_ALREAD_EXIST:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_master_of_the_child_exist_already,
						Toast.LENGTH_LONG).show();
				adapter.notifyDataSetChanged();
				// parseJson(getData).clear();
				break;

			}

		}
	};

}