package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class AuthorizeKidsActivity extends Activity {
	private ListView listView;
	private ChangeKidsListViewAdapter adapter;
	private Button btnAddNewGuest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_authorize_guest_list);
		new Thread(postFindGuestsToServerRunnable).start();
		btnAddNewGuest = (Button) findViewById(R.id.btn_add_new_guest);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new ChangeKidsListViewAdapter(this,
				DBChildren.getChildrenList(this), false);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

			
			}
		});

		btnAddNewGuest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AuthorizeKidsActivity.this,
						SearchGuestActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			String retStr = HttpRequestUtils.getNoPara(
					HttpConstants.AUTH_FIND_GUESTS);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 0) {

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

			}

		}
	};
}
