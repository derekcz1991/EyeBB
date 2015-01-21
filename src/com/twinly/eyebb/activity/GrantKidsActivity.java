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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.GrantKidsListViewFromGuestAdapter;
import com.twinly.eyebb.adapter.GrantKidsListViewFromMasterAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class GrantKidsActivity extends Activity {
	private ListView listView;
	private GrantKidsListViewFromGuestAdapter guest_adapter;
	private GrantKidsListViewFromMasterAdapter master_adapter;
	private ArrayList<Child> returnList;
	private ArrayList<Child> childList;
	// private Button btnConfirm;
	// private Button btnCancel;
	private String guestChildrenRetStr;

	private String guestdId;
	private String guestName;
	private String grantChildId;
	private String from_master_or_guest;
	private String noAccessGrantChildId;
	private boolean from_where = false;
	public static final int UPDATE_VIEW = 11111;
	private ArrayList<Child> new_children_data;

	/**
	 * 
	 * from_master_or_guest 來自第一個activity AuthorizeKidsActivity ,因為chily並沒有給我
	 * master 給來的json 所以我要自己傳值. 來判斷來自哪裡
	 * 
	 * 
	 * from_where 這個用來判斷是不是來自search activity
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		new_children_data = new ArrayList<Child>();
		guestdId = intent.getStringExtra("guestId");
		guestName = intent.getStringExtra("guestName");

		from_where = intent
				.getBooleanExtra("from_search_guest_activity", false);

		if (!from_where) {
			from_master_or_guest = intent.getStringExtra("from_where");
			if (from_master_or_guest.equals("master")) {
				new_children_data = (ArrayList<Child>) intent
						.getSerializableExtra("child_data");
			}
		}

		setTitle(getString(R.string.text_auth_to_user) + guestName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.dialog_grant_kids_list);

		listView = (ListView) findViewById(R.id.listView);
		// returnList = DBChildren.getChildrenList(this);
		childList = new ArrayList<Child>();
		returnList = new ArrayList<Child>();
		new Thread(postGuestChildrenToServerRunnable).start();

		// listView.setOnItemClickListener(new OnItemClickListener() {
		// public void onItemClick(AdapterView<?> arg0, View arg1,
		// int position, long arg3) {
		// // Intent data = new Intent();
		// // Bundle bundle = new Bundle();
		// // bundle.putSerializable("child", adapter.getItem(position));
		// // data.putExtras(bundle);
		// // setResult(ActivityConstants.RESULT_RESULT_OK, data);
		// // finish();
		// }
		// });

	}

	Runnable postGuestChildrenToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postGuestChildrenToServer();

		}
	};

	private ArrayList<Child> parseChildJson(String getData) {
		// TODO Auto-generated method stub
		// System.out.println("getData=>" + getData);

		try {
			childList.clear();
			if (!JSONObject.NULL.equals(getData)) {
				// guest_data.clear();
				boolean isChildNull = new JSONObject(getData)
						.isNull(HttpConstants.JSON_KEY_CHILDREN_QUOTA);
				if (!isChildNull) {
					JSONArray children = new JSONObject(getData)
							.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_QUOTA);
					if (children.length() > 0) {
						for (int i = 0; i < children.length(); i++) {
							JSONObject child = ((JSONObject) children.opt(i))
									.getJSONObject(HttpConstants.JSON_KEY_CHILD);

							Child ChildMode = new Child();
							System.out
									.println("--->"
											+ child.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_ID));

							System.out
									.println("--->"
											+ child.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_NAME));
							System.out
									.println("--->"
											+ child.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_ICON));

							System.out
									.println("--->"
											+ ((JSONObject) children.opt(i))
													.getBoolean(HttpConstants.JSON_KEY_WITH_ACCESS));
							System.out
									.println("--->"
											+ ((JSONObject) children.opt(i))
													.getString(HttpConstants.JSON_KEY_TOTAL_QUOTA));
							System.out
									.println("--->"
											+ ((JSONObject) children.opt(i))
													.getString(HttpConstants.JSON_KEY_QUOTA_LEFT));

							System.out
									.println("--------------------------------------");

							ChildMode
									.setChildId(Long.valueOf(child
											.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_ID)));
							ChildMode
									.setName(child
											.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_NAME));
							ChildMode
									.setIcon(child
											.getString(HttpConstants.JSON_CHECK_CHILD_CHILD_ICON));
							ChildMode
									.setWithAccess(((JSONObject) children
											.opt(i))
											.getBoolean(HttpConstants.JSON_KEY_WITH_ACCESS));
							ChildMode
									.setTotalQuota(((JSONObject) children
											.opt(i))
											.getString(HttpConstants.JSON_KEY_TOTAL_QUOTA));

							ChildMode
									.setQuotaLeft(((JSONObject) children.opt(i))
											.getString(HttpConstants.JSON_KEY_QUOTA_LEFT));

							childList.add(ChildMode);
						}
					}
				}
			}

			System.out.println("childList_data>" + childList.size());

			// adapter = new GuestListViewAdapter(AuthorizeKidsActivity.this,
			// guest_data);
			// listView.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// tvHint.setVisibility(View.VISIBLE);
			// content.setVisibility(View.GONE);
			e.printStackTrace();
		}
		return childList;
	}

	@SuppressLint("ShowToast")
	private void postGuestChildrenToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();

		map.put("guestId", guestdId);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			guestChildrenRetStr = HttpRequestUtils.postTo(
					GrantKidsActivity.this, HttpConstants.GUEST_CHILDREN, map);
			System.out.println("guestchildren======>" + guestChildrenRetStr);
			if (guestChildrenRetStr
					.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| guestChildrenRetStr.equals("")
					|| guestChildrenRetStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (guestChildrenRetStr.length() > 0) {
					Message msg = handler.obtainMessage();
					msg.what = UPDATE_VIEW;
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
		System.out.println("info=>" + guestdId + " ");

		map.put("guestId", guestdId);
		if (grantChildId.length() > 0) {
			map.put("accessChildIds",
					grantChildId.substring(0, grantChildId.length() - 1));
		} else {
			map.put("accessChildIds", "");
		}

		if (noAccessGrantChildId.length() > 0) {
			map.put("noAccessChildIds",
					noAccessGrantChildId.substring(0,
							noAccessGrantChildId.length() - 1));
		} else {
			map.put("noAccessChildIds", "");
		}

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(GrantKidsActivity.this,
					HttpConstants.GRANT_GUESTS, map);
			System.out.println("grant======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals(HttpConstants.SERVER_RETURN_T)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.GRANT_SUCCESS;
					handler.sendMessage(msg);

				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.CONNECT_ERROR;
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
				Toast.makeText(GrantKidsActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case Constants.GRANT_SUCCESS:
				Toast.makeText(GrantKidsActivity.this,
						R.string.text_grant_success, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(GrantKidsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				// AuthorizeKidsActivity.instance.finish();
				// SearchGuestActivity.instance.finish();
				setResult(ActivityConstants.RESULT_RESULT_OK);
				finish();
				break;

			case Constants.NO_SELECT_CHILDREN:
				Toast.makeText(GrantKidsActivity.this,
						R.string.text_select_child, Toast.LENGTH_LONG).show();

				break;

			case UPDATE_VIEW:
				if (!from_where) {
					if (from_master_or_guest.equals("master")) {
						master_adapter = new GrantKidsListViewFromMasterAdapter(
								GrantKidsActivity.this, new_children_data);
						listView.setAdapter(master_adapter);
					} else if (from_master_or_guest.equals("guest")) {
						returnList = parseChildJson(guestChildrenRetStr);
						guest_adapter = new GrantKidsListViewFromGuestAdapter(
								GrantKidsActivity.this, returnList);
						listView.setAdapter(guest_adapter);
					}
				} else {
					returnList = parseChildJson(guestChildrenRetStr);
					guest_adapter = new GrantKidsListViewFromGuestAdapter(
							GrantKidsActivity.this, returnList);
					listView.setAdapter(guest_adapter);
				}

				break;

			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!from_where) {
			if (from_master_or_guest.equals("guest")) {
				menu.add(0, 0, 0, R.string.btn_confirm).setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			}
		} else {
			menu.add(0, 0, 0, R.string.btn_confirm).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM
							| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (from_where) {
				finish();
			} else {
				Intent intent = new Intent(GrantKidsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				finish();
			}

			return true;
		} else if (item.getItemId() == 0) {
			grantChildId = "";
			noAccessGrantChildId = "";
			// System.out.println("SIZE==?"+GrantKidsListViewAdapter.grantkidId.size());

			for (int i = 0; i < GrantKidsListViewFromGuestAdapter.grantkidId
					.size(); i++) {
				grantChildId += GrantKidsListViewFromGuestAdapter.grantkidId
						.get(i).toString() + ",";
			}
			for (int i = 0; i < GrantKidsListViewFromGuestAdapter.noAccessGrantkidId
					.size(); i++) {
				noAccessGrantChildId += GrantKidsListViewFromGuestAdapter.noAccessGrantkidId
						.get(i).toString() + ",";
			}

			System.out.println("grantChildId-->" + grantChildId
					+ "    nograntChildId--->" + noAccessGrantChildId);

			new Thread(postGrantToServerRunnable).start();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something you want to

			if (from_where) {
				finish();
			} else {
				Intent intent = new Intent(GrantKidsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
