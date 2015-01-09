package com.twinly.eyebb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.BeepAllForRadarDialog;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.RadarServicesActivity;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.BroadcastUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

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

	private int batteryIntentTime = 0;
	private TimerTask TimeOutTask = null;
	Timer timer = new Timer();

	// private ArrayList<ArrayList<BluetoothGattCharacteristic>>
	// mGattCharacteristics = new
	// ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private static String mDeviceName;
	private static String mDeviceAddress;
	private boolean mConnected = false;
	private int ReadService = 1;

	private String serviceComeFrom;

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
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		new Thread() {
			public void run() {
				//timer = 0
				batteryIntentTime = 0;
				if (intent == null) {
					stopSelf();
				}
				if (TimeOutTask != null) {
					TimeOutTask.cancel();
					TimeOutTask = null;
				}
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
				mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
				serviceComeFrom = intent
						.getStringExtra(BleDeviceConstants.BLE_SERVICE_COME_FROM);

				try {
					if (serviceComeFrom.equals("battery")) {
						TimeOutTask = new TimerTask() {
							public void run() {
								batteryIntentTime++;
								System.out.println("batteryIntentTime==>"
										+ batteryIntentTime);
								if (batteryIntentTime == 20) {
									batteryIntentTime = 21;
									// UPDATE BATTERY VIEW
									Intent broadcast = new Intent();
									broadcast
											.setAction(BroadcastUtils.BROADCAST_GET_DEVICE_BATTERY);
									SharePrefsUtils.setdeviceBattery(
											BleServicesService.this, "");
									sendBroadcast(broadcast);
									if (TimeOutTask != null) {
										TimeOutTask.cancel();
										TimeOutTask = null;
									}
									if (timer != null) {
										timer.cancel();
										timer = null;
									}
								}
							}
						};
						timer = new Timer();
						timer.schedule(TimeOutTask, 0, 1000);
					}
					System.out.println("serviceComeFrom===>" + serviceComeFrom);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("mDeviceName:" + mDeviceName
						+ " mDeviceAddress:" + mDeviceAddress);

				registerReceiver(mGattUpdateReceiver,
						makeGattUpdateIntentFilter());
				if (BleDeviceConstants.mBluetoothLeService != null) {
					final boolean result = BleDeviceConstants.mBluetoothLeService
							.connect(mDeviceAddress);
					System.out.println("Connect request result=" + result);
				}

				BleDeviceConstants.gattServiceData.clear();
				BleDeviceConstants.gattServiceObject.clear();

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
			if (BleDeviceConstants.mBluetoothLeService != null) {
				BleDeviceConstants.mBluetoothLeService.disconnect();
			}
			unregisterReceiver(mGattUpdateReceiver);
			unbindService(mServiceConnection);
			mConnected = false;
			BleDeviceConstants.mBluetoothLeService = null;
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
			if (BleDeviceConstants.mBluetoothLeService != null) {
				BleDeviceConstants.mBluetoothLeService.disconnect();
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

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			// System.out.println("mServiceConnectionmServiceConnection");
			BleDeviceConstants.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!BleDeviceConstants.mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				// BleServicesService.this.finish();
				stopSelf();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			BleDeviceConstants.mBluetoothLeService.connect(mDeviceAddress);
			// status_text.setText(mDeviceAddress + ": Connecting...");
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

				displayGattServices(BleDeviceConstants.mBluetoothLeService
						.getSupportedGattServices());

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

	@SuppressLint("NewApi")
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

				intentToChara.putExtra(
						BleCharacteristicsService.EXTRAS_SERVICE_NAME, i);
				System.out.println("servidxservidx=>" + i);
				// System.out.println("servidxservidx=>"
				// + listItem.get(i).get("text").toString());

				intentToChara
						.setAction("com.twinly.eyebb.service.BLE_CHARACTERISTICS_SERVICES");
				intentToChara.putExtra(
						BleDeviceConstants.BLE_SERVICE_COME_FROM,
						serviceComeFrom);
				startService(intentToChara);
				// Constants.mBluetoothLeService = null;
				try {
					if (TimeOutTask != null) {
						TimeOutTask.cancel();
						TimeOutTask = null;
					}
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

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
