package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.eyebb.R;
import com.twinly.eyebb.bluetooth.BaseApp;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.bluetooth.ServicesActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class CheckBeaconActivity extends Activity {
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	private Button scanBtn;
	private boolean scan_flag = false;
	private final static int START_SCAN = 4;
	private final static int STOP_SCAN = 5;
	private final static int DELETE_SCAN = 6;
	private final static int ALREADY_BING_DEVICE = 7;
	public static final int CONNECT_ERROR = 8;
	private Handler autoScanHandler;
	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;

	private static final int POSTDELAYTIME = 10000;
	private static final int SCANTIME = 10500;
	// true 更新
	private Boolean isUpadate = false;

	int if_has = 0; // 1为有
	String UUID;
	List<String> address_temp = new ArrayList<String>();

	int UUID_i = 0;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();

	private HashMap<String, Device> deviceMap = new HashMap<String, Device>();;
	private long ChildIDfromKidsList;

	private String getDeviceMajorAndMinorURL = "reportService/api/checkBeacon";
	private Dialog dialog;
	private String major;
	private String minor;
	final static int START_PROGRASSS_BAR = 1;
	final static int STOP_PROGRASSS_BAR = 2;
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	private Boolean isWhileLoop = true;
	private Boolean isConnectError = false;

	private String MACaddress4submit;
	private String deviceName4submit;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.text_matching_device));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.ble_peripheral);
		BaseApp.getInstance().addActivity(this);
		Constants.gattServiceData.clear();
		Constants.gattServiceObject.clear();

		mHandler = new Handler();
		autoScanHandler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();

		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.ble_listview,
				new String[] { "image", "title", "text" }, new int[] {
						R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });
		myList = (ListView) findViewById(R.id.listView_peripheral);
		myList.setAdapter(listItemAdapter);

		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out.println("isConnectError=>" + isConnectError);

				final BluetoothDevice device = mLeDevices.get(arg2);
				System.out.println("arg2-PERI========>" + arg2);
				if (device == null)
					return;

				System.out.println("ChildIDfromKidsList=>"
						+ ChildIDfromKidsList);

				SharePrefsUtils.setBleServiceRunOnceFlag(
						CheckBeaconActivity.this, 1);

				MACaddress4submit = device.getAddress();
				deviceName4submit = device.getName();

				new Thread(postToServerRunnable).start();

				System.out.println("new Thread(postToServerRunnable).start();");
				if (scan_flag) {
					scanLeDevice();
				}

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				autoScanHandler.removeCallbacks(autoScan);
				mHandler.removeCallbacks(scanLeDeviceRunable);
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				isWhileLoop = false;

				//finish();
			}

		});

		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.text_ble_not_supported,
					Toast.LENGTH_SHORT).show();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.text_error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
		}

		// if (scan_flag) {
		// scanLeDevice(false);
		// }

		if (scan_flag) {

			autoScanHandler.postDelayed(autoScan, POSTDELAYTIME);
		} else {
			new Thread(autoScan).start();
		}
		// new Thread(autoScan).start();
	}

	Runnable autoScan = new Runnable() {
		@Override
		public void run() {
			// System.out.println("isWhileLoop==>" + isWhileLoop);
			while (isWhileLoop) {
				scanLeDevice();
			}

		}
	};

	@SuppressLint("NewApi")
	public void scanLeDevice() {

		// Stops scanning after a pre-defined scan period.
		try {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mBluetoothAdapter.startLeScan(mLeScanCallback);

		try {

			Thread.sleep(Constants.SCAN_INRERVAL_TIME);

			mBluetoothAdapter.stopLeScan(mLeScanCallback);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void deleteItem() {
		int size = listItem.size();
		if (size > 0) {

			listItem.remove(listItem.size() - 1);
			listItemAdapter.notifyDataSetChanged();
		}
		// mLeDevices.clear();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	Runnable scanLeDeviceRunable = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			// scan_flag = false;

			// HANDLER
			Message msg = handler.obtainMessage();
			msg.what = START_SCAN;
			handler.sendMessage(msg);

			// new autoConnection().start();
			// autoScan.start();

		}
	};
	//
	// @SuppressLint("NewApi")
	// public void scanLeDevice(final boolean enable) {
	// if (enable) {
	// // // Stops scanning after a pre-defined scan period.
	// mHandler.postDelayed(scanLeDeviceRunable, POSTDELAYTIME);
	//
	// mBluetoothAdapter.startLeScan(mLeScanCallback);
	//
	// scan_flag = true;
	//
	// Message msg = handler.obtainMessage();
	// msg.what = STOP_SCAN;
	// handler.sendMessage(msg);
	//
	// } else {
	//
	// mBluetoothAdapter.stopLeScan(mLeScanCallback);
	// scan_flag = false;
	//
	// Message msg = handler.obtainMessage();
	// msg.what = DELETE_SCAN;
	// handler.sendMessage(msg);
	//
	// // autoScan.start();
	// }
	//
	// }

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ALREADY_BING_DEVICE:
				Toast.makeText(CheckBeaconActivity.this,
						R.string.text_device_already_binded, Toast.LENGTH_LONG)
						.show();
				break;

			case CONNECT_ERROR:
				Toast.makeText(CheckBeaconActivity.this,
						R.string.text_check_username_error, Toast.LENGTH_LONG)
						.show();

				break;

			case DELETE_SCAN:
				listItem.clear();
				listItemAdapter.notifyDataSetChanged();
				mLeDevices.clear();
				break;
			case START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialog(
						CheckBeaconActivity.this,
						getString(R.string.toast_loading));
				dialog.show();
				break;

			case STOP_PROGRASSS_BAR:
				// Toast.makeText(DeviceListAcitivity.this,
				// R.string.text_connect_error,
				// Toast.LENGTH_LONG);
				dialog.dismiss();

				break;

			}
		}
	};

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int majorid, minorid;
					// System.out
					// .println(" BluetoothAdapter.LeScanCallback mLeScanCallback");
					Device newDevice = new Device();
					newDevice.setAddress(device.getAddress());
					newDevice.setMajor(scanRecord[25] * 256 + scanRecord[26]);
					newDevice.setMinor(scanRecord[27] * 256 + scanRecord[28]);
					newDevice.setName(device.getName());
					newDevice.setRssi(rssi);
					newDevice.setUuid(bytesToHex(scanRecord, 9, 16));
					if (bytesToHex(scanRecord, 9, 16).equals(
							Constants.DEVICE_UUID_VERSON_1)
							|| bytesToHex(scanRecord, 9, 16).substring(8, 32)
									.equals(Constants.DEVICE_UUID_VERSON_2)) {
						if (deviceMap.put(device.getAddress(), newDevice) != null) {
							Iterator<Entry<String, Device>> it = deviceMap
									.entrySet().iterator();
							listItem.clear();
							while (it.hasNext()) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								Map.Entry<String, Device> entry = it.next();
								map.put("image", R.drawable.ble_icon);
								map.put("title", entry.getValue().getName());
								map.put("text", entry.getValue().getAddress()
										+ " UUID:" + entry.getValue().getUuid()
										+ " MajorID:"
										+ entry.getValue().getMajor()
										+ " MinorID:"
										+ entry.getValue().getMinor()
										+ " RSSI:" + entry.getValue().getRssi());
								listItem.add(map);
								mLeDevices.add(device);
								// System.out.println("mLeDevicesmLeDevices=>"
								// + listItem.size());
							}
							listItemAdapter.notifyDataSetChanged();

						}
					}

				}
			});
		}
	};

	public static String bytesToHex(byte[] bytes, int begin, int length) {
		StringBuilder sbuf = new StringBuilder();
		for (int idx = begin; idx < begin + length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10)
				sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase());
		}
		return sbuf.toString();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (scan_flag) {
				scanLeDevice();
			}
			isWhileLoop = false;
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	Runnable postToServerRunnable = new Runnable() {
		@Override
		public void run() {
			// HANDLER
			// Message msg = handler.obtainMessage();
			// msg.what = START_PROGRASSS_BAR;
			// handler.sendMessage(msg);
			postCheckBeaconToServer(ChildIDfromKidsList);

		}
	};

	@SuppressLint("ShowToast")
	private void postCheckBeaconToServer(long childIDfromDeviceList) {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		map.put("childId",
				SharePrefsUtils.signUpChildId(CheckBeaconActivity.this));
		map.put("macAddress", MACaddress4submit);
		System.out.println("CheckBeacon=>"
				+ SharePrefsUtils.signUpChildId(CheckBeaconActivity.this) + " "
				+ MACaddress4submit);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(HttpConstants.CHECK_BEACON,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");
				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);

			} else {
				// successful
				if (retStr.length() > 0) {
					major = retStr.substring(0, retStr.indexOf(":"));
					minor = retStr.substring(retStr.indexOf(":") + 1,
							retStr.length());
					System.out.println("retStrpost======>" + major + " "
							+ minor);

					SharePrefsUtils.setSignUpDeviceMajor(
							CheckBeaconActivity.this, major);
					SharePrefsUtils.setSignUpDeviceMinor(
							CheckBeaconActivity.this, minor);

					// Intent intent = new Intent(CheckBeaconActivity.this,
					// ServicesActivity.class);
					//
					// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
					// deviceName4submit);
					// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
					// MACaddress4submit);
					//
					// startActivity(intent);
					new Thread(postDeviceToChildToServerRunnable).start();
					// save to database
					// DBChildren.updateMacAddress(this, childIDfromDeviceList,
					// MACaddress4submit);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = ALREADY_BING_DEVICE;
					handler.sendMessage(msg);

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

			// Message msg = handler.obtainMessage();
			// msg.what = STOP_PROGRASSS_BAR;
			// handler.sendMessage(msg);

		}

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
				SharePrefsUtils.signUpChildId(CheckBeaconActivity.this));
		map.put("macAddress", MACaddress4submit);
		map.put("major",
				SharePrefsUtils.signUpDeviceMajor(CheckBeaconActivity.this));
		map.put("minor",
				SharePrefsUtils.signUpDeviceMinor(CheckBeaconActivity.this));
		System.out.println("postDeviceToChildToServer=>"
				+ SharePrefsUtils.signUpChildId(CheckBeaconActivity.this) + " "
				+ MACaddress4submit + " "
				+ SharePrefsUtils.signUpDeviceMajor(CheckBeaconActivity.this)
				+ " "
				+ SharePrefsUtils.signUpDeviceMinor(CheckBeaconActivity.this));

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(HttpConstants.CHECK_BEACON,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");
				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);

			} else {
				// successful
				if (retStr.equals("true")) {

					Intent intent = new Intent(CheckBeaconActivity.this,
							VerifyDialog.class);

					startActivity(intent);
					// save to database
					// DBChildren.updateMacAddress(this, childIDfromDeviceList,
					// MACaddress4submit);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = ALREADY_BING_DEVICE;
					handler.sendMessage(msg);

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

			// Message msg = handler.obtainMessage();
			// msg.what = STOP_PROGRASSS_BAR;
			// handler.sendMessage(msg);

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		autoScanHandler.removeCallbacks(autoScan);
		mHandler.removeCallbacks(scanLeDeviceRunable);
		isWhileLoop = false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		autoScanHandler.removeCallbacks(autoScan);
		mHandler.removeCallbacks(scanLeDeviceRunable);
		isWhileLoop = false;
	}

}