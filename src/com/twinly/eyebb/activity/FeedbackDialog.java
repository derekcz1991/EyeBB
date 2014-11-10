package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class FeedbackDialog extends Activity {

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;
	private String content;
	private EditText ed;
	private RadioGroup group;
	private int radioButtonId;
	private String type;

	public static final int SUCCESS_FEEDBACK = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_feedback);

		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
		ed = (EditText) findViewById(R.id.feedback_comments);
		group = (RadioGroup) findViewById(R.id.feedback_rg);

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				radioButtonId = checkedId;
				switch (checkedId) {
				case R.id.radio_bug:
					type = "B";
					break;

				case R.id.radio_idea:
					type = "I";
					break;

				case R.id.radio_question:
					type = "Q";
					break;

				}
				// 根据ID获取RadioButton的实例
				System.out.println("radioButtonId>" + radioButtonId + "--"
						+ checkedId);
			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				content = ed.getText().toString();

				new Thread(postFeedBackToServerRunnable).start();

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
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
		System.out.println("info=>" + content + " " + radioButtonId + "");

		map.put("content", content);
		map.put("type", type);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(FeedbackDialog.this,
					HttpConstants.FEED_BACK, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("true")) {
					Message msg = handler.obtainMessage();
					msg.what = SUCCESS_FEEDBACK;
					handler.sendMessage(msg);

					finish();
				} else if (retStr.equals("false")) {
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
				Toast.makeText(FeedbackDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG)
						.show();

				break;
			case SUCCESS_FEEDBACK:
				Toast.makeText(FeedbackDialog.this,
						R.string.text_feed_back_successful, Toast.LENGTH_LONG)
						.show();

				break;
			}

		}
	};
}
