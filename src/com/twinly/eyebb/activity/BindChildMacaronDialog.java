package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.widget.Toast;

import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class BindChildMacaronDialog extends WriteMajorMinorDialog {
	public static final String EXTRAS_RECEIVER_MAJOR = "RECEIVER_MAJOR";
	public static final String EXTRAS_RECEIVER_MINOR = "RECEIVER_MINOR";

	private long childId;
	private String major;
	private String minor;

	@Override
	public void onPreWriteGetData() {
		super.onPreWriteGetData();
		childId = intent.getLongExtra(ActivityConstants.EXTRA_CHILD_ID, -1L);
		major = intent.getStringExtra(EXTRAS_RECEIVER_MAJOR);
		minor = intent.getStringExtra(EXTRAS_RECEIVER_MINOR);
	}

	@Override
	public void postToServer() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("childId", String.valueOf(childId));
				map.put("macAddress", mDeviceAddress);
				map.put("major", major);
				map.put("minor", minor);
				map.put("guardianId", "");
				return HttpRequestUtils
						.post(HttpConstants.DEVICE_TO_CHILD, map);
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println(HttpConstants.DEVICE_TO_CHILD + " = "
						+ result);
				if (result.length() > 0) {
					if (result
							.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
						return;
					}
					if (result.equals("T")) {
						Toast.makeText(BindChildMacaronDialog.this,
								"write success", Toast.LENGTH_SHORT).show();
						setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
					} else {
						setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
					}
				}
				finish();
			}

		}.execute();
	}

}
