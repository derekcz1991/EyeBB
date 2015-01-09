package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.twinly.eyebb.constant.ActivityConstants;

public class SharePrefsUtils {

	public static void clear(Context context) {
		getPrefs(context).edit().clear().commit();
	}

	public static String getLoginAccount(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_ITEM_LOGIN_ACCOUNT);
	}

	public static void setLoginAccount(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_LOGIN_ACCOUNT,
				value);
	}

	public static String getPassword(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_PASSWORD);
	}

	public static void setPassowrd(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_PASSWORD, value);
	}

	public static Boolean isLogin(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_IS_LOGIN,
				false);
	}

	public static void setLogin(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_IS_LOGIN, value);
	}

	public static String getUserType(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_TYPE);
	}

	public static void setUserType(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_TYPE, value);
	}

	public static String getUserName(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_NAME);
	}

	public static void setUserName(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_NAME, value);
	}

	public static String getUserPhone(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_PHONE);
	}

	public static void setUserPhone(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_PHONE, value);
	}

	public static long getUserId(Context context, long defValue) {
		return getLong(context, ActivityConstants.SHARE_PREFS_ITEM_USER_ID,
				defValue);
	}

	public static void setUserId(Context context, long value) {
		setLong(context, ActivityConstants.SHARE_PREFS_ITEM_USER_ID, value);
	}

	public static long getReportChildId(Context context, long defValue) {
		return getLong(context,
				ActivityConstants.SHARE_PREFS_ITEM_REPORT_CHILD_ID, defValue);
	}

	public static void setReportChildId(Context context, long value) {
		setLong(context, ActivityConstants.SHARE_PREFS_ITEM_REPORT_CHILD_ID,
				value);
	}

	public static boolean isAutoUpdate(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_AUDO_UPDATE, false);
	}

	public static void setAutoUpdate(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_AUDO_UPDATE,
				value);
	}

	public static boolean isSoundOn(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_SOUND,
				true);
	}

	public static void setSoundOn(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_SOUND, value);
	}

	public static boolean isVibrateOn(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_VIBRATE,
				true);
	}

	public static void setVibrateOn(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_VIBRATE, value);
	}

	public static int getLanguage(Context context) {
		int language = getInt(context,
				ActivityConstants.SHARE_PREFS_ITEM_LANGUAGE);
		if (language < 0) {
			language = SystemUtils.getLocale(context);
			setLanguage(context, language);
		}
		return language;
	}

	public static void setLanguage(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_LANGUAGE, value);
	}

	public static Long getAutoUpdateTime(Context context, long defValue) {
		return getLong(context,
				ActivityConstants.SHARE_PREFS_ITEM_AUTO_UPDATE_TIME, defValue);
	}

	public static void setAutoUpdateTime(Context context, Long value) {
		setLong(context, ActivityConstants.SHARE_PREFS_ITEM_AUTO_UPDATE_TIME,
				value);
	}

	public static int getAppVersion(Context context) {
		return getInt(context, ActivityConstants.SHARE_PREFS_ITEM_APP_VERSION);
	}

	public static void setAppVersion(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_APP_VERSION, value);
	}

	public static String getDeviceId(Context context) {
		String deviceId = getString(context,
				ActivityConstants.SHARE_PREFS_ITEM_DEVICE_ID);
		if (TextUtils.isEmpty(deviceId)) {
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = getAppVersion(context);
		int currentVersion = SystemUtils.getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}

		return deviceId;
	}

	public static void setDeviceId(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_DEVICE_ID, value);
	}

	/*public static int getKindergartenId(Context context) {
		return getInt(context,
				ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_ID);
	}

	public static void setKindergartenId(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_ID,
				value);
	}*/

	/*public static String getKindergartenName(Context context) {
		int locale = SharePrefsUtils.getLanguage(context);
		switch (locale) {
		case BleDeviceConstants.LOCALE_CN:
			return getString(context,
					ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_TC);
		case BleDeviceConstants.LOCALE_TW:
			return getString(context,
					ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_SC);
		case BleDeviceConstants.LOCALE_HK:
			return getString(context,
					ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_SC);
		default:
			return getString(context,
					ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_EN);
		}

	}

	public static void setKindergartenNameTc(Context context, String value) {
		setString(context,
				ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_TC, value);
	}

	public static void setKindergartenNameSc(Context context, String value) {
		setString(context,
				ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_SC, value);
	}

	public static void setKindergartenNameEn(Context context, String value) {
		setString(context,
				ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_EN, value);
	}*/

	public static boolean isInitHead(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_INIT_HEAD,
				true);
	}

	public static void setInitHead(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_INIT_HEAD, value);
	}

	public static boolean isfinishBeep(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_FINISH_BEEP,
				false);
	}

	public static void setfinishBeep(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_FINISH_BEEP, value);
	}

	public static int isConnectBleService(Context context) {
		return getInt(context,
				ActivityConstants.SHARE_PREFS_CONNECT_BLE_SERVICE);
	}

	public static void setConnectBleService(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_CONNECT_BLE_SERVICE,
				value);
	}

	public static int bleServiceIndex(Context context) {
		return getInt(context, ActivityConstants.SHARE_PREFS_BLE_SERVICE_INDEX);
	}

	public static void setBleServiceIndex(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_BLE_SERVICE_INDEX, value);
	}

	public static boolean isStartBeepDialog(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_START_BEEP, false);
	}

	public static void setStartBeepDialog(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_BEEP_ALL_DEVICE,
				value);
	}

	public static boolean isBeepAllDevice(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_BEEP_ALL_DEVICE, false);
	}

	public static void setBeepAllDevice(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_BINDING_DEVICE, value);
	}

	public static boolean isOpenBindingDevice(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_BINDING_DEVICE, false);
	}

	public static void setOpenBindingDevice(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_START_BEEP,
				value);
	}

	public static boolean isNotificationDot(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_NOTIFICATION_DOT, false);
	}

	public static void setNotificationDot(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_NOTIFICATION_DOT,
				value);
	}
	
	public static String isMacAddress(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_MAC_ADDRESSS);
	}

	public static void setMacAddress(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_MAC_ADDRESSS, value);
	}

	public static int CancelConnectBleServiceTimes(Context context) {
		return getInt(context, ActivityConstants.SHARE_PREFS_ITEM_RUN_NUM_RADAR);
	}

	public static void setCancelConnectBleServiceTimes(Context context,
			int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_RUN_NUM_RADAR, value);
	}

	public static int DeviceConnectStatus(Context context) {
		return getInt(context,
				ActivityConstants.SHARE_PREFS_DEVICE_CONNECT_STATUS);
	}

	public static void setDeviceConnectStatus(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_DEVICE_CONNECT_STATUS,
				value);
	}

	public static String KeepDeviceConnectStatus(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_KEEP_DEVICE_CONNECT_STATUS);
	}

	public static void setKeepDeviceConnectStatus(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_KEEP_DEVICE_CONNECT_STATUS, value);
	}

	public static int BleServiceRunOnceFlag(Context context) {
		return getInt(context,
				ActivityConstants.SHARE_PREFS_BLE_SERVICE_RUN_ONCE_FLAG);
	}

	public static void setBleServiceRunOnceFlag(Context context, int value) {
		setInt(context,
				ActivityConstants.SHARE_PREFS_BLE_SERVICE_RUN_ONCE_FLAG, value);
	}

	public static String deviceBattery(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_DEVICE_BATTERY);
	}

	public static void setdeviceBattery(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_DEVICE_BATTERY, value);
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(
				ActivityConstants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	private static String getString(Context context, String name) {
		return getPrefs(context).getString(name, "");
	}

	private static boolean getBoolean(Context context, String name,
			boolean defaultValue) {
		return getPrefs(context).getBoolean(name, defaultValue);
	}

	private static int getInt(Context context, String name) {
		return getPrefs(context).getInt(name, Integer.MIN_VALUE);
	}

	private static long getLong(Context context, String name, long defValue) {
		return getPrefs(context).getLong(name, defValue);
	}

	private static void setString(Context context, String name, String value) {
		getPrefs(context).edit().putString(name, value).commit();
	}

	private static void setBoolean(Context context, String name, boolean value) {
		getPrefs(context).edit().putBoolean(name, value).commit();
	}

	private static void setInt(Context context, String name, int value) {
		getPrefs(context).edit().putInt(name, value).commit();
	}

	private static void setLong(Context context, String name, long value) {
		getPrefs(context).edit().putLong(name, value).commit();
	}

}
