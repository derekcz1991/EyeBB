package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.BarcodeCreater;
import com.twinly.eyebb.utils.DensityUtil;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class RequireQrCodeDialog extends Activity {
	private ImageView img_qr_code;
	private TextView txt_qr_code_address;
	private TextView btn_cancel;
	private Dialog qrCodeDialog;
	private String child_id;
	private String device_address;

	private Bitmap btMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_require_qr_code);

		img_qr_code = (ImageView) findViewById(R.id.device_qr_code);
		txt_qr_code_address = (TextView) findViewById(R.id.device_address);
		btn_cancel = (TextView) findViewById(R.id.btn_cancel);

		Intent intent = getIntent();
		child_id = intent.getStringExtra("child_id");

		qrCodeDialog = LoadingDialog.createLoadingDialog(
				RequireQrCodeDialog.this, getString(R.string.text_loading));

		new Thread(getQrCodeFromServerRunnable).start();

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	Runnable getQrCodeFromServerRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getQrCodeFromServer();
		}
	};

	private void getQrCodeFromServer() {
		try {

			Map<String, String> map = new HashMap<String, String>();
			if (child_id != null)
				map.put("childId", child_id);
			// System.out.println("child_id--->" + child_id);

			String retStr = HttpRequestUtils.post(
					HttpConstants.REQUIRE_OR_GET_QR_CODE, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.GET_QR_CODE_FAIL;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 4) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.GET_QR_CODE_SUCCESS;
					handler.sendMessage(msg);

					device_address = retStr;
					// System.out.println("device_address -- >" +
					// device_address);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = Constants.GET_QR_CODE_FAIL;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.GET_QR_CODE_SUCCESS:
				if (qrCodeDialog.isShowing() && qrCodeDialog != null) {
					qrCodeDialog.dismiss();
				}
				txt_qr_code_address.setText(device_address);

				btMap = BarcodeCreater.creatBarcode(RequireQrCodeDialog.this,
						"aaaaa",
						DensityUtil.dip2px(RequireQrCodeDialog.this, 250),
						DensityUtil.dip2px(RequireQrCodeDialog.this, 250),
						true, 2);
				img_qr_code.setImageBitmap(btMap);

				break;

			case Constants.GET_QR_CODE_FAIL:
				if (qrCodeDialog.isShowing() && qrCodeDialog != null) {
					qrCodeDialog.dismiss();
				}

				txt_qr_code_address
						.setText(getString(R.string.text_Apply_qr_code_fail));
				img_qr_code.setBackground(getResources().getDrawable(
						R.drawable.ic_verify_cross));

				break;

			}
		}
	};
}
