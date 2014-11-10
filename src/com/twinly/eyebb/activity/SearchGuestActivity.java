package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.eyebb.R;
import com.twinly.eyebb.adapter.GuestListViewAdapter;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Guest;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class SearchGuestActivity extends Activity {
	private EditText edGuestname;
	private TextView btnSearchNewGuest;
	private String guestName;

	private GuestListViewAdapter adapter;
	private ListView listView;
	private ArrayList<Guest> guest_data;
	private String retStr;
	private String guestId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_search_guest_list);

		listView = (ListView) findViewById(R.id.listView);
		edGuestname = (EditText) findViewById(R.id.ed_guestname);
		btnSearchNewGuest = (TextView) findViewById(R.id.btn_search_new_guest);
		guest_data = new ArrayList<Guest>();
		btnSearchNewGuest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				guestName = edGuestname.getText().toString();

				new Thread(postFeedBackToServerRunnable).start();

			}
		});

	}

	Runnable postFeedBackToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postFeedBackRelationToServer();

		}
	};

	private ArrayList<Guest> parseJson(String getData) {
		// TODO Auto-generated method stub
		// System.out.println("getData=>" + getData);

		Guest guest = new Guest();
		try {

			if (guest_data != null && guest_data.size() > 0)
				guest_data.clear();
			JSONArray arr = new JSONArray(getData);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);

				System.out.println("--->"
						+ temp.getString(HttpConstants.JSON_GUEST_NAME));
				guest.setGuardianId(Long.parseLong(temp
						.getString(HttpConstants.JSON_GUEST_ID)) + "");
				guest.setName(temp.getString(HttpConstants.JSON_GUEST_NAME));
				guest.setPhoneNumber(temp
						.getString(HttpConstants.JSON_GUEST_PHONE));
				guest_data.add(guest);

				// adapter.notifyDataSetChanged();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return guest_data;
	}

	@SuppressLint("ShowToast")
	private void postFeedBackRelationToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + guestName);

		map.put("guestName", guestName);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			retStr = HttpRequestUtils.get(HttpConstants.SEARCH_GUEST, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 2) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.SUCCESS_SEARCH;
					handler.sendMessage(msg);

				} else if (retStr.equals("[]")) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.SEARCH_GUEST_NULL;
					handler.sendMessage(msg);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	Runnable postGrantToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postGrantToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postGrantToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + guestId);

		map.put("guestId", guestId);
		map.put("childIds", guestName);
		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			retStr = HttpRequestUtils.get(HttpConstants.GRANT_GUESTS, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 2) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.SUCCESS_SEARCH;
					handler.sendMessage(msg);

				} else if (retStr.equals("[]")) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.SEARCH_GUEST_NULL;
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
				Toast.makeText(SearchGuestActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case Constants.SUCCESS_SEARCH:
				Toast.makeText(SearchGuestActivity.this,
						R.string.text_feed_back_successful, Toast.LENGTH_LONG)
						.show();

				adapter = new GuestListViewAdapter(SearchGuestActivity.this,
						parseJson(retStr), false);

				System.out.println("parseJson(retStr)-->"
						+ parseJson(retStr).size());
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						guestId = parseJson(retStr).get(position)
								.getGuardianId();
					}
				});
				break;

			case Constants.SEARCH_GUEST_NULL:
				Toast.makeText(SearchGuestActivity.this,
						R.string.text_search_guest_null, Toast.LENGTH_LONG)
						.show();

				break;

			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			System.out.println("=========>onKeyDown");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
