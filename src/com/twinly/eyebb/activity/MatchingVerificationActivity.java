package com.twinly.eyebb.activity;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MatchingVerificationActivity extends Activity {
	private DatePicker datePicker;
	private Calendar calendar;
	private Button btnVerify;

	// sharedPreferences
	private SharedPreferences SandVpreferences;
	private SharedPreferences.Editor editor;

	private String dateOfBirth;

	private String submitDateOfBirth;
	private String submitUserName;
	private String submitPassword;
	private String submitEmail;
	private int submitKinderGartenId;
	private String submitKinderGartenNameEns;
	private String submitDeviceUUID;
	private String submitDeviceMajor;
	private String submitDeviceMinor;

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
				submitDateOfBirth = SandVpreferences.getString("dateOfBirth",
						"1990/12/08");
				submitUserName = SandVpreferences.getString("usrname",
						"usrname");
				submitPassword = SandVpreferences.getString("password",
						"password");
				submitEmail = SandVpreferences.getString("email", "email");
				submitKinderGartenId = SandVpreferences.getInt(
						"kindergartenId", 0);
				submitKinderGartenNameEns = SandVpreferences.getString(
						"nameEns", "nameEns");
				submitDeviceUUID = SandVpreferences.getString(
						"submitDeviceUUID", "submitDeviceUUID");
				submitDeviceMajor = SandVpreferences.getString(
						"submitDeviceMajor", "submitDeviceMajor");
				submitDeviceMinor = SandVpreferences.getString(
						"submitDeviceMinor", "submitDeviceMinor");

				System.out
						.println("submitDateOfBirth + submitUserName + submitPassword + submitEmail + submitKinderGartenId + submitKinderGartenNameEns=>"
								+ submitDateOfBirth
								+ " "
								+ submitUserName
								+ " "
								+ submitPassword
								+ " "
								+ submitEmail
								+ " "
								+ submitKinderGartenId
								+ " "
								+ submitKinderGartenNameEns);
				System.out
						.println("submitDeviceUUID + submitDeviceMajor + submitDeviceMinor=>"
								+ submitDeviceUUID
								+ " "
								+ submitDeviceMajor
								+ " " + submitDeviceMinor);

				Intent intent = new Intent(MatchingVerificationActivity.this,
						VerifyDialog.class);
				startActivity(intent);

			}
		});
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
