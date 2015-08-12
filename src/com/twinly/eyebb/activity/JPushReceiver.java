package com.twinly.eyebb.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.model.ActivityInfo;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.JPushUtils;
import com.twinly.eyebb.utils.NotificationUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		/*Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));*/
		setExtras(bundle, context);

		/*if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			//send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}*/
	}

	private void setExtras(Bundle bundle, Context context) {
		String request = "";
		String json = "";
		if (bundle != null) {
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_TITLE)) {
					request = bundle.getString(JPushInterface.EXTRA_TITLE);
					//System.out.println("request = " + request);
				} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
					json = bundle.getString(JPushInterface.EXTRA_EXTRA);
					//System.out.println("json = " + json);
				}
			}
		}

		Bundle mBundle = new Bundle();
		if (request.equals(JPushUtils.JPUSH_NEW_ACTIVITY)) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_DATE,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_DATE));
				mBundle.putString(
						HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_ICON,
						jsonObject
								.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_ICON));
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			handleNewActivity(mBundle, context);
		} else if (request.equals(JPushUtils.JPUSH_NEW_NOTICE)) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				mBundle.putString(
						HttpConstants.JSON_KEY_NOTICES_TITLE,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_TITLE));
				mBundle.putString(
						HttpConstants.JSON_KEY_NOTICES_TITLE_TC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_TC));
				mBundle.putString(
						HttpConstants.JSON_KEY_NOTICES_TITLE_SC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_SC));
				mBundle.putString(
						HttpConstants.JSON_KEY_NOTICES_URL_TC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_URL_TC));
				mBundle.putString(
						HttpConstants.JSON_KEY_NOTICES_URL_SC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_URL_SC));
				mBundle.putString(HttpConstants.JSON_KEY_NOTICES_ICON,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_ICON));
				mBundle.putString(
						HttpConstants.JSON_KEY_NOTICES_VALID_UNTIL,
						jsonObject
								.getString(HttpConstants.JSON_KEY_NOTICES_VALID_UNTIL));
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			handleNewNotice(mBundle, context);
		}
	}

	private void handleNewActivity(Bundle bundle, Context context) {
		ActivityInfo activityInfo = new ActivityInfo();
		activityInfo.setChildId(SharePrefsUtils.getReportChildId(context, -1L));
		activityInfo.setTitle(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE));
		activityInfo
				.setTitleSc(bundle
						.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC));
		activityInfo
				.setTitleTc(bundle
						.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC));
		activityInfo.setUrl(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL));
		activityInfo.setUrlSc(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC));
		activityInfo.setUrlTc(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC));
		activityInfo.setDate(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_DATE));
		activityInfo.setIcon(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_ICON));
		DBActivityInfo.insert(context, activityInfo);

		Intent notificationIntent = new Intent(context, WebViewActivity.class);
		Bundle data = new Bundle();
		data.putInt("from", ActivityConstants.FRAGMENT_REPORT_ACTIVITY);
		data.putSerializable("activityInfo", activityInfo);
		notificationIntent.putExtras(bundle);

		String title = "";
		switch (SharePrefsUtils.getLanguage(context)) {
		case Constants.LOCALE_CN:
			title = activityInfo.getTitleSc();
			break;
		case Constants.LOCALE_TW:
			title = activityInfo.getTitleTc();
			break;
		case Constants.LOCALE_HK:
			title = activityInfo.getTitleTc();
			break;
		default:
			title = activityInfo.getTitle();
			break;
		}

		NotificationUtils.sendNotification(notificationIntent, context,
				WebViewActivity.class, title, "", true);
	}

	private void handleNewNotice(Bundle bundle, Context context) {
		Notifications notice = new Notifications();
		notice.setTitle(bundle.getString(HttpConstants.JSON_KEY_NOTICES_TITLE));
		notice.setTitleTc(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_TC));
		notice.setTitleSc(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_SC));
		notice.setUrl(bundle.getString(HttpConstants.JSON_KEY_NOTICES_URL));
		notice.setUrlTc(bundle.getString(HttpConstants.JSON_KEY_NOTICES_URL_TC));
		notice.setUrlSc(bundle.getString(HttpConstants.JSON_KEY_NOTICES_URL_SC));
		notice.setIcon(bundle.getString(HttpConstants.JSON_KEY_NOTICES_ICON));
		notice.setDate(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_VALID_UNTIL));
		DBNotifications.insert(context, notice);

		Intent notificationIntent = new Intent(context, WebViewActivity.class);
		Bundle data = new Bundle();
		data.putInt("from", ActivityConstants.FRAGMENT_PROFILE);
		data.putSerializable("notifications", notice);
		notificationIntent.putExtras(data);

		String title = "";
		switch (SharePrefsUtils.getLanguage(context)) {
		case Constants.LOCALE_CN:
			title = notice.getTitleSc();
			break;
		case Constants.LOCALE_TW:
			title = notice.getTitleTc();
			break;
		case Constants.LOCALE_HK:
			title = notice.getTitleTc();
			break;
		default:
			title = notice.getTitle();
			break;
		}

		NotificationUtils.sendNotification(notificationIntent, context,
				WebViewActivity.class, title, "", true);
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

}
