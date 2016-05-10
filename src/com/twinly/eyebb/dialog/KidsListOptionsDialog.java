package com.twinly.eyebb.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;

import com.twinly.twinly.R;
import com.twinly.eyebb.constant.ActivityConstants;

public class KidsListOptionsDialog extends Activity {
	public static final String EXTRA_SORT_BY_NAME = "SORT_BY_NAME ";
	public static final String EXTRA_SORT_BY_LOCATION = "SORT_BY_LOCATION";

	private CheckedTextView tvSortByName;
	private CheckedTextView tvSortByLocation;
	private boolean isSortByName;
	private boolean isSortByLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_kids_list_options);

		isSortByName = getIntent().getBooleanExtra(EXTRA_SORT_BY_NAME, false);
		isSortByLocation = getIntent().getBooleanExtra(EXTRA_SORT_BY_LOCATION,
				false);

		tvSortByName = (CheckedTextView) findViewById(R.id.tv_name);
		tvSortByLocation = (CheckedTextView) findViewById(R.id.tv_location);

		tvSortByName.setChecked(isSortByName);
		tvSortByLocation.setChecked(isSortByLocation);

		findViewById(R.id.item_name).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSortByName) {
					isSortByName = false;
				} else {
					isSortByName = true;
					isSortByLocation = false;
				}
				tvSortByName.setChecked(isSortByName);
				tvSortByLocation.setChecked(isSortByLocation);
			}
		});

		findViewById(R.id.item_location).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isSortByLocation) {
							isSortByLocation = false;
						} else {
							isSortByLocation = true;
							isSortByName = false;
						}
						tvSortByName.setChecked(isSortByName);
						tvSortByLocation.setChecked(isSortByLocation);
					}
				});
		findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent data = new Intent();
						data.putExtra(EXTRA_SORT_BY_NAME, isSortByName);
						data.putExtra(EXTRA_SORT_BY_LOCATION, isSortByLocation);
						setResult(ActivityConstants.RESULT_RESULT_OK, data);
						finish();
					}
				});
	}
}
