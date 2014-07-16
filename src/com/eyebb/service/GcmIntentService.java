package com.eyebb.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class GcmIntentService extends IntentService {

	private static final String TAG = "GcmIntentService";

	public GcmIntentService() {
		super("GcmIntentService");
		Log.i(TAG, "Start GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		String request = "";//bundle.getString(GCMConstants.GCM_COMMAND);

		// if receive nothing, return
		if (TextUtils.isEmpty(request)) {
			return;
		}
		
		Log.i(TAG, "Received message: " + request);
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

}
