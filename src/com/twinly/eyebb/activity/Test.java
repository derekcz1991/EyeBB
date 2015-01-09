package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.twinly.eyebb.R;
import com.twinly.eyebb.utils.BluetoothUtils;

public class Test extends Activity implements BluetoothUtils.BleConnectCallback {
	private ArrayList<String> list;
	private BluetoothUtils mBluetoothUtils1;
	private Button button;
	private int index;
	private int count;
	private Handler mHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			System.out.println(count++);
			switch (msg.what) {
			case 0:
				mBluetoothUtils1.connectOnly(list.get(index), 5000);
				break;
			case 1:
				mBluetoothUtils1.close();
				System.out.println("onConnectCanceled  " + list.get(index));
				System.out.println("-------------------------------");
				System.out.println(" ");
				index++;
				index = index % list.size();
				mBluetoothUtils1.connectOnly(list.get(index), 5000);
				break;
			case 2:
				mBluetoothUtils1.close();
				System.out.println("onConnected  " + list.get(index));
				System.out.println("-------------------------------");
				System.out.println(" ");
				index++;
				index = index % list.size();
				mBluetoothUtils1.connectOnly(list.get(index), 5000);
				break;

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		list = new ArrayList<String>();
		list.add("44:A6:E5:00:37:E7");
		list.add("44:A6:E5:00:37:EE");
		list.add("44:A6:E5:00:37:EA");
		list.add("44:A6:E5:00:38:DD");
		//list.add("44:A6:E5:00:04:EF");
		//list.add("44:A6:E5:00:04:DD");
		//list.add("44:A6:E5:00:04:A7");
		//list.add("44:46:E5:00:37:E9");
		//list.add("78:A5:04:55:28:C7");

		mBluetoothUtils1 = new BluetoothUtils(this, getFragmentManager(), this);

		button = (Button) findViewById(R.id.btn_start);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(0);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mBluetoothUtils1 != null) {
			mBluetoothUtils1.registerReceiver();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mBluetoothUtils1 != null) {
			mBluetoothUtils1.unregisterReceiver();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBluetoothUtils1 != null) {
			mBluetoothUtils1.disconnect();
		}
	}

	@Override
	public void onPreConnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectCanceled() {
		mHandler.sendEmptyMessageDelayed(1, 500);
	}

	@Override
	public void onConnected() {
		mHandler.sendEmptyMessageDelayed(2, 1000);
	}

	@Override
	public void onDisConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDiscovered() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataAvailable(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResult(boolean result) {
		// TODO Auto-generated method stub

	}

}
