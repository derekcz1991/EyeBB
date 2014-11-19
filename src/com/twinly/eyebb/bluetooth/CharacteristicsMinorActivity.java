package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.activity.CheckBeaconActivity;
import com.twinly.eyebb.activity.ErrorDialog;
import com.twinly.eyebb.activity.VerifyWhenLoginDialog;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class CharacteristicsMinorActivity extends Activity {

	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	TextView status_text;
	Timer timer = new Timer();
	int servidx, charaidxMajor, charaidxMinor;
	String name = null;
	String name2 = null;
	BluetoothGattService gattService;
	BluetoothGattService gattService2;
	ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> gattCharacteristicGroupData2 = new ArrayList<HashMap<String, String>>();
	ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
	ArrayList<BluetoothGattCharacteristic> charas2 = new ArrayList<BluetoothGattCharacteristic>();
	private String uuid;
	private String uuid2;

	private Dialog dialog;

	private TimerTask TimeOutTask = null;
	private int intentTime = 0;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ble_characteristics);

		BaseApp.getInstance().addActivity(this);

		SharePrefsUtils.setOpenBindingDevice(CharacteristicsMinorActivity.this,
				true);

		status_text = (TextView) findViewById(R.id.characteristics_status);
		dialog = LoadingDialog.createLoadingDialogCanCancel(
				CharacteristicsMinorActivity.this,
				getString(R.string.toast_write_minor));
		dialog.show();
		final Intent intent = getIntent();
		servidx = intent.getIntExtra("servidx", -1);
		System.out.println("servidx--minor->" + servidx);
		if (servidx == -1) {
			Toast.makeText(this, "Characteristics Index Error!",
					Toast.LENGTH_LONG).show();
			CharacteristicsMinorActivity.this.finish();
		}

		TimeOutTask = new TimerTask() {
			public void run() {
				intentTime++;
				System.out.println("minor ==>" + intentTime);
				if (intentTime == 20) {
					intentTime = 0;
					Intent intent = new Intent(
							CharacteristicsMinorActivity.this,
							ErrorDialog.class);
					if (dialog.isShowing())
						dialog.dismiss();
					CheckBeaconActivity.instance.finish();

					try {
						intentTime = 21;
						TimeOutTask.cancel();
						TimeOutTask = null;
						timer.cancel();
						timer.purge();
						timer = null;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					startActivity(intent);
					finish();
				}
			}
		};

		timer.schedule(TimeOutTask, 0, 1000);

		SharePrefsUtils.setBleServiceIndex(CharacteristicsMinorActivity.this,
				servidx);

		listItem = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.ble_characteristics_listview, new String[] { "title",
						"text" }, new int[] { R.id.characteristics_ItemTitle,
						R.id.characteristics_ItemText });
		myList = (ListView) findViewById(R.id.characteristics_listView);
		myList.setAdapter(listItemAdapter);

		// final BluetoothGattCharacteristic characteristic = charas.get(0);
		// uuid = characteristic.getUuid().toString();
		// uuid = uuid.substring(4,8);
		// charaidx = 0;
		// Constans.mBluetoothLeService.readCharacteristic(characteristic);

		gattService2 = BleDeviceConstants.gattServiceObject.get(servidx);

		registerReceiver(mGattUpdateReceiver2, new IntentFilter(
				BluetoothLeService.ACTION_DATA_AVAILABLE));

		Thread disconverThread2 = new Thread() {
			@SuppressLint("NewApi")
			public void run() {

				// System.out.println("disconverThread ");
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService2
						.getCharacteristics();

				// System.out.println("disconverThread. ");
				// Loops through available Characteristics.
				if (gattCharacteristics == null) {
					System.out.println("gattCharacteristics NULL");
				}

				for (int i = 0; i < gattCharacteristics.size(); i++) {
					charas2.add(gattCharacteristics.get(i));
					// System.out.println("disconverThread1 ");
					HashMap<String, String> currentCharaData = new HashMap<String, String>();
					// System.out.println("disconverThread2 ");
					uuid2 = gattCharacteristics.get(i).getUuid().toString();
					// System.out.println("disconverThread3 ");
					uuid2 = uuid2.substring(4, 8);
					// System.out.println("uuid char==>" + uuid);

					name2 = SampleGattAttributes.lookup(uuid2, "Unknow");
					currentCharaData.put("NAME", name2);
					currentCharaData.put("UUID", uuid2);
					gattCharacteristicGroupData2.add(currentCharaData);
					addItem(name2, uuid2);

					if (uuid2.equals(BleDeviceConstants.BEEP_CHAR_MINOR)) {
						final BluetoothGattCharacteristic characteristic2 = gattCharacteristics
								.get(i);
						final int charaProp = characteristic2.getProperties();
						if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
							// uuid =
							// characteristic.getUuid().toString();
							System.out.println(" charas2 uuid ==>" + uuid2
									+ "  " + i);
							// uuid = uuid.substring(4, 8);
							// uuid = uuid;
							charaidxMinor = i;
							if (BleDeviceConstants.mBluetoothLeService != null)
								BleDeviceConstants.mBluetoothLeService
										.readCharacteristic(characteristic2);

							break;
						}
					}
				}

			}
		};
		disconverThread2.start();

		// Intent intentToVerify = new Intent();
		//
		// intentToVerify.setClass(CharacteristicsActivity.this,
		// VerifyDialog.class);
		// // 關掉BLE服務
		//
		// startActivity(intentToVerify);
		// unregisterReceiver(mGattUpdateReceiver);
		// finish();

	}

	private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				String data = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				System.out.println("data========>" + data);

				if (uuid2.equals("1009")) {
					System.out.println("this is 1009" + data + " "
							+ Integer.parseInt(data, 16));
					modify1009();
				}
			}
		}

		@SuppressLint("NewApi")
		private void modify1009() {
			// TODO Auto-generated method stub

			try {
				String data2 = SharePrefsUtils
						.signUpDeviceMinor(CharacteristicsMinorActivity.this);

				BluetoothGattCharacteristic characteristic2 = charas2
						.get(charaidxMinor);

				characteristic2.setValue(BLEUtils.HexString2Bytes(data2));

				BleDeviceConstants.mBluetoothLeService
						.wirteCharacteristic(characteristic2);
				System.out.println("---->finish1009");

				try {
					TimeOutTask.cancel();
					TimeOutTask = null;
					timer.cancel();
					timer.purge();
					timer = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dialog.isShowing())
					dialog.dismiss();
				Intent intent = new Intent(CharacteristicsMinorActivity.this,
						VerifyWhenLoginDialog.class);
				startActivity(intent);

				finish();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		// registerReceiver(mGattUpdateReceiver, new IntentFilter(
		// BluetoothLeService.ACTION_DATA_AVAILABLE));
		registerReceiver(mGattUpdateReceiver2, new IntentFilter(
				BluetoothLeService.ACTION_DATA_AVAILABLE));
		System.out.println("chara onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(mGattUpdateReceiver);
		//System.out.println("1009 onpause");
		if (dialog.isShowing())
			dialog.dismiss();
		try {
			TimeOutTask.cancel();
			TimeOutTask = null;
			timer.cancel();
			timer.purge();
			timer = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unregisterReceiver(mGattUpdateReceiver2);
	}

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

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// CharacteristicsActivity.this.finish();
	// System.out.println("=========>onKeyDown");
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}