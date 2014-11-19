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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class UnbindDeviceDialog extends Activity {

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;
	private long childId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_unbind_device);

		childId = getIntent().getLongExtra("child_id", 0);

		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(postUnbindChildBeaconToServerRunnable).start();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

	private void postUnbindChildBeaconToServer() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("childId", String.valueOf(childId));
		try {
			String retStr = HttpRequestUtils.postTo(UnbindDeviceDialog.this,
					HttpConstants.UNBIND_CHILD_BEACON, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = BleDeviceConstants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("N")) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.UNBIND_FAIL;
					handler.sendMessage(msg);

					setResult(ActivityConstants.RESULT_UNBIND_CANCEL);
					finish();

				} else if (retStr.equals("Y")) {
					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.UNBIND_SUCCESS;
					handler.sendMessage(msg);
					DBChildren.updateMacAddressByChildId(
							UnbindDeviceDialog.this, childId, "");

					//UPDATE RADAR VIEW
					Intent broadcast = new Intent();
					broadcast.setAction(BleDeviceConstants.FINISH_BIND);
					sendBroadcast(broadcast);
					
					System.out.println("=====>>>>");
					setResult(ActivityConstants.RESULT_UNBIND_SUCCESS);
					finish();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BleDeviceConstants.CONNECT_ERROR:
				Toast.makeText(UnbindDeviceDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();
				break;

			case BleDeviceConstants.UNBIND_SUCCESS:
				Toast.makeText(UnbindDeviceDialog.this,
						R.string.text_unbind_success, Toast.LENGTH_LONG).show();
				break;

			case BleDeviceConstants.UNBIND_FAIL:
				Toast.makeText(UnbindDeviceDialog.this,
						R.string.text_unbind_fail, Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

}
