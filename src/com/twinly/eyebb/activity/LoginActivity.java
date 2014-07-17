package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class LoginActivity extends Activity {
	private TextView forgetPasswordBtn;
	private LayoutInflater inflater;
	private TextView backBtn;
	private AlertDialog dialog;
	private EditText edEmail;
	private RelativeLayout kindergartenItem;
	private EditText loginAccount;
	private EditText password;
	private TextView kindergarten;
	private int kindergartenId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.btn_login));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		kindergartenItem = (RelativeLayout) findViewById(R.id.kindergartenItem);
		loginAccount = (EditText) findViewById(R.id.login_account);
		loginAccount.setText(SharePrefsUtils
				.getLoginAccount(LoginActivity.this));

		password = (EditText) findViewById(R.id.password);
		kindergarten = (TextView) findViewById(R.id.kindergarten);

		kindergartenItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						KindergartenListActivity.class);
				startActivityForResult(intent,
						Constants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY);
			}
		});

		forgetPasswordBtn = (TextView) findViewById(R.id.forget_password_btn);
		inflater = LayoutInflater.from(this);
		forgetPasswordBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// build forget password dialog
				AlertDialog.Builder forgetPassword = new AlertDialog.Builder(
						LoginActivity.this);
				View layout = inflater.inflate(R.layout.dialog_forget_password,
						null);

				edEmail = (EditText) layout.findViewById(R.id.enter_mail);
				edEmail.setFocusable(true);
				edEmail.setFocusableInTouchMode(true);
				edEmail.requestFocus();
				openKeyBoard();

				forgetPassword.setView(layout);

				backBtn = (TextView) layout.findViewById(R.id.back_btn);

				backBtn.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						closeKeyBoard();
						dialog.dismiss();
					}
				});

				dialog = forgetPassword.create();
				dialog.show();
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

	public void openKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edEmail, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public void closeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edEmail.getWindowToken(), 0);
	}

	public void OnLoginClicked(View view) {

		if (TextUtils.isEmpty(loginAccount.getText().toString())) {
			return;
		} else if (TextUtils.isEmpty(password.getText().toString())) {
			return;
		} else if (kindergartenId == -1) {
			Toast.makeText(LoginActivity.this,
					getString(R.string.toast_please_select_kindergartens),
					Toast.LENGTH_SHORT).show();
			return;
		}

		new AsyncTask<Void, Void, String>() {
			Dialog dialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = LoadingDialog.createLoadingDialog(LoginActivity.this,
						getString(R.string.toast_login));
				dialog.show();
			}

			@Override
			protected String doInBackground(Void... params) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("j_username", loginAccount.getText().toString());
				map.put("j_password", password.getText().toString());
				map.put("kId", String.valueOf(kindergartenId));

				return HttpRequestUtils.post(HttpConstants.LOGIN, map);
			}

			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				System.out.println("result = " + result);
				try {
					
					JSONObject json = new JSONObject(result);

					if (loginAccount.getText().toString().equals("May")) {
						SharePrefsUtils.setRole(LoginActivity.this, true);
					} else {
						SharePrefsUtils.setRole(LoginActivity.this, false);
					}
					SharePrefsUtils.setLogin(LoginActivity.this, true);
					SharePrefsUtils.setLoginAccount(LoginActivity.this,
							loginAccount.getText().toString());
					setResult(Constants.RESULT_RESULT_OK);
					finish();
				} catch (JSONException e) {
					Toast.makeText(
							LoginActivity.this,
							getString(R.string.toast_invalid_username_or_password),
							Toast.LENGTH_SHORT).show();
					System.out.println("login ---->> " + e.getMessage());
				}
			}

		}.execute();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY) {
			if (resultCode == Constants.RESULT_RESULT_OK) {
				kindergartenId = data.getIntExtra("kindergartenId", -1);
				kindergarten.setText(data.getStringExtra("name"));
			}
		}
	}

}
