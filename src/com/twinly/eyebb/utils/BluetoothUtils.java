package com.twinly.eyebb.utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
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
import android.os.IBinder;
import android.util.Log;

import com.twinly.eyebb.R;
import com.twinly.eyebb.bluetooth.BleDevicesScanner;
import com.twinly.eyebb.dialog.ErrorDialog;
import com.twinly.eyebb.service.BluetoothLeService;

@SuppressLint("NewApi")
public class BluetoothUtils {
	private final static String SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_MAJOR_UUID = "00001008-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_MINOR_UUID = "00001009-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_BEEP_UUID = "00001001-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_BATTERY_UUID = "00001004-0000-1000-8000-00805f9b34fb";

	public final static int WRITE_MAJOR_MINOR = 1;
	public final static int WRITE_BEEP = 2;
	public final static int READ_BATTERY = 3;

	private final static String TAG = BluetoothUtils.class.getSimpleName();

	private Context context;
	private BleConnectCallback callback;
	private FragmentManager manager;

	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mGattUpdateReceiver;
	private BluetoothLeService mBluetoothLeService;
	private BleDevicesScanner scanner;
	private ServiceConnection mServiceConnection;
	private List<BluetoothGattService> gattServices;

	private int command;
	private String[] value;
	private Timer timer;

	public interface BleConnectCallback {
		/**
		 * UI update before connecting to BLE device 
		 */
		public void onPreConnect();

		/**
		 * UI update when cancel connect to BLE device, always connect to device failed
		 */
		public void onConnectCanceled();

		/**
		 * UI update when connected to BLE device
		 */
		public void onConnected();

		/**
		 * UI update when disconnected to BLE device
		 */
		public void onDisConnected();

		/**
		 * UI update when discovered the BLE services
		 */
		public void onDiscovered();

		/**
		 * UI update when read data from BLE device
		 * @param value The data read from BLE device
		 */
		public void onDataAvailable(String value);

		/**
		 * UI update when read or write BLE device
		 * @param result
		 */
		public void onResult(boolean result);
	}

	public BluetoothUtils(Context context, FragmentManager manager,
			final BleConnectCallback callback) {
		this.context = context;
		this.manager = manager;
		this.callback = callback;

		mGattUpdateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				System.out.println("mGattUpdateReceiver ==>> " + action);
				if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
					callback.onConnected();
				} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
						.equals(action)) {
					callback.onDisConnected();
				} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
						.equals(action)) {
					callback.onDiscovered();
					gattServices = mBluetoothLeService
							.getSupportedGattServices();
					onDiscovered();
				} else if (BluetoothLeService.ACTION_GATT_READ_SUCCESS
						.equals(action)) {
					callback.onDataAvailable(intent
							.getStringExtra(BluetoothLeService.EXTRA_DATA));
				} else if (BluetoothLeService.ACTION_GATT_READ_FAILURE
						.equals(action)) {
					callback.onResult(false);
				} else if (BluetoothLeService.ACTION_GATT_WRITE_SUCCESS
						.equals(action)) {
					callback.onResult(true);
				} else if (BluetoothLeService.ACTION_GATT_WRITE_FAILURE
						.equals(action)) {
					callback.onResult(false);
				}
			}
		};
	}

	/**
	 * Initialize the Bluetooth
	 * @return Whether initialize succeed
	 */
	private boolean initialize() {
		if (!isBluetoothAndBleSupport()) {
			return false;
		}
		if (!isBluetoothOpen()) {
			return false;
		}
		return true;
	}

	/**
	 * Register broadcast receiver, called in onResume()
	 */
	public void registerReceiver() {
		context.registerReceiver(mGattUpdateReceiver,
				makeGattUpdateIntentFilter());
	}

	/**
	 * Unregister broadcast receiver, called i onPause()
	 */
	public void unregisterReceiver() {
		if (mGattUpdateReceiver != null) {
			context.unregisterReceiver(mGattUpdateReceiver);
		}
	}

	/**
	 * Read battery from device, keep connection if not disconnect manual
	 * @param mDeviceAddress The device mac address
	 * @param timeout The timeout to connect to device
	 */
	public void readBattery(String mDeviceAddress, long timeout) {
		command = READ_BATTERY;
		if (gattServices != null) {
			read(CHARACTERISTICS_BATTERY_UUID);
		} else {
			if (initialize()) {
				connect(mDeviceAddress, timeout);
			}
		}
	}

	/**
	 * Write major & minor to device, disconnect device when finish writing
	 * @param mDeviceAddress The device mac address
	 * @param timeout The timeout to connect to device
	 * @param value The value of major & minor
	 */
	public void writeMajorMinor(String mDeviceAddress, long timeout,
			String... value) {
		command = WRITE_MAJOR_MINOR;
		this.value = value;
		if (gattServices != null) {
			write(CHARACTERISTICS_MAJOR_UUID,
					BLEUtils.checkMajorMinor(value[0]), false);
			write(CHARACTERISTICS_MAJOR_UUID,
					BLEUtils.checkMajorMinor(value[1]), true);
		} else {
			if (initialize()) {
				connect(mDeviceAddress, timeout);
			}
		}
	}

	/**
	 * Make device beep, disconnect device when finish writing
	 * @param mDeviceAddress The device mac address
	 * @param timeout The timeout to connect to device
	 * @param value The value of to beep
	 */
	public void writeBeep(String mDeviceAddress, long timeout, String... value) {
		command = WRITE_BEEP;
		this.value = value;
		if (initialize()) {
			connect(mDeviceAddress, timeout);
		}
	}

	/**
	 * Start scan ble device, called in onResume
	 * @param leScanCallback Callback function to receive scanned ble device
	 * @param scanPeriod The period time of switch between scan off and scan on, normally set 500ms
	 */
	public void startLeScan(BluetoothAdapter.LeScanCallback leScanCallback,
			long scanPeriod) {
		if (initialize()) {
			if (scanner == null) {
				scanner = new BleDevicesScanner(mBluetoothAdapter,
						leScanCallback);
				scanner.setScanPeriod(scanPeriod);
			}
			scanner.start();
		}

	}

	/**
	 * Stop scan ble device, called in onPause()
	 */
	public void stopLeScan() {
		if (scanner != null) {
			scanner.stop();
		}
	}

	/**
	 * To check whether the phone supports the ble
	 */
	private boolean isBluetoothAndBleSupport() {
		// Use this check to determine whether BLE is supported on the device.  
		// Then you can selectively disable BLE-related features.
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE) == false) {
			ErrorDialog.newInstance(R.string.dialog_error_no_ble).show(manager,
					ErrorDialog.TAG);
			return false;
		} else {
			// Initializes a Bluetooth adapter. 
			// For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
			final BluetoothManager bluetoothManager = (BluetoothManager) context
					.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();

			// Checks if Bluetooth is supported on the device.
			if (mBluetoothAdapter == null) {
				ErrorDialog.newInstance(R.string.dialog_error_no_bluetooth)
						.show(manager, ErrorDialog.TAG);
				return false;
			}
		}
		return true;
	}

	/**
	 * To check whether the Bluetooth is open, request to open it if not
	 * @return whether Bluetooth opened
	 */
	private boolean isBluetoothOpen() {
		if (mBluetoothAdapter == null) {
			return false;
		}
		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				context.startActivity(enableBtIntent);
				return false;
			}
		}

		return true;
	}

	/**
	 * Connect to the device. If it is the first connection, create a new BluetoothLeService, otherwise connect to device directly
	 * @param mDeviceAddress The device mac address
	 * @param timeout The timeout to connect to device
	 */
	private void connect(final String mDeviceAddress, long timeout) {
		callback.onPreConnect();
		if (mServiceConnection == null) {
			mServiceConnection = new ServiceConnection() {

				@Override
				public void onServiceConnected(ComponentName componentName,
						IBinder service) {
					mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
							.getService();
					if (!mBluetoothLeService.initialize()) {
						Log.e(TAG, "Unable to initialize Bluetooth");
					}
					// Automatically connects to the device upon successful start-up initialization.
					mBluetoothLeService.connect(mDeviceAddress);
				}

				@Override
				public void onServiceDisconnected(ComponentName componentName) {
					Log.d(TAG, "onServiceDisconnected");
					mBluetoothLeService = null;
					gattServices = null;
				}
			};

			Intent gattServiceIntent = new Intent(context,
					BluetoothLeService.class);
			context.bindService(gattServiceIntent, mServiceConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			mBluetoothLeService.connect(mDeviceAddress);
		}
		// cancel connect to device if not success when timeout
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mBluetoothLeService != null) {
					if (mBluetoothLeService.getmConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
						callback.onConnectCanceled();
						mBluetoothLeService.disconnect();
						mBluetoothLeService = null;
					}
				}
			}
		}, timeout);
	}

	/**
	 * Call when descovered the BLE services
	 */
	private void onDiscovered() {
		if (timer != null) {
			timer.cancel();
		}
		switch (command) {
		case WRITE_BEEP:
			write(CHARACTERISTICS_BEEP_UUID, value[0], true);
			break;
		case WRITE_MAJOR_MINOR:
			write(CHARACTERISTICS_MAJOR_UUID,
					BLEUtils.checkMajorMinor(value[0]), false);
			write(CHARACTERISTICS_MAJOR_UUID,
					BLEUtils.checkMajorMinor(value[1]), true);
			break;
		case READ_BATTERY:
			read(CHARACTERISTICS_BATTERY_UUID);
			break;
		}
	}

	/**
	 * Write the value to given UUID
	 * @param gattUuid
	 * @param value
	 * @param needCallback whether need callback when finish write to BLE device
	 */
	private void write(String gattUuid, String value, boolean needCallback) {
		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			System.out.println("Service == >> " + uuid);
			if (uuid.equals(SERVICE_UUID)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(gattUuid)) {
						//System.out.println("Characteristic == >> " + uuid);
						gattCharacteristic.setValue(BLEUtils
								.HexString2Bytes(value));
						if (!mBluetoothLeService
								.writeCharacteristic(gattCharacteristic)) {
							if (needCallback) {
								callback.onResult(false);
							}
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * Read value from given UUID
	 * @param gattUuid
	 */
	private void read(String gattUuid) {
		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			System.out.println("Service == >> " + uuid);
			if (uuid.equals(SERVICE_UUID)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(gattUuid)) {
						if (!mBluetoothLeService
								.readCharacteristic(gattCharacteristic)) {
							callback.onResult(false);
						}
					}
				}
			}
		}
	}

	/**
	 * Disconnet to Bluetooth service and gatt service, called in onDestroy()
	 */
	public void disconnect() {
		if (mServiceConnection != null) {
			context.unbindService(mServiceConnection);
		}
		if (mBluetoothLeService != null) {
			mBluetoothLeService.disconnect();
			mBluetoothLeService = null;
		}
		gattServices = null;
	}

	private IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_READ_SUCCESS);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_READ_FAILURE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_WRITE_SUCCESS);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_WRITE_FAILURE);
		return intentFilter;
	}
}
