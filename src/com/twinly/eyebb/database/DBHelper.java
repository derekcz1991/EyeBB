package com.twinly.eyebb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.twinly.eyebb.constant.Constants;

public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}

	public SQLiteDatabase openDatabase() {

		if (db == null) {
			/*得到一个数据库的实例
			 * 调用这个方法时，查找系统中的资源，
			 * 如果不存在相关资源，调用onCreate(SQLiteDatabase db)方法,
			 * 如果存在，直接返回相关数据库
			 * */
			//db = this.getWritableDatabase(Constants.DB_PASSWORD);
			db = this.getWritableDatabase();
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer tableCreate = new StringBuffer();
		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists children")
						.append("(id integer primary key autoincrement,")
						.append("child_id integer,")
						.append("name text,")
						.append("icon text,")
						.append("phone text)");
		db.execSQL(tableCreate.toString());
		
		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists indoor_area")
						.append("(id integer primary key autoincrement,")
						.append("area_id integer,")
						.append("name text)");
		db.execSQL(tableCreate.toString());
		
		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists notification")
						.append("(id integer primary key autoincrement,")
						.append("notification_id integer,")
						.append("title text,")
						.append("date integer,")
						.append("icon text,")
						.append("url text,")
						.append("is_read boolean)");
		db.execSQL(tableCreate.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

}
