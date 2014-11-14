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
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class UnbindDeviceDialog extends Activity {

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_unbind_device);

		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// keyboard
				new Thread(postUnbindChildBeaconToServerRunnable).start();
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

	Runnable postUnbindChildBeaconToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postUnbindChildBeaconToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postUnbindChildBeaconToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();

		map.put("childId",
				SharePrefsUtils.signUpChildId(UnbindDeviceDialog.this));
		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.postTo(UnbindDeviceDialog.this,
					HttpConstants.UNBIND_CHILD_BEACON, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("N")) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.UNBIND_FAIL;
					handler.sendMessage(msg);
					finish();

				} else if (retStr.equals("Y")) {
					// Intent data = new Intent(UnbindDeviceDialog.this,
					// LancherActivity.class);
					Message msg = handler.obtainMessage();
					msg.what = Constants.UNBIND_SUCCESS;
					handler.sendMessage(msg);
					DBChildren.deleteDeviceOfChild(UnbindDeviceDialog.this,
							SharePrefsUtils
									.signUpChildId(UnbindDeviceDialog.this));
					//restartApplication();
					Intent intent = new Intent(UnbindDeviceDialog.this,
							SettingsActivity.class);
					
					startActivity(intent);
					
					SettingsActivity.instance.finish();
					LoginAuthKidsActivity.instance.finish();
					finish();
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
				Toast.makeText(UnbindDeviceDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case Constants.UNBIND_SUCCESS:
				Toast.makeText(UnbindDeviceDialog.this,
						R.string.text_unbind_success, Toast.LENGTH_LONG).show();

				break;

			case Constants.UNBIND_FAIL:

				Toast.makeText(UnbindDeviceDialog.this,
						R.string.text_unbind_fail, Toast.LENGTH_LONG).show();

				// parseJson(getData).clear();
				break;

			}

		}
	};

	private void restartApplication() {
		Intent intent = new Intent(this, LancherActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
