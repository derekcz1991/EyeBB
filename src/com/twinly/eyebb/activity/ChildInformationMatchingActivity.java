package com.twinly.eyebb.activity;

import com.eyebb.R;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChildInformationMatchingActivity extends Activity {
	private EditText userName;
	private LinearLayout childBirthdayLayout;
	private RelativeLayout kindergartenItem;
	private TextView childBirthday;
	private TextView kindergarten;
	private Button binding;

	private int kindergartenId = -1;
	private String kindergartenNameEn;
	private String kindergartenNameSc;
	private String kindergartenNameTc;
	private String childName;
	private String birthday;

	private boolean childNameFlag = false;
	private boolean birthdayFlag = false;
	private boolean kindergartenFlag = false;

	private TextView ic_kindergarten;
	private TextView ic_childName;
	private TextView ic_birthday;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_information_matching);
		setTitle(getString(R.string.text_child_information_matching));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		initView();
	}

	private void initView() {
		userName = (EditText) findViewById(R.id.ed_username);
		childBirthdayLayout = (LinearLayout) findViewById(R.id.child_birthday_layout);
		kindergartenItem = (RelativeLayout) findViewById(R.id.kindergartenItem);
		kindergarten = (TextView) findViewById(R.id.kindergarten);
		childBirthday = (TextView) findViewById(R.id.birthday);
		binding = (Button) findViewById(R.id.btn_confirm);
		ic_kindergarten = (TextView) findViewById(R.id.ic_kindergarten);
		ic_childName = (TextView) findViewById(R.id.ic_child_name);
		ic_birthday = (TextView) findViewById(R.id.ic_birthday);

		kindergartenItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ChildInformationMatchingActivity.this,
						KindergartenListActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY);
			}
		});

		childBirthdayLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					// TODO Auto-generated method stub
					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							ChildBirthdayDialog.class);
					startActivityForResult(intent,
							ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY);
				}
			}
		});

		binding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				childName = userName.getText().toString();
				if (childName != null && childName.length() > 0) {
					childNameFlag = true;
					ic_childName.setBackground(getResources().getDrawable(
							R.drawable.ic_login_name));
				} else {
					childNameFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					ic_childName.setBackground(getResources().getDrawable(
							R.drawable.ic_radar_missed));
				}

				if (birthday != null && birthday.length() > 0) {
					birthdayFlag = true;
					ic_birthday.setBackground(getResources().getDrawable(
							R.drawable.ic_login_email));
				} else {
					birthdayFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					ic_birthday.setBackground(getResources().getDrawable(
							R.drawable.ic_radar_missed));
				}

				if (kindergartenId >= 0) {
					kindergartenFlag = true;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					ic_kindergarten.setVisibility(View.INVISIBLE);
				} else {
					kindergartenFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					ic_kindergarten.setVisibility(View.VISIBLE);

				}

				if (kindergartenFlag && birthdayFlag && childNameFlag) {
					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							CheckBeaconActivity.class);
					startActivity(intent);
				}

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityConstants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				kindergarten.setText(data.getStringExtra("displayName"));
				kindergartenId = data.getIntExtra("kindergartenId", -1);
				kindergartenNameEn = data.getStringExtra("nameEn");
				kindergartenNameTc = data.getStringExtra("nameTc");
				kindergartenNameSc = data.getStringExtra("nameSc");
			}
		}
		if (requestCode == ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_BIRTHDAY_OK) {
				childBirthday.setText(data.getStringExtra("childBirthday"));
				birthday = data.getStringExtra("childBirthday");
			}
		}
	}
}
