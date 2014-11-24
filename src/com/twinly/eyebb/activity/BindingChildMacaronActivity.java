package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.service.BluetoothLeService;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class BindingChildMacaronActivity extends Activity {
	public final static String BEEP_SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb";
	public final static String BEEP_CHARACTERISTICS_MAJOR_UUID = "00001008-0000-1000-8000-00805f9b34fb";
	public final static String BEEP_CHARACTERISTICS_MINOR_UUID = "00001009-0000-1000-8000-00805f9b34fb";

	private final static String TAG = BindingChildMacaronActivity.class
			.getSimpleName();

	private CircleImageView avatar;
	private TextView[] tvAnimation;
	private TextView tvMessage;
	private TextView iconBeacon;
	private Handler mHandler;
	private int index;

	private String mDeviceAddress = "44:A6:E5:00:04:F5";
	private long childId;
	private long guardianId;
	private String major;
	private String minor;
	private boolean deviceValid; // this device is not bind

	// for ble scan
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 15000;
	private boolean mScanning;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean deviceScanned;

	// for ble connect
	private BluetoothLeService mBluetoothLeService;
	private ServiceConnection mServiceConnection;
	private BroadcastReceiver mGattUpdateReceiver;

	// Handles various events fired by the Service.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_child_macaron);

		checkBLE();

		avatar = (CircleImageView) findViewById(R.id.avatar);
		tvMessage = (TextView) findViewById(R.id.message);
		iconBeacon = (TextView) findViewById(R.id.beacon);

		tvAnimation = new TextView[6];
		tvAnimation[0] = (TextView) findViewById(R.id.animation_0);
		tvAnimation[1] = (TextView) findViewById(R.id.animation_1);
		tvAnimation[2] = (TextView) findViewById(R.id.animation_2);
		tvAnimation[3] = (TextView) findViewById(R.id.animation_3);
		tvAnimation[4] = (TextView) findViewById(R.id.animation_4);
		tvAnimation[5] = (TextView) findViewById(R.id.animation_5);

		iconBeacon.setAlpha(0.3f);
		tvMessage.setText("搜索macaron...");

		childId = getIntent().getLongExtra("child_id", 0);
		childId = 3;

		mHandler = new Handler();
		mHandler.postDelayed(new UpdateAnimation(), 500);
		new GetMajorMinorTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		scanLeDevice(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
		}
		mBluetoothLeService = null;
	}

	private void checkBLE() {
		// Use this check to determine whether BLE is supported on the device.  Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "error_bluetooth_not_supported",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	private class GetMajorMinorTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", mDeviceAddress);

			return HttpRequestUtils.post(HttpConstants.CHECK_BEACON, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.CHECK_BEACON + " = " + result);

			if (result.length() > 0) {
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					return;
				} else if (result.equals(HttpConstants.SERVER_RETURN_NC)) {
					return;
				}
				if (result.equals(HttpConstants.SERVER_RETURN_USED)) {
					Toast.makeText(BindingChildMacaronActivity.this,
							R.string.text_device_already_binded,
							Toast.LENGTH_SHORT).show();
				} else {
					major = result.substring(0, result.indexOf(":"));
					minor = result.substring(result.indexOf(":") + 1,
							result.length());
					System.out.println("major = " + major + "  minor = "
							+ minor);
					deviceValid = true;
					connectDevice();
				}
			}
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device.getAddress().equals(mDeviceAddress)) {
						scanLeDevice(false);
						iconBeacon.setAlpha(1);
						tvMessage.setText("连接设备...");

						deviceScanned = true;
						connectDevice();
					}

				}
			});
		}
	};

	private void connectDevice() {
		// Code to manage Service lifecycle.
		if (deviceValid && deviceScanned) {
			mServiceConnection = new ServiceConnection() {

				@Override
				public void onServiceConnected(ComponentName componentName,
						IBinder service) {
					mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
							.getService();
					if (!mBluetoothLeService.initialize()) {
						Log.e(TAG, "Unable to initialize Bluetooth");
						finish();
					}
					// Automatically connects to the device upon successful start-up initialization.
					mBluetoothLeService.connect(mDeviceAddress);
				}

				@Override
				public void onServiceDisconnected(ComponentName componentName) {
					mBluetoothLeService = null;
				}
			};

			Intent gattServiceIntent = new Intent(
					BindingChildMacaronActivity.this, BluetoothLeService.class);
			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

			mGattUpdateReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					final String action = intent.getAction();
					System.out.println("mGattUpdateReceiver ==>> " + action);
					if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
						tvMessage.setText("连接成功");
					} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
							.equals(action)) {
					} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
							.equals(action)) {
						// Show all the supported services and characteristics on the user interface.
						System.out
								.println("mGattUpdateReceiver ==>> writeToMacaron");
						tvMessage.setText("写入设备");
						writeToMacaron(mBluetoothLeService
								.getSupportedGattServices());
					} else if (BluetoothLeService.ACTION_DATA_AVAILABLE
							.equals(action)) {
					}
				}
			};

			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			if (mBluetoothLeService != null) {
				final boolean result = mBluetoothLeService
						.connect(mDeviceAddress);
				Log.d(TAG, "Connect request result=" + result);
			}
		}
	}

	public void writeToMacaron(List<BluetoothGattService> gattServices) {
		BluetoothGattCharacteristic majorGattCharacteristic = null;
		BluetoothGattCharacteristic minorGattCharacteristic = null;

		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			System.out.println("Service == >> " + uuid);
			if (uuid.equals(BEEP_SERVICE_UUID)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(BEEP_CHARACTERISTICS_MAJOR_UUID)) {
						System.out.println("Characteristic == >> " + uuid);
						majorGattCharacteristic = gattCharacteristic;
					} else if (uuid.equals(BEEP_CHARACTERISTICS_MINOR_UUID)) {
						System.out.println("Characteristic == >> " + uuid);
						minorGattCharacteristic = gattCharacteristic;
					}
				}
				break;
			}
		}

		if (majorGattCharacteristic != null) {
			majorGattCharacteristic.setValue(BLEUtils.HexString2Bytes(BLEUtils
					.checkMajorMinor(major)));
			if (mBluetoothLeService
					.writeCharacteristic(majorGattCharacteristic)) {
			} else {
				Toast.makeText(this, "write major fail", Toast.LENGTH_SHORT)
						.show();
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				finish();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (minorGattCharacteristic != null) {
			minorGattCharacteristic.setValue(BLEUtils.HexString2Bytes(BLEUtils
					.checkMajorMinor(minor)));
			if (mBluetoothLeService
					.writeCharacteristic(minorGattCharacteristic)) {
			} else {
				Toast.makeText(this, "write minor fail", Toast.LENGTH_SHORT)
						.show();
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				finish();
			}
		}
		tvMessage.setText("写入成功，同步到服务器");

		//postToServer();
	}

	private class UpdateAnimation implements Runnable {

		@Override
		public void run() {
			if (index == 6) {
				index = 0;
				tvAnimation[0].setVisibility(View.INVISIBLE);
				tvAnimation[1].setVisibility(View.INVISIBLE);
				tvAnimation[2].setVisibility(View.INVISIBLE);
				tvAnimation[3].setVisibility(View.INVISIBLE);
				tvAnimation[4].setVisibility(View.INVISIBLE);
				tvAnimation[5].setVisibility(View.INVISIBLE);
			} else {
				tvAnimation[index].setVisibility(View.VISIBLE);
				index++;
			}
			mHandler.postDelayed(new UpdateAnimation(), 500);
		}
	}

	private IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	private class PostToServerTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", mDeviceAddress);
			map.put("major", major);
			map.put("minor", minor);
			map.put("guardianId", "");
			return HttpRequestUtils.post(HttpConstants.DEVICE_TO_CHILD, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.DEVICE_TO_CHILD + " = " + result);
			if (result.length() > 0) {
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					return;
				}
				if (result.equals("T")) {
					Toast.makeText(BindingChildMacaronActivity.this,
							"write success", Toast.LENGTH_SHORT).show();
					setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
				} else {
					setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				}
			}
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		} else if (requestCode == ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_DIALOG) {
			if (resultCode == ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS) {
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
				finish();
				return;
			}
		}

	}
}
