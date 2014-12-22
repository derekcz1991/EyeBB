package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.utils.BluetoothUtils;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.ImageUtils;

public class BindingChildMacaronActivity extends Activity implements
		BluetoothUtils.BleConnectCallback {
	private final static int BIND_STEP_CONNECTING = 1;
	private final static int BIND_STEP_CONNECT_FAIL = 2;
	private final static int BIND_STEP_UPLOADING = 3;
	private final static int BIND_STEP_UPLOAD_FAIL = 4;
	private final static int BIND_STEP_BIND_FINISH = 5;

	private CircleImageView avatar;
	private TextView[] tvAnimation;
	private TextView tvMessage;
	private TextView iconBeacon;
	private TextView tvAddress;
	private Button btnEvent;
	private Handler mHandler;
	private int index;
	private ImageLoader imageLoader;

	private int from;
	private String mDeviceAddress;
	private long childId;
	private String childIcon;
	private long guardianId;
	private String major;
	private String minor;
	private int bindStep;

	private BluetoothUtils mBluetoothUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_child_macaron);

		from = getIntent().getIntExtra(ActivityConstants.EXTRA_FROM, -1);
		mDeviceAddress = getIntent().getStringExtra(
				ActivityConstants.EXTRA_MAC_ADDRESS);
		guardianId = getIntent().getLongExtra(
				ActivityConstants.EXTRA_GUARDIAN_ID, -1L);
		childId = getIntent().getLongExtra(ActivityConstants.EXTRA_CHILD_ID, 0);
		childIcon = getIntent().getStringExtra(
				ActivityConstants.EXTRA_CHILD_ICON);

		avatar = (CircleImageView) findViewById(R.id.avatar);
		tvMessage = (TextView) findViewById(R.id.message);
		iconBeacon = (TextView) findViewById(R.id.beacon);
		btnEvent = (Button) findViewById(R.id.btn_event);
		tvAddress = (TextView) findViewById(R.id.tv_address);

		tvAddress.setText(mDeviceAddress);

		tvAnimation = new TextView[6];
		tvAnimation[0] = (TextView) findViewById(R.id.animation_0);
		tvAnimation[1] = (TextView) findViewById(R.id.animation_1);
		tvAnimation[2] = (TextView) findViewById(R.id.animation_2);
		tvAnimation[3] = (TextView) findViewById(R.id.animation_3);
		tvAnimation[4] = (TextView) findViewById(R.id.animation_4);
		tvAnimation[5] = (TextView) findViewById(R.id.animation_5);

		iconBeacon.setAlpha(0.3f);

		if (TextUtils.isEmpty(childIcon) == false) {
			if (ImageUtils.isLocalImage(childIcon)) {
				avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(childIcon));
			} else {
				imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(childIcon, avatar,
						CommonUtils.getDisplayImageOptions(), null);
			}
		}

		mHandler = new Handler();
		mHandler.postDelayed(new UpdateAnimation(), 500);

		mBluetoothUtils = new BluetoothUtils(BindingChildMacaronActivity.this,
				getFragmentManager(), BindingChildMacaronActivity.this);
		new GetMajorMinorTask().execute();

		btnEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (bindStep) {
				case BIND_STEP_CONNECTING:
					finish();
					break;
				case BIND_STEP_CONNECT_FAIL:
					// TODO
					break;
				case BIND_STEP_UPLOAD_FAIL:
					new PostToServerTask().execute();
					break;
				case BIND_STEP_BIND_FINISH:
					switch (from) {
					case ActivityConstants.ACTIVITY_CHECK_CHILD_TO_BIND:
						Intent intent = new Intent(
								BindingChildMacaronActivity.this,
								LancherActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						break;
					case ActivityConstants.ACTIVITY_KID_PROFILE:
						setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
						break;
					}
					finish();
					break;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBluetoothUtils.registerReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBluetoothUtils.unregisterReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBluetoothUtils.disconnect();
	}

	/**
	 * To get major & minor from server by child_id and mac address
	 * @author derek
	 *
	 */
	private class GetMajorMinorTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", mDeviceAddress);

			return HttpRequestUtils.post(HttpConstants.CHECK_BEACON, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.CHECK_BEACON + " = " + result);

			if (result.length() > 0) {
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					return;
				} else if (result.equals(HttpConstants.SERVER_RETURN_NC)) {
					return;
				}
				if (result.equals(HttpConstants.SERVER_RETURN_USED)) {
					Toast.makeText(BindingChildMacaronActivity.this,
							R.string.text_device_already_binded,
							Toast.LENGTH_LONG).show();
					finish();
					return;
				} else {
					major = result.substring(0, result.indexOf(":"));
					minor = result.substring(result.indexOf(":") + 1,
							result.length());
					System.out.println("major = " + major + "  minor = "
							+ minor);
					mBluetoothUtils.writeMajorMinor(mDeviceAddress, 15000L,
							new String[] { major, minor });
				}
			}
		}
	}

	private class UpdateAnimation implements Runnable {

		@Override
		public void run() {
			if (index == 6) {
				index = 0;
				tvAnimation[0].setVisibility(View.INVISIBLE);
				tvAnimation[1].setVisibility(View.INVISIBLE);
				tvAnimation[2].setVisibility(View.INVISIBLE);
				tvAnimation[3].setVisibility(View.INVISIBLE);
				tvAnimation[4].setVisibility(View.INVISIBLE);
				tvAnimation[5].setVisibility(View.INVISIBLE);
			} else {
				tvAnimation[index].setVisibility(View.VISIBLE);
				index++;
			}
			mHandler.postDelayed(new UpdateAnimation(), 500);
		}
	}

	/**
	 * To upload the data to server when bind target device succeed
	 * @author derek
	 *
	 */
	private class PostToServerTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			bindStep = BIND_STEP_UPLOADING;
			tvMessage.setText(R.string.text_update_server_data);
			btnEvent.setEnabled(false);
		}

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", mDeviceAddress);
			map.put("major", major);
			map.put("minor", minor);
			map.put("guardianId",
					guardianId == -1 ? "" : String.valueOf(guardianId));
			return HttpRequestUtils.post(HttpConstants.DEVICE_TO_CHILD, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.DEVICE_TO_CHILD + " = " + result);
			if (result.length() > 0) {
				btnEvent.setText(R.string.btn_finish);
				btnEvent.setEnabled(true);
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					bindStep = BIND_STEP_UPLOAD_FAIL;
					tvMessage.setText(R.string.text_update_server_data_fail);
					return;
				}
				if (result.equals("T")) {
					bindStep = BIND_STEP_BIND_FINISH;
					tvMessage.setText(R.string.text_bind_success);
					DBChildren.updateMacAddressByChildId(
							BindingChildMacaronActivity.this, childId,
							mDeviceAddress);
				} else {
					bindStep = BIND_STEP_UPLOAD_FAIL;
					tvMessage.setText(R.string.text_update_server_data_fail);
					setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				}
			} else {
				bindStep = BIND_STEP_UPLOAD_FAIL;
				tvMessage.setText(R.string.text_update_server_data_fail);
			}

		}
	}

	@Override
	public void onPreConnect() {
		bindStep = BIND_STEP_CONNECTING;
		tvMessage.setText(R.string.text_connecting);
		btnEvent.setText(R.string.btn_cancel);
	}

	@Override
	public void onConnectCanceled() {
		bindStep = BIND_STEP_CONNECT_FAIL;
		tvMessage.setText(R.string.text_connect_device_failed);
		btnEvent.setText(R.string.btn_re_connect);
	}

	@Override
	public void onConnected() {
		iconBeacon.setAlpha(1);
	}

	@Override
	public void onDisConnected() {
		bindStep = BIND_STEP_CONNECT_FAIL;
		tvMessage.setText(R.string.text_connect_device_failed);
		btnEvent.setText(R.string.btn_re_connect);
	}

	@Override
	public void onDiscovered() {
		bindStep = BIND_STEP_CONNECTING;
		tvMessage.setText(R.string.text_update_device_data);
		btnEvent.setText(R.string.btn_cancel);
	}

	@Override
	public void onDataAvailable(String value) {
		// do nothing

	}

	@Override
	public void onResult(boolean result) {
		if (result) {
			new PostToServerTask().execute();
		} else {
			bindStep = BIND_STEP_CONNECT_FAIL;
			tvMessage.setText(R.string.text_connect_device_failed);
			btnEvent.setText(R.string.btn_re_connect);
		}

	}
}
