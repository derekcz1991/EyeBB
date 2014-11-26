package com.twinly.eyebb.activity;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ChildBirthdayDialog extends Activity {
	private DatePicker childBirthdayDatePicker;

	private Calendar calendar;
	private int year;
	private int monthOfYear;
	private int dayOfMonth;
	private String dateOfBirth;

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;

	private String getBirthday;

	View dataPicker;
	EditText dataPickerEd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_child_birthday);
		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);

		Intent intent = getIntent();
		getBirthday = intent.getStringExtra("birthday");

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (dateOfBirth != null && dateOfBirth.length() > 0) {
					SharePrefsUtils.setRegisterChildBirthday(
							ChildBirthdayDialog.this, dateOfBirth);
					Intent data = new Intent();
					data.putExtra("childBirthday", dateOfBirth);
					setResult(ActivityConstants.RESULT_RESULT_BIRTHDAY_OK, data);
					finish();
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		if (getBirthday != null && getBirthday.length() > 0) {
			String[] sGetBirthday = getBirthday.split("/");
			dayOfMonth = Integer.parseInt(sGetBirthday[0]);
			monthOfYear = Integer.parseInt(sGetBirthday[1]) - 1;
			year = Integer.parseInt(sGetBirthday[2]);
		} else {
			calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			monthOfYear = calendar.get(Calendar.MONTH);
			dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		}

		childBirthdayDatePicker = (DatePicker) findViewById(R.id.datePicker);

		// initial date for date
		dateOfBirth = dayOfMonth + "/" + monthOfYear + "/" + year;

		childBirthdayDatePicker.init(year, monthOfYear, dayOfMonth,
				new OnDateChangedListener() {
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						monthOfYear = monthOfYear + 1;
						dateOfBirth = dayOfMonth + "/" + monthOfYear + "/"
								+ year;
						System.out.println("dateOfBirth==>" + dateOfBirth);

					}
				});

	}

}
