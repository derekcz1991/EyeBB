package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.eyebb.R;
import com.twinly.eyebb.activity.MatchingVerificationActivity;
import com.twinly.eyebb.activity.ServicesActivity;
import com.twinly.eyebb.activity.VerifyBirthdayFromDeviceListActivity;
import com.twinly.eyebb.model.Device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceListAcitivity extends Activity {
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	private Button scanBtn;
	private boolean scan_flag = false;
	private final static int START_SCAN = 4;
	private final static int STOP_SCAN = 5;
	private final static int DELETE_SCAN = 6;
	private Handler autoScanHandler;
	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;

	private static final int POSTDELAYTIME = 29000;
	private static final int SCANTIME = 30000;
	// true 更新
	private Boolean isUpadate = false;

	int if_has = 0; // 1为有
	String UUID;
	List<String> address_temp = new ArrayList<String>();

	int UUID_i = 0;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();

	private String ourDeviceUUID = "4D616361726F6E202020202020202020";
	private int ourDefaultMajor = 8224;
	private int ourDefaultMinor = 8197;
	private HashMap<String, Device> deviceMap = new HashMap<String, Device>();;
	private long ChildIDfromKidsList;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.text_matching_device));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.ble_peripheral);
		BaseApp.getInstance().addActivity(this);

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
				final BluetoothDevice device = mLeDevices.get(arg2);
				System.out.println("arg2-PERI========>" + arg2);
				if (device == null)
					return;
				final Intent intent = new Intent();
				final Intent getIntent = getIntent();
				intent.setClass(DeviceListAcitivity.this,
						VerifyBirthdayFromDeviceListActivity.class);
				ChildIDfromKidsList = getIntent.getLongExtra("childID", 0);
				System.out.println("ChildIDfromKidsList=>"
						+ ChildIDfromKidsList);
				intent.putExtra("ChildIDfromDeviceList",
						ChildIDfromKidsList);
				String fromDeviceList = "DeviceListAcitivity";
				intent.putExtra("fromDeviceList",
						fromDeviceList);
	
				if (scan_flag) {
					scanLeDevice(false);
				}
				startActivity(intent);
				finish();
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
		
//		if (scan_flag) {
//			scanLeDevice(false);
//		}

		if (scan_flag) {
			autoScanHandler.postDelayed(autoScan, POSTDELAYTIME);
		} else {
			new Thread(autoScan).start();
		}

	}

	Runnable autoScan = new Runnable() {
		@Override
		public void run() {
			while (true) {
				if (scan_flag) {

					scanLeDevice(false);

				} else {

					scanLeDevice(true);
					try {
						Thread.sleep(SCANTIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	};

	// private void addItem(String devname, String address) {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("image", R.drawable.ble_icon);
	// map.put("title", devname);
	// map.put("text", address);
	// listItem.add(map);
	// listItemAdapter.notifyDataSetChanged();
	// }

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

	@SuppressLint("NewApi")
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
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

			}, POSTDELAYTIME);

			mBluetoothAdapter.startLeScan(mLeScanCallback);

			scan_flag = true;

			Message msg = handler.obtainMessage();
			msg.what = STOP_SCAN;
			handler.sendMessage(msg);
			// scanBtn.setText(R.string.stop_scan);

		} else {

			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			scan_flag = false;

			Message msg = handler.obtainMessage();
			msg.what = DELETE_SCAN;
			handler.sendMessage(msg);

			// autoScan.start();
		}

	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_SCAN:
				// scanBtn.setText("開始");
				// scanBtn.setText("start scan");
				break;

			case STOP_SCAN:
				// scanBtn.setText("結束");
				// scanBtn.setText("stop scan");
				break;

			case DELETE_SCAN:
				listItem.clear();
				listItemAdapter.notifyDataSetChanged();
				mLeDevices.clear();
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

					Device newDevice = new Device();
					newDevice.setAddress(device.getAddress());
					newDevice.setMajor(scanRecord[25] * 256 + scanRecord[26]);
					newDevice.setMinor(scanRecord[27] * 256 + scanRecord[28]);
					newDevice.setName(device.getName());
					newDevice.setUuid(bytesToHex(scanRecord, 9, 16));
					if (bytesToHex(scanRecord, 9, 16).equals(ourDeviceUUID)) {
						if (deviceMap.put(device.getAddress(), newDevice) == null) {
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
										+ " RSSI:" + rssi);
								listItem.add(map);
								mLeDevices.add(device);
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

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// Constans.exit_ask(this);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (scan_flag) {
				scanLeDevice(false);
			}
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
