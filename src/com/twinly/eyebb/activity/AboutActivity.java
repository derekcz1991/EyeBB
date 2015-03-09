package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

/**
 * @author eyebb team
 * 
 * @category AboutActivity
 * 
 *           this activity is in options activity (The eleventh layer), changing
 *           the password whit is its main function.
 */
public class AboutActivity extends Activity {
	private ImageView logo;
	private TextView versionTxt;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setTitle(getString(R.string.text_about));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		logo = (ImageView) findViewById(R.id.logo_img);

		versionTxt = (TextView) findViewById(R.id.version);
		versionTxt.setText(SystemUtils.getAppVersion(this));
		setLogo();

	}

	private void setLogo() {
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			logo.setBackgroundResource(R.drawable.logo_cht);
			break;
		default:
			logo.setBackgroundResource(R.drawable.logo_en);
			break;
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

}
