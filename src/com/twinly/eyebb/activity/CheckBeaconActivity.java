package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.eyebb.R;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.bluetooth.BaseApp;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.bluetooth.ServicesActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class CheckBeaconActivity extends Activity {
	// ListView的适配器
	SimpleAdapter listItemAdapter;
	// ListView的数据源，这里是一个HashMap的列表
	ArrayList<HashMap<String, Object>> listItem;
	ListView myList; // ListView控件
	private ArrayList<HashMap<String, Object>> searchList;
	private Button scanBtn;
	private boolean scan_flag = false;
	private final static int START_SCAN = 4;
	private final static int SCAN_CHILD_FOR_LIST = 5;
	private final static int DELETE_SCAN = 6;
	private final static int ALREADY_BING_DEVICE = 7;

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

	public static CheckBeaconActivity instance;

	int UUID_i = 0;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();

	private HashMap<String, Device> deviceMap = new HashMap<String, Device>();;
	private long ChildIDfromKidsList;

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
	Timer timer = null;
	TimerTask task = null;

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
		instance = this;
		mHandler = new Handler();
		autoScanHandler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();
		searchList = new ArrayList<HashMap<String, Object>>();

		listItemAdapter = new SimpleAdapter(CheckBeaconActivity.this, listItem,
				R.layout.ble_listview,
				new String[] { "image", "search", "text" }, new int[] {
						R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });
		myList = (ListView) findViewById(R.id.listView_peripheral);
		myList.setAdapter(listItemAdapter);

		TimerTask task = new TimerTask() {

			public void run() {
				// System.out.println("AAAAAAAAAAA");
				Message msg = handler.obtainMessage();
				msg.what = SCAN_CHILD_FOR_LIST;
				handler.sendMessage(msg);

			}

		};
		timer = new Timer();

		if (timer != null && task != null)
			// System.out.println("bbbbbbbbbbbbbb");
			timer.schedule(task, Constants.DELAY, Constants.BINDING_PERIOD);

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

				// MACaddress4submit = device.getAddress();
				MACaddress4submit = listItem.get(arg2).get("title").toString()
						.substring(4, 21);
				deviceName4submit = device.getName();
				System.out.println("MACaddress4submit-->" + MACaddress4submit);
				new Thread(postToServerRunnable).start();

				// SharePrefsUtils.setBleServiceRunOnceFlag(
				// CheckBeaconActivity.this, 1);
				// SharePrefsUtils.setSignUpDeviceMajor(CheckBeaconActivity.this,
				// "0333");
				// SharePrefsUtils.setSignUpDeviceMinor(CheckBeaconActivity.this,
				// "0333");
				// SharePrefsUtils.setMacAddress(CheckBeaconActivity.this,
				// MACaddress4submit);
				//
				// Intent intent = new Intent(CheckBeaconActivity.this,
				// ServicesActivity.class);
				//
				// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
				// deviceName4submit);
				// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
				// MACaddress4submit);
				// isWhileLoop = false;
				// startActivity(intent);

				//
				// SharePrefsUtils.setSignUpDeviceMajor(CheckBeaconActivity.this,
				// "0004");
				// SharePrefsUtils.setSignUpDeviceMinor(CheckBeaconActivity.this,
				// "0004");
				// SharePrefsUtils.setMacAddress(CheckBeaconActivity.this,
				// MACaddress4submit);
				//
				//
				// Intent intent = new Intent(CheckBeaconActivity.this,
				// ServicesActivity.class);
				//
				// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
				// deviceName4submit);
				// intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
				// MACaddress4submit);
				//
				// startActivity(intent);

				// System.out.println("new Thread(postToServerRunnable).start();");
				// if (scan_flag) {
				// scanLeDevice();
				// }
				//
				// if (dialog != null && dialog.isShowing()) {
				// dialog.dismiss();
				// }
				//
				// autoScanHandler.removeCallbacks(autoScan);
				// mHandler.removeCallbacks(scanLeDeviceRunable);
				// mBluetoothAdapter.stopLeScan(mLeScanCallback);
				// isWhileLoop = false;

				// finish();
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

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

		if (task != null) {
			task.cancel();
			task = null;
		}
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

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ALREADY_BING_DEVICE:
				Toast.makeText(CheckBeaconActivity.this,
						R.string.text_device_already_binded, Toast.LENGTH_LONG)
						.show();
				break;

			case Constants.CONNECT_ERROR:
				Toast.makeText(CheckBeaconActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case SCAN_CHILD_FOR_LIST:
				// System.out.println("AAAAASSS");
				listItem.clear();

				listItemAdapter.notifyDataSetChanged();

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
							// mLeDevices.clear();
							while (it.hasNext()) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								Map.Entry<String, Device> entry = it.next();
								map.put("image", R.drawable.ble_icon);

//								map.put("text", "UUID:"
//										+ entry.getValue().getUuid() + "\n強度:"
								map.put("text","強度:"
										+ entry.getValue().getRssi());
								map.put("title", "Mac:"
										+ entry.getValue().getAddress());
								map.put("search", "Mac:"
										+ entry.getValue().getAddress()
												.replace(":", "").toLowerCase());
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
			
			try {
				timer.cancel();
				timer.purge();
				timer = null;
				task.cancel();
				task = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
				System.out.println("connect error");
				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);

			} else {
				// successful
				if (retStr.length() > 0
						&& !retStr.equals(HttpConstants.SERVER_RETURN_USED)) {
					Message msg = handler.obtainMessage();
					msg.what = START_PROGRASSS_BAR;
					handler.sendMessage(msg);

					major = retStr.substring(0, retStr.indexOf(":"));
					minor = retStr.substring(retStr.indexOf(":") + 1,
							retStr.length());

					major = checkMajor(major);
					minor = checkMinor(minor);
					System.out.println("retStrpost======>" + major + " "
							+ minor);

					// SharePrefsUtils.setBleServiceRunOnceFlag(
					// CheckBeaconActivity.this, 1);
					SharePrefsUtils.setSignUpDeviceMajor(
							CheckBeaconActivity.this, major);
					SharePrefsUtils.setSignUpDeviceMinor(
							CheckBeaconActivity.this, minor);
					SharePrefsUtils.setMacAddress(CheckBeaconActivity.this,
							MACaddress4submit);

					Intent intent = new Intent(CheckBeaconActivity.this,
							ServicesActivity.class);

					intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
							deviceName4submit);
					intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
							MACaddress4submit);
					isWhileLoop = false;
					startActivity(intent);

					// new Thread(postDeviceToChildToServerRunnable).start();
					// save to database
					// DBChildren.updateMacAddress(this, childIDfromDeviceList,
					// MACaddress4submit);
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_USED)) {
					// Intent intent = new Intent(CheckBeaconActivity.this,
					// UnbindDeviceDialog.class);
					// startActivity(intent);
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

	private String checkMinor(String minor) {
		// TODO Auto-generated method stub
		switch (minor.length()) {
		case 1:
			minor = "000" + minor;
			break;

		case 2:
			minor = "00" + minor;
			break;
		case 3:
			minor = "0" + minor;
			break;

		}
		return minor;

	}

	private String checkMajor(String major) {
		// TODO Auto-generated method stub
		switch (major.length()) {
		case 1:
			major = "000" + major;
			break;

		case 2:
			major = "00" + major;
			break;
		case 3:
			major = "0" + major;
			break;

		}
		return major;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		autoScanHandler.removeCallbacks(autoScan);
		mHandler.removeCallbacks(scanLeDeviceRunable);
		isWhileLoop = false;
		try {
			if (dialog.isShowing() && dialog != null) {
				dialog.dismiss();
			}
			timer.cancel();
			timer.purge();
			timer = null;
			task.cancel();
			task = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		// autoScanHandler.removeCallbacks(autoScan);
		// mHandler.removeCallbacks(scanLeDeviceRunable);
		// isWhileLoop = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem search = menu.add(0, 1, 0, getString(R.string.btn_search_mac));
		search.setIcon(R.drawable.ic_search)
				.setActionView(R.layout.actionbar_search_device)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		final EditText etSearch = (EditText) search.getActionView()
				.findViewById(R.id.search_addr);

		search.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				etSearch.requestFocus();
				CommonUtils.switchSoftKeyboardstate(CheckBeaconActivity.this);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				etSearch.clearFocus();
				CommonUtils
						.hideSoftKeyboard(etSearch, CheckBeaconActivity.this);
				// adapter = new ChangeKidsListViewAdapter(
				// ChangeKidsActivity.this, list, isSortByName);
				// listView.setAdapter(adapter);
				return true;
			}
		});

		etSearch.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				// editStart = etSearch.getSelectionStart();
				// editEnd = etSearch.getSelectionEnd();
				// System.out.println(editStart + " " + editEnd);
				// if (temp.length() % 2 == 0) {
				//
				// s.insert(editEnd, ":");
				// //int tempSelection = editStart;
				// etSearch.setText(s);
				// //etSearch.setSelection(tempSelection);
				// }
				search(etSearch.getText().toString());
			}
		});
		// search.collapseActionView();
		return super.onCreateOptionsMenu(menu);
	}

	private void search(String keyword) {
		if (!TextUtils.isEmpty(keyword)) {
			searchList.clear();
			for (int i = 0; i < listItem.size(); i++) {
				if (listItem.get(i).get("search").toString().contains(keyword)) {
					searchList.add(listItem.get(i));
				}
			}

			listItemAdapter = new SimpleAdapter(CheckBeaconActivity.this,
					searchList, R.layout.ble_listview, new String[] { "image",
							"search", "text" }, new int[] { R.id.ItemImage,
							R.id.ItemTitle, R.id.ItemText });
			myList.setAdapter(listItemAdapter);
		} else {
			listItemAdapter = new SimpleAdapter(CheckBeaconActivity.this,
					listItem, R.layout.ble_listview, new String[] { "image",
							"search", "text" }, new int[] { R.id.ItemImage,
							R.id.ItemTitle, R.id.ItemText });
			myList.setAdapter(listItemAdapter);
		}
	}


}