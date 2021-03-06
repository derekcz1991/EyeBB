package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class ChildInformationMatchingActivity extends Activity {
	private EditText userName;
	private LinearLayout childBirthdayLayout;
	private RelativeLayout areaItem;
	private TextView childBirthday;
	private TextView area;
	private Button binding;

	private int areaId = -1;
	private String areaType;
	private String childName;
	private String birthday;

	private boolean childNameFlag = false;
	private boolean birthdayFlag = false;
	private boolean kindergartenFlag = false;

	private ImageView icArea;
	private ImageView icChildName;
	private ImageView icBirthday;

	public static final int CONNECT_ERROR = 1;
	public static final int CHILD_EXIST = 2;
	public static final int CHILD_NOT_EXIST = 3;

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
		areaItem = (RelativeLayout) findViewById(R.id.areaItem);
		area = (TextView) findViewById(R.id.area);
		childBirthday = (TextView) findViewById(R.id.birthday);
		binding = (Button) findViewById(R.id.btn_confirm);
		icArea = (ImageView) findViewById(R.id.ic_kindergarten);
		icChildName = (ImageView) findViewById(R.id.ic_child_name);
		icBirthday = (ImageView) findViewById(R.id.ic_birthday);

		areaItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ChildInformationMatchingActivity.this,
						AreaListActivity.class);
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
					//hide keyboard
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							childBirthdayLayout.getWindowToken(), 0);
					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							ChildBirthdayDialog.class);
					if (birthday != null) {
						intent.putExtra("birthday", birthday);
						System.out.println("birthday--child-->" + birthday);
					}
					startActivityForResult(intent,
							ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY);
				}
			}
		});

		binding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				childName = userName.getText().toString();
				if (childName != null && childName.length() > 0) {
					childNameFlag = true;
					icChildName.setBackgroundResource(R.drawable.ic_login_name);
				} else {
					childNameFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					icChildName.setBackgroundResource(R.drawable.ic_cross);
				}

				if (birthday != null && birthday.length() > 0) {
					birthdayFlag = true;
					icBirthday.setBackgroundResource(R.drawable.ic_login_email);
				} else {
					birthdayFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					icBirthday.setBackgroundResource(R.drawable.ic_cross);
				}

				if (areaId >= 0) {
					kindergartenFlag = true;
					icArea.setVisibility(View.INVISIBLE);
				} else {
					kindergartenFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					icArea.setVisibility(View.VISIBLE);
				}

				if (kindergartenFlag && birthdayFlag && childNameFlag) {
					if (areaType.equals("D")) {
						new AddChildInDummyTask().execute();
					} else if (areaType.equals("K")) {
						new Thread(postRegParentsCheckToServerRunnable).start();
					}
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

	Runnable postRegParentsCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postChildInformationToServer();
		}
	};

	private void postChildInformationToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println(HttpConstants.CHILD_CHECKING + "==>>" + childName
				+ " " + birthday + " " + areaId);

		map.put("childName", childName);
		map.put("dateOfBirth", birthday);
		map.put("kId", String.valueOf(areaId));

		try {
			String retStr = HttpRequestUtils.post(HttpConstants.CHILD_CHECKING,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("[]")) {
					Message msg = handler.obtainMessage();
					msg.what = CHILD_NOT_EXIST;
					handler.sendMessage(msg);
				} else {
					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							CheckChildToBindDialog.class);
					intent.putExtra(
							ActivityConstants.EXTRA_GUARDIAN_ID,
							getIntent().getLongExtra(
									ActivityConstants.EXTRA_GUARDIAN_ID, -1L));
					intent.putExtra(CheckChildToBindDialog.EXTRA_CHILDREN_LIST,
							retStr);
					startActivity(intent);
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHILD_EXIST:
				break;
			case CONNECT_ERROR:
				Toast.makeText(ChildInformationMatchingActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();
				break;
			case CHILD_NOT_EXIST:
				Toast.makeText(ChildInformationMatchingActivity.this,
						R.string.text_child_not_exist, Toast.LENGTH_LONG)
						.show();
				break;
			}
		}
	};

	private class AddChildInDummyTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childName", childName);
			map.put("dateOfBirth", birthday);
			map.put("areaId", String.valueOf(areaId));
			//System.out.println("map = " + map);
			return HttpRequestUtils.post(HttpConstants.ADD_CHILD, map);
		}

		@Override
		protected void onPostExecute(String result) {
			//System.out.println("result = " + result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				childName = jsonObject
						.getString(HttpConstants.JSON_KEY_CHILD_NAME);
				if (CommonUtils.isNotNull(childName)) {
					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							CheckChildToBindDialog.class);
					intent.putExtra(
							ActivityConstants.EXTRA_GUARDIAN_ID,
							getIntent().getLongExtra(
									ActivityConstants.EXTRA_GUARDIAN_ID, -1L));
					String retStr = "[{'childId':_id,'name':'_n','icon':''}]";
					retStr = retStr.replace("_id", jsonObject
							.getString(HttpConstants.JSON_KEY_CHILD_ID));
					retStr = retStr.replace("_n", childName);
					intent.putExtra(CheckChildToBindDialog.EXTRA_CHILDREN_LIST,
							retStr);
					startActivity(intent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityConstants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				area.setText(data
						.getStringExtra(AreaListActivity.EXTRA_AREA_DISPLAY_NAME));
				areaId = data.getIntExtra(AreaListActivity.EXTRA_AREA_ID, -1);
				areaType = data
						.getStringExtra(AreaListActivity.EXTRA_AREA_TYPE);
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
