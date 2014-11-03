package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.adapter.GridViewMissAdapter;
import com.twinly.eyebb.model.Child;

public class RadarShowAllMissImageDialog extends Activity {

	private TextView btnVerify;
	private GridView gv;
	ArrayList<Child> data;
	ArrayList<Child> antiData;

	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_radar_all_miss_child_image);

		btnVerify = (TextView) findViewById(R.id.btn_verify);
		btnVerify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		gv = (GridView) findViewById(R.id.miss_gv);
		Intent intent = getIntent();
		data = (ArrayList<Child>) intent
				.getSerializableExtra("showAllMissImage");
		antiData = (ArrayList<Child>) intent
				.getSerializableExtra("showAllMissImageAnti");

		GridViewMissAdapter adapter = new GridViewMissAdapter(
				RadarShowAllMissImageDialog.this, data,antiData);
		gv.setAdapter(adapter);

	}
}
