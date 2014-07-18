package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.ViewFlipper;

import com.eyebb.R;
import com.twinly.eyebb.utils.DensityUtil;

public class ActivityDetailsActivity extends Activity {

	private ImageView Image2;
	private ViewFlipper mainLayout;
	private float startX;
	private int imageHight = 350;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		setTitle(getString(R.string.text_activityDetails));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	//	addImageView(R.drawable.activity_details_img1);
		addImageView(R.drawable.activity_details_img2);
		addImageView(R.drawable.activity_details_img3);
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		mainLayout = (ViewFlipper) this.findViewById(R.id.viewFlipper);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.share_action_provider, menu);

		MenuItem actionItem = menu
				.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem
				.getActionProvider();
		actionProvider
				.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		actionProvider.setShareIntent(createShareIntent());

		return true;
	}

	/**
	 * Creates a sharing {@link Intent}.
	 * 
	 * @return The sharing intent.
	 */
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent
				.putExtra(Intent.EXTRA_TEXT,
						"Welcome to www.eyebb.com");
		return shareIntent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addImageView(int img) {
		// 手动添加imageview
		mainLayout = (ViewFlipper) findViewById(R.id.viewFlipper);
		Image2 = new ImageView(this);
		Image2.setImageResource(img);
		//DensityUtil.px2dip(this, imageHight);
		// Image2.setId(110); //注意这点 设置id
		// Image2.setOnClickListener(this);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// lp1.addRule(RelativeLayout.ALIGN_TOP);
		// lp1.setMargins(30, 50, 100, 100);//(int left, int top, int right, int
		// bottom)
//		lp1.leftMargin = 30;
//		lp1.topMargin = 100;
		mainLayout.addView(Image2, lp1);
	}

	// add flipper
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			break;
		case MotionEvent.ACTION_UP:

			if (event.getX() > startX) { // 向右滑动
				mainLayout.setInAnimation(this, R.anim.in_leftright);
				mainLayout.setOutAnimation(this, R.anim.out_leftright);
				mainLayout.showNext();
			} else if (event.getX() < startX) { // 向左滑动
				mainLayout.setInAnimation(this, R.anim.in_rightleft);
				mainLayout.setOutAnimation(this, R.anim.out_rightleft);
				mainLayout.showPrevious();
			}
			break;
		}

		return super.onTouchEvent(event);
	}
}
