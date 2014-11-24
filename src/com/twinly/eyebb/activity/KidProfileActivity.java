package com.twinly.eyebb.activity;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_codescan.MipcaActivityCapture;
import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.service.BleServicesService;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.ImageUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class KidProfileActivity extends Activity {
	private final static int SCANNIN_GREQUEST_CODE = 500;

	private Child child;
	private ImageView avatar;
	private TextView kidName;
	private TextView binding;
	private ImageLoader imageLoader;
	private RelativeLayout deviceItem;
	private BluetoothAdapter mBluetoothAdapter;
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 100;
	private static final int CROP_PHOTO = 200;
	private static final int PICK_FROM_FILE = 300;
	public static KidProfileActivity instance = null;

	private TextView deviceAddress;
	private TextView deviceBattery;
	public static Intent checkBatteryService;

	Handler readBatteryHandler = new Handler();

	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kid_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
		registerForContextMenu(findViewById(R.id.avatarItem));

		instance = this;
		child = DBChildren.getChildById(this,
				getIntent().getLongExtra("child_id", 0));

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		avatar = (ImageView) findViewById(R.id.avatar);
		kidName = (TextView) findViewById(R.id.kidname);
		binding = (TextView) findViewById(R.id.btn_binding);
		deviceAddress = (TextView) findViewById(R.id.device_address);
		deviceBattery = (TextView) findViewById(R.id.device_battery);
		deviceItem = (RelativeLayout) findViewById(R.id.device_item);

		if (SharePrefsUtils.deviceBattery(KidProfileActivity.this).length() > 0) {
			deviceBattery.setText(SharePrefsUtils
					.deviceBattery(KidProfileActivity.this) + "%");
		}
		registerReceiver(new UpdateDb(), new IntentFilter(
				BleDeviceConstants.BROADCAST_GET_DEVICE_BATTERY));

		if (child.getMacAddress().length() > 0) {
			deviceItem.setVisibility(View.VISIBLE);
			deviceAddress.setText(child.getMacAddress());
			deviceItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!mBluetoothAdapter.isEnabled()) {

						openBluetooth();
						// return;
					} else {
						readBattery();
					}

					registerReceiver(bluetoothState, new IntentFilter(
							BluetoothAdapter.ACTION_STATE_CHANGED));
				}
			});
		} else {
			deviceItem.setVisibility(View.GONE);
		}

		kidName.setText(child.getName());
		imageLoader = ImageLoader.getInstance();
		if (ImageUtils.isLocalImage(child.getIcon())) {
			avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(child.getIcon()));
		} else {
			imageLoader.displayImage(child.getIcon(), avatar,
					CommonUtils.getDisplayImageOptions(), null);
		}

		if (CommonUtils.isNull(child.getMacAddress())) {
			binding.setText(getString(R.string.btn_binding));
		} else {
			binding.setText(getString(R.string.btn_unbind));
		}
		mImageCaptureUri = Uri.fromFile(new File(
				BleDeviceConstants.EYEBB_FOLDER + "temp.jpg"));
	}

	Runnable readBatteryRunable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			readBattery();
		}
	};

	private void readBattery() {
		deviceBattery.setText(getResources().getString(R.string.toast_loading));

		checkBatteryService = new Intent();
		checkBatteryService.putExtra(BleServicesService.EXTRAS_DEVICE_NAME,
				BleDeviceConstants.DB_NAME);
		checkBatteryService.putExtra(BleServicesService.EXTRAS_DEVICE_ADDRESS,
				child.getMacAddress());
		checkBatteryService
				.setAction("com.twinly.eyebb.service.BLE_SERVICES_SERVICES");
		checkBatteryService.putExtra(BleDeviceConstants.BLE_SERVICE_COME_FROM,
				"battery");
		startService(checkBatteryService);
	}

	BroadcastReceiver bluetoothState = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String stateExtra = BluetoothAdapter.EXTRA_STATE;
			int state = intent.getIntExtra(stateExtra, -1);
			switch (state) {
			case BluetoothAdapter.STATE_TURNING_ON:
				System.out.println("STATE_TURNING_ON");

				break;
			case BluetoothAdapter.STATE_ON:
				System.out.println("STATE_ON");
				readBatteryHandler.postDelayed(readBatteryRunable,
						BleDeviceConstants.BATTERY_DELAY_LOADING);
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				System.out.println("STATE_TURNING_OFF");

				break;
			case BluetoothAdapter.STATE_OFF:
				System.out.println("STATE_OFF");

				break;

			}

		}
	};

	private class UpdateDb extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BleDeviceConstants.BROADCAST_GET_DEVICE_BATTERY)) {
				if (SharePrefsUtils.deviceBattery(KidProfileActivity.this)
						.equals("")) {
					deviceBattery.setText(getResources().getString(
							R.string.text_no_device_nearby));
				} else {
					deviceBattery.setText(SharePrefsUtils
							.deviceBattery(KidProfileActivity.this) + "%");
				}

			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 0, 0, getString(R.string.btn_take_photo));
		menu.add(0, 1, 1, getString(R.string.btn_choose_photo));
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			onCameraClicked();
			break;
		case 1:
			onGalleryClicked();
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void onAvatarItemCliked(View view) {
		this.openContextMenu(view);
	}

	public void onBindClicked(View view) {
		if (CommonUtils.isNull(child.getMacAddress())) {
			Intent intent = new Intent(this, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
		} else {
			Intent intent = new Intent();
			intent.putExtra("child_id", child.getChildId());
			intent.setClass(this, UnbindDeviceDialog.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_UNBIND_ACTIVITY);
		}
	}

	private void onCameraClicked() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);
		try {
			intent.putExtra("return-data", true);

			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void onGalleryClicked() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(
				Intent.createChooser(intent, "Complete action using"),
				PICK_FROM_FILE);
	}

	private void doCrop() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);

			i.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));

			startActivityForResult(i, CROP_PHOTO);
		}
	}

	private void saveAvatar(Bitmap bitmap) {
		String path = BleDeviceConstants.EYEBB_FOLDER + "avatar"
				+ child.getChildId() + ".jpg";
		if (ImageUtils.saveBitmap(bitmap, path)) {
			child.setIcon(path);
			DBChildren.updateIconByChildId(this, child.getChildId(), path);
			avatar.setImageBitmap(BitmapFactory.decodeFile(path));
		}
	}

	private void openBluetooth() {
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent,
						BleDeviceConstants.REQUEST_ENABLE_BT);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * if (resultCode != RESULT_OK) return;
		 */
		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;
		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop();
			break;
		case CROP_PHOTO:
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				saveAvatar(photo);
			}
			break;
		case ActivityConstants.REQUEST_GO_TO_UNBIND_ACTIVITY:
			if (resultCode == ActivityConstants.RESULT_UNBIND_SUCCESS) {
				setResult(ActivityConstants.RESULT_UNBIND_SUCCESS);
				finish();
			}
			break;
		case ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_ACTIVITY:
			if (resultCode == ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS) {
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
				finish();
			}
			break;
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				System.out.println("qrcode------->"
						+ bundle.getString("result"));
				//TODO check the mac address
				String macAddress = bundle.getString("result");
				Intent intent = new Intent();
				intent.setClass(this, BindingChildMacaronActivity.class);
				intent.putExtra(ActivityConstants.EXTRA_FROM,
						ActivityConstants.ACTIVITY_KID_PROFILE);
				intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID, -1);
				intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
						child.getChildId());
				intent.putExtra(ActivityConstants.EXTRA_MAC_ADDRESS, macAddress);
				startActivityForResult(
						intent,
						ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_ACTIVITY);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		registerReceiver(new UpdateDb(), new IntentFilter(
				BleDeviceConstants.BROADCAST_GET_DEVICE_BATTERY));
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// unregisterReceiver(new UpdateDb());
		super.onDestroy();
	}
}
