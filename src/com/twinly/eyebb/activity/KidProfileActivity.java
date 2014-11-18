package com.twinly.eyebb.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.ImageUtils;

public class KidProfileActivity extends Activity {
	private Child child;
	private ImageView avatar;
	private TextView kidName;
	private TextView binding;
	private ImageLoader imageLoader;

	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_PHOTO = 2;
	private static final int PICK_FROM_FILE = 3;
	public static KidProfileActivity instance = null;

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

		avatar = (ImageView) findViewById(R.id.avatar);
		kidName = (TextView) findViewById(R.id.kidname);
		binding = (TextView) findViewById(R.id.btn_binding);

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
		mImageCaptureUri = Uri.fromFile(new File(Constants.EYEBB_FOLDER
				+ "temp.jpg"));
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
		Intent intent = new Intent();
		intent.putExtra("child_id", child.getChildId());

		if (CommonUtils.isNull(child.getMacAddress())) {
			intent.setClass(this, CheckBeaconActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_CHECK_BEACON_ACTIVITY);
		} else {
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
		/*if (resultCode != RESULT_OK)
			return;*/
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
				finish();
			}
			break;
		}
	}
}
