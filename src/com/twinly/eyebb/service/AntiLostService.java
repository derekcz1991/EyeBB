package com.twinly.eyebb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.bluetooth.BLEUtils;
import com.twinly.eyebb.bluetooth.BleDevicesScanner;
import com.twinly.eyebb.model.Macaron;
import com.twinly.eyebb.model.SerializableMacaronMap;

@SuppressLint("NewApi")
public class AntiLostService extends Service {
	private final static String TAG = AntiLostService.class.getSimpleName();
	/**
	 * Command to the service to register a client, receiving callbacks
	 * from the service.  The Message's replyTo field must be a Messenger of
	 * the client where callbacks should be sent.
	 */
	public static final int MSG_REGISTER_CLIENT = 1;

	/**
	 * Command to the service to unregister a client, ot stop receiving callbacks
	 * from the service.  The Message's replyTo field must be a Messenger of
	 * the client as previously given with MSG_REGISTER_CLIENT.
	 */
	public static final int MSG_UNREGISTER_CLIENT = 2;

	/**
	 * Command to service to set a new value.  This can be sent to the
	 * service to supply a new value, and will be sent by the service to
	 * any registered clients with the new value.
	 */
	public static final int MSG_SET_VALUE = 3;

	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	/** Holds last value set by a client. */
	int mValue = 0;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());

	public static final String EXTRA_DEVICE_LIST = "DEVICE_LIST";
	public static final int MAX_DUAL_MODE_SIZE = 3;
	private final int MESSAGE_INIT_NOTIFICAION = 1;
	private final int MESSAGE_CONNECT_DEVICE = 2;
	private final int MESSAGE_SCANN = 3;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private ArrayList<String> antiLostDeviceList;
	private HashMap<String, Macaron> antiLostDeviceHashMap;
	private SerializableMacaronMap serializableMacaronMap;

	private BleDevicesScanner scanner;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private List<BluetoothGattService> gattServices;
	private List<BluetoothGatt> mBluetoothGattList;

	private int mConnectionState = BLEUtils.STATE_DISCONNECTED;
	private boolean isPasswordSet;
	private Timer timer;

	LeScanCallback leScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (antiLostDeviceHashMap.get(device.getAddress()) != null) {
				antiLostDeviceHashMap.get(device.getAddress()).setPreRssi(
						antiLostDeviceHashMap.get(device.getAddress())
								.getRssi());
				antiLostDeviceHashMap.get(device.getAddress()).setRssi(rssi);
				antiLostDeviceHashMap.get(device.getAddress())
						.setLastAppearTime(System.currentTimeMillis());
			}
		}
	};

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED
					&& status == BluetoothGatt.GATT_SUCCESS) {
				timer.cancel();
				isPasswordSet = false;
				mConnectionState = BLEUtils.STATE_CONNECTED;
				// Attempts to discover services after successful connection.
				Log.i(TAG,
						"Connected to GATT server. Attempting to start service discovery:");
				mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.i(TAG, "==> Disconnected from GATT server ==> "
						+ gatt.getDevice().getAddress());
				if (gatt == mBluetoothGatt) {
					timer.cancel();
				}
				mConnectionState = BLEUtils.STATE_DISCONNECTED;
				disconnect(gatt);
				mBluetoothGattList.remove(gatt);
				if (antiLostDeviceList.contains(gatt.getDevice().getAddress()) == false) {
					antiLostDeviceList.add(gatt.getDevice().getAddress());
				}
				if (antiLostDeviceList.size() == 1) {
					mServiceHandler.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
				}
				antiLostDeviceHashMap.get(gatt.getDevice().getAddress())
						.setMissed(true);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				gattServices = mBluetoothGatt.getServices();
				write(BLEUtils.SERVICE_UUID_0002,
						BLEUtils.CHARACTERISTICS_PASSWORD, "C3A60D00");
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (isPasswordSet == false) {
					isPasswordSet = true;
					write(BLEUtils.SERVICE_UUID_0001,
							BLEUtils.CHARACTERISTICS_ANTI_LOST_PERIOD_UUID,
							"FFFF");
				} else {
					antiLostDeviceHashMap.get(gatt.getDevice().getAddress())
							.setMissed(false);
					antiLostDeviceList.remove(gatt.getDevice().getAddress());
					mServiceHandler.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
				}
			} else {
				System.out
						.println("onCharacteristicWrite failed ==>> connect next");
				connectNext(gatt);
			}
		}

	};

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_INIT_NOTIFICAION:
				NotificationCompat.Builder builder = new NotificationCompat.Builder(
						AntiLostService.this);
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle("title");
				builder.setContentText("text");

				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(AntiLostService.this,
						LancherActivity.class);
				resultIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
				TaskStackBuilder stackBuilder = TaskStackBuilder
						.create(AntiLostService.this);
				stackBuilder.addParentStack(LancherActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

				builder.setContentIntent(resultPendingIntent);
				Notification notification = builder.build();
				startForeground(1, notification);

				initialize();
				if (antiLostDeviceList.size() > MAX_DUAL_MODE_SIZE) {
					mServiceHandler.sendEmptyMessage(MESSAGE_SCANN);
				} else {
					mServiceHandler.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
				}
				break;
			case MESSAGE_SCANN:
				startLeScan(leScanCallback, 500);
				break;
			case MESSAGE_CONNECT_DEVICE:
				if (mConnectionState != BLEUtils.STATE_CONNECTING) {
					if (antiLostDeviceList.size() > 0) {
						connect(antiLostDeviceList.get(0));
					}
				}
				break;
			}

		}
	}

	@Override
	public void onCreate() {
		System.out.println("AntiLostService ==>> onCreate");
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		serializableMacaronMap = new SerializableMacaronMap();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand ==>> flags:" + flags
				+ " ==>> startId:" + startId);
		antiLostDeviceList = intent.getStringArrayListExtra(EXTRA_DEVICE_LIST);
		antiLostDeviceHashMap = new HashMap<String, Macaron>();
		for (String device : antiLostDeviceList) {
			antiLostDeviceHashMap.put(device, new Macaron(device));
		}
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.what = MESSAGE_INIT_NOTIFICAION;
		mServiceHandler.sendMessage(msg);
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	/**
	 * When binding to the service, we return an interface to our messenger
	 * for sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		stopLeScan();
		// TODO
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		System.out.println("AntiLostService ==>> onDestroy");
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		mBluetoothGattList = new ArrayList<BluetoothGatt>();

		return true;
	}

	public void startLeScan(BluetoothAdapter.LeScanCallback leScanCallback,
			long scanPeriod) {
		if (scanner == null) {
			scanner = new BleDevicesScanner(mBluetoothAdapter, leScanCallback);
			scanner.setScanPeriod(scanPeriod);
		}
		scanner.start();
	}

	public void stopLeScan() {
		if (scanner != null) {
			scanner.stop();
		}
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. 
	 * 		   The connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	private boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found. Unable to connect.");
			return false;
		}

		mConnectionState = BLEUtils.STATE_CONNECTING;
		// We want to directly connect to the device, so we are setting the
		// autoConnect  parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		mBluetoothGattList.add(mBluetoothGatt);

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mConnectionState != BLEUtils.STATE_CONNECTED) {
					System.out.println("timeout ==>> " + address
							+ " ==>> connect next");
					connectNext(mBluetoothGatt);
				}
			}
		}, 5000);

		System.out.println(" ");
		Log.d(TAG, "Trying to create a new connection ==>> " + address);
		return true;
	}

	private void connectNext(BluetoothGatt gatt) {
		disconnect(gatt);
		mBluetoothGattList.remove(gatt);
		antiLostDeviceList.remove(gatt.getDevice().getAddress());
		antiLostDeviceList.add(gatt.getDevice().getAddress());
		System.out.println("antiLostDeviceList size = "
				+ antiLostDeviceList.size());
		mServiceHandler.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
	}

	/**
	 * Write the value to given UUID
	 * @param serviceUuid
	 * @param gattUuid
	 * @param value
	 */
	private void write(String serviceUuid, String gattUuid, String value) {
		System.out.println("write == >> " + gattUuid + "  " + value);
		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			//System.out.println("Service == >> " + uuid);
			if (uuid.equals(serviceUuid)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(gattUuid)) {
						//System.out.println("Characteristic == >> " + uuid);
						gattCharacteristic.setValue(BLEUtils
								.HexString2Bytes(value));
						mBluetoothGatt.writeCharacteristic(gattCharacteristic);
					}
				}
				break;
			}
		}
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void disconnect(BluetoothGatt gatt) {
		mConnectionState = BLEUtils.STATE_DISCONNECTED;
		if (gatt == null) {
			return;
		}
		gatt.disconnect();
		gatt.close();
		gatt = null;
	}

	/**
	 * Handler of incoming messages from clients.
	 */
	private final class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SET_VALUE:
				mValue = msg.arg1;
				serializableMacaronMap.setMap(antiLostDeviceHashMap);
				Bundle data = new Bundle();
				data.putSerializable(EXTRA_DEVICE_LIST, serializableMacaronMap);
				for (int i = mClients.size() - 1; i >= 0; i--) {
					try {
						Message message = Message.obtain(null, MSG_SET_VALUE,
								mValue, 0);
						message.setData(data);
						mClients.get(i).send(message);
					} catch (RemoteException e) {
						// The client is dead.  Remove it from the list;
						// we are going through the list from back to front
						// so this is safe to do inside the loop.
						mClients.remove(i);
					}
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

}
