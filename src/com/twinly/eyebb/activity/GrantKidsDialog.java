package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewSimpleAdapter;
import com.twinly.eyebb.adapter.GrantKidsListViewAdapter;
import com.twinly.eyebb.adapter.GuestListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class GrantKidsDialog extends Activity {
	private ListView listView;
	private GrantKidsListViewAdapter adapter;
	private boolean isSortByName;
	private ArrayList<Child> list;
	private ArrayList<Child> searchList;
	private Button btnConfirm;
	private Button btnCancel;

	private String guestdId;
	private String grantChildId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_grant_kids_list);

		listView = (ListView) findViewById(R.id.listView);
		list = DBChildren.getChildrenList(this);
		searchList = new ArrayList<Child>();

		Intent intent = getIntent();
		guestdId = intent.getStringExtra("guestId");

		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		btnCancel = (Button) findViewById(R.id.btn_cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				grantChildId = GrantKidsListViewAdapter.grantkidId.get(0)
						.toString() + ",";
				if (GrantKidsListViewAdapter.grantkidId.size() > 0) {
					for (int i = 1; i < GrantKidsListViewAdapter.grantkidId
							.size(); i++) {
						grantChildId += GrantKidsListViewAdapter.grantkidId
								.get(i).toString() + ",";
					}
					System.out.println(grantChildId);

					new Thread(postGrantToServerRunnable).start();
				} else {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.NO_SELECT_CHILDREN;
					handler.sendMessage(msg);
					// finish();
				}

				// SharePrefsUtils.setGrantChildID(context, child.getChildId()
				// + ",");
			}
		});

		adapter = new GrantKidsListViewAdapter(this, list, isSortByName);
		listView.setAdapter(adapter);

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
		System.out.println("info=>" + guestdId + " "
				+ grantChildId.substring(0, grantChildId.length() - 1));

		map.put("guestId", guestdId);
		map.put("childIds",
				grantChildId.substring(0, grantChildId.length() - 1));
		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(GrantKidsDialog.this,
					HttpConstants.GRANT_GUESTS, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = BleDeviceConstants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("true")) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.GRANT_SUCCESS;
					handler.sendMessage(msg);

				} else if (retStr.equals("false")) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.CONNECT_ERROR;
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
				Toast.makeText(GrantKidsDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case BleDeviceConstants.GRANT_SUCCESS:
				Toast.makeText(GrantKidsDialog.this,
						R.string.text_grant_success, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(GrantKidsDialog.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				//SearchGuestActivity.instance.finish();
				finish();
				break;

			case BleDeviceConstants.NO_SELECT_CHILDREN:
				Toast.makeText(GrantKidsDialog.this,
						R.string.text_select_child, Toast.LENGTH_LONG).show();

				break;

			}

		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
