package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.twinly.eyebb.constant.Constants;

public class SystemUtils {

	public static int getLocale(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();

		if (config.locale.toString().equals("zh_TW")
				|| config.locale.toString().equals("zh")) {
			return Constants.LOCALE_TW;
		} else if (config.locale.toString().equals("zh_HK")
				|| config.locale.toString().equals("zh")) {
			return Constants.LOCALE_HK;
		} else if (config.locale.toString().equals("zh_CN")
				|| config.locale.toString().equals("zh")) {
			return Constants.LOCALE_CN;
		} else {
			return Constants.LOCALE_EN;
		}
	}

	/*public static void checkBluetooth(Context context) {
		// TODO Auto-generated method stub
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(context, R.string.text_ble_not_supported,
					Toast.LENGTH_SHORT).show();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(context,
					R.string.text_error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
		}
	}*/
}
