package com.twinly.eyebb.constant;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.eyebb.R;

import com.twinly.eyebb.bluetooth.BaseApp;
import com.twinly.eyebb.bluetooth.BluetoothLeService;

public class Constants {
	public static final String DB_NAME = "eyebb.db";
	public static final int DB_VERSION = 1;

	public static final int LOCALE_EN = 0;
	public static final int LOCALE_CN = 1;
	public static final int LOCALE_HK = 2;
	public static final int LOCALE_TW = 3;
	
	//public static final String OURDEVICEUUID = "4D616361726F6E202020202020202020";
	public static final String OURDEVICEUUID =  "4D616361726F6E051250002003020A00";
	public static final int BEEP_RSSI = -90;
	private int DEFAULT_MAJOR = 0;
	private int DEFAULT_MINOR = 0;
	
	public static boolean DEBUG = true;
	
	public static BluetoothLeService mBluetoothLeService;
	public static ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	public static ArrayList<BluetoothGattService> gattServiceObject = new ArrayList<BluetoothGattService>();
	
	
	public static final int[] progressBarStyleSet = {
			R.drawable.my_progress_green01, R.drawable.my_progress_blue01,
			R.drawable.my_progress_green02, R.drawable.my_progress_blue02,
			R.drawable.my_progress_pink, R.drawable.my_progress_purple,
			R.drawable.my_progress_red, R.drawable.my_progress_yellow };
	
	public static void exit_ask(final Activity act){
		AlertDialog dialog = new AlertDialog.Builder(act).setIcon(android.R.drawable.btn_star).setTitle("Exit the APP？").setPositiveButton("Yes", new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub		
				BaseApp.getInstance().exit();
			}	
		}).setNegativeButton("Cancel", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub									
			}				
		}).create();
		dialog.show();	
	}	
}
