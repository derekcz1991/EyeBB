package com.twinly.eyebb.activity;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_codescan.MipcaActivityCapture;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.BluetoothUtils;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.ImageUtils;

public class KidProfileActivity extends Activity implements
		BluetoothUtils.BleConnectCallback {
	private final static int SCANNIN_GREQUEST_CODE = 500;

	private Child child;
	private ImageView avatar;
	private TextView kidName;
	private TextView binding;
	private ImageLoader imageLoader;
	private LinearLayout deviceItem;
	private LinearLayout bindItem;
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 100;
	private static final int CROP_PHOTO = 200;
	private static final int PICK_FROM_FILE = 300;

	private TextView deviceAddress;
	private TextView deviceBattery;
	private BluetoothUtils mBluetoothUtils;
	Handler readBatteryHandler = new Handler();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kid_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
		registerForContextMenu(findViewById(R.id.avatarItem));

		child = DBChildren
				.getChildById(
						this,
						getIntent().getLongExtra(
								ActivityConstants.EXTRA_CHILD_ID, -1L));
		setTitle(child.getName());

		avatar = (ImageView) findViewById(R.id.avatar);
		kidName = (TextView) findViewById(R.id.kidname);
		binding = (TextView) findViewById(R.id.btn_binding);
		deviceAddress = (TextView) findViewById(R.id.device_address);
		deviceBattery = (TextView) findViewById(R.id.device_battery);
		deviceItem = (LinearLayout) findViewById(R.id.device_item);
		bindItem = (LinearLayout) findViewById(R.id.bind_item);

		deviceBattery.setText(getResources().getString(
				R.string.text_check_battery_life));

		if (child.getMacAddress().length() > 0) {
			mBluetoothUtils = new BluetoothUtils(KidProfileActivity.this,
					getFragmentManager(), this);

			deviceItem.setVisibility(View.VISIBLE);
			deviceAddress.setText(child.getMacAddress());

			deviceItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mBluetoothUtils.readBattery(child.getMacAddress(), 15000L);
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

		if (child.getRelationWithUser().equals("P") == false) {
			bindItem.setVisibility(View.INVISIBLE);
		}

		mImageCaptureUri = Uri.fromFile(new File(Constants.EYEBB_FOLDER
				+ "temp.jpg"));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mBluetoothUtils != null) {
			mBluetoothUtils.registerReceiver();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mBluetoothUtils != null) {
			mBluetoothUtils.unregisterReceiver();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBluetoothUtils != null) {
			mBluetoothUtils.disconnect();
		}
	}

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
			intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
					child.getChildId());
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
		String path = Constants.EYEBB_FOLDER + "avatar" + child.getChildId()
				+ ".jpg";
		if (ImageUtils.saveBitmap(bitmap, path)) {
			child.setIcon(path);
			DBChildren.updateIconByChildId(this, child.getChildId(), path);
			avatar.setImageBitmap(BitmapFactory.decodeFile(path));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				String macAddress = bundle.getString("result");
				Intent intent = new Intent();
				intent.setClass(this, BindingChildMacaronActivity.class);
				intent.putExtra(ActivityConstants.EXTRA_FROM,
						ActivityConstants.ACTIVITY_KID_PROFILE);
				intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID, -1L);
				intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
						child.getChildId());
				intent.putExtra(ActivityConstants.EXTRA_CHILD_ICON,
						child.getIcon());
				intent.putExtra(ActivityConstants.EXTRA_MAC_ADDRESS, macAddress);
				startActivityForResult(
						intent,
						ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_ACTIVITY);
			}
		}
	}

	@Override
	public void onPreConnect() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				deviceBattery.setText(getResources().getString(
						R.string.toast_loading));
			}
		});
	}

	@Override
	public void onConnectCanceled() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				deviceBattery.setText(getResources().getString(
						R.string.text_no_device_nearby));
			}
		});
	}

	@Override
	public void onConnected() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				deviceBattery.setText(getResources().getString(
						R.string.toast_loading));
			}
		});
	}

	@Override
	public void onDisConnected() {
		// do nothing
	}

	@Override
	public void onDiscovered() {
		// do nothing
	}

	@Override
	public void onDataAvailable(final String value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				deviceBattery.setText(Integer.parseInt(value, 16) + "%");
			}
		});

	}

	@Override
	public void onResult(final boolean result) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (result) {

				} else {
					deviceBattery.setText(getResources().getString(
							R.string.text_no_device_nearby));
				}
			}
		});
	}

}
