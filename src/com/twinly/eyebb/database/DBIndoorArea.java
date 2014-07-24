package com.twinly.eyebb.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.IndoorAera;

public class DBIndoorArea {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, IndoorAera indoorAera) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("area_id", indoorAera.getAeraId());
		values.put("name", indoorAera.getName());
		db.insertOrThrow("indoor_area", null, values);
		db.close();
	}

	public static ArrayList<IndoorAera> getAll(Context context) {
		ArrayList<IndoorAera> list = new ArrayList<IndoorAera>();

		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from indoor_area", null);
		while (cursor.moveToNext()) {
			IndoorAera indoorArea = new IndoorAera();
			indoorArea.setAeraId(cursor.getLong(cursor
					.getColumnIndex("area_id")));
			indoorArea.setName(cursor.getString(cursor.getColumnIndex("name")));
			list.add(indoorArea);
		}
		return list;
	}
}
