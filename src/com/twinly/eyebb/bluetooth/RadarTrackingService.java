package com.twinly.eyebb.bluetooth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.fragment.RadarFragment;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.model.SerializableDeviceMap;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

@SuppressLint("NewApi")
public class RadarTrackingService extends Service implements LocationListener {
	public final static String ACTION_DATA_CHANGED = "radar_tracking.ACTION_DATA_CHANGED";
	public final static String ACTION_STOP_SERVICE = "radar_tracking.ACTION_STOP_SERVICE";
	public static final String EXTRA_DEVICE_LIST = "DEVICE_LIST";

	private final static String TAG = RadarTrackingService.class
			.getSimpleName();

	private final int MESSAGE_INIT_NOTIFICAION = 1;
	private final int MESSAGE_SCAN = 2;
	private final int MESSAGE_STOP_SCAN = 3;
	private final int MESSAGE_START_LOCATING = 4;
	private final int MESSAGE_STOP_LOCATING = 5;

	private final long TIMEMILLS_SCAN_INTERVAL = 15000;
	private final long TIMEMILLS_TO_START_LOCATING = 16000;
	private final int TIME_TO_START = 1;
	private final int TIME_TO_RESET = 6;

	private HashMap<String, Device> deviceHashMap;
	private SerializableDeviceMap serializableDeviceMap;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager mBluetoothManager;
	private BleDevicesScanner scanner;

	private ServiceHandler mServiceHandler;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder serviceNotificationbuilder;
	private Notification serviceNotification;

	private double latitude, longitude;
	private int radius;
	private boolean isFirstTime = true;
	//private boolean isLocatingWorking = false;
	private int timeTickerCounter;

	private RadarTrackingService instance;
	private LocationManager locationManager;
	private String locationProvider;

	LeScanCallback leScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (deviceHashMap.get(device.getAddress()) != null) {
				deviceHashMap.get(device.getAddress()).setPreRssi(
						deviceHashMap.get(device.getAddress()).getRssi());
				deviceHashMap.get(device.getAddress()).setRssi(rssi);
				deviceHashMap.get(device.getAddress()).setLastAppearTime(
						System.currentTimeMillis());
			}
		}
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_STOP_SERVICE.equals(action)) {
				stop();
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
					stop();
				}
			} else if (Intent.ACTION_TIME_TICK.equals(action)) {
				timeTickerCounter++;
				switch (timeTickerCounter) {
				case TIME_TO_START:
					if (isFirstTime == false) {
						mServiceHandler.sendEmptyMessage(MESSAGE_SCAN);
						mServiceHandler.sendEmptyMessageDelayed(
								MESSAGE_STOP_SCAN, TIMEMILLS_SCAN_INTERVAL);
						mServiceHandler.sendEmptyMessageDelayed(
								MESSAGE_START_LOCATING,
								TIMEMILLS_TO_START_LOCATING);
					}
					break;
				case TIME_TO_RESET:
					timeTickerCounter = 0;
					mServiceHandler.sendEmptyMessage(MESSAGE_STOP_LOCATING);
					break;
				}
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
				System.out.println("MESSAGE_INIT_NOTIFICAION");
				if (initialize()) {
					buildNotification(getString(R.string.text_is_runnig));
					if (isFirstTime == true) {
						isFirstTime = false;
						mServiceHandler.sendEmptyMessage(MESSAGE_SCAN);
						mServiceHandler.sendEmptyMessageDelayed(
								MESSAGE_STOP_SCAN, TIMEMILLS_SCAN_INTERVAL);
						mServiceHandler.sendEmptyMessageDelayed(
								MESSAGE_START_LOCATING,
								TIMEMILLS_TO_START_LOCATING);
					}
				}
				break;
			case MESSAGE_SCAN:
				System.out.println("MESSAGE_SCAN");
				//Toast.makeText(getApplicationContext(), "start scan", Toast.LENGTH_SHORT).show();
				startLeScan(leScanCallback, 500);
				break;
			case MESSAGE_STOP_SCAN:
				System.out.println("MESSAGE_STOP_SCAN");
				//Toast.makeText(getApplicationContext(), "stop scan", Toast.LENGTH_SHORT).show();
				stopLeScan();
				// once stop scan, send broadcast to update view
				broadcastUpdate();
				break;
			case MESSAGE_START_LOCATING:
				System.out.println("MESSAGE_START_LOCATING");
				//Toast.makeText(getApplicationContext(), "start locating", Toast.LENGTH_SHORT).show();
				if (locationManager != null) {
					//isLocatingWorking = true;
					locationManager.requestLocationUpdates(locationProvider, 0,
							0, instance);
				}
				break;
			case MESSAGE_STOP_LOCATING:
				//if (isLocatingWorking) {
				//isLocatingWorking = false;
				System.out.println("MESSAGE_STOP_LOCATING");
				//Toast.makeText(getApplicationContext(), "stop locating", Toast.LENGTH_SHORT).show();
				if (locationManager != null)
					locationManager.removeUpdates(instance);
				// once stop locating, upload the data to server
				new UploadLocationTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				//}
				break;
			}
		}
	}

	@Override
	public void onCreate() {
		System.out.println("RadarTrackingService ==>> onCreate");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_STOP_SERVICE);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(mReceiver, intentFilter);

		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				android.os.Process.THREAD_PRIORITY_FOREGROUND);
		thread.start();
		// Get the HandlerThread's Looper and use it for our Handler
		mServiceHandler = new ServiceHandler(thread.getLooper());

		serializableDeviceMap = new SerializableDeviceMap();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		instance = this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand ==>> flags:" + flags
				+ " ==>> startId:" + startId);
		setUpLocationClientIfNeeded();
		deviceHashMap = DBChildren.getChildrenMapWithAddress(this);
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
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	private boolean initialize() {
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
		return true;
	}

	private void setUpLocationClientIfNeeded() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//获取所有可用的位置提供器  
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			//如果是GPS  
			locationProvider = LocationManager.GPS_PROVIDER;
		} else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			//如果是Network  
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} else {

		}
		System.out.println("locationProvider = " + locationProvider);
	}

	private void startLeScan(BluetoothAdapter.LeScanCallback leScanCallback,
			long scanPeriod) {
		if (scanner == null) {
			scanner = new BleDevicesScanner(mBluetoothAdapter, leScanCallback);
			scanner.setScanPeriod(scanPeriod);
		}
		scanner.start();
	}

	private void stopLeScan() {
		if (scanner != null) {
			scanner.stop();
		}
	}

	private void stop() {
		mNotificationManager.cancelAll();
		unregisterReceiver(mReceiver);
		stopLeScan();
		stopSelf();
		stopForeground(true);

		if (locationManager != null)
			locationManager.removeUpdates(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Broadcast the newest data to AntiLost Fragment
	 */
	private void broadcastUpdate() {
		Intent data = new Intent(ACTION_DATA_CHANGED);
		Bundle bundle = new Bundle();
		serializableDeviceMap.setMap(deviceHashMap);
		bundle.putSerializable(EXTRA_DEVICE_LIST, serializableDeviceMap);
		data.putExtras(bundle);
		sendBroadcast(data);
	}

	/**
	 * Build the foreground notification and alert notification
	 * @param content
	 */
	private void buildNotification(String content) {
		/*
		 *  build service foreground notification
		 */
		serviceNotificationbuilder = new NotificationCompat.Builder(
				RadarTrackingService.this);
		serviceNotificationbuilder.setSmallIcon(R.drawable.ic_location_default);
		serviceNotificationbuilder
				.setContentTitle(getString(R.string.text_radar_tracking));
		serviceNotificationbuilder.setContentText(content);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, LancherActivity.class);
		resultIntent.setAction("android.intent.action.MAIN");
		resultIntent.addCategory("android.intent.category.LAUNCHER");

		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		serviceNotificationbuilder.setContentIntent(resultPendingIntent);
		serviceNotification = serviceNotificationbuilder.build();
		startForeground(1, serviceNotification);
	}

	private class UploadLocationTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			if (latitude != 0 && longitude != 0) {
				String macAddress;
				StringBuffer sb = new StringBuffer();
				Iterator<String> it = deviceHashMap.keySet().iterator();
				while (it.hasNext()) {
					macAddress = it.next();
					if (System.currentTimeMillis()
							- deviceHashMap.get(macAddress).getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
						sb.append(macAddress);
						sb.append(";");
					}
				}
				if (sb.length() == 0) {
					return null;
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", String.valueOf(SharePrefsUtils.getUserId(
						RadarTrackingService.this, 0)));
				map.put("latitude", String.valueOf(latitude));
				map.put("longitude", String.valueOf(longitude));
				map.put("radius", String.valueOf(radius));
				map.put("macAddressList",
						sb.toString().substring(0, sb.length() - 1));
				System.out.println(map);
				return HttpRequestUtils
						.post(HttpConstants.UPLOAD_LOCATION, map);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			/*Toast.makeText(RadarTrackingService.this,
					HttpConstants.UPLOAD_LOCATION + " ==>> " + result,
					Toast.LENGTH_SHORT).show();*/
			System.out.println(HttpConstants.UPLOAD_LOCATION + " ==>> "
					+ result);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		radius = (int) location.getAccuracy();
		System.out.println(latitude + "--" + longitude + "--" + radius);
		mServiceHandler.sendEmptyMessage(MESSAGE_STOP_LOCATING);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

}
