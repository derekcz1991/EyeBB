package com.twinly.eyebb.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.model.ActivityInfo;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.JPushUtils;
import com.twinly.eyebb.utils.NotificationUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class JPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		/*System.out.println("[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));*/
		setExtras(bundle, context);
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
				handleNewActivity(mBundle, context);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
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
				handleNewNotice(mBundle, context);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else if (request.equals(JPushUtils.JPUSH_DAILY_PUSH)) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				mBundle.putString(HttpConstants.JSON_KEY_CHILD_ID,
						jsonObject.getString(HttpConstants.JSON_KEY_CHILD_ID));
				mBundle.putString(
						HttpConstants.JSON_KEY_LOCATION_NAME,
						jsonObject
								.getString(HttpConstants.JSON_KEY_LOCATION_NAME));
				mBundle.putString(
						HttpConstants.JSON_KEY_LOCATION_NAME_SC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_LOCATION_NAME_SC));
				mBundle.putString(
						HttpConstants.JSON_KEY_LOCATION_NAME_TC,
						jsonObject
								.getString(HttpConstants.JSON_KEY_LOCATION_NAME_TC));
				handleDailyPush(mBundle, context);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
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
		notificationIntent.setAction("android.intent.action.MAIN");
		notificationIntent.addCategory("android.intent.category.LAUNCHER");
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

		NotificationUtils.pushNotification(context, context
				.getString(R.string.btn_activities), title, notificationIntent,
				Integer.parseInt(String.valueOf(System.currentTimeMillis())
						.substring(5)));
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
		notificationIntent.setAction("android.intent.action.MAIN");
		notificationIntent.addCategory("android.intent.category.LAUNCHER");
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

		NotificationUtils.pushNotification(context, context
				.getString(R.string.text_notifications), title,
				notificationIntent, Integer.parseInt(String.valueOf(
						System.currentTimeMillis()).substring(5)));
	}

	private void handleDailyPush(Bundle bundle, Context context) {
		Child child = DBChildren
				.getChildById(context, Long.valueOf(bundle
						.getString(HttpConstants.JSON_KEY_CHILD_ID)));
		if (child != null) {
			String locationName = "";
			switch (SharePrefsUtils.getLanguage(context)) {
			case Constants.LOCALE_CN:
				locationName = bundle
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME_TC);
				break;
			case Constants.LOCALE_TW:
				locationName = bundle
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME_SC);
				break;
			case Constants.LOCALE_HK:
				locationName = bundle
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME_SC);
				break;
			default:
				locationName = bundle
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME);
				break;
			}
			NotificationUtils.pushNotification(context,
					context.getString(R.string.text_daily_enter),
					child.getName() + context.getString(R.string.text_enter)
							+ locationName, new Intent(),
					(int) child.getChildId());
		}
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
