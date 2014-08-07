package com.twinly.eyebb.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.Child;

public class DBChildren {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, Child child) {
		if (updateIfExist(context, child)) {
			return;
		}
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("child_id", child.getChildId());
		values.put("name", child.getName());
		values.put("icon", child.getIcon());
		values.put("phone", child.getPhone());
		db.insertOrThrow("children", null, values);
		db.close();
	}

	private static boolean updateIfExist(Context context, Child child) {
		SQLiteDatabase db = getInstance(context);
		// if exist the friend, update his information
		ContentValues values = new ContentValues();
		values.put("child_id", child.getChildId());
		values.put("name", child.getName());
		values.put("icon", child.getIcon());
		values.put("phone", child.getPhone());
		int result = db.update("children", values, "child_id=?",
				new String[] { String.valueOf(child.getChildId()) });

		db.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static HashMap<String, Child> getChildrenMap(Context context) {
		HashMap<String, Child> map = new HashMap<String, Child>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from children", null);
		while (cursor.moveToNext()) {
			Child child = new Child();
			child.setChildId(cursor.getLong(cursor.getColumnIndex("child_id")));
			child.setName(cursor.getString(cursor.getColumnIndex("name")));
			child.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			child.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
			child.setLocationName("");

			map.put(String.valueOf(child.getChildId()), child);
		}
		cursor.close();
		db.close();
		return map;
	}

	public static ArrayList<Child> getChildrenList(Context context) {
		ArrayList<Child> childList = new ArrayList<Child>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from children", null);
		while (cursor.moveToNext()) {
			Child child = new Child();
			child.setChildId(cursor.getLong(cursor.getColumnIndex("child_id")));
			child.setName(cursor.getString(cursor.getColumnIndex("name")));
			child.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			child.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
			child.setLocationName("");

			childList.add(child);
		}
		cursor.close();
		db.close();
		return childList;
	}
}
