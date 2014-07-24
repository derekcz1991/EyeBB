package com.twinly.eyebb.utils;

import com.twinly.eyebb.constant.Constants;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

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
}
