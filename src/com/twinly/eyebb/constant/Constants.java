package com.twinly.eyebb.constant;

import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothGattService;

import com.eyebb.R;
import com.twinly.eyebb.bluetooth.BluetoothLeService;

public class Constants {
	public static final String DB_NAME = "eyebb.db";
	public static final int DB_VERSION = 1;

	public static final int LOCALE_EN = 0;
	public static final int LOCALE_CN = 1;
	public static final int LOCALE_HK = 2;
	public static final int LOCALE_TW = 3;

	// public static final String OURDEVICEUUID =
	// "4D616361726F6E202020202020202020";
	public static final String DEVICE_UUID_VERSON_1 = "4D616361726F6E051250002003020A00";
	public static final String DEVICE_UUID_VERSON_2 = "08094D616361726F6E051250";
	public static final String BEEP_UUID = "00001001-0000-1000-8000-00805f9b34fb";
	public static final String APPLICATION_UUID = "00001000-0000-1000-8000-00805f9b34fb";
	public static final String BEEP_CHAR_UUID = "1001";
	public static final String BEEP_CHAR_MAJOR = "1008";
	public static final String BEEP_CHAR_MINOR = "1009";
	public final static int START_PROGRASSS_BAR = 1;
	public final static int STOP_PROGRASSS_BAR = 2;
	public static final int BEEP_RSSI = -90;
	public static final int SCAN_INRERVAL_TIME = 1000;
	public static final int SCANTIME = Integer.MAX_VALUE;

	public static final int POSTDELAYTIME = Integer.MAX_VALUE - 500;

	// public static final int SCANTIME = 5000;
	// public static final int POSTDELAYTIME = 5000 - 500;

	public static final int DEVICE_CONNECT_STATUS_LOADING = 100;
	public static final int DEVICE_CONNECT_STATUS_ERROR = 101;
	public static final int DEVICE_CONNECT_STATUS_SUCCESS = 102;
	public static final int DEVICE_CONNECT_STATUS_DEFAULT = 103;
	public static final String BEEP_ALL_DEVICE = "beep_all_device";

	public static int DELAY = 0;
	public static final int PERIOD = 5000;
	public static final int BINDING_PERIOD = 15000;
	// private static final int SCANTIME = 5000;
	// private static final int POSTDELAYTIME = 4500;

	private int DEFAULT_MAJOR = 0;
	private int DEFAULT_MINOR = 0;

	public static boolean DEBUG = true;

	public static BluetoothLeService mBluetoothLeService;
	public static BluetoothLeService mBluetoothLeService2;
	public static ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	public static ArrayList<BluetoothGattService> gattServiceObject = new ArrayList<BluetoothGattService>();

	public static final int[] progressBarStyleSet = {
			R.drawable.my_progress_green01, R.drawable.my_progress_blue01,
			R.drawable.my_progress_green02, R.drawable.my_progress_blue02,
			R.drawable.my_progress_pink, R.drawable.my_progress_purple,
			R.drawable.my_progress_red, R.drawable.my_progress_yellow };

	public static final long validTimeDuration = 600000; // 10 minutes
	public static final int averageDays = 5;

	public static final int CONNECT_ERROR = 10001;
	public static final int SUCCESS_SEARCH = 10002;
	public static final int SEARCH_GUEST_NULL = 10003;

}
