package com.twinly.eyebb.dialog;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.DisplayLocationActivity;
import com.twinly.eyebb.activity.DisplayLocationBaiduActivity;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.ImageUtils;

public class ChildDialog extends Activity {
	public static String EXTRA_CHILD = "EXTRA_CHILD";

	private TextView phone;
	private TextView name;
	private TextView locationName;
	private TextView lastAppearTime;
	private LinearLayout phoneBtn;
	private CircleImageView avatar;
	private ChildForLocator childForLocator;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	final static int START_PROGRASSS_BAR = 1;
	final static int STOP_PROGRASSS_BAR = 2;
	private Dialog dialog;
	private String URL = "reportService/api/configBeaconRel";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_child);

		childForLocator = (ChildForLocator) getIntent().getExtras()
				.getSerializable(EXTRA_CHILD);

		phone = (TextView) findViewById(R.id.phone);
		phoneBtn = (LinearLayout) findViewById(R.id.phone_btn);
		name = (TextView) findViewById(R.id.name);
		locationName = (TextView) findViewById(R.id.areaNameText);
		lastAppearTime = (TextView) findViewById(R.id.last_appear_time);
		avatar = (CircleImageView) findViewById(R.id.avatar);

		phone.setText(childForLocator.getPhone());
		name.setText(childForLocator.getName());
		locationName.setText("@ " + childForLocator.getLocationName());
		lastAppearTime.setText(CommonUtils
				.ConvertTimestampToDateFormat(childForLocator
						.getLastAppearTime()));

		if (phone.getText().toString().trim().length() == 0) {
			phoneBtn.setVisibility(View.GONE);
		}

		imageLoader = ImageLoader.getInstance();
		if (ImageUtils.isLocalImage(childForLocator.getLocalIcon())) {
			avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(childForLocator
					.getLocalIcon()));
		} else {
			imageLoader.displayImage(childForLocator.getIcon(), avatar,
					ImageUtils.avatarOpitons, null);
		}

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
							new Thread(postToServerRunnable).start();
						}
					}
				});
		findViewById(R.id.btn_map).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(ChildDialog.this,DisplayLocationBaiduActivity.class);		//switch to baidu map for testing
				Intent intent = new Intent(ChildDialog.this, DisplayLocationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ChildDialog.EXTRA_CHILD, childForLocator);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});
	}

	Runnable postToServerRunnable = new Runnable() {
		@Override
		public void run() {
			// HANDLER
			Message msg = handler.obtainMessage();
			msg.what = START_PROGRASSS_BAR;
			handler.sendMessage(msg);
			postToServer();

		}
	};

	private void postToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("childId", childForLocator.getChildId() + "");
		map.put("macAddress", childForLocator.getMacAddress());

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(URL, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals("retStr.equals => "
					+ HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				dialog.dismiss();

			} else {
				// successful

				dialog.dismiss();

			}

		} catch (Exception e) {

			e.printStackTrace();

			Message msg = handler.obtainMessage();
			msg.what = STOP_PROGRASSS_BAR;
			handler.sendMessage(msg);

		}

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialog(ChildDialog.this,
						getString(R.string.toast_loading));
				dialog.show();
				break;

			case STOP_PROGRASSS_BAR:
				dialog.dismiss();

				break;

			}
		}
	};
}
