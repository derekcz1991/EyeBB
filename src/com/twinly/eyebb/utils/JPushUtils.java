package com.twinly.eyebb.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.twinly.eyebb.constant.HttpConstants;

public class JPushUtils {
	public static final String JPUSH_NEW_NOTICE = "JPUSH_NEW_NOTICE";
	public static final String JPUSH_NEW_ACTIVITY = "JPUSH_NEW_ACTIVITY";
	public static final String JPUSH_DAILY_PUSH = "JPUSH_DAILY_PUSH";
	public static final String JPUSH_SOS_MSG = "JPUSH_SOS_MSG";

	/**
	 * update registration id in server
	 */
	public static void updateRegistrationId(final Context context) {
		Set<String> tagSet = new LinkedHashSet<String>();
		tagSet.add(String.valueOf(SharePrefsUtils.getUserId(context, -1L)));

		JPushInterface.setTags(context, tagSet, mTagsCallback);

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("deviceId",
						String.valueOf(SharePrefsUtils.getUserId(context, -1L)));
				map.put("type", "A");
				System.out.println("map = " + map);
				return HttpRequestUtils.post(
						HttpConstants.UPDATE_REGISTRATION_ID, map);
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println("update device id result = " + result);
			}

		}.execute();
		//.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private static final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				System.out.println(logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				System.out.println(logs);
				/*Log.i(TAG, logs);
				if (ExampleUtil.isConnected(getApplicationContext())) {
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(MSG_SET_TAGS, tags),
							1000 * 60);
				} else {
					System.out.println("No network");
				}*/
				break;

			default:
				logs = "Failed with errorCode = " + code;
				System.out.println(logs);
			}

		}

	};
}
