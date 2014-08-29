package com.twinly.eyebb.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.utils.CommonUtils;

public class ChildDialog extends Activity {

	private TextView phone;
	private TextView name;
	private TextView locationName;
	private LinearLayout phoneBtn;
	private CircleImageView avatar;
	private String icon;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_child);

		phone = (TextView) findViewById(R.id.phone);
		phoneBtn = (LinearLayout) findViewById(R.id.phone_btn);
		name = (TextView) findViewById(R.id.name);
		locationName = (TextView) findViewById(R.id.area_name);
		avatar = (CircleImageView) findViewById(R.id.avatar);

		phone.setText(getIntent().getStringExtra("phone"));
		name.setText(getIntent().getStringExtra("name"));
		locationName.setText("@ " + getIntent().getStringExtra("location"));
		icon = getIntent().getStringExtra("icon");

		if (phone.getText().toString().trim().length() == 0) {
			phoneBtn.setVisibility(View.GONE);
		}

		imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(icon, avatar,
				CommonUtils.getDisplayImageOptions(), animateFirstListener);

		phoneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri telUri = Uri.parse("tel:" + phone.getText());
				Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.maindialog_beep_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(ChildDialog.this,
									BeepDialog.class);
							startActivity(intent);
						}
					}
				});

	}

	private class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				CircleImageView imageView = (CircleImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
