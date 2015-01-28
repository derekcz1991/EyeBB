package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.GuestListViewAdapter;
import com.twinly.eyebb.adapter.MasterListViewAdapter;
import com.twinly.eyebb.adapter.GuestListViewAdapter.ViewHolder;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.User;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class AuthorizeKidsActivity extends Activity {
	private LinearLayoutForListView guest_listView;
	private LinearLayoutForListView master_listView;
	private GuestListViewAdapter guest_adapter;
	private MasterListViewAdapter master_adapter;
	// private Button btnAddNewGuest;
	private ArrayList<User> auth_to_guest_data;
	private ArrayList<User> auth_from_master_data;
	private ArrayList<Child> auth_from_master_children_data;
	private TextView tvHint;
	private TextView tvHint_auth_to;
	private TextView tvHint_auth_from;
	private String retStr;
	private GridView grid_auth_to;
	private GridView grid_auth_from;
	private TextView txt_num_auth_to;
	private TextView txt_num_auth_from;
	private boolean hasGuestFlag = false;
	private boolean hasMasterFlag = false;
	private ScrollView ScrollView;
	public static final int UPDATE_VIEW = 11111;
	private Dialog authDialog;
	private Runnable postFindGuestsToServerRunnable;
	public CircleImageView avatar;
	public TextView name;
	public TextView phone;
	public RelativeLayout btn_guest_view;

	private LayoutInflater inflater;
	private View grid_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.btn_auth_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_authorize_guest_list);

		authDialog = LoadingDialog.createLoadingDialog(
				AuthorizeKidsActivity.this, getString(R.string.toast_loading));
		authDialog.show();
		postFindGuestsToServerRunnable = new Runnable() {

			@Override
			public void run() {
				postFindGuestsToServer();
			}
		};
		new Thread(postFindGuestsToServerRunnable).start();

		// btnAddNewGuest = (Button) findViewById(R.id.btn_add_new_guest);
		// guest_listView = (LinearLayoutForListView)
		// findViewById(R.id.listView_authorized_to_others);
		// master_listView = (LinearLayoutForListView)
		// findViewById(R.id.listView_authorization_from_others);
		tvHint = (TextView) findViewById(R.id.tv_hint);
		grid_auth_to = (GridView) findViewById(R.id.auth_to);
		grid_auth_from = (GridView) findViewById(R.id.auth_from);
		txt_num_auth_from = (TextView) findViewById(R.id.auth_to_children_num);
		txt_num_auth_to = (TextView) findViewById(R.id.auth_from_children_num);
		// tvHint_auth_to = (TextView)
		// findViewById(R.id.tv_hint_authorized_to_others);
		// tvHint_auth_from = (TextView)
		// findViewById(R.id.tv_hint_authorization_from_others);

		// inflater = LayoutInflater.from(AuthorizeKidsActivity.this);
		// grid_layout = inflater.inflate(R.layout.list_item_grant_kid_new,
		// null);

		// name = (TextView) grid_layout.findViewById(R.id.auth_nick_name);
		// phone = (TextView) grid_layout.findViewById(R.id.auth_user_name);

		btn_guest_view = (RelativeLayout) findViewById(R.id.btn_guest_view);

		auth_to_guest_data = new ArrayList<User>();
		auth_from_master_data = new ArrayList<User>();
		auth_from_master_children_data = new ArrayList<Child>();

		ScrollView = (ScrollView) findViewById(R.id.scrollview);
		ScrollView.smoothScrollTo(0, 0);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			System.out.println("=========>onKeyDown");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void postFindGuestsToServer() {
		try {
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

	private ArrayList<User> parseGuestJson(String getData) {
		try {
			auth_to_guest_data.clear();
			if (!JSONObject.NULL.equals(getData)) {
				// guest_data.clear();
				boolean isGusetNull = new JSONObject(getData)
						.isNull(HttpConstants.JSON_KEY_GUESTS);
				if (!isGusetNull) {
					JSONArray guests = new JSONObject(getData)
							.getJSONArray(HttpConstants.JSON_KEY_GUESTS);
					if (guests.length() > 0) {
						for (int i = 0; i < guests.length(); i++) {
							JSONObject guest = ((JSONObject) guests.opt(i))
									.getJSONObject(HttpConstants.JSON_KEY_USER);

							User guestMode = new User();
							// System.out
							// .println("--->"
							// +
							// guest.getString(HttpConstants.JSON_KEY_USER_ID));
							//
							// System.out
							// .println("--->"
							// +
							// guest.getString(HttpConstants.JSON_KEY_USER_NAME));
							// System.out
							// .println("--->"
							// +
							// guest.getString(HttpConstants.JSON_KEY_USER_PHONE));
							// System.out
							// .println("--->"
							// +
							// guest.getString(HttpConstants.JSON_KEY_USER_TYPE));
							// System.out
							// .println("--------------------------------------");

							guestMode.setGuardianId(guest
									.getString(HttpConstants.JSON_KEY_USER_ID));
							guestMode
									.setName(guest
											.getString(HttpConstants.JSON_KEY_USER_NAME));
							guestMode
									.setPhoneNumber(guest
											.getString(HttpConstants.JSON_KEY_USER_PHONE));
							guestMode
									.setType(guest
											.getString(HttpConstants.JSON_KEY_USER_TYPE));

							auth_to_guest_data.add(guestMode);
						}
					}
				}
			}

			// System.out.println("guest_data>" + auth_to_guest_data.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return auth_to_guest_data;
	}

	private ArrayList<User> parseMasterJson(String getData) {
		try {
			auth_from_master_data.clear();
			auth_from_master_children_data.clear();
			if (!JSONObject.NULL.equals(getData)) {
				boolean isMasterNull = new JSONObject(getData)
						.isNull(HttpConstants.JSON_KEY_MASTERS);
				if (!isMasterNull) {
					JSONArray masters = new JSONObject(getData)
							.getJSONArray(HttpConstants.JSON_KEY_MASTERS);

					if (masters.length() > 0) {
						for (int i = 0; i < masters.length(); i++) {
							JSONObject master = ((JSONObject) masters.opt(i))
									.getJSONObject(HttpConstants.JSON_KEY_USER);

							User masterMode = new User();
							// System.out
							// .println("--->"
							// +
							// master.getString(HttpConstants.JSON_KEY_USER_ID));
							//
							// System.out
							// .println("--->"
							// +
							// master.getString(HttpConstants.JSON_KEY_USER_NAME));
							// System.out
							// .println("--->"
							// +
							// master.getString(HttpConstants.JSON_KEY_USER_PHONE));
							// System.out
							// .println("--->"
							// +
							// master.getString(HttpConstants.JSON_KEY_USER_TYPE));
							// System.out
							// .println("--------------------------------------");

							masterMode.setGuardianId(master
									.getString(HttpConstants.JSON_KEY_USER_ID));
							masterMode
									.setName(master
											.getString(HttpConstants.JSON_KEY_USER_NAME));
							masterMode
									.setPhoneNumber(master
											.getString(HttpConstants.JSON_KEY_USER_PHONE));
							masterMode
									.setType(master
											.getString(HttpConstants.JSON_KEY_USER_TYPE));

							auth_from_master_data.add(masterMode);

							JSONArray master_children = ((JSONObject) masters
									.opt(i))
									.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_BY_GUARDIAN);

							for (int j = 0; j < master_children.length(); j++) {
								JSONObject master_child_json = master_children
										.getJSONObject(j);

								Child master_child = new Child();

								master_child
										.setChildId(master_child_json
												.getLong(HttpConstants.JSON_KEY_CHILD_ID));
								master_child
										.setName(master_child_json
												.getString(HttpConstants.JSON_KEY_CHILD_NAME));
								master_child
										.setIcon(master_child_json
												.getString(HttpConstants.JSON_KEY_CHILD_ICON));

								/**
								 * keep USER id
								 */
								master_child
										.setPhone(master
												.getString(HttpConstants.JSON_KEY_USER_ID));

								// System.out
								// .println("--->"
								// + master_child_json
								// .getLong(HttpConstants.JSON_KEY_CHILD_ID));
								// System.out
								// .println("--->"
								// + master_child_json
								// .getString(HttpConstants.JSON_KEY_CHILD_NAME));
								// System.out
								// .println("--->"
								// + master_child_json
								// .getString(HttpConstants.JSON_KEY_CHILD_ICON));
								// System.out
								// .println("--------------------------------------");

								auth_from_master_children_data
										.add(master_child);
							}
						}
					}
				}
			}

			System.out.println("master_data>" + auth_from_master_data.size());
			System.out.println("master_data_children>"
					+ auth_from_master_children_data.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return auth_from_master_data;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				if (authDialog.isShowing() && authDialog != null) {
					authDialog.dismiss();
				}
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
				if (authDialog.isShowing() && authDialog != null) {
					authDialog.dismiss();
				}

				if (parseGuestJson(retStr).size() > 0) {
					hasGuestFlag = true;
					System.out.println("parseGuestJson(retStr).size() --->"
							+ parseGuestJson(retStr).size());
					txt_num_auth_to.setText(parseGuestJson(retStr).size() + "");
					// tvHint.setVisibility(View.GONE);
					// tvHint_auth_to.setVisibility(View.GONE);
					// tvHint_auth_from.setVisibility(View.VISIBLE);
					guest_adapter = new GuestListViewAdapter(
							AuthorizeKidsActivity.this, parseGuestJson(retStr));
					grid_auth_to.setAdapter(guest_adapter);

					// add view

					// for (int i = 0; i < parseGuestJson(retStr).size(); i++) {
					// name.setText(parseGuestJson(retStr).get(i).getName());
					// phone.setText(parseGuestJson(retStr).get(i)
					// .getPhoneNumber());
					//
					//
					// grid_auth_to.setaaddView(grid_layout);
					// }

				}

				if (parseMasterJson(retStr).size() > 0) {
					hasMasterFlag = true;
					// tvHint_auth_from.setVisibility(View.GONE);
					// tvHint_auth_to.setVisibility(View.VISIBLE);
					// tvHint.setVisibility(View.GONE);
					// master_adapter = new MasterListViewAdapter(
					// AuthorizeKidsActivity.this,
					// parseMasterJson(retStr),
					// auth_from_master_children_data);
					// master_listView.setAdapter(master_adapter);
					txt_num_auth_from.setText(parseMasterJson(retStr).size()
							+ "");
					master_adapter = new MasterListViewAdapter(
							AuthorizeKidsActivity.this,
							parseMasterJson(retStr),
							auth_from_master_children_data);
					grid_auth_from.setAdapter(master_adapter);

				}

				// if (hasMasterFlag && hasGuestFlag) {
				// tvHint_auth_from.setVisibility(View.GONE);
				// tvHint_auth_to.setVisibility(View.GONE);
				// } else if (!hasMasterFlag && !hasGuestFlag) {
				// tvHint_auth_to.setVisibility(View.VISIBLE);
				// tvHint_auth_from.setVisibility(View.VISIBLE);
				// }

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
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_SEARCH_GUEST_ACTIVITY);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SEARCH_GUEST_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				finish();
			}
		}
	}

}
