package com.eyebb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.eyebb.R;
import com.eyebb.constant.Constants;

public class LoginActivity extends Activity {
	private TextView forgetPasswordBtn;
	private LayoutInflater inflater;
	private TextView backBtn;
	private AlertDialog dialog;
	private EditText edEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.btn_login));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(Constants.RESULT_RESULT_OK);
				finish();
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

}
