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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.SharePrefsUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RadarSOSServicesActivity extends Activity {
	private final static String TAG = RadarSOSServicesActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	TextView status_text;
	// 做計時器 自動關閉activity 放置阻塞主線程
	private Timer timer;
	// 控制時間
	private int keepTim = 0;
	private Dialog dialog;
	public final static int START_PROGRASSS_BAR = 1;
	public final static int STOP_PROGRASSS_BAR = 2;
	public static final int FINISH_ACTIVITY = 3;
	// sharedPreferences
	private SharedPreferences MajorAndMinorPreferences;
	private SharedPreferences.Editor editor;
	private String major;
	private String minor;

	// private ArrayList<ArrayList<BluetoothGattCharacteristic>>
	// mGattCharacteristics = new
	// ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean mConnected = false;
	private int ReadService = 1;

	//SharedPreferences SandVpreferences;
	@SuppressLint({ "NewApi", "ShowToast" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ble_services);
		BaseApp.getInstance().addActivity(this);
		// setTitle(getString(R.string.toast_loading));
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setIcon(android.R.color.transparent);
		dialog = LoadingDialog.createLoadingDialog(
				RadarSOSServicesActivity.this,
				getString(R.string.toast_loading));
		dialog.show();
		//
		//		SandVpreferences = getSharedPreferences(
		//				"soundAndVibrate", MODE_PRIVATE);
		//		editor = SandVpreferences.edit();
		//		

		//		major = MajorAndMinorPreferences.getString("major", "-1");
		//		minor = MajorAndMinorPreferences.getString("minor", "-1");

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
			RadarSOSServicesActivity.this.finish();
		}

		// Message msg = handler.obtainMessage();
		// msg.what = START_PROGRASSS_BAR;
		// handler.sendMessage(msg);
		//
		// if (major.equals("-1") || minor.equals("-1")) {
		// Toast.makeText(this, R.string.text_connect_error, Toast.LENGTH_LONG);
		// if (dialog != null)
		// dialog.dismiss();
		//
		// editor.putBoolean("connectFail", true);
		// editor.commit();
		// Intent intentKidsListActivity = new
		// Intent(RadarServicesActivity.this,
		// ErrorDialog.class);
		// startActivity(intentKidsListActivity);
		// finish();
		// }
	}

	// 倒計時
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = FINISH_ACTIVITY;
			handler.sendMessage(message);
		}
	};

	Runnable finishConnection = new Runnable() {
		@Override
		public void run() {
			//			try {
			//				Thread.sleep(1000);
			//			} catch (InterruptedException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			if (BleDeviceConstants.mBluetoothLeService != null) {
				BleDeviceConstants.mBluetoothLeService.disconnect();
			}
			mConnected = false;
			RadarSOSServicesActivity.this.finish();
		}
	};

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialog(
						RadarSOSServicesActivity.this,
						getString(R.string.toast_loading));
				dialog.show();
				break;

			case STOP_PROGRASSS_BAR:
				dialog.dismiss();
				break;

			case FINISH_ACTIVITY:
				keepTim++;
				if (keepTim == 3) {
					new Thread(finishConnection).start();
				}

				break;
			}
		}
	};

	public class autoConnection extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < listItem.size(); i++) {
				if (listItem.get(i).get("text").equals("1000")) {
					final Intent intentToChara = new Intent();
					intentToChara.setClass(RadarSOSServicesActivity.this,
							RadarSOSCharacteristicsActivity.class);
					intentToChara.putExtra("servidx", i);
					System.out.println("servidxservidx=>" + i);

					timer = new Timer(true);
					timer.schedule(task, 1000, 1000); // 延时1000ms后执行，1000ms执行一次

					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}

					startActivity(intentToChara);

					RadarSOSServicesActivity.this.finish();
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
			System.out.println(": Connecting...");
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
				RadarSOSServicesActivity.this.finish();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				status_text.setText(mDeviceName + ": Discovered");
				displayGattServices(BleDeviceConstants.mBluetoothLeService
						.getSupportedGattServices());

				//int num = MajorAndMinorPreferences.getInt("runNumRadar", 1);
				int num = SharePrefsUtils.CancelConnectBleServiceTimes(context);
				System.out.println("numnumnumnum" + num);
				if (num == 1) {
					new autoConnection().start();
					//					editor.putInt("runNumRadar", 2);
					//					editor.commit();
					SharePrefsUtils.setCancelConnectBleServiceTimes(context, 2);
				}

			}
			/*
			 * else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
			 * { status_text.setText(mDeviceName+": DATA AVAILABLE"); String
			 * temp = intent.getStringExtra(BluetoothLeService.EXTRA_DATA); }
			 */
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

		// ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
		// = new ArrayList<ArrayList<HashMap<String, String>>>();
		// mGattCharacteristics = new
		// ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			uuid = uuid.substring(4, 8);
			boolean exist = false;
			for (HashMap<String, String> sItem : BleDeviceConstants.gattServiceData) {
				if (sItem.get(LIST_UUID).equals(uuid)) {
					exist = true;
					break;
				}
			}
			if (exist) {
				continue;
			}
			name = SampleGattAttributes.lookup(uuid, "Unknow Service");
			currentServiceData.put(LIST_NAME, name);
			currentServiceData.put(LIST_UUID, uuid);
			BleDeviceConstants.gattServiceData.add(currentServiceData);
			BleDeviceConstants.gattServiceObject.add(gattService);
			addItem(name, uuid);
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
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Constans.exit_ask(this);
			if (BleDeviceConstants.mBluetoothLeService != null) {
				BleDeviceConstants.mBluetoothLeService.disconnect();
			}
			mConnected = false;
			RadarSOSServicesActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}