package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class SearchGuestActivity extends Activity {
	private EditText edGuestname;
	private TextView btnSearchNewGuest;
	private String guestName;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_search_guest_list);

		edGuestname = (EditText) findViewById(R.id.ed_guestname);
		btnSearchNewGuest = (TextView) findViewById(R.id.btn_search_new_guest);

		btnSearchNewGuest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				guestName = edGuestname.getText().toString();

			}
		});

	}

	Runnable postFeedBackToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postFeedBackRelationToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postFeedBackRelationToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + guestName);

		map.put("guestName", guestName);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(SearchGuestActivity.this,
					HttpConstants.SEARCH_GUEST, map);
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
					msg.what = Constants.SUCCESS_SEARCH;
					handler.sendMessage(msg);

					finish();
				} else {
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

				break;

			case Constants.SEARCH_GUEST_NULL:
				Toast.makeText(SearchGuestActivity.this,
						R.string.text_search_guest_null, Toast.LENGTH_LONG)
						.show();

				break;

			}

		}
	};
}
