package com.twinly.eyebb.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.twinly.twinly.R;

public class ImageUtils {
	public static DisplayImageOptions avatarOpitons = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_avatar_dark)
			.showImageForEmptyUri(R.drawable.icon_avatar_dark)
			.showImageOnFail(R.drawable.icon_avatar_dark).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true).build();

	public static DisplayImageOptions locationIconOpitons = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_location_default)
			.showImageForEmptyUri(R.drawable.ic_location_default)
			.showImageOnFail(R.drawable.ic_location_default)
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.build();

	public static boolean saveBitmap(Bitmap bitmap, String path) {
		File file = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				System.out.println("saveBitmap ==>>" + e.getMessage());
				return false;
			}
		} catch (FileNotFoundException e) {
			System.out.println("saveBitmap ==>>" + e.getMessage());
			return false;
		}
		return true;
	}

	public static Bitmap getBitmapFromLocal(String path) {
		return BitmapFactory.decodeFile(path);
	}

	public static boolean isLocalImage(String path) {
		if (CommonUtils.isNotNull(path))
			return true;
		else
			return false;
	}
}
