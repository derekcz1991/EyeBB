package com.twinly.eyebb.activity;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.TextView;

import com.eyebb.R;
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

	// sharedPreferences
	SharedPreferences SandVpreferences;
	

	// boolean sound and vibrate
	private Boolean isSound;
	private Boolean isVibrate;

	public static RadarOutOfRssiBeepDialog instance = null;

	public static boolean isStart  = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_beep);
		SandVpreferences = getSharedPreferences("soundAndVibrate", MODE_PRIVATE);
		isSound = SandVpreferences.getBoolean("sound", true);
		isVibrate = SandVpreferences.getBoolean("vibrate", true);
		//isStart = true;
	//	SharePrefsUtils.setStartBeepDialog(this, isStart);
		

		isStart = true;

		instance = this;

		// secText = (TextView) findViewById(R.id.sec_text);
		getTime();
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

	private void getTime() {
		// 获得计时器对象
		timer = (Chronometer) this.findViewById(R.id.chronometer);

		// 将计时器清零
		timer.setBase(SystemClock.elapsedRealtime());
		setFlickerAnimation(timer);
		// 开始计时
		timer.start();
	}

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
		mPlayer = MediaPlayer.create(RadarOutOfRssiBeepDialog.this, R.raw.beep);
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