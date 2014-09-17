package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
		int locale = SystemUtils.getLocale(context);
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
