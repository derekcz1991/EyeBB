package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eyebb.R;

public class ChooseUsrRoleActivity extends Activity {

	private View masterLayout;
	private View guestLayout;
	private TextView guestLayoutSelectedIc;
	private TextView masterLayoutSelectedIc;
	private boolean masterSelectFlag = false;
	private boolean guestSelectFlag = false;
	private Button btnConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_user_role);
		setTitle(getString(R.string.text_choose_role));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		initView();
	}

	private void initView() {
		masterLayout = (View) findViewById(R.id.master_layout);
		guestLayout = (View) findViewById(R.id.guest_layout);
		masterLayoutSelectedIc = (TextView) findViewById(R.id.master_layout_selected_ic);
		guestLayoutSelectedIc = (TextView) findViewById(R.id.guest_layout_selected_ic);
		btnConfirm = (Button) findViewById(R.id.btn_confirm);

		masterLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (masterSelectFlag) {
					masterSelectFlag = false;
					masterLayoutSelectedIc.setBackground(getResources()
							.getDrawable(R.drawable.ic_selected_off));
					// guestLayoutSelectedIc.setBackground(getResources()
					// .getDrawable(R.drawable.ic_selected));
				} else {
					masterSelectFlag = true;
					guestSelectFlag = false;
					masterLayoutSelectedIc.setBackground(getResources()
							.getDrawable(R.drawable.ic_selected));
					guestLayoutSelectedIc.setBackground(getResources()
							.getDrawable(R.drawable.ic_selected_off));
				}

			}
		});

		guestLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("CLICK");
				if (guestSelectFlag) {
					guestSelectFlag = false;
					guestLayoutSelectedIc.setBackground(getResources()
							.getDrawable(R.drawable.ic_selected_off));
					// masterLayoutSelectedIc.setBackground(getResources()
					// .getDrawable(R.drawable.ic_selected));
				} else {
					guestSelectFlag = true;
					masterSelectFlag = false;
					guestLayoutSelectedIc.setBackground(getResources()
							.getDrawable(R.drawable.ic_selected));
					masterLayoutSelectedIc.setBackground(getResources()
							.getDrawable(R.drawable.ic_selected_off));
				}

			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (guestSelectFlag || masterSelectFlag) {
					Intent intent = new Intent(ChooseUsrRoleActivity.this,
							ChildInformationMatchingActivity.class);
					startActivity(intent);
				} else {
					setTitle(getString(R.string.text_choose_role_hint));
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

}
