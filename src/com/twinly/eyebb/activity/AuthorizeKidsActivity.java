package com.twinly.eyebb.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.GuestListViewAdapter;
import com.twinly.eyebb.bluetooth.CharacteristicsActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Guest;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class AuthorizeKidsActivity extends Activity {
	private ListView listView;
	private GuestListViewAdapter adapter;
	// private Button btnAddNewGuest;
	private ArrayList<Guest> auth_to_guest_data;
	private ArrayList<Guest> auth_from_guest_data;
	private LinearLayout content;
	private String guardianId;
	private String name;
	private String phoneNumber;

	private TextView tvHint;
	private TextView tvHint_auth_to;
	private TextView tvHint_auth_from;
	private String retStr;

	public static final int UPDATE_VIEW = 11111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_authorize_guest_list);
		new Thread(postFindGuestsToServerRunnable).start();
		// btnAddNewGuest = (Button) findViewById(R.id.btn_add_new_guest);
		listView = (ListView) findViewById(R.id.listView_authorized_to_others);
		tvHint = (TextView) findViewById(R.id.tv_hint);
		tvHint_auth_to = (TextView) findViewById(R.id.tv_hint_authorized_to_others);
		tvHint_auth_from = (TextView) findViewById(R.id.tv_hint_authorization_from_others);

		content = (LinearLayout) findViewById(R.id.view_content);
		auth_to_guest_data = new ArrayList<Guest>();
		auth_from_guest_data = new ArrayList<Guest>();

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

			}
		});

	}

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

	Runnable postFindGuestsToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postFindGuestsToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postFindGuestsToServer() {
		// TODO Auto-generated method stub

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			retStr = HttpRequestUtils.get(HttpConstants.AUTH_FIND_GUESTS, null);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 0) {
					Message msg = handler.obtainMessage();
					msg.what = UPDATE_VIEW;
					handler.sendMessage(msg);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	private ArrayList<Guest> parseJson(String getData) {
		// TODO Auto-generated method stub
		// System.out.println("getData=>" + getData);

		try {

			// guest_data.clear();

			JSONArray guests = new JSONObject(getData).getJSONArray("guests");

			for (int i = 0; i < guests.length(); i++) {
				JSONObject guest = ((JSONObject) guests.opt(i))
						.getJSONObject("guest");

				Guest guestMode = new Guest();
				System.out.println("--->"
						+ guest.getString(HttpConstants.JSON_KEY_USER_ID));

				System.out.println("--->"
						+ guest.getString(HttpConstants.JSON_KEY_USER_NAME));
				System.out.println("--->"
						+ guest.getString(HttpConstants.JSON_KEY_USER_PHONE));

				guestMode.setGuardianId(guest
						.getString(HttpConstants.JSON_KEY_USER_ID));
				guestMode.setName(guest
						.getString(HttpConstants.JSON_KEY_USER_NAME));
				guestMode.setPhoneNumber(guest
						.getString(HttpConstants.JSON_KEY_USER_PHONE));

				auth_to_guest_data.add(guestMode);
			}

			System.out.println("guest_data>" + auth_to_guest_data.size());

			// adapter = new GuestListViewAdapter(AuthorizeKidsActivity.this,
			// guest_data);
			// listView.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			tvHint.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
			e.printStackTrace();
		}
		return auth_to_guest_data;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case Constants.UNBIND_SUCCESS:
				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_unbind_success, Toast.LENGTH_LONG).show();

				break;

			case Constants.UNBIND_FAIL:

				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_unbind_fail, Toast.LENGTH_LONG).show();

				// parseJson(getData).clear();
				break;

			case UPDATE_VIEW:
				adapter = new GuestListViewAdapter(AuthorizeKidsActivity.this,
						parseJson(retStr));
				listView.setAdapter(adapter);
				if (auth_from_guest_data.size() == 0) {
					tvHint_auth_from.setVisibility(View.VISIBLE);
				}
				tvHint_auth_to.setVisibility(View.GONE);
				// content.setVisibility(View.VISIBLE);
				break;
			}

		}
	};

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
			// Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(AuthorizeKidsActivity.this,
					SearchGuestActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
