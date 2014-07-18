package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyebb.R;

public class NotificationActivity extends Activity {

	private int child;
	private TextView tittle;
	private TextView content;
	private ImageView detailsImage;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_detials);
		setTitle(getString(R.string.text_notificationDetails));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		Intent intent = getIntent();
		child = intent.getIntExtra("child", 1);
		tittle = (TextView) findViewById(R.id.tittle_detials);
		content = (TextView) findViewById(R.id.details);
		detailsImage = (ImageView) findViewById(R.id.details_image);
		selectNotificationByChild(child);

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

	private void selectNotificationByChild(int child) {
		
		
		switch (child) {
		case 1:
			detailsImage.setBackground(getResources().getDrawable(R.drawable.notification_details1));
			break;

		case 2:
			detailsImage.setBackground(getResources().getDrawable(R.drawable.notification_details2));
			tittle.setText(R.string.child2_notification_tittle);
			content.setText(R.string.child2_notification);
			break;
		case 3:
			detailsImage.setBackground(getResources().getDrawable(R.drawable.notification_details3));
			tittle.setText(R.string.child3_notification_tittle);
			content.setText(R.string.child3_notification);
			break;

		default:
			break;
		}
	}

}
