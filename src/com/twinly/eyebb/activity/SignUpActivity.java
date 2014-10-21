package com.twinly.eyebb.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eyebb.R;

public class SignUpActivity extends Activity {
	private Button btnContinue;
	// sharedPreferences
	private SharedPreferences SandVpreferences;
	private SharedPreferences.Editor editor;
	private EditText ed_username;
	private EditText ed_email;
	private EditText ed_password;
	private String username;
	private String email;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		setTitle(getString(R.string.btn_sign_up));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		// sharedPreferences for signup
		SandVpreferences = getSharedPreferences("signup", MODE_PRIVATE);
		editor = SandVpreferences.edit();

		// username password email
		ed_username = (EditText) findViewById(R.id.ed_username);
		ed_email = (EditText) findViewById(R.id.ed_email);
		ed_password = (EditText) findViewById(R.id.ed_password);

		btnContinue = (Button) findViewById(R.id.btn_continue);
		btnContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Regex for username password username
				username = ed_username.getText().toString();
				email = ed_email.getText().toString();
				password = ed_password.getText().toString();
				System.out.println("username + email + password" + username
						+ " " + email + " " + password);
				if (isUsername(username)) {

					if (isEmail(email)) {

						if (isPassword(password)) {
							editor.putString("usrname", username);
							editor.putString("email", email);
							editor.putString("password", password);
							editor.commit();

							Intent intent = new Intent(SignUpActivity.this,
									ChildInformationMatchingActivity.class);
							startActivity(intent);

						} else {
							Toast.makeText(SignUpActivity.this,
									R.string.text_error_password,
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(SignUpActivity.this,
								R.string.text_error_email, Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(SignUpActivity.this,
							R.string.text_error_username, Toast.LENGTH_SHORT)
							.show();
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

	public static boolean isUsername(String usrname) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
		Matcher m = p.matcher(usrname);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static boolean isPassword(String password) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{8,30}$");
		Matcher m = p.matcher(password);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static boolean isEmail(String email) {
		Pattern p = Pattern
				.compile("^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
		Matcher m = p.matcher(email);
		System.out.println(m.matches() + "---");
		return m.matches();
	}
}
