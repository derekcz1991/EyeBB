package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.TextView;

import com.twinly.eyebb.R;

public class BeepDialog extends Activity {
	// record the time
	private Chronometer timer;
	private TextView secText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_beep);

		// secText = (TextView) findViewById(R.id.sec_text);
		// 获得计时器对象
		timer = (Chronometer) this.findViewById(R.id.chronometer);
		
		// 将计时器清零
		timer.setBase(SystemClock.elapsedRealtime());
		setFlickerAnimation(timer);
		// 开始计时
		timer.start();
		findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

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
}