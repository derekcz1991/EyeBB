package com.twinly.eyebb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.eyebb.R;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.RadarCharacteristicsActivity;
import com.twinly.eyebb.bluetooth.SampleGattAttributes;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.BLEUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BleCharacteristicsService extends Service {
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	TextView status_text;

	int servidx, charaidx;

	BluetoothGattService gattService;
	ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
	ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
	// 做計時器 自動關閉activity 放置阻塞主線程
	private Timer timer;
	// 控制時間
	private int keepTim = 0;

	private String uuid;
	public static final int FINISH_ACTIVITY = 1;

	private SharedPreferences MajorAndMinorPreferences;
	private SharedPreferences.Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		registerReceiver(mGattUpdateReceiver, new IntentFilter(
				BluetoothLeService.ACTION_DATA_AVAILABLE));
		System.out.println("chara onCreate");

	}

	@SuppressLint("NewApi")
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.out.println("chara onStart");
		
		MajorAndMinorPreferences = getSharedPreferences("MajorAndMinor",
				MODE_PRIVATE);
		editor = MajorAndMinorPreferences.edit();

		servidx = intent.getIntExtra("servidx", -1);

		if (servidx == -1) {
			Toast.makeText(this, "Characteristics Index Error!",
					Toast.LENGTH_LONG).show();

		}

		listItem = new ArrayList<HashMap<String, Object>>();

		gattService = Constants.gattServiceObject.get(servidx);

		Thread disconverThread = new Thread() {
			@SuppressLint("NewApi")
			public void run() {
				// status_text.setText(Constants.gattServiceData.get(servidx).get(
				// "NAME")
				// + ": Discovering Characteristics...");
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();

				String uuid = null;
				String name = null;
				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					charas.add(gattCharacteristic);
					HashMap<String, String> currentCharaData = new HashMap<String, String>();
					uuid = gattCharacteristic.getUuid().toString();
					uuid = uuid.substring(4, 8);
					boolean exist = false;
					for (HashMap<String, String> sItem : gattCharacteristicGroupData) {
						if (sItem.get("UUID").equals(uuid)) {
							exist = true;
							break;
						}
					}
					if (exist) {
						continue;
					}
					name = SampleGattAttributes.lookup(uuid, "Unknow");
					currentCharaData.put("NAME", name);
					currentCharaData.put("UUID", uuid);
					gattCharacteristicGroupData.add(currentCharaData);
					addItem(name, uuid);

				}
				// status_text.setText(Constants.gattServiceData.get(servidx).get(
				// "NAME")
				// + ": Discovered");

			}
		};
		disconverThread.start();

		registerReceiver(mGattUpdateReceiver, new IntentFilter(
				BluetoothLeService.ACTION_DATA_AVAILABLE));

		if (charas.size() > 0) {
			// 蜂鳴開始
			final BluetoothGattCharacteristic characteristic = charas.get(0);
			final int charaProp = characteristic.getProperties();
			if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
				uuid = characteristic.getUuid().toString();
				System.out.println(" charas uuid ==>" + uuid);
				uuid = uuid.substring(4, 8);
				charaidx = 0;
				Constants.mBluetoothLeService
						.readCharacteristic(characteristic);
			}
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				String data = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				System.out.println("data========>" + data);
				System.out.println("uuid========>" + uuid);
				if (uuid.equals("1001")) {
					System.out.println("this is 1001" + data + "=>16/ "
							+ Integer.parseInt(data, 16));
					modify1001(data);
				} else if (uuid.equals("1004")) {
					System.out.println("this is battary life!!!!!!!!" + data
							+ "=>16/ " + Integer.parseInt(data, 16));

				} else if (uuid.equals("1006")) {
					// modify1006(data);

				}
			}
		}

	};
	// // 倒計時
	// TimerTask task = new TimerTask() {
	// public void run() {
	// Message message = new Message();
	// message.what = FINISH_ACTIVITY;
	// handler.sendMessage(message);
	// }
	// };

	Runnable finishConnection = new Runnable() {
		@Override
		public void run() {

			// RadarCharacteristicsActivity.this.finish();
		}
	};

	// Handler handler = new Handler() {
	//
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	//
	// case FINISH_ACTIVITY:
	//
	// keepTim++;
	// if (keepTim == 3) {
	// new Thread(finishConnection).start();
	// }
	//
	// break;
	// }
	// }
	// };

	@SuppressLint("NewApi")
	private void modify1001(String data) {
		// TODO Auto-generated method stub

		data = "01";

		BluetoothGattCharacteristic characteristic = charas.get(charaidx);
		characteristic.setValue(BLEUtils.HexString2Bytes(data));
		Constants.mBluetoothLeService.wirteCharacteristic(characteristic);
		// timer = new Timer(true);
		// timer.schedule(task, 1000, 1000); // 延时1000ms后执行，1000ms执行一次

		// editor.putInt("startDialog", 1);
		// editor.commit();
		// //finish();
	}

	private void addItem(String devname, String address) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", devname);
		map.put("text", address);
		listItem.add(map);
		// listItemAdapter.notifyDataSetChanged();
	}

}
