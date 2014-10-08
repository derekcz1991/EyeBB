package com.twinly.eyebb.database;

import com.twinly.eyebb.model.Performance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBPerformance {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, Performance performance) {
		if (updateIfExist(context, performance)) {
			return;
		}
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("child_id", performance.getChildId());
		values.put("daily", performance.getDaily());
		values.put("weekly", performance.getWeekly());
		values.put("monthly", performance.getMonthly());
		values.put("last_update_date", performance.getLastUpdateDate());
		db.insertOrThrow("performance", null, values);
		db.close();
	}

	private static boolean updateIfExist(Context context,
			Performance performance) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("daily", performance.getDaily());
		values.put("weekly", performance.getWeekly());
		values.put("monthly", performance.getMonthly());
		values.put("last_update_date", performance.getLastUpdateDate());
		int result = db.update("performance", values, "child_id=?",
				new String[] { String.valueOf(performance.getChildId()) });

		db.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static Performance getPerformanceByChildId(Context context,
			long childId) {
		Performance performance = null;
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery(
				"select * from performance where child_id = " + childId, null);
		if (cursor.moveToFirst()) {
			performance = new Performance();
			performance.setChildId(childId);
			performance.setDaily(cursor.getString(cursor
					.getColumnIndex("daily")));
			performance.setWeekly(cursor.getString(cursor
					.getColumnIndex("weekly")));
			performance.setMonthly(cursor.getString(cursor
					.getColumnIndex("monthly")));
			performance.setLastUpdateDate(cursor.getString(cursor
					.getColumnIndex("last_update_date")));
		}
		db.close();
		return performance;
	}
}
