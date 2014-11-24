package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.activity.ErrorDialog;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.SharePrefsUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ServicesActivity extends Activity {
	private final static String TAG = ServicesActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	TextView status_text;

	private Dialog dialog;
	final static int START_PROGRASSS_BAR = 1;
	final static int STOP_PROGRASSS_BAR = 2;

	private boolean runOnceFlag = true;
	private int repeat_read_times = 0;
	// sharedPreferences

	private String major;
	private String minor;
	Timer timer = new Timer();
	// private ArrayList<ArrayList<BluetoothGattCharacteristic>>
	// mGattCharacteristics = new
	// ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean mConnected = false;
	private int ReadService = 0;

	private TimerTask TimeOutTask = null;
	private int intentTime = 0;


	public static ServicesActivity instance;


	private long childId;
	

	@SuppressLint({ "NewApi", "ShowToast" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ble_services);
		
		childId = getIntent().getLongExtra("child_id", 0);
		
		BaseApp.getInstance().addActivity(this);
		instance = this;
		// setTitle(getString(R.string.toast_loading));
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setIcon(android.R.color.transparent);
		/*if (CharacteristicsMajorActivity.majorFinished) {
			// CharacteristicsActivity.instance.finish();
			dialog = LoadingDialog.createLoadingDialogCanCancel(
					ServicesActivity.this,
					getString(R.string.toast_read_service));
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					ServicesActivity.this.finish();

				}
			});
			dialog.show();

		} else {
			dialog = LoadingDialog.createLoadingDialogCanCancel(
					ServicesActivity.this,
					getString(R.string.toast_read_service));
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					ServicesActivity.this.finish();

				}
			});
			dialog.show();
		}*/

		TimeOutTask = new TimerTask() {
			public void run() {
				intentTime++;
				System.out.println("intentTime==>" + intentTime);
				if (intentTime == 10) {
					intentTime = 0;
					// Intent intent = new Intent(ServicesActivity.this,
					// ErrorDialog.class);
					// startActivity(intent);
					// if (dialog.isShowing())
					// dialog.dismiss();
					// CheckBeaconActivity.instance.finish();
					repeat_read_times++;

					// if (dialog.isShowing() && dialog != null) {
					// dialog.dismiss();
					// }
					//
					// dialog = LoadingDialog.createLoadingDialogCanCancel(
					// ServicesActivity.this,
					// getString(R.string.toast_repeat_read_service)
					// + repeat_read_times);
					// dialog.show();

					Message msg = handler.obtainMessage();
					msg.what = START_PROGRASSS_BAR;
					handler.sendMessage(msg);

					// Constants.mBluetoothLeService.connect(mDeviceAddress);
					registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
					if (BleDeviceConstants.mBluetoothLeService != null) {
						final boolean result = BleDeviceConstants.mBluetoothLeService
								.connect(mDeviceAddress);
						System.out.println("Connect request result=" + result);
					}
					// status_text.setText(mDeviceName + ": Connecting...");
					System.out.println(": Connecting..." + mDeviceAddress + " "
							+ repeat_read_times);

					if (repeat_read_times == 5) {
						repeat_read_times = 0;

						// finish activity

						// if error
						try {
							TimeOutTask.cancel();
							TimeOutTask = null;
							timer.cancel();
							timer.purge();
							timer = null;
						} catch (Exception e) {

							e.printStackTrace();
						}

						Intent intentError = new Intent(ServicesActivity.this,
								ErrorDialog.class);
						startActivity(intentError);
						finish();
					}
					// timer = null;
					// finish();
				}
			}
		};

		timer.schedule(TimeOutTask, 0, 1000);
		// major = MajorAndMinorPreferences.getString("major", "-1");
		// minor = MajorAndMinorPreferences.getString("minor", "-1");

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		System.out.println("mDeviceName:" + mDeviceName + " mDeviceAddress:"
				+ mDeviceAddress);
		BleDeviceConstants.gattServiceData.clear();
		BleDeviceConstants.gattServiceObject.clear();

		status_text = (TextView) findViewById(R.id.services_status);

		listItem = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.ble_services_listview,
				new String[] { "title", "text" }, new int[] {
						R.id.services_ItemTitle, R.id.services_ItemText });
		myList = (ListView) findViewById(R.id.services_listView);
		myList.setAdapter(listItemAdapter);

		// myList.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// final Intent intent = new Intent();
		// intent.setClass(ServicesActivity.this,
		// CharacteristicsActivity.class);
		// intent.putExtra("servidx", arg2);
		// System.out.println("servidxservidx=>" + arg2);
		// startActivity(intent);
		//
		// }
		// });

		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		boolean bll = bindService(gattServiceIntent, mServiceConnection,
				BIND_AUTO_CREATE);
		if (!bll) {
			Toast.makeText(this, "Bind Service Failed!", Toast.LENGTH_SHORT)
					.show();
			ServicesActivity.this.finish();
		}

		// Message msg = handler.obtainMessage();
		// msg.what = START_PROGRASSS_BAR;
		// handler.sendMessage(msg);

		/*
		 * 如果連接失敗則彈出 error dialog
		 */
		if (major.equals("") || minor.equals("")) {
			Toast.makeText(this, R.string.text_connect_error, Toast.LENGTH_LONG);
			if (dialog != null)
				dialog.dismiss();

			Toast.makeText(ServicesActivity.this, R.string.text_network_error,
					Toast.LENGTH_LONG).show();

			finish();
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case START_PROGRASSS_BAR:
				LoadingDialog.createLoadingDialogCanCancelForMsg(getResources()
						.getString(R.string.toast_repeat_read_service)
						+ repeat_read_times);

				break;

			case STOP_PROGRASSS_BAR:
				dialog.dismiss();
				break;
			}
		}
	};

	public class autoConnection extends Thread {
		@Override
		public void run() {
			if (runOnceFlag) {
				runOnceFlag = false;
				final Intent intentToChara = new Intent();
				/*System.out.println("CharacteristicsActivity.majorFinished-->"
						+ CharacteristicsMajorActivity.majorFinished);
				if (CharacteristicsMajorActivity.majorFinished) {
					intentToChara.setClass(ServicesActivity.this,
							CharacteristicsMinorActivity.class);
				} else {
					intentToChara.setClass(ServicesActivity.this,
							CharacteristicsMajorActivity.class);
				}*/

				if (ReadService > 0) {

					intentToChara.putExtra("servidx", ReadService);
					System.out.println("servidxservidx=>" + 2);
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}

					try {
						TimeOutTask.cancel();
						TimeOutTask = null;
						timer.cancel();
						timer.purge();
						timer = null;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					startActivity(intentToChara);

					ServicesActivity.this.finish();
				}

			}

		}
	}

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			System.out.println("mServiceConnectionmServiceConnection");
			BleDeviceConstants.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!BleDeviceConstants.mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			BleDeviceConstants.mBluetoothLeService.connect(mDeviceAddress);
			// status_text.setText(mDeviceName + ": Connecting...");
			System.out.println(": Connecting..." + mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			BleDeviceConstants.mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				BleDeviceConstants.mBluetoothLeService.discoverServices();
				status_text.setText(mDeviceName + ": Discovering services...");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;
				status_text.setText(mDeviceName + ": Disconnected");
				ServicesActivity.this.finish();
				// if error
				try {
					TimeOutTask.cancel();
					TimeOutTask = null;
					timer.cancel();
					timer.purge();
					timer = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Intent intentError = new Intent(ServicesActivity.this,
						ErrorDialog.class);
				startActivity(intentError);
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				// status_text.setText(mDeviceName + ": Discovered");
				displayGattServices(BleDeviceConstants.mBluetoothLeService
						.getSupportedGattServices());

				int num = SharePrefsUtils.BleServiceRunOnceFlag(context);
				// if (num == 1) {

				
				// SharePrefsUtils.setBleServiceRunOnceFlag(context, 2);
				// }

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// status_text.setText(mDeviceName + ": DATA AVAILABLE");
				String temp = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				System.out.println("ACTION_DATA_AVAILABLE--->" + temp);
			}

		}
	};

	private void addItem(String devname, String address) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", devname);
		map.put("text", address);
		listItem.add(map);
		listItemAdapter.notifyDataSetChanged();
	}

	private void deleteItem() {
		int size = listItem.size();
		if (size > 0) {
			listItem.remove(listItem.size() - 1);
			listItemAdapter.notifyDataSetChanged();
		}
	}

	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		String uuid = null;
		String name = null;

		// Loops through available GATT Services.
		for (int i = 0; i < gattServices.size(); i++) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();

			currentServiceData.put(LIST_NAME, name);
			currentServiceData.put(LIST_UUID, uuid);
			BleDeviceConstants.gattServiceData.add(currentServiceData);
			BleDeviceConstants.gattServiceObject.add(gattServices.get(i));
			// S addItem(name, uuid);
			// System.out.println("gattServices.get(i).getUuid()==>"
			// + gattServices.get(i).getUuid());

			if (gattServices.get(i).getUuid().toString()
					.equals(BleDeviceConstants.APPLICATION_UUID)) {

				ReadService = i;
				new autoConnection().start();
				break;
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (BleDeviceConstants.mBluetoothLeService != null) {
			final boolean result = BleDeviceConstants.mBluetoothLeService
					.connect(mDeviceAddress);
			System.out.println("Connect request result=" + result);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		try {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			TimeOutTask.cancel();
			TimeOutTask = null;
			timer.cancel();
			timer.purge();
			timer = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BleDeviceConstants.mBluetoothLeService = null;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		// intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("keyCode=>" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Constans.exit_ask(this);
			System.out.println("onKeyDown<----------");
			if (BleDeviceConstants.mBluetoothLeService != null) {
				BleDeviceConstants.mBluetoothLeService.disconnect();
			}
			try {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				TimeOutTask.cancel();
				TimeOutTask = null;
				timer.cancel();
				timer.purge();
				timer = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mConnected = false;
			ServicesActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}