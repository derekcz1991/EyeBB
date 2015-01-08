package com.twinly.eyebb.activity;

import java.io.File;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
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
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_codescan.MipcaActivityCapture;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.HoloCircularProgressBar;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.BLEUtils;
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
	private LinearLayout avatarItemLayout;
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 100;
	private static final int CROP_PHOTO = 200;
	private static final int PICK_FROM_FILE = 300;

	private TextView deviceAddress;
	private TextView deviceBattery;
	private BluetoothUtils mBluetoothUtils;
	private BluetoothAdapter mBluetoothAdapter;

	private HoloCircularProgressBar mHoloCircularProgressBar;
	private ObjectAnimator mProgressBarAnimator;

	private String getDeviceBattery;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kid_profile_temp);
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
		avatarItemLayout = (LinearLayout) findViewById(R.id.avatarItem);

		mBluetoothUtils = new BluetoothUtils(KidProfileActivity.this,
				getFragmentManager(), this);
		deviceAddress.setText(child.getMacAddress());

		kidName.setText(child.getName());
		imageLoader = ImageLoader.getInstance();
		if (ImageUtils.isLocalImage(child.getIcon())) {
			avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(child.getIcon()));
		} else {
			imageLoader.displayImage(child.getIcon(), avatar,
					ImageUtils.avatarOpitons, null);
		}

		if (CommonUtils.isNull(child.getMacAddress())) {
			binding.setText(getString(R.string.btn_binding));
		} else {
			binding.setText(getString(R.string.btn_unbind));
		}

		if (child.getRelationWithUser().equals("P") == false) {
			binding.setVisibility(View.INVISIBLE);
		}

		mImageCaptureUri = Uri.fromFile(new File(Constants.EYEBB_FOLDER
				+ "temp.jpg"));

		mHoloCircularProgressBar = (HoloCircularProgressBar) findViewById(R.id.holoCircularProgressBar);

		// deviceBattery.setText(getResources().getString(
		// R.string.text_check_battery_life));
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		initToReadBattery();
	}

	@SuppressLint("NewApi")
	private void initToReadBattery() {

		if (!mBluetoothAdapter.isEnabled()) {
			bluetoothNotOpenCancelReadBattery();
		} else {
			avatarItemLayout.setBackgroundColor(getResources().getColor(
					R.color.activity_background_red));

			if (getDeviceBattery == null) {
				System.out.println("start to read battery");
				mBluetoothUtils.readBattery(child.getMacAddress(), 10000);
				mHoloCircularProgressBar.setMarkerEnabled(true);

				// mBluetoothUtils.readBattery(child.getMacAddress(), 10000);
				mHoloCircularProgressBar.setMarkerEnabled(false);
				mHoloCircularProgressBar.setProgress(1.0f);
			} else {
				mHoloCircularProgressBar.setProgress(1f);

				if (mProgressBarAnimator != null) {
					mProgressBarAnimator.cancel();
				}

				animate(mHoloCircularProgressBar, null,
						Float.valueOf(getDeviceBattery), 2000);
				mHoloCircularProgressBar.setMarkerProgress(Float
						.valueOf(getDeviceBattery));

				deviceBattery.setText(getResources().getString(
						R.string.text_battery_life)
						+ " "
						+ (1 - Float.valueOf(getDeviceBattery) + "").substring(
								2, 4) + "%");

			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mBluetoothUtils != null) {
			mBluetoothUtils.registerReceiver();
		}

		registerReceiver(bluetoothState, new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED));

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

		try {
			unregisterReceiver(bluetoothState);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Receiver not registered")) {
				// Ignore this exception. This is exactly what is desired
			} else {
				// unexpected, re-throw
				throw e;
			}
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

	private void bluetoothNotOpenCancelReadBattery() {
		avatarItemLayout.setBackgroundColor(getResources().getColor(
				R.color.lilac_colour));
		mHoloCircularProgressBar.setMarkerEnabled(false);
		mHoloCircularProgressBar.setProgress(0.0f);

		deviceBattery.setText(getResources().getString(
				R.string.text_no_device_nearby));
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

				initToReadBattery();

				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				System.out.println("STATE_TURNING_OFF");

				break;
			case BluetoothAdapter.STATE_OFF:
				System.out.println("STATE_OFF");
				bluetoothNotOpenCancelReadBattery();

				break;

			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;
		case PICK_FROM_FILE:
			if (data != null) {
				mImageCaptureUri = data.getData();
				doCrop();
			}

			break;
		case CROP_PHOTO:
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					saveAvatar(photo);
				}
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
				macAddress = BLEUtils.getValidMacAddress(this, macAddress);
				if (macAddress != null) {
					Intent intent = new Intent();
					intent.setClass(this, BindingChildMacaronActivity.class);
					intent.putExtra(ActivityConstants.EXTRA_FROM,
							ActivityConstants.ACTIVITY_KID_PROFILE);
					intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID, -1L);
					intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
							child.getChildId());
					intent.putExtra(ActivityConstants.EXTRA_CHILD_ICON,
							child.getIcon());
					intent.putExtra(ActivityConstants.EXTRA_MAC_ADDRESS,
							macAddress);
					startActivityForResult(
							intent,
							ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_ACTIVITY);
				}
			}
		}
	}

	private void animate(final HoloCircularProgressBar progressBar,
			final AnimatorListener listener, final float progress,
			final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress",
				progress);
		mProgressBarAnimator.setDuration(duration);

		mProgressBarAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBar.setMarkerProgress(progress);
		mProgressBarAnimator.start();
	}

	@Override
	public void onPreConnect() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				deviceBattery.setText(getResources().getString(
						R.string.text_battery_life)
						+ " "
						+ getResources().getString(R.string.toast_loading));
			}
		});
	}

	@Override
	public void onConnectCanceled() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				System.out.println("onConnectCanceled() ");
				bluetoothNotOpenCancelReadBattery();
				// deviceBattery.setText(getResources().getString(
				// R.string.text_battery_life));
			}
		});
	}

	@Override
	public void onConnected() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				deviceBattery.setText(getResources().getString(
						R.string.text_battery_life)
						+ " "
						+ getResources().getString(R.string.toast_loading));
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

				System.out.println("BATTERY-->" + Integer.parseInt(value, 16)
						+ "%");
				mHoloCircularProgressBar.setProgress(1f);

				if (mProgressBarAnimator != null) {
					mProgressBarAnimator.cancel();
				}

				animate(mHoloCircularProgressBar,
						null,
						(1 - Float.valueOf("0." + Integer.parseInt(value, 16))),
						2000);
				mHoloCircularProgressBar.setMarkerProgress((1 - Float
						.valueOf("0." + Integer.parseInt(value, 16))));

				deviceBattery.setText(getResources().getString(
						R.string.text_battery_life)
						+ " " + Integer.parseInt(value, 16) + "%");

				getDeviceBattery = (1 - Float.valueOf("0."
						+ Integer.parseInt(value, 16)))
						+ "";
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
					bluetoothNotOpenCancelReadBattery();
				}
			}
		});
	}

}
