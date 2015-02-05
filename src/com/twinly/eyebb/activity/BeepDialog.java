package com.twinly.eyebb.activity;

import android.app.Activity;
import android.app.Service;
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

import com.twinly.eyebb.R;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class BeepDialog extends Activity {
	// record the time
	private Chronometer timer;
	// vibrator pattern
	private long pattern[] = { 1000, 800, 1000, 800 };
	private Vibrator vibrator;
	// sound
	private MediaPlayer mPlayer;

	// boolean sound and vibrate
	private Boolean isSound;
	private Boolean isVibrate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_beep);
		isSound = SharePrefsUtils.isSoundOn(BeepDialog.this);
		isVibrate = SharePrefsUtils.isVibrateOn(BeepDialog.this);

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
		timer = (Chronometer) this.findViewById(R.id.chronometer);

		timer.setBase(SystemClock.elapsedRealtime());
		setFlickerAnimation(timer);
		timer.start();
	}

	// vibrate
	private void vibrate() {
		// get vibrate service
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, 1);

	}

	// SOUND
	private void sound() {
		mPlayer = new MediaPlayer();
		mPlayer = MediaPlayer.create(BeepDialog.this, R.raw.antibeep);
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