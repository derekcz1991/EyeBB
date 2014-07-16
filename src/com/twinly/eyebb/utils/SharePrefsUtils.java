package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.twinly.eyebb.constant.Constants;

public class SharePrefsUtils {

	public static String getLoginAccount(Context context) {
		return getString(context, Constants.SHARE_PREFS_ITEM_LOGIN_ACCOUNT);
	}

	public static void setLoginAccount(Context context, String value) {
		setString(context, Constants.SHARE_PREFS_ITEM_LOGIN_ACCOUNT, value);
	}

	public static Boolean isLogin(Context context) {
		return getBoolean(context, Constants.SHARE_PREFS_ITEM_IS_LOGIN, false);
	}

	public static void setLogin(Context context, boolean value) {
		setBoolean(context, Constants.SHARE_PREFS_ITEM_IS_LOGIN, value);
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
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

	private static void setString(Context context, String name, String value) {
		getPrefs(context).edit().putString(name, value).commit();
	}

	private static void setBoolean(Context context, String name, boolean value) {
		getPrefs(context).edit().putBoolean(name, value).commit();
	}

	private static void setInt(Context context, String name, int value) {
		getPrefs(context).edit().putInt(name, value).commit();
	}
}
