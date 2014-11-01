package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.eyebb.R.string;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;

public class SharePrefsUtils {

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

	public static int getKindergartenId(Context context) {
		return getInt(context,
				ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_ID);
	}

	public static void setKindergartenId(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_ID,
				value);
	}

	public static String getKindergartenName(Context context) {
		int locale = SharePrefsUtils.getLanguage(context);
		switch (locale) {
		case Constants.LOCALE_CN:
			return getString(context,
					ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_TC);
		case Constants.LOCALE_TW:
			return getString(context,
					ActivityConstants.SHARE_PREFS_ITEM_KINDERGARTEN_NAME_SC);
		case Constants.LOCALE_HK:
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
	}

	public static boolean isUpdateIndoorLocator(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_INDOOR_LOCATOR_FLAG,
				true);
	}

	public static void setUpdateIndoorLocatorFlag(Context context, boolean value) {
		setBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_INDOOR_LOCATOR_FLAG,
				value);
	}

	public static boolean isUpdateReportPerformance(Context context) {
		return getBoolean(
				context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_REPORT_PERFORMANCE_FLAG,
				true);
	}

	public static void setUpdateReportPerfromanceFlag(Context context,
			boolean value) {
		setBoolean(
				context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_REPORT_PERFORMANCE_FLAG,
				value);
	}

	public static boolean isUpdateReportActivities(Context context) {
		return getBoolean(
				context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_REPORT_ACTIVITIES_FLAG,
				true);
	}

	public static void setUpdateReportActivitiesFlag(Context context,
			boolean value) {
		setBoolean(
				context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_REPORT_ACTIVITIES_FLAG,
				value);
	}

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

	public static boolean isUpdateNotice(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_NOTICE_FLAG, true);
	}

	public static void setUpdateNoticeFlag(Context context, boolean value) {
		setBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_UPDATE_NOTICE_FLAG, value);
	}

	public static long getReportChildId(Context context) {
		return getLong(context,
				ActivityConstants.SHARE_PREFS_ITEM_REPORT_CHILD_ID);
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
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_START_BEEP,
				value);
	}

	public static String refreshTime(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_REFRESH_TIME);
	}

	public static void setRefreshTime(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_REFRESH_TIME, value);
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
	
	public static String registerChildBirthday(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_CHILID_BIRTHDAY);
	}

	public static void setRegisterChildBirthday(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_CHILID_BIRTHDAY, value);
	}
	
	public static int BleServiceRunOnceFlag(Context context) {
		return getInt(context,
				ActivityConstants.SHARE_PREFS_BLE_SERVICE_RUN_ONCE_FLAG);
	}

	public static void setBleServiceRunOnceFlag(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_BLE_SERVICE_RUN_ONCE_FLAG,
				value);
	}
	
	public static String signUpDeviceMajor(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_DEVICE_MAJOR);
	}

	public static void setSignUpDeviceMajor(Context context, String value) {
		setString(context,
				ActivityConstants.SHARE_PREFS_DEVICE_MAJOR, value);
	}
	
	public static String signUpDeviceMinor(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_DEVICE_MINOR);
	}

	public static void setSignUpDeviceMinor(Context context, String value) {
		setString(context,
				ActivityConstants.SHARE_PREFS_DEVICE_MINOR, value);
	}
    /**
     * sign up
     */
	public static String signUpUsername(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_USERNAME);
	}

	public static void setSignUpUsername(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_USERNAME, value);
	}
	
	public static String signUpPassword(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_PASSWORD);
	}

	public static void setSignUpPassword(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_PASSWORD, value);
	}
	
	public static String signUpEmail(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_EMAIL);
	}

	public static void setSignUpEmail(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_EMAIL, value);
	}
	

	public static String signUpNickname(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_NICKNAME);
	}

	public static void setSignUpNickname(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_NICKNAME, value);
	}
	
	

	public static String signUpPhoneNumber(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_PHONE_NUMBER);
	}

	public static void setSignUpPhoneNumber(Context context, String value) {

		setString(context,
				ActivityConstants.SHARE_PREFS_SIGN_UP_PHONE_NUMBER, value);
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

	private static long getLong(Context context, String name) {
		return getPrefs(context).getLong(name, Long.MIN_VALUE);
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
