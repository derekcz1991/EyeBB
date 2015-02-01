package com.twinly.eyebb.bluetooth;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;

public class BLEUtils {
	public final static String SERVICE_UUID_0001 = "00001000-0000-1000-8000-00805f9b34fb";
	public final static String SERVICE_UUID_0002 = "00002000-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_PASSWORD = "00002005-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_MAJOR_UUID = "00001008-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_MINOR_UUID = "00001009-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_BEEP_UUID = "00001001-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_ANTI_LOST_PERIOD_UUID = "00001003-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_ANTI_LOST_TIMEOUT_UUID = "0000100a-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_BATTERY_UUID = "00001004-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTICS_LED_BLINK_UUID = "0000100b-0000-1000-8000-00805f9b34fb";
	
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;
	public static final int STATE_DISCOVERED = 3;

	public final static String ACTION_GATT_CONNECTED = "bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_READ_SUCCESS = "bluetooth.le.ACTION_GATT_READ_SUCCESS";
	public final static String ACTION_GATT_READ_FAILURE = "bluetooth.le.ACTION_GATT_READ_FAILURE";
	public final static String ACTION_GATT_WRITE_SUCCESS = "bluetooth.le.ACTION_GATT_WRITE_SUCCEED";
	public final static String ACTION_GATT_WRITE_FAILURE = "bluetooth.le.ACTION_GATT_WRITE_FAILURE";
	public final static String EXTRA_DATA = "bluetooth.le.EXTRA_DATA";

	public static final int RSSI_STRONG = -50;
	public static final int RSSI_GOOD = -70;
	public static final int RSSI_WEAK = -100;

	public static String bytesToHex(byte[] bytes, int begin, int length) {
		StringBuilder sbuf = new StringBuilder();
		for (int idx = begin; idx < begin + length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10)
				sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase(Locale.US));
		}
		return sbuf.toString();
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase(Locale.US) + " ");
		}
		System.out.println("");
	}

	/**
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase(Locale.US);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static String checkMajorMinor(String value) {
		switch (value.length()) {
		case 1:
			value = "000" + value;
			break;
		case 2:
			value = "00" + value;
			break;
		case 3:
			value = "0" + value;
			break;

		}
		return value;
	}

	/**
	 * if device phone supports the ble
	 */
	public static boolean isSupportBle(Context context) {
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			System.out.println("CAN NOT SUPPORT BLE");
			return false;

		} else {
			return true;
		}
	}

	public static int getRssiLevel(int rssi) {
		if (rssi > RSSI_STRONG) {
			return RSSI_STRONG;
		} else if (rssi < RSSI_GOOD) {
			return RSSI_WEAK;
		} else {
			return RSSI_GOOD;
		}
	}

}
