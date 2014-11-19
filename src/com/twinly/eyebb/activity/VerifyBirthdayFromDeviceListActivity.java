package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.Parameter;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class VerifyBirthdayFromDeviceListActivity extends Activity {
	private DatePicker datePicker;
	private Calendar calendar;
	private Button btnVerify;
	private ArrayList<Parameter> params;
	// sharedPreferences
	private SharedPreferences SandVpreferences;
	private SharedPreferences.Editor editor;

	private String dateOfBirth;


	// private String submitDateOfBirth;
	// private String submitUserName;
	// private String submitPassword;
	// private String submitEmail;
	// private int submitKinderGartenId;
	// private String submitKinderGartenNameEns;
	// private String submitDeviceUUID;
	// private String submitDeviceMajor;
	// private String submitDeviceMinor;
	private long childIDfromDeviceList;
	private String fromDeviceList = "list";
	private String getDeviceMajorAndMinorURL = "reportService/api/configBeaconRel";
	private Dialog dialog;
	private String major;
	private String minor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matching_verification);
		setTitle(getString(R.string.text_matching_verification));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		// sharedPreferences for signup
		SandVpreferences = getSharedPreferences("signup", MODE_PRIVATE);
		editor = SandVpreferences.edit();

		Intent intent = getIntent();
		fromDeviceList = intent.getStringExtra("fromDeviceList");
		if (fromDeviceList != null || !fromDeviceList.equals("")) {
			if (fromDeviceList.equals("DeviceListAcitivity")) {
				System.out.println("DeviceListAcitivityDeviceListAcitivity=>");
			}
		}

		childIDfromDeviceList = intent
				.getLongExtra("ChildIDfromDeviceList", 10);

		System.out.println("ChildIDfromDeviceList=>" + childIDfromDeviceList);

		calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		datePicker = (DatePicker) findViewById(R.id.datePicker);
		datePicker.init(year, monthOfYear, dayOfMonth,
				new OnDateChangedListener() {
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// Toast.makeText(
						// MatchingVerificationActivity.this,
						// "当前日期为" + year + "年" + monthOfYear + "月"
						// + dayOfMonth + "日", Toast.LENGTH_SHORT)
						// .show();
						monthOfYear = monthOfYear + 1;
						dateOfBirth = dayOfMonth + "/" + monthOfYear + "/"
								+ year;
						System.out.println("dateOfBirth==>" + dateOfBirth);
						editor.putString("dateOfBirth", dateOfBirth);
						editor.commit();
					}
				});

		btnVerify = (Button) findViewById(R.id.btn_verify);
		btnVerify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// submitDateOfBirth = SandVpreferences.getString("dateOfBirth",
				// "1990/12/08");
				// submitUserName = SandVpreferences.getString("usrname",
				// "usrname");
				// submitPassword = SandVpreferences.getString("password",
				// "password");
				// submitEmail = SandVpreferences.getString("email", "email");
				// submitKinderGartenId = SandVpreferences.getInt(
				// "kindergartenId", 0);
				// submitKinderGartenNameEns = SandVpreferences.getString(
				// "nameEns", "nameEns");
				// submitDeviceUUID = SandVpreferences.getString(
				// "submitDeviceUUID", "submitDeviceUUID");
				// submitDeviceMajor = SandVpreferences.getString(
				// "submitDeviceMajor", "submitDeviceMajor");
				// submitDeviceMinor = SandVpreferences.getString(
				// "submitDeviceMinor", "submitDeviceMinor");
				//
				// System.out
				// .println("submitDateOfBirth + submitUserName + submitPassword + submitEmail + submitKinderGartenId + submitKinderGartenNameEns=>"
				// + submitDateOfBirth
				// + " "
				// + submitUserName
				// + " "
				// + submitPassword
				// + " "
				// + submitEmail
				// + " "
				// + submitKinderGartenId
				// + " "
				// + submitKinderGartenNameEns);
				// System.out
				// .println("submitDeviceUUID + submitDeviceMajor + submitDeviceMinor=>"
				// + submitDeviceUUID
				// + " "
				// + submitDeviceMajor
				// + " " + submitDeviceMinor);
				new Thread(postToServerRunnable).start();

				Intent intent = new Intent(
						VerifyBirthdayFromDeviceListActivity.this,
						VerifyDialog.class);
				

				startActivity(intent);
				
				finish();

			}

		});
	}

	Runnable postToServerRunnable = new Runnable() {
		@Override
		public void run() {
			// HANDLER
			Message msg = handler.obtainMessage();
			msg.what = BleDeviceConstants.START_PROGRASSS_BAR;
			handler.sendMessage(msg);
			postToServer(childIDfromDeviceList);
	
		}
	};

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BleDeviceConstants.START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialog(
						VerifyBirthdayFromDeviceListActivity.this,
						getString(R.string.toast_loading));
				dialog.show();
				break;

			case BleDeviceConstants.STOP_PROGRASSS_BAR:
				dialog.dismiss();
				break;
			}
		}
	};

	private void postToServer(long childIDfromDeviceList) {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		map.put("childId", String.valueOf(childIDfromDeviceList));

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(getDeviceMajorAndMinorURL,
					map);
			System.out.println("retStrpost======>" + retStr);
			major = retStr.substring(0, retStr.indexOf(":"));
			minor = retStr.substring(retStr.indexOf(":") + 1, retStr.length());
			System.out.println("retStrpost======>" + major + " " + minor);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
