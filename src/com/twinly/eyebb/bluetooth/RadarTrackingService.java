package com.twinly.eyebb.bluetooth;

import java.util.HashMap;
import java.util.Iterator;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
public class RadarTrackingService extends Service implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	public final static String ACTION_DATA_CHANGED = "radar_tracking.ACTION_DATA_CHANGED";
	public final static String ACTION_STOP_SERVICE = "radar_tracking.ACTION_STOP_SERVICE";
	public static final String EXTRA_DEVICE_LIST = "DEVICE_LIST";

	private final static String TAG = RadarTrackingService.class
			.getSimpleName();

	private final int MESSAGE_INIT_NOTIFICAION = 1;
	private final int MESSAGE_SCANN = 2;
	private final int MESSAGE_UPDATE_VIEW = 3;

	private HashMap<String, Device> deviceHashMap;
	private SerializableDeviceMap serializableDeviceMap;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager mBluetoothManager;
	private BleDevicesScanner scanner;

	private ServiceHandler mServiceHandler;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder serviceNotificationbuilder;
	private Notification serviceNotification;

	private LocationClient mLocationClient;
	private double latitude, longitude;

	// These settings are the same as the settings for the map. They will in fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
				new UploadLocationTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				if (initialize()) {
					buildNotification(getString(R.string.text_is_runnig));
					mServiceHandler.sendEmptyMessage(MESSAGE_SCANN);
					mServiceHandler.sendEmptyMessage(MESSAGE_UPDATE_VIEW);
				}
				break;
			case MESSAGE_SCANN:
				startLeScan(leScanCallback, 500);
				break;
			case MESSAGE_UPDATE_VIEW:
				broadcastUpdate();
				mServiceHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_VIEW,
						5000);
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

		setUpLocationClientIfNeeded();
		mLocationClient.connect();
		//new HttpRequestUtils();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand ==>> flags:" + flags
				+ " ==>> startId:" + startId);
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
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(this, this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
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
		mServiceHandler.removeMessages(MESSAGE_UPDATE_VIEW);

		mNotificationManager.cancelAll();
		unregisterReceiver(mReceiver);
		stopLeScan();
		stopSelf();
		stopForeground(true);

		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
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
			Toast.makeText(RadarTrackingService.this,
					HttpConstants.UPLOAD_LOCATION + " ==>> " + result,
					Toast.LENGTH_SHORT).show();
			System.out.println(HttpConstants.UPLOAD_LOCATION + " ==>> "
					+ result);
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		System.out.println(latitude + "--" + longitude);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
}
