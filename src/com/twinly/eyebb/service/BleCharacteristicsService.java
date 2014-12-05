package com.twinly.eyebb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.twinly.eyebb.activity.KidProfileActivity;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.SampleGattAttributes;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class BleCharacteristicsService extends Service {
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	TextView status_text;
	private String serviceComeFrom;
	int servidx, charaidx;

	private boolean loopToFindChars = true;
	BluetoothGattService gattService;
	ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
	ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
	// 做計時器 自動關閉activity 放置阻塞主線程
	private Timer timer;
	// 控制時間
	private int keepTim = 0;
	String uuid = null;
	String name = null;
	// private String uuid;
	public static final int FINISH_ACTIVITY = 1;
	public static final String EXTRAS_SERVICE_NAME = "SERVICE_NAME";
	private int beepUUID = -1;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// registerReceiver(mGattUpdateReceiver, new IntentFilter(
		// BluetoothLeService.ACTION_DATA_AVAILABLE));

		System.out.println("chara onCreate");

		super.onCreate();
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		serviceComeFrom = intent
				.getStringExtra(BleDeviceConstants.BLE_SERVICE_COME_FROM);
		System.out.println("char onStartCommand");
		new Thread() {
			public void run() {
				registerReceiver(mGattUpdateReceiver, new IntentFilter(
						BluetoothLeService.ACTION_DATA_AVAILABLE));

				if (intent == null) {
					// unregisterReceiver(mGattUpdateReceiver);
					stopSelf();
				} else {

					servidx = intent.getIntExtra(EXTRAS_SERVICE_NAME, -1);
					System.out.println("servidx char=>" + servidx);

					listItem = new ArrayList<HashMap<String, Object>>();

					gattService = BleDeviceConstants.gattServiceObject
							.get(servidx);

					Thread disconverThread = new Thread() {
						@SuppressLint("NewApi")
						public void run() {

							// System.out.println("disconverThread ");
							List<BluetoothGattCharacteristic> gattCharacteristics = gattService
									.getCharacteristics();

							// System.out.println("disconverThread. ");
							// Loops through available Characteristics.
							if (gattCharacteristics == null) {
								System.out.println("gattCharacteristics NULL");
							}

							for (int i = 0; i < gattCharacteristics.size(); i++) {
								charas.add(gattCharacteristics.get(i));
								// System.out.println("disconverThread1 ");
								HashMap<String, String> currentCharaData = new HashMap<String, String>();
								// System.out.println("disconverThread2 ");
								uuid = gattCharacteristics.get(i).getUuid()
										.toString();
								// System.out.println("disconverThread3 ");
								uuid = uuid.substring(4, 8);
								// System.out.println("uuid char==>" + uuid);

								name = SampleGattAttributes.lookup(uuid,
										"Unknow");
								currentCharaData.put("NAME", name);
								currentCharaData.put("UUID", uuid);
								gattCharacteristicGroupData
										.add(currentCharaData);
								addItem(name, uuid);
								// System.out.println("gattCharacteristic=>" +
								// gattCharacteristics.get(i).toString());
								if (serviceComeFrom.equals("radar")) {
									if (uuid.equals(BleDeviceConstants.BEEP_CHAR_UUID)) {
										final BluetoothGattCharacteristic characteristic = gattCharacteristics
												.get(i);
										final int charaProp = characteristic
												.getProperties();
										if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
											// uuid =
											// characteristic.getUuid().toString();
											System.out
													.println(" charas uuid ==>"
															+ uuid + "  " + i);
											// uuid = uuid.substring(4, 8);
											// uuid = uuid;
											charaidx = i;
											BleDeviceConstants.mBluetoothLeService
													.readCharacteristic(characteristic);

											break;
										}
									}
								} else if (serviceComeFrom.equals("battery")) {
									if (uuid.equals(BleDeviceConstants.BEEP_CHAR_BATTERY)) {
										final BluetoothGattCharacteristic characteristic = gattCharacteristics
												.get(i);
										final int charaProp = characteristic
												.getProperties();
										if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
											// uuid =
											// characteristic.getUuid().toString();
											System.out
													.println(" charas uuid ==>"
															+ uuid + "  " + i);
											// uuid = uuid.substring(4, 8);
											// uuid = uuid;
											charaidx = i;
											BleDeviceConstants.mBluetoothLeService
													.readCharacteristic(characteristic);

											break;
										}
									}
								}

							}

						}
					};
					disconverThread.start();

					// registerReceiver(mGattUpdateReceiver, new IntentFilter(
					// BluetoothLeService.ACTION_DATA_AVAILABLE));
				}

			};
		}.start();

		return super.onStartCommand(intent, flags, startId);

	}

	@SuppressLint("NewApi")
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub

		System.out.println("chara onStart");

		super.onStart(intent, startId);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("chara onDestroy");
		try {
			unregisterReceiver(mGattUpdateReceiver);

			// if (Constants.mBluetoothLeService != null) {
			// Constants.mBluetoothLeService.disconnect();
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Char Receiver not registered");
			e.printStackTrace();
		}

		stopSelf();
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				String data = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				System.out.println("data========>" + data);
				System.out.println("uuid========>" + uuid);
				if (uuid.equals("1001")) {
					System.out.println("this is 1001" + data + " "
							+ Integer.parseInt(data, 16));
					modify1001(data);
				} else if (uuid.equals("1004")) {
					System.out.println("this is battary life!!!!!!!!" + data
							+ "=>16/ " + Integer.parseInt(data, 16));
					SharePrefsUtils.setdeviceBattery(context,
							Integer.parseInt(data, 16) + "");
					// UPDATE BATTERY VIEW
					Intent broadcast = new Intent();
					broadcast
							.setAction(BleDeviceConstants.BROADCAST_GET_DEVICE_BATTERY);
					sendBroadcast(broadcast);

					stopService(BleServicesService.intentToChara);
					stopService(KidProfileActivity.checkBatteryService);

					if (BleDeviceConstants.mBluetoothLeService != null) {
						BleDeviceConstants.mBluetoothLeService.disconnect();
						BleDeviceConstants.mBluetoothLeService = null;
					}
				} else if (uuid.equals("1006")) {
					// modify1006(data);

				}
			}
		}

	};

	Runnable rinModify1001 = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {

			// RadarCharacteristicsActivity.this.finish();
			String data = "01";

			BluetoothGattCharacteristic characteristic = charas.get(charaidx);
			System.out
					.println("BluetoothGattCharacteristic characteristic = charas.get(charaidx);"
							+ " ==>"
							+ charaidx
							+ " "
							+ charas.get(charaidx).toString());
			characteristic.setValue(BLEUtils.HexString2Bytes(data));

			BleDeviceConstants.mBluetoothLeService.wirteCharacteristic(characteristic);
			System.out
					.println("Constants.mBluetoothLeService.wirteCharacteristic(characteristic);");
		}
	};

	@SuppressLint("NewApi")
	private void modify1001(String data) {
		// TODO Auto-generated method stub

		new Thread(rinModify1001).start();
		// unregisterReceiver(mGattUpdateReceiver);
		//
		// // mConnected = false;

		// data = "01";
		// // System.out.println("BLEUtils.HexString2Bytes(data)====>"
		// // + BLEUtils.HexString2Bytes(data));
		// BluetoothGattCharacteristic characteristic = charas.get(charaidx);
		// System.out
		// .println("BluetoothGattCharacteristic characteristic = charas.get(charaidx);"
		// + " ==>"
		// + charaidx
		// + " "
		// + charas.get(charaidx).toString());
		// characteristic.setValue(BLEUtils.HexString2Bytes(data));
		//
		// Constants.mBluetoothLeService.wirteCharacteristic(characteristic);
		// System.out
		// .println("Constants.mBluetoothLeService.wirteCharacteristic(characteristic);");
	}

	private void addItem(String devname, String address) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", devname);
		map.put("text", address);
		listItem.add(map);
		// listItemAdapter.notifyDataSetChanged();
	}

	// @SuppressLint("HandlerLeak")
	// Handler handler = new Handler() {
	//
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	//
	// case FINISH_ACTIVITY:
	// keepTim++;
	// if (keepTim == 3) {
	// new Thread(finishConnection).start();
	// }
	//
	// break;
	// }
	// }
	// };

}
