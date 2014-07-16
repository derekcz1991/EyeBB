package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;

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
	
	//private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean mConnected = false;
		
    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ble_services);
		BaseApp.getInstance().addActivity(this);
		
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		
		Constans.gattServiceData.clear();
		Constans.gattServiceObject.clear();
		
		status_text = (TextView)findViewById(R.id.services_status);
		
		listItem = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItem, R.layout.ble_services_listview,
		new String[]{"title", "text"},
		new int[]{R.id.services_ItemTitle, R.id.services_ItemText});
		myList = (ListView)findViewById(R.id.services_listView);
		myList.setAdapter(listItemAdapter);
		
		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		        final Intent intent = new Intent();
		        intent.setClass(ServicesActivity.this, CharacteristicsActivity.class);
		        intent.putExtra("servidx", arg2);
		        startActivity(intent);
			}
		});		
		
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		boolean bll = bindService(gattServiceIntent, mServiceConnection,BIND_AUTO_CREATE);
		if(!bll){
			Toast.makeText(this, "Bind Service Failed!", Toast.LENGTH_SHORT).show();
			ServicesActivity.this.finish();
		}
    }
    
 // Code to manage Service lifecycle.
 	private final ServiceConnection mServiceConnection = new ServiceConnection() {

 		@Override
 		public void onServiceConnected(ComponentName componentName,
 				IBinder service) {
 			Constans.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
 			if (!Constans.mBluetoothLeService.initialize()) {
 				Log.e(TAG, "Unable to initialize Bluetooth");
 				finish();
 			}
 			// Automatically connects to the device upon successful start-up
 			// initialization.
 			Constans.mBluetoothLeService.connect(mDeviceAddress);
 			status_text.setText(mDeviceName+": Connecting...");
 		}

 		@Override
 		public void onServiceDisconnected(ComponentName componentName) {
 			Constans.mBluetoothLeService = null;
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
 				Constans.mBluetoothLeService.discoverServices();
 				status_text.setText(mDeviceName+": Discovering services...");
 			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
 				mConnected = false;
 				status_text.setText(mDeviceName+": Disconnected");
 				ServicesActivity.this.finish();
 			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
 				// Show all the supported services and characteristics on the
 				// user interface.
 				status_text.setText(mDeviceName+": Discovered");
 				displayGattServices(Constans.mBluetoothLeService.getSupportedGattServices());
 			} 
 			/*
 			else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
 				status_text.setText(mDeviceName+": DATA AVAILABLE");
 				String temp = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
 			}
 			*/
 		}
 	};
    private void addItem(String devname,String address)
    {
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	map.put("title", devname);
    	map.put("text", address);
    	listItem.add(map);
    	listItemAdapter.notifyDataSetChanged();
    }
    private void deleteItem()
    {
    	int size = listItem.size();
    	if( size > 0 )
    	{
    		listItem.remove(listItem.size() - 1);
    		listItemAdapter.notifyDataSetChanged();
    	}
    }
    private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		String uuid = null;
		String name = null;
		
		//ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		//mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			uuid = uuid.substring(4,8);
			boolean exist = false;
			for(HashMap<String, String> sItem:Constans.gattServiceData){
				if(sItem.get(LIST_UUID).equals(uuid)){
					exist = true;
					break;
				}
			}
			if(exist){
				continue;
			}
			name = SampleGattAttributes.lookup(uuid, "Unknow Service");
			currentServiceData.put(LIST_NAME, name);
			currentServiceData.put(LIST_UUID, uuid);
			Constans.gattServiceData.add(currentServiceData);
			Constans.gattServiceObject.add(gattService);
			addItem(name,uuid);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (Constans.mBluetoothLeService != null) {
			final boolean result = Constans.mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
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
		Constans.mBluetoothLeService = null;
	}
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		//intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {			
			//Constans.exit_ask(this);
			if(Constans.mBluetoothLeService != null){
				Constans.mBluetoothLeService.disconnect();
			}
			mConnected = false;
			ServicesActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}