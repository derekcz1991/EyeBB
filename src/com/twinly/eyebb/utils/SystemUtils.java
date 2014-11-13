package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.twinly.eyebb.constant.Constants;

public class SystemUtils {

	public static int getLocale(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();

		System.out.println("--->>" + config.locale);
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

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	

}
