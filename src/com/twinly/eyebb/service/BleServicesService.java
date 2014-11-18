package com.twinly.eyebb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.eyebb.R;
import com.twinly.eyebb.activity.BeepAllForRadarDialog;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.RadarCharacteristicsActivity;
import com.twinly.eyebb.bluetooth.RadarServicesActivity;
import com.twinly.eyebb.bluetooth.SampleGattAttributes;
import com.twinly.eyebb.bluetooth.RadarServicesActivity.autoConnection;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.SharePrefsUtils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BleServicesService extends Service {
	private final static String TAG = RadarServicesActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	public static Intent intentToChara = new Intent();;
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
	private static String mDeviceName;
	private static String mDeviceAddress;
	private boolean mConnected = false;
	private int ReadService = 1;

	// SharedPreferences SandVpreferences;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("Service onBind()");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("Service onCreate");
		// registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		// if (Constants.mBluetoothLeService != null) {
		// final boolean result = Constants.mBluetoothLeService
		// .connect(mDeviceAddress);
		// System.out.println("Connect request result=" + result);
		// }
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		new Thread() {
			public void run() {

				if (intent == null) {
					stopSelf();
				}
				mDeviceName = "";
				mDeviceAddress = "";

				try {
					mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
					mDeviceAddress = intent
							.getStringExtra(EXTRAS_DEVICE_ADDRESS);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("mDeviceName:" + mDeviceName
						+ " mDeviceAddress:" + mDeviceAddress);

				registerReceiver(mGattUpdateReceiver,
						makeGattUpdateIntentFilter());
				if (Constants.mBluetoothLeService != null) {
					final boolean result = Constants.mBluetoothLeService
							.connect(mDeviceAddress);
					System.out.println("Connect request result=" + result);
				}

				Constants.gattServiceData.clear();
				Constants.gattServiceObject.clear();

				// status_text = (TextView) findViewById(R.id.services_status);

				listItem = new ArrayList<HashMap<String, Object>>();
				// listItemAdapter = new SimpleAdapter(this, listItem,
				// R.layout.ble_services_listview,
				// new String[] { "title", "text" }, new int[] {
				// R.id.services_ItemTitle, R.id.services_ItemText });
				// myList = (ListView) findViewById(R.id.services_listView);
				// myList.setAdapter(listItemAdapter);

				Intent gattServiceIntent = new Intent(BleServicesService.this,
						BluetoothLeService.class);
				boolean bll = bindService(gattServiceIntent,
						mServiceConnection, BIND_AUTO_CREATE);
				if (!bll) {
					// Toast.makeText(this, "Bind Service Failed!",
					// Toast.LENGTH_SHORT)
					// .show();
					System.out.println("Bind Service Failed!");
				}
			};
		}.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.out.println("Service onStart");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("Service onDestroy");
		super.onDestroy();
		try {
			if (Constants.mBluetoothLeService != null) {
				Constants.mBluetoothLeService.disconnect();
			}
			unregisterReceiver(mGattUpdateReceiver);
			unbindService(mServiceConnection);
			mConnected = false;
			Constants.mBluetoothLeService = null;
			// if (Constants.mBluetoothLeService != null) {
			// Constants.mBluetoothLeService.disconnect();
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Server Receiver not registered");
			e.printStackTrace();
		}

		// Constants.mBluetoothLeService = null;

		// stopSelf();
	}

	Runnable finishConnection = new Runnable() {
		@Override
		public void run() {
			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (Constants.mBluetoothLeService != null) {
				Constants.mBluetoothLeService.disconnect();
			}
			mConnected = false;
			// RadarServicesActivity.this.finish();
		}
	};

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case FINISH_ACTIVITY:
				keepTim++;
				if (keepTim == 3) {
					new Thread(finishConnection).start();
				}

				break;
			}
		}
	};

	// public class autoConnection extends Thread {
	// @Override
	// public void run() {
	// for (int i = 0; i < listItem.size(); i++) {
	// if (listItem.get(i).get("text").toString().equals("1000")) {
	// final Intent intentToChara = new Intent();
	//
	// intentToChara.putExtra(
	// BleCharacteristicsService.EXTRAS_SERVICE_NAME, i);
	// System.out.println("servidxservidx=>" + i);
	// System.out.println("servidxservidx=>"
	// + listItem.get(i).get("text").toString());
	//
	// intentToChara
	// .setAction("com.twinly.eyebb.service.BLE_CHARACTERISTICS_SERVICES");
	//
	// startService(intentToChara);
	// // Constants.mBluetoothLeService = null;
	// }
	// }
	//
	// }
	// }

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			// System.out.println("mServiceConnectionmServiceConnection");
			Constants.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!Constants.mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				// BleServicesService.this.finish();
				stopSelf();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			Constants.mBluetoothLeService.connect(mDeviceAddress);
			// status_text.setText(mDeviceAddress + ": Connecting...");
			System.out.println(": Connecting..." + mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Constants.mBluetoothLeService = null;
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
				Constants.mBluetoothLeService.discoverServices();
				// status_text.setText(mDeviceName +
				// ": Discovering services...");
				System.out.println("(BluetoothLeService.ACTION_GATT_CONNECTED");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;
				stopSelf();
				System.out
						.println("BluetoothLeService.ACTION_GATT_DISCONNECTED");

				if (SharePrefsUtils.isBeepAllDevice(BleServicesService.this)) {
					if (BluetoothLeService.isSuccessfulWrite) {
						BluetoothLeService.isSuccessfulWrite = false;
						BeepAllForRadarDialog.BeepAlli++;
						BeepAllForRadarDialog.StartAllBeepFlag = true;
						LoadingDialog
								.createLoadingDialogCanCancelForMsg(getString(R.string.toast_loading)
										+ "\n"
										+ BeepAllForRadarDialog.BeepAlli
										+ "/"
										+ BeepAllForRadarDialog.BeepAllTempChildDataSize);
					}
					SharePrefsUtils.setBeepAllDevice(BleServicesService.this,
							false);
				}

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				// status_text.setText(mDeviceName + ": Discovered");
				int connect_ble_service = SharePrefsUtils
						.isConnectBleService(BleServicesService.this);
				displayGattServices(Constants.mBluetoothLeService
						.getSupportedGattServices());
				//
				// if (connect_ble_service == 1) {
				//
				// connect_ble_service++;
				// SharePrefsUtils.setConnectBleService(BleServicesService.this,
				// connect_ble_service);
				System.out
						.println("BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED");
				// }

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
		// listItemAdapter.notifyDataSetChanged();
	}

	private void deleteItem() {
		int size = listItem.size();
		if (size > 0) {
			listItem.remove(listItem.size() - 1);
			// listItemAdapter.notifyDataSetChanged();
		}
	}

	@SuppressLint("NewApi")
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
		for (int i = 0; i < gattServices.size(); i++) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			// uuid = gattServices.get(i).getUuid().toString();
			// uuid = uuid.substring(4, 8);
			// boolean exist = false;
			// for (HashMap<String, String> sItem : Constants.gattServiceData) {
			// if (sItem.get(LIST_UUID).equals(uuid)) {
			// exist = true;
			// break;
			// }
			// }
			// if (exist) {
			// continue;
			// }
			// name = SampleGattAttributes.lookup(uuid, "Unknow Service");
			currentServiceData.put(LIST_NAME, name);
			currentServiceData.put(LIST_UUID, uuid);
			Constants.gattServiceData.add(currentServiceData);
			Constants.gattServiceObject.add(gattServices.get(i));
			// S addItem(name, uuid);
			// System.out.println("gattServices.get(i).getUuid()==>"
			// + gattServices.get(i).getUuid());

			if (gattServices.get(i).getUuid().toString()
					.equals(Constants.APPLICATION_UUID)) {

				intentToChara.putExtra(
						BleCharacteristicsService.EXTRAS_SERVICE_NAME, i);
				System.out.println("servidxservidx=>" + i);
				// System.out.println("servidxservidx=>"
				// + listItem.get(i).get("text").toString());

				intentToChara
						.setAction("com.twinly.eyebb.service.BLE_CHARACTERISTICS_SERVICES");

				startService(intentToChara);
				// Constants.mBluetoothLeService = null;

				break;
			}
		}

		// for (BluetoothGattService gattService : gattServices) {
		// HashMap<String, String> currentServiceData = new HashMap<String,
		// String>();
		// uuid = gattService.getUuid().toString();
		// uuid = uuid.substring(4, 8);
		// boolean exist = false;
		// for (HashMap<String, String> sItem : Constants.gattServiceData) {
		// if (sItem.get(LIST_UUID).equals(uuid)) {
		// exist = true;
		// break;
		// }
		// }
		// if (exist) {
		// continue;
		// }
		// name = SampleGattAttributes.lookup(uuid, "Unknow Service");
		// currentServiceData.put(LIST_NAME, name);
		// currentServiceData.put(LIST_UUID, uuid);
		// Constants.gattServiceData.add(currentServiceData);
		// Constants.gattServiceObject.add(gattService);
		// addItem(name, uuid);
		// }

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

}
