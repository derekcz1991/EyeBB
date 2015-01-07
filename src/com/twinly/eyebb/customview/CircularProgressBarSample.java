package com.twinly.eyebb.customview;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import java.util.Random;

import com.twinly.eyebb.R;



/**
 * The Class CircularProgressBarSample.
 *
 * @author Pascal Welsch
 * @since 05.03.2013
 */
public class CircularProgressBarSample extends Activity {


	    private static final String TAG = CircularProgressBarSample.class.getSimpleName();

	    protected boolean mAnimationHasEnded = false;

	    private Switch mAutoAnimateSwitch;

	    /**
	     * The Switch button.
	     */
	    private Button mColorSwitchButton;

	    private HoloCircularProgressBar mHoloCircularProgressBar;

	    private Button mOne;

	    private ObjectAnimator mProgressBarAnimator;

	    private Button mZero;

	    /*
	     * (non-Javadoc)
	     *
	     * @see android.app.Activity#onCreate(android.os.Bundle)
	     */
	    @Override
	    protected void onCreate(final Bundle savedInstanceState) {
	        if (getIntent() != null) {
	            final Bundle extras = getIntent().getExtras();
	            if (extras != null) {
	                final int theme = extras.getInt("theme");
	                if (theme != 0) {
	                    setTheme(theme);
	                }
	            }
	        }

	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_kid_profile_temp);

	        mHoloCircularProgressBar = (HoloCircularProgressBar) findViewById(
	                R.id.holoCircularProgressBar);

	    

//	        mZero = (Button) findViewById(R.id.zero);
//	        mZero.setOnClickListener(new OnClickListener() {
//
//	            @Override
//	            public void onClick(View v) {
//	                if (mProgressBarAnimator != null) {
//	                    mProgressBarAnimator.cancel();
//	                }
//	                animate(mHoloCircularProgressBar, null, 0f, 1000);
//	                mHoloCircularProgressBar.setMarkerProgress(0f);
//
//	            }
//	        });
//
//	        mOne = (Button) findViewById(R.id.one);
//	        mOne.setOnClickListener(new OnClickListener() {
//
//	            @Override
//	            public void onClick(View v) {
//	                if (mProgressBarAnimator != null) {
//	                    mProgressBarAnimator.cancel();
//	                }
//	                animate(mHoloCircularProgressBar, null,  v, 1000);
//	                mHoloCircularProgressBar.setMarkerProgress(1f);
//
//	            }
//	        });

	      

	    }

	    /**
	     * generates random colors for the ProgressBar
	     */
	    protected void switchColor() {
	        Random r = new Random();
	        int randomColor = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
	        mHoloCircularProgressBar.setProgressColor(randomColor);

	        randomColor = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
	        mHoloCircularProgressBar.setProgressBackgroundColor(randomColor);
	    }

	
	    /**
	     * Switch theme.
	     */
	    public void switchTheme() {

	        final Intent intent = getIntent();
	        final Bundle extras = getIntent().getExtras();
	        if (extras != null) {
	            final int theme = extras.getInt("theme", -1);
	            if (theme == R.style.CircularProgressBarLight) {
	                getIntent().removeExtra("theme");
	            } else {
	                intent.putExtra("theme", R.style.CircularProgressBarLight);
	            }
	        } else {
	            intent.putExtra("theme", R.style.CircularProgressBarLight);
	        }
	        finish();
	        startActivity(intent);
	    }

	    /**
	     * Animate.
	     *
	     * @param progressBar the progress bar
	     * @param listener    the listener
	     */
	    private void animate(final HoloCircularProgressBar progressBar,
	            final AnimatorListener listener) {
	        final float progress = (float) (Math.random() * 2);
	        int duration = 3000;
	        animate(progressBar, listener, progress, duration);
	    }

	    private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener,
	            final float progress, final int duration) {

	        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
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

}
