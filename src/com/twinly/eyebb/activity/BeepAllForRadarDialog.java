package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.service.BleServicesService;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class BeepAllForRadarDialog extends Activity {

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;

	private ArrayList<Child> BeepAllTempChildData;
	private Dialog dialog;
	public static int BeepAlli = 0;
	public static boolean BeepAllFlag = true;
	public static BeepAllForRadarDialog instance = null;
	public static boolean StartAllBeepFlag = true;
	public static int BeepAllTempChildDataSize = 0;
	private TextView notifyTxt;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_fragment_radar_tracking_beep_all);
		instance = this;
		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
		notifyTxt = (TextView) findViewById(R.id.notify_txt);

		final Intent intent = getIntent();
		//
		// BeepAllTempChildData = (ArrayList<Child>) intent
		// .getSerializableExtra(Constants.BEEP_ALL_DEVICE);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				BeepAllTempChildData = (ArrayList<Child>) intent
						.getSerializableExtra(BleDeviceConstants.BEEP_ALL_DEVICE);

				BeepAllTempChildDataSize = BeepAllTempChildData.size();
				//notifyTxt.setText(getString(R.string.text_connect_device));
				if (BeepAllTempChildDataSize > 0) {

					Message msg = handler.obtainMessage();
					msg.what = BleDeviceConstants.START_PROGRASSS_BAR;
					handler.sendMessage(msg);
					// 開始循環
					BeepAllFlag = true;
					StartAllBeepFlag = true;
					
					btnBeepAll.start();
				} else {
					notifyTxt
							.setText(getString(R.string.text_none_of_the_kids_that_stay_nearby));
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// notifyTxt.setText(getString(R.string.text_connect_device));
				finish();
			}
		});

		// dialog.setOnCancelListener(new OnCancelListener() {
		//
		// public void onCancel(DialogInterface dialog) {
		// // TODO Auto-generated method stub
		//
		// BeepAllFlag = false;
		// btnBeepAll.interrupt();
		// btnBeepAll = null;
		// BeepAllForRadarDialog.instance.finish();
		// // finish();
		// }
		// });

	}

	public boolean dispatchKeyEvent(KeyEvent event) {

		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			//device 进度恢复为1
			BeepAlli = 0;
			finish();
			break;
		default:
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BleDeviceConstants.START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialogCanCancel(
						BeepAllForRadarDialog.this,
						getString(R.string.toast_loading) + "\n" + BeepAlli
								+ "/" + BeepAllTempChildData.size());

				dialog.show();

				dialog.setOnKeyListener(new OnKeyListener() {

					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (keyCode == KeyEvent.KEYCODE_BACK
								&& event.getRepeatCount() == 0) {
							dialog.dismiss();
						}
						return false;
					}
				});

				break;

			case BleDeviceConstants.STOP_PROGRASSS_BAR:
				BeepAllFlag = false;
				btnBeepAll.interrupt();
				btnBeepAll = null;
				BeepAllForRadarDialog.instance.finish();
				finish();
				break;
			}
		}
	};

	protected void onDestroy() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

		// finish();
		super.onDestroy();
	};

	Thread btnBeepAll = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// firstly pop the dialog
			System.out.println("BeepAllFlag==>" + BeepAllFlag);
			while (BeepAllFlag) {

				if (StartAllBeepFlag) {

					StartAllBeepFlag = false;
					boolean isFinish = SharePrefsUtils
							.isfinishBeep(BeepAllForRadarDialog.this);
					if (BeepAlli < BeepAllTempChildData.size()) {
						if (isFinish) {

						} else {

							System.out.println("BeepAllTempChildData.size()==>"
									+ BeepAllTempChildData.size());
							if (BeepAllTempChildData.size() > 0) {
								// isFirstBeep = false;

								Intent beepIntent = new Intent();

								beepIntent.putExtra(
										BleServicesService.EXTRAS_DEVICE_NAME,
										"Macaron");
								beepIntent
										.putExtra(
												BleServicesService.EXTRAS_DEVICE_ADDRESS,
												BeepAllTempChildData.get(
														BeepAlli)
														.getMacAddress());
								System.out
										.println("BeepTempChildData.get(position).getMacAddress()==>"
												+ BeepAllTempChildData.get(
														BeepAlli)
														.getMacAddress());
								beepIntent
										.setAction("com.twinly.eyebb.service.BLE_SERVICES_SERVICES");
								// if (scan_flag) {
								// scanLeDevice(false);
								// }
								SharePrefsUtils.setConnectBleService(
										BeepAllForRadarDialog.this, 1);
								SharePrefsUtils.setfinishBeep(
										BeepAllForRadarDialog.this, true);

								startService(beepIntent);
							}
						}

					} else {
						BeepAllFlag = false;
						BeepAlli = 0;
						// StartAllBeepFlag = true;
						StartAllBeepFlag = true;
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}
						finish();
					}

				}

			}

		}
	});

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			// for (int i = 0; i < 2; i++) {
			// BeepAllForRadarDialog.instance.finish();
			// }
			// Message msg = handler.obtainMessage();
			// msg.what = Constants.STOP_PROGRASSS_BAR;
			// handler.sendMessage(msg);

			return true;
		}
		return super.onKeyDown(keyCode, event);

	};
}