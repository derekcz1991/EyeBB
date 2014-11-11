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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.activity.CheckBeaconActivity;
import com.twinly.eyebb.activity.ErrorDialog;
import com.twinly.eyebb.activity.KidsListActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.service.BleCharacteristicsService;
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

	// sharedPreferences

	private String major;
	private String minor;
	final Timer timer = new Timer();
	// private ArrayList<ArrayList<BluetoothGattCharacteristic>>
	// mGattCharacteristics = new
	// ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean mConnected = false;
	private int ReadService = 0;

	private TimerTask TimeOutTask = null;
	private int intentTime = 0;

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
		if (CharacteristicsActivity.majorFinished) {
			dialog = LoadingDialog.createLoadingDialogCanCancel(
					ServicesActivity.this,
					getString(R.string.toast_read_service));
			dialog.show();
		} else {
			dialog = LoadingDialog.createLoadingDialogCanCancel(
					ServicesActivity.this,
					getString(R.string.toast_read_service));
			dialog.show();
		}

		TimeOutTask = new TimerTask() {
			public void run() {
				intentTime++;
				System.out.println("intentTime==>" + intentTime);
				if (intentTime == 20) {
					intentTime = 0;
					Intent intent = new Intent(ServicesActivity.this,
							ErrorDialog.class);
					startActivity(intent);
					if (dialog.isShowing())
						dialog.dismiss();
					CheckBeaconActivity.instance.finish();

					TimeOutTask.cancel();
					TimeOutTask = null;
					timer.cancel();
					timer.purge();
					// timer = null;
					finish();
				}
			}
		};

		//timer.schedule(TimeOutTask, 0, 1000);
		// major = MajorAndMinorPreferences.getString("major", "-1");
		// minor = MajorAndMinorPreferences.getString("minor", "-1");
		major = SharePrefsUtils.signUpDeviceMajor(ServicesActivity.this);
		minor = SharePrefsUtils.signUpDeviceMinor(ServicesActivity.this);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		System.out.println("mDeviceName:" + mDeviceName + " mDeviceAddress:"
				+ mDeviceAddress);
		Constants.gattServiceData.clear();
		Constants.gattServiceObject.clear();

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
			//
			// editor.putBoolean("connectFail", true);
			// editor.commit();
			Intent intentKidsListActivity = new Intent(ServicesActivity.this,
					ErrorDialog.class);
			startActivity(intentKidsListActivity);
			finish();
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialogCanCancel(
						ServicesActivity.this,
						getString(R.string.toast_write_major));
				dialog.show();
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
			final Intent intentToChara = new Intent();
			if (CharacteristicsActivity.majorFinished) {
				intentToChara.setClass(ServicesActivity.this,
						CharacteristicsMinorActivity.class);
			} else {
				intentToChara.setClass(ServicesActivity.this,
						CharacteristicsActivity.class);
			}

			if (ReadService > 0) {

				intentToChara.putExtra("servidx", ReadService);
				System.out.println("servidxservidx=>" + 2);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				startActivity(intentToChara);

				ServicesActivity.this.finish();
			}

		}
	}

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			System.out.println("mServiceConnectionmServiceConnection");
			Constants.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!Constants.mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			Constants.mBluetoothLeService.connect(mDeviceAddress);
			// status_text.setText(mDeviceName + ": Connecting...");
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
				status_text.setText(mDeviceName + ": Discovering services...");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;
				status_text.setText(mDeviceName + ": Disconnected");
				ServicesActivity.this.finish();
				// if error
				TimeOutTask.cancel();
				TimeOutTask = null;
				timer.cancel();
				timer.purge();
				Intent intentError = new Intent(ServicesActivity.this,
						ErrorDialog.class);
				startActivity(intentError);
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				// status_text.setText(mDeviceName + ": Discovered");
				displayGattServices(Constants.mBluetoothLeService
						.getSupportedGattServices());

				int num = SharePrefsUtils.BleServiceRunOnceFlag(context);
				if (num == 1) {
					System.out
							.println("SharePrefsUtils.BleServiceRunOnceFlag(context)>"
									+ SharePrefsUtils
											.BleServiceRunOnceFlag(context));
					new autoConnection().start();
					SharePrefsUtils.setBleServiceRunOnceFlag(context, 2);
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

		// Loops through available GATT Services.
		for (int i = 0; i < gattServices.size(); i++) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();

			currentServiceData.put(LIST_NAME, name);
			currentServiceData.put(LIST_UUID, uuid);
			Constants.gattServiceData.add(currentServiceData);
			Constants.gattServiceObject.add(gattServices.get(i));
			// S addItem(name, uuid);
			// System.out.println("gattServices.get(i).getUuid()==>"
			// + gattServices.get(i).getUuid());

			if (gattServices.get(i).getUuid().toString()
					.equals(Constants.APPLICATION_UUID)) {

				ReadService = i;
				break;
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (Constants.mBluetoothLeService != null) {
			final boolean result = Constants.mBluetoothLeService
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
		if (dialog.isShowing())
			dialog.dismiss();
		Constants.mBluetoothLeService = null;
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
			if (Constants.mBluetoothLeService != null) {
				Constants.mBluetoothLeService.disconnect();
			}
			mConnected = false;
			ServicesActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}