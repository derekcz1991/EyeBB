package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.eyebb.R;

@SuppressLint("NewApi")
public class PeripheralActivity extends Activity {
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	private Button scanBtn;
	private boolean scan_flag = false;
	private final static int START_SCAN = 4;
	private final static int STOP_SCAN = 5;
	private final static int DELETE_SCAN = 6;

	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;
	// true 为第一次扫描
	Boolean firstScan = true;
	int if_has = 1; // 1为有
	String UUID;
	List<String> UUID_temp = new ArrayList<String>();
	int UUID_i = 0;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ble_peripheral);
		BaseApp.getInstance().addActivity(this);

		mHandler = new Handler();

		listItem = new ArrayList<HashMap<String, Object>>();

		listItemAdapter = new SimpleAdapter(this, listItem, R.layout.ble_listview,
				new String[] { "image", "title", "text" }, new int[] {
						R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });
		myList = (ListView) findViewById(R.id.listView);
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
				intent.setClass(PeripheralActivity.this, ServicesActivity.class);
				intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
						device.getName());
				intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
						device.getAddress());
				if (scan_flag) {
					scanLeDevice(false);
				}
				startActivity(intent);
			}
		});

//		scanBtn = (Button) findViewById(R.id.scanButton);
//		scanBtn.setOnClickListener(new Button.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				firstScan = true;
//				if (scan_flag) {
//					scanLeDevice(false);
//				} else {
//					scanLeDevice(true);
//				}
//			}
//		});

		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
//			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
//					.show();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
//			Toast.makeText(this, R.string.error_bluetooth_not_supported,
//					Toast.LENGTH_SHORT).show();
		}

		//autoScan.start();

	}

	private class autoConnection extends Thread {
		@Override
		public void run() {
			// 测试自动连接功能
			if (mLeDevices.size() > 0) {
				BluetoothDevice device = mLeDevices.get(0);

				Intent intent = new Intent();
				intent.setClass(PeripheralActivity.this, ServicesActivity.class);
				intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
						device.getName());
				intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
						device.getAddress());
				if (scan_flag) {
					scanLeDevice(false);
				}
				startActivity(intent);
			}

		}
	}

	Thread autoScan = new Thread() {
		@Override
		public void run() {
			// 自动扫描

			if (scan_flag) {
				scanLeDevice(false);

			} else {
				scanLeDevice(true);

			}

		}
	};

	private void addItem(String devname, String address) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("image", R.drawable.ble_icon);
		map.put("title", devname);
		map.put("text", address);
		listItem.add(map);
		listItemAdapter.notifyDataSetChanged();
	}

	private void deleteItem() {
		int size = listItem.size();
		if (size > 0) {

			// HANDLER
			Message msg = handler.obtainMessage();
			msg.what = DELETE_SCAN;
			handler.sendMessage(msg);
		}
		mLeDevices.clear();
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

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					scan_flag = false;

					// HANDLER
					Message msg = handler.obtainMessage();
					msg.what = START_SCAN;
					handler.sendMessage(msg);
					// scanBtn.setText(R.string.start_scan);
					
					//自动连接
					//new autoConnection().start();
					// autoScan.start();
				}

			}, SCAN_PERIOD);

			mBluetoothAdapter.startLeScan(mLeScanCallback);

			scan_flag = true;
			deleteItem();

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
				// scanBtn.setText(R.string.start_scan);
				// scanBtn.setText("start scan");
				break;

			case STOP_SCAN:
			//	 scanBtn.setText(R.string.stop_scan);
				// scanBtn.setText("stop scan");
				break;

			case DELETE_SCAN:
			//	 scanBtn.setText(R.string.start_scan);
				listItem.remove(listItem.size() - 1);
				listItemAdapter.notifyDataSetChanged();
				break;

			}
		}
	};

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int majorid, minorid;

					UUID = bytesToHex(scanRecord, 9, 16);

					majorid = scanRecord[25] * 256 + scanRecord[26];
					minorid = scanRecord[27] * 256 + scanRecord[28];
					// System.out.println("111111111firstScan======>" +
					// firstScan
					// + "  UUID======>" + bytesToHex(scanRecord, 9, 16));

					if (firstScan) {

						addItem(device.getName(), device.getAddress()
								+ " UUID:" + bytesToHex(scanRecord, 9, 16)
								+ " MajorID:" + majorid + " MinorID:" + minorid
								+ " RSSI:" + rssi);
						mLeDevices.add(device);

						UUID_temp.add(UUID);

						firstScan = false;
					} else {

						for (int i = 0; i < UUID_temp.size(); i++) {
							if (UUID.equals(UUID_temp.get(i))) {
								// System.out.println(firstScan
								// +"   ==UUID_temp.size()======>" +
								// UUID_temp.size()
								// + "  UUID======>" + bytesToHex(scanRecord, 9,
								// 16)+"   UUIDTEMP==>" + UUID_temp.get(i) + i);
								if_has = 1;
								break;
							} else {
								if_has = 0;

								// System.out.println("111");
							}

						}

						if (if_has == 0) {
							UUID_temp.add(UUID);
							addItem(device.getName(), device.getAddress()
									+ " UUID:" + bytesToHex(scanRecord, 9, 16)
									+ " MajorID:" + majorid + " MinorID:"
									+ minorid + " RSSI:" + rssi);
							mLeDevices.add(device);
						}
					}
					// System.out.println("firstScan======>" + firstScan
					// + "  UUID======>" + bytesToHex(scanRecord, 9, 16));

					// addItem(device.getName(),device.getAddress()+" UUID:"+bytesToHex(scanRecord,9,16)+" MajorID:"+majorid+" MinorID:"+minorid+" RSSI:"+rssi);
					// mLeDevices.add(device);
					// firstScan = false;
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Constans.exit_ask(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}