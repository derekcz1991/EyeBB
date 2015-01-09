package com.twinly.eyebb.constant;

import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothGattService;

import com.twinly.eyebb.bluetooth.BluetoothLeService;

public class BleDeviceConstants {

	public static final String DEVICE_UUID_VERSON_1 = "4D616361726F6E051250002003020A00";
	public static final String DEVICE_UUID_VERSON_2 = "08094D616361726F6E051250";
	public static final String BEEP_UUID = "00001001-0000-1000-8000-00805f9b34fb";
	public static final String APPLICATION_UUID = "00001000-0000-1000-8000-00805f9b34fb";
	public static final String BEEP_CHAR_UUID = "1001";
	public static final String BEEP_CHAR_BATTERY = "1004";
	public static final String BEEP_CHAR_MAJOR = "1008";
	public static final String BEEP_CHAR_MINOR = "1009";
	public final static int START_PROGRASSS_BAR = 1;
	public final static int STOP_PROGRASSS_BAR = 2;
	public static final int BEEP_RSSI = -90;
	public static final long SCAN_INRERVAL_TIME = 10000;
	public static final int SCANTIME = Integer.MAX_VALUE;

	public static final int RSSI_STRONG = -50;
	public static final int RSSI_GOOD = -70;
	public static final int RSSI_WEAK = -100;

	public static final int POSTDELAYTIME = Integer.MAX_VALUE - 500;
	public static final int DEVICE_CONNECT_STATUS_LOADING = 100;
	public static final int DEVICE_CONNECT_STATUS_ERROR = 101;
	public static final int DEVICE_CONNECT_STATUS_SUCCESS = 102;
	public static final int DEVICE_CONNECT_STATUS_DEFAULT = 103;
	public static final String BEEP_ALL_DEVICE = "beep_all_device";

	public static int DELAY = 0;
	public static final int PERIOD = 10000;
	public static final int BINDING_PERIOD = 40000;
	public static final int BATTERY_DELAY_LOADING = 3000;




	public static boolean DEBUG = true;

	public static BluetoothLeService mBluetoothLeService;
	public static BluetoothLeService mBluetoothLeService2;
	public static ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	public static ArrayList<BluetoothGattService> gattServiceObject = new ArrayList<BluetoothGattService>();

	public static final String BLE_SERVICE_COME_FROM = "device_come_from";
	
	//handler
	public static final int CAN_NOT_SUPPORT_BLE = 30003;
}
