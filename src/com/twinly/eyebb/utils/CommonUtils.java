package com.twinly.eyebb.utils;

import android.text.TextUtils;

public class CommonUtils {
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isNull(String value) {
		if (TextUtils.isEmpty(value)) {
			return true;
		} else if (value.equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotNull(String value) {
		if (TextUtils.isEmpty(value)) {
			return false;
		} else if (value.equalsIgnoreCase("null")) {
			return false;
		} else {
			return true;
		}
	}
}
