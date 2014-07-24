package com.twinly.eyebb.activity;

import com.eyebb.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;



public class AboutActivity extends Activity {
	private ImageView logo;
	// sharedPreferences
	SharedPreferences languagePreferences;
	private int language;
	private String version;
	private TextView version_txt;

	protected void onCreate(Bundle savedInstanceState) {
		languagePreferences = getSharedPreferences("soundAndVibrate",
				MODE_PRIVATE);
		language = languagePreferences.getInt("language",
				SettingsActivity.ENGLISH);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// check logo
		logo = (ImageView) findViewById(R.id.logo_img);
        //version
		try {
			version =  getVersionName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		version_txt = (TextView) findViewById(R.id.version);
		version_txt.setText(version);
		checkLogo();

		setTitle(getString(R.string.text_about));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

	}

	private void checkLogo() {
		if (language == 1) {
			logo.setBackground(getResources().getDrawable(R.drawable.logo_en));
		} else if (language == 2) {
			logo.setBackground(getResources().getDrawable(R.drawable.logo_cht));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * version
	 */
	private String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return version;
	}
}
