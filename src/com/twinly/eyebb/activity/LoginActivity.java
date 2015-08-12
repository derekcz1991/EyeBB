package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.JPushUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

/**
 * @author eyebb team
 * 
 * @category LoginActivity
 * 
 *           this activity is used to login. you should fill in the password and
 *           account name. the password that is hashed to post to the server.
 * 
 */
public class LoginActivity extends Activity {
	private TextView forgetPasswordBtn;

	private LinearLayout llCountry;
	private TextView tvCountry;
	private TextView country;

	private EditText loginAccount;
	private EditText password;
	private String hashPassword;
	private Dialog loginDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.btn_login));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		llCountry = (LinearLayout) findViewById(R.id.ll_country);
		tvCountry = (TextView) findViewById(R.id.tv_country);
		country = (TextView) findViewById(R.id.country);

		loginAccount = (EditText) findViewById(R.id.login_account);
		loginAccount.setText(SharePrefsUtils
				.getLoginAccount(LoginActivity.this));

		password = (EditText) findViewById(R.id.password);

		forgetPasswordBtn = (TextView) findViewById(R.id.forget_password_btn);

		forgetPasswordBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// build forget password dialog
				Intent intent = new Intent(LoginActivity.this,
						ForgetPasswordDialog.class);
				startActivity(intent);

			}
		});

		llCountry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						SelectRegionActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_SELECT_REGION);
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

	public void OnLoginClicked(View view) {

		if (TextUtils.isEmpty(loginAccount.getText().toString())) {
			return;
		} else if (TextUtils.isEmpty(password.getText().toString())) {
			return;
		}

		new LoginTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private class LoginTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			hashPassword = CommonUtils.getSHAHashValue(password.getText()
					.toString());
			// System.out.println("hashPassword = " + hashPassword);
			loginDialog = LoadingDialog.createLoadingDialog(LoginActivity.this,
					getString(R.string.toast_login));
			loginDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("j_username", loginAccount.getText().toString());
			map.put("j_password", hashPassword);

			return HttpRequestUtils.post(HttpConstants.LOGIN, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("login result = " + result);
			try {
				JSONObject json = new JSONObject(result);
				/*String receivedDeviceId = json
						.getString(HttpConstants.JSON_KEY_REGISTRATION_ID);*/

				json = json.getJSONObject(HttpConstants.JSON_KEY_USER);

				SystemUtils.clearData(LoginActivity.this);

				SharePrefsUtils.setUserId(LoginActivity.this,
						json.getLong(HttpConstants.JSON_KEY_USER_ID));
				SharePrefsUtils.setUserName(LoginActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_NAME));
				SharePrefsUtils.setUserPhone(LoginActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_PHONE));
				SharePrefsUtils.setUserType(LoginActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_TYPE));

				SharePrefsUtils.setLogin(LoginActivity.this, true);
				SharePrefsUtils.setLoginAccount(LoginActivity.this,
						loginAccount.getText().toString());
				SharePrefsUtils.setPassowrd(LoginActivity.this, hashPassword);

				/*new GCMUtils().GCMRegistration(LoginActivity.this,
						receivedDeviceId);*/
				JPushUtils.updateRegistrationId(LoginActivity.this);

				new GetChildrenInfoTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (JSONException e) {
				loginDialog.dismiss();
				Toast.makeText(LoginActivity.this,
						getString(R.string.toast_invalid_username_or_password),
						Toast.LENGTH_SHORT).show();
				System.out.println("login ---->> " + e.getMessage());
			}
		}
	}

	private class GetChildrenInfoTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return HttpRequestUtils.get(HttpConstants.GET_CHILDREN_INFO_LIST,
					null);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.GET_CHILDREN_INFO_LIST + " ==>> "
					+ result);
			try {
				JSONObject json = new JSONObject(result);
				if (CommonUtils.isNotNull(json
						.getString(HttpConstants.JSON_KEY_CHILDREN_INFO))) {
					JSONArray childrenInfoJSONList = json
							.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_INFO);
					for (int i = 0; i < childrenInfoJSONList.length(); i++) {
						JSONObject childrenInfoObject = childrenInfoJSONList
								.getJSONObject(i);
						JSONObject childRelObject = childrenInfoObject
								.getJSONObject(HttpConstants.JSON_KEY_CHILD_REL);
						JSONObject childObject = childRelObject
								.getJSONObject(HttpConstants.JSON_KEY_CHILD);

						Child child = new Child(
								childObject
										.getInt(HttpConstants.JSON_KEY_CHILD_ID),
								childObject
										.getString(HttpConstants.JSON_KEY_CHILD_NAME),
								childObject
										.getString(HttpConstants.JSON_KEY_CHILD_ICON));
						child.setRelationWithUser(childRelObject
								.getString(HttpConstants.JSON_KEY_CHILD_RELATION));
						child.setMacAddress(childrenInfoObject
								.getString(HttpConstants.JSON_KEY_CHILD_MAC_ADDRESS));
						// get parents' phone
						if (CommonUtils.isNotNull(childrenInfoObject
								.getString(HttpConstants.JSON_KEY_PARENTS))) {
							JSONObject parentObject = childrenInfoObject
									.getJSONObject(HttpConstants.JSON_KEY_PARENTS);
							child.setPhone(parentObject
									.getString(HttpConstants.JSON_KEY_PARENTS_PHONE));
						}
						DBChildren.insert(LoginActivity.this, child);
					}
				}
				setResult(ActivityConstants.RESULT_RESULT_OK);

				if (loginDialog.isShowing() && loginDialog != null) {
					loginDialog.dismiss();
				}
				finish();
			} catch (JSONException e) {
				loginDialog.dismiss();
				Toast.makeText(LoginActivity.this,
						getString(R.string.toast_invalid_username_or_password),
						Toast.LENGTH_SHORT).show();
				System.out.println("GetChildrenInfoTask ---->> "
						+ e.getMessage());
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ActivityConstants.REQUEST_GO_TO_SELECT_REGION:
			switch (resultCode) {
			case SelectRegionActivity.RESULT_CODE_CHINA:
				tvCountry.setText(getString(R.string.text_china));
				country.setText("+86");
				break;
			case SelectRegionActivity.RESULT_CODE_HK:
				tvCountry.setText(getString(R.string.text_hk));
				country.setText("+852");
				break;
			}
			break;
		}
	}

}
