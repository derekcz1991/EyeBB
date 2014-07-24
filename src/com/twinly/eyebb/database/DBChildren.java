package com.twinly.eyebb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.Child;

public class DBChildren {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, Child child) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("child_id", child.getChildId());
		values.put("name", child.getName());
		values.put("icon", child.getIcon());
		values.put("phone", child.getPhone());
		db.insertOrThrow("children", null, values);
		db.close();
	}
}
