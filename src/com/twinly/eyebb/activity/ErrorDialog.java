package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eyebb.R;

public class ErrorDialog extends Activity {
	private TextView btnVerify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_error);

		
		btnVerify = (TextView) findViewById(R.id.btn_verify);
		btnVerify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Constans.mBluetoothLeService = null;
//				BluetoothLeService bls = new BluetoothLeService();
//				Intent gattServiceIntent = new Intent(VerifyDialog.this, BluetoothLeService.class);
//				bls.onUnbind(gattServiceIntent);
				finish();
			}
		});

	}
}
