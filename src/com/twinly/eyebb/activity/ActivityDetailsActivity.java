package com.twinly.eyebb.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.ViewFlipper;

import com.eyebb.R;

public class ActivityDetailsActivity extends Activity {
	final Activity activity = this;
	private ImageView Image2;
	private ViewFlipper mainLayout;
	private float startX;
	private int imageHight = 350;
	private WebView webViewDetails;
	private String webViewDetailsURL = " http://158.182.246.221/twinly/share/html/notices/testing.html#sl_i1";
	//"http://158.182.246.221/twinly/share/html/notices/testing.html#sl_i1";
	private int width;

	@SuppressLint("SetJavaScriptEnabled")
	@JavascriptInterface
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		setTitle(getString(R.string.text_activityDetails));

		webViewDetails = (WebView) findViewById(R.id.webview_show);
		// 获取当前显示的界面大小
		WebSettings webSettings = webViewDetails.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		System.out.println("webViewDetailswebViewDetails"
				+ webViewDetails.getScale());

		// 设置可以支持缩放
		webViewDetails.getSettings().setSupportZoom(true);
		// 设置出现缩放工具
		webViewDetails.getSettings().setBuiltInZoomControls(true);
		// 扩大比例的缩放
		// webViewDetails.getSettings().setUseWideViewPort(true);
		// 滾動與縮放
		webViewDetails.setVerticalScrollBarEnabled(false);
		webViewDetails.setHorizontalScrollBarEnabled(false);
		webViewDetails.getSettings().setDomStorageEnabled(true);
		webViewDetails.getSettings().setJavaScriptEnabled(true);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
		// addImageView(R.drawable.activity_details_img1);
		// addImageView(R.drawable.activity_details_img2);
		// addImageView(R.drawable.activity_details_img3);
		// init();
		webViewDetails.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setTitle(R.string.text_loading);
				activity.setProgress(progress * 100);
				if (progress == 100)
					activity.setTitle(R.string.text_activityDetails);
			}
		});
		webViewDetails.setWebViewClient(new WebViewClient() {

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// Handle the error

			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});

		webViewDetails.loadUrl(webViewDetailsURL);

	}

	private void init() {
		// TODO Auto-generated method stub
		// mainLayout = (ViewFlipper) this.findViewById(R.id.viewFlipper);

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
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Welcome to "
				+ webViewDetailsURL);
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
		// mainLayout = (ViewFlipper) findViewById(R.id.viewFlipper);
		Image2 = new ImageView(this);
		Image2.setImageResource(img);
		// DensityUtil.px2dip(this, imageHight);
		// Image2.setId(110); //注意这点 设置id
		// Image2.setOnClickListener(this);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// lp1.addRule(RelativeLayout.ALIGN_TOP);
		// lp1.setMargins(30, 50, 100, 100);//(int left, int top, int right, int
		// bottom)
		// lp1.leftMargin = 30;
		// lp1.topMargin = 100;
		mainLayout.addView(Image2, lp1);
	}

	// add flipper
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// startX = event.getX();
	// break;
	// case MotionEvent.ACTION_UP:
	//
	// if (event.getX() > startX) { // 向右滑动
	// mainLayout.setInAnimation(this, R.anim.in_leftright);
	// mainLayout.setOutAnimation(this, R.anim.out_leftright);
	// mainLayout.showNext();
	// } else if (event.getX() < startX) { // 向左滑动
	// mainLayout.setInAnimation(this, R.anim.in_rightleft);
	// mainLayout.setOutAnimation(this, R.anim.out_rightleft);
	// mainLayout.showPrevious();
	// }
	// break;
	// }
	//
	// return super.onTouchEvent(event);
	// }
}
