package com.twinly.eyebb.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class RadarOutOfRssiBeepDialog extends Activity {
	// record the time
	private Chronometer timer;
	private TextView secText;
	// 震動頻率
	private long pattern[] = { 1000, 800, 1000, 800 };
	private Vibrator vibrator;
	// sound
	private AudioManager aManager;;
	private MediaPlayer mPlayer;
	private Child data;

	// boolean sound and vibrate
	private Boolean isSound;
	private Boolean isVibrate;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private TextView hintForUser;

	public static RadarOutOfRssiBeepDialog instance = null;

	public static boolean isStart = false;
	private ImageView antiHeadImg;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_radar_rssi_beep);
		// SandVpreferences = getSharedPreferences("soundAndVibrate",
		// MODE_PRIVATE);
		isSound = SharePrefsUtils.isSoundOn(RadarOutOfRssiBeepDialog.this);
		isVibrate = SharePrefsUtils.isVibrateOn(RadarOutOfRssiBeepDialog.this);
		// isStart = true;
		// SharePrefsUtils.setStartBeepDialog(this, isStart);

		antiHeadImg = (ImageView) findViewById(R.id.anti_head_img);
		hintForUser = (TextView) findViewById(R.id.hint_for_user);

		if (SharePrefsUtils.getLanguage(RadarOutOfRssiBeepDialog.this) == BleDeviceConstants.LOCALE_HK
				|| SharePrefsUtils.getLanguage(RadarOutOfRssiBeepDialog.this) == BleDeviceConstants.LOCALE_TW) {
			SpannableStringBuilder builder = new SpannableStringBuilder(
					hintForUser.getText().toString());

			// ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
			ForegroundColorSpan redSpan1 = new ForegroundColorSpan(
					getResources().getColor(R.color.red));
			ForegroundColorSpan redSpan2 = new ForegroundColorSpan(
					getResources().getColor(R.color.red));
			builder.setSpan(redSpan1, 4, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.setSpan(redSpan2, 12, 14,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			hintForUser.setText(builder);

		}

		Intent intent = getIntent();
		data = (Child) intent.getSerializableExtra("child_information");

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();

		imageLoader.displayImage(data.getIcon(), antiHeadImg, options, null);
		isStart = true;

		instance = this;

		// secText = (TextView) findViewById(R.id.sec_text);
		// getTime();
		if (isVibrate) {
			vibrate();
		}

		if (isSound) {
			sound();
		}

		findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				stopALL();
			}
		});

	}

	// private void getTime() {
	// // 获得计时器对象
	// timer = (Chronometer) this.findViewById(R.id.chronometer);
	//
	// // 将计时器清零
	// timer.setBase(SystemClock.elapsedRealtime());
	// setFlickerAnimation(timer);
	// // 开始计时
	// timer.start();
	// }

	// 震動
	private void vibrate() {
		// 获取服务
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, 1);

	}

	// SOUND
	private void sound() {
		aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

		mPlayer = new MediaPlayer();
		mPlayer = MediaPlayer.create(RadarOutOfRssiBeepDialog.this, R.raw.antibeep);
		mPlayer.setLooping(true);
		mPlayer.start();
	}

	// 閃爍動畫
	private void setFlickerAnimation(Chronometer timer) {
		final Animation animation = new AlphaAnimation(1, 0);

		animation.setDuration(500);
		animation.setInterpolator(new LinearInterpolator());

		animation.setRepeatCount(Animation.INFINITE);

		animation.setRepeatMode(Animation.REVERSE);
		timer.setAnimation(animation);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPause()");
		stopALL();
	}

	private void stopALL() {
		if (isVibrate) {
			vibrator.cancel();
		}
		if (isSound) {
			mPlayer.stop();
			mPlayer.setLooping(false);
		}
		finish();
	}
}