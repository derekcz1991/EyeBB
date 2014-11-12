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

import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class VerifyWhenLoginDialog extends Activity {
	private TextView btnVerify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_verify);

		
//		Intent broadcast = new Intent();
//		broadcast.setAction(Constants.FINISH_BIND);
//		broadcast.putExtra("msg", true);
//		sendBroadcast(broadcast);
		
		btnVerify = (TextView) findViewById(R.id.btn_verify);
		btnVerify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Constans.mBluetoothLeService = null;
				// BluetoothLeService bls = new BluetoothLeService();
				// Intent gattServiceIntent = new Intent(VerifyDialog.this,
				// BluetoothLeService.class);
				// bls.onUnbind(gattServiceIntent);
				new Thread(postDeviceToChildToServerRunnable).start();

			}
		});

	}

	Runnable postDeviceToChildToServerRunnable = new Runnable() {
		@Override
		public void run() {
			// HANDLER
			// Message msg = handler.obtainMessage();
			// msg.what = START_PROGRASSS_BAR;
			// handler.sendMessage(msg);
			postDeviceToChildToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postDeviceToChildToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		map.put("childId",
				SharePrefsUtils.signUpChildId(VerifyWhenLoginDialog.this));
		map.put("macAddress",
				SharePrefsUtils.isMacAddress(VerifyWhenLoginDialog.this));
		map.put("major",
				SharePrefsUtils.signUpDeviceMajor(VerifyWhenLoginDialog.this));
		map.put("minor",
				SharePrefsUtils.signUpDeviceMinor(VerifyWhenLoginDialog.this));
		System.out
				.println("postDeviceToChildToServer=>"
						+ SharePrefsUtils
								.signUpChildId(VerifyWhenLoginDialog.this)
						+ " "
						+ SharePrefsUtils
								.isMacAddress(VerifyWhenLoginDialog.this)
						+ " "
						+ SharePrefsUtils
								.signUpDeviceMajor(VerifyWhenLoginDialog.this)
						+ " "
						+ SharePrefsUtils
								.signUpDeviceMinor(VerifyWhenLoginDialog.this));

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(
					HttpConstants.DEVICE_TO_CHILD, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");
				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);

			} else {
				// successful
				if (retStr.equals("true")) {

					Intent intent = new Intent(VerifyWhenLoginDialog.this,
							LancherActivity.class);

					startActivity(intent);

					
					SettingsActivity.instance.finish();
					CheckBeaconActivity.instance.finish();
					finish();

					// save to database
					// DBChildren.updateMacAddress(this, childIDfromDeviceList,
					// MACaddress4submit);
				} else {
					String major = retStr.substring(0, retStr.indexOf(":"));
					String minor = retStr.substring(retStr.indexOf(":") + 1,
							retStr.length());
					System.out.println("retStrpost======>" + major + " "
							+ minor);

					SharePrefsUtils.setSignUpDeviceMajor(
							VerifyWhenLoginDialog.this, major);
					SharePrefsUtils.setSignUpDeviceMinor(
							VerifyWhenLoginDialog.this, minor);

					// Intent intent = new Intent(CheckBeaconActivity.this,
					// ServicesActivity.class);
					//
					// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
					// deviceName4submit);
					// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
					// MACaddress4submit);
					//
					// startActivity(intent);
					Intent intent = new Intent(VerifyWhenLoginDialog.this,
							ErrorDialog.class);
					startActivity(intent);
					finish();

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

			// Message msg = handler.obtainMessage();
			// msg.what = STOP_PROGRASSS_BAR;
			// handler.sendMessage(msg);

		}

	}

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(VerifyWhenLoginDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			}
		}
	};
}
