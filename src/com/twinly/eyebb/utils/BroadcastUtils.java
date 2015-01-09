package com.twinly.eyebb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class BroadcastUtils extends Activity{
	//broadcast
	public static final String BROADCAST_FINISH_BIND = "finish_bind";
	public static final String BROADCAST_GET_DEVICE_BATTERY = "battery_broadcast";
	public static final String BROADCAST_CANCEL_NOTIFICATION_DOT = "cancel_notification_dot";
	public static final String BROADCAST_ADD_NOTIFICATION_DOT = "add_notification_dot";
	
	public static void addNotificationDot(Context content){
		Intent broadcast = new Intent();
		broadcast
				.setAction(BroadcastUtils.BROADCAST_ADD_NOTIFICATION_DOT);
		content.sendBroadcast(broadcast);
	}
	
	public static void cancelNotificationDot(Context content){
		Intent broadcast = new Intent();
		broadcast
				.setAction(BroadcastUtils.BROADCAST_CANCEL_NOTIFICATION_DOT);
		content.sendBroadcast(broadcast);
	}
}
