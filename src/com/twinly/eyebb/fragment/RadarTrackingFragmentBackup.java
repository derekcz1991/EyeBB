package com.twinly.eyebb.fragment;

import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.BeepAllForRadarDialog;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.activity.RadarOutOfRssiBeepDialog;
import com.twinly.eyebb.adapter.MissRadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter.RadarKidsListViewAdapterCallback;
import com.twinly.eyebb.bluetooth.BluetoothLeService;
import com.twinly.eyebb.bluetooth.RadarSOSServicesActivity;
import com.twinly.eyebb.bluetooth.RadarServicesActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.service.BleCharacteristicsService;
import com.twinly.eyebb.service.BleServicesService;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.DensityUtil;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class RadarTrackingFragmentBackup extends Fragment implements
		RadarKidsListViewAdapterCallback, UncaughtExceptionHandler {
	private SimpleAdapter mkidsListAdapter;
	ArrayList<HashMap<String, Object>> mKidsData;
	ImageView radar_rotate;
	private View radarBeepAllBtn;

	private View btnStatus;

	private BluetoothAdapter mBluetoothAdapter;
	private final static int BLE_VERSION = 18;
	private static final int REQUEST_ENABLE_BT = 1;

	// bluetooth
	private boolean scan_flag = false;
	// true 为第一次扫描
	private Boolean firstScan = true;

	private Handler mHandler;
	private Handler autoScanHandler;
	private final static int START_SCAN = 4;
	private final static int STOP_SCAN = 5;
	private final static int DELETE_SCAN = 6;
	private final static int BEEPTIMEOUT = 7;
	private final static int SCAN_CHILD_FOR_LIST = 8;

	private String UUID;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
	List<String> UUID_temp = new ArrayList<String>();

	// SimpleAdapter Childadapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	private LinearLayoutForListView ChildlistView;
	private RadarKidsListViewAdapter Childadapter;

	// miss children
	private LinearLayoutForListView MissChildlistView;
	private MissRadarKidsListViewAdapter MissChildadapter;

	private ScrollView radarScrollView;
	// private CircleImageView circleImageView;
	private View RadarView;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private int getScreenWidth;
	private int DipGetScreenWidth;
	private int initX;
	private int initY;
	private int addX;
	private int addY;
	private String missingChildNum;
	private String unMissingChildNum;
	private TextView missingChildNumTxt;
	private TextView unMissingChildNumTxt;
	private Boolean isClickConnection = false;
	private Boolean isWhileLoop = true;
	private TextView confirmRadarBtn;
	// 判斷confirmRadarBtn是否被點擊
	private Boolean isConfirmRadarBtn = false;

	// device map
	private HashMap<String, Device> deviceMap = new HashMap<String, Device>();;

	private ArrayList<Child> ChildData;
	private ArrayList<Child> ScanedChildData;
	private static ArrayList<Child> ScanedTempChildData;
	private ArrayList<Child> BeepTempChildData;
	private ArrayList<Child> BeepAllTempChildData;
	// 檢測頭像是否有改變
	private ArrayList<Child> scanedHeadChildData;
	private ArrayList<Child> missedHeadChildData;
	// private Child child;
	private ArrayList<Child> MissChildData;

	private View v;
	private static RadarTrackingFragmentBackup RadarTrackingFragmentInstance = null;
	RelativeLayout InitMainLayout = null;
	RelativeLayout MissMainLayout = null;
	RelativeLayout mainLayout = null;
	private TimerTask BeepCheckTimeOutTask = null;

	private boolean isFirstBeep = true;
	private int beepAllTime = 0;
	Timer timer = null;
	TimerTask task = null;
	private int MissChildDataHeadNum = 0;
	private int ScanedChildDataHeadNum = 0;
	Thread BLEScanThread;
	// 控制timetask
	private boolean buttonCancel = true;

	public static Intent beepIntent;
	private int beepTime = 0;

	// 開啟防丟器
	private LinearLayout openAntiTheft;
	private TextView openAntiTheftTX;
	private boolean openAnti = false;

	private int deviceStatusError = 0;

	private boolean isLeScan;

	// 頭像四方位定位
	private int zeroMissX = 100;
	private int zeroMissY = 10;

	private int oneMissX = 10;
	private int oneMissY = 100;

	private int twoMissX = 10;
	private int twoMissY = 100;

	private int threeMissX = 100;
	private int threeMissY = 10;

	@SuppressWarnings("static-access")
	@SuppressLint({ "NewApi", "CutPasteId" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");

		v = inflater.inflate(R.layout.fragment_radar_tracking_verson_2,
				container, false);
		RadarTrackingFragmentInstance = this;

		initView(v);

		ChildData = DBChildren.getChildrenList(getActivity());

		mHandler = new Handler();
		autoScanHandler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();

		radarBeepAllBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isClickConnection) {
					if (CommonUtils.isFastDoubleClick()) {
						return;
					} else {
						Intent intent = new Intent(getActivity(),
								BeepAllForRadarDialog.class);
						intent.putExtra(Constants.BEEP_ALL_DEVICE,
								(Serializable) BeepAllTempChildData);

						SharePrefsUtils.setStartBeepDialog(getActivity(), true);
						startActivity(intent);
					}
				}
			}
		});

		// 點擊這裡i
		confirmRadarBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					if (!isConfirmRadarBtn) {
						btnConfirmConnect();
						SharePrefsUtils.setfinishBeep(getActivity(), false);
					} else {
						btnCancelConnect();
						closeBluetooth();
					}
				}

			}
		});

		openAntiTheft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (openAnti) {
					openAnti = false;
					openAntiTheftTX.setText(getResources().getString(
							R.string.text_radar_status_start_connected));
				} else {
					openAnti = true;
					openAntiTheftTX.setText(getResources().getString(
							R.string.text_radar_status_disconnection));
				}

			}
		});

		return v;

	}

	@SuppressLint("CutPasteId")
	private void initView(View v) {
		ChildlistView = (LinearLayoutForListView) v
				.findViewById(R.id.radar_children_list);
		MissChildlistView = (LinearLayoutForListView) v
				.findViewById(R.id.radar_children_list_miss);
		// listview初始化為看不見
		ChildlistView.setVisibility(View.GONE);
		MissChildlistView.setVisibility(View.GONE);
		ScanedChildData = new ArrayList<Child>();
		ScanedTempChildData = new ArrayList<Child>();
		MissChildData = new ArrayList<Child>();

		// ScanedChildData.clear();
		// MissChildData.clear();

		RadarView = v.findViewById(R.id.radar_view);

		radarBeepAllBtn = v.findViewById(R.id.radar_beep_all_btn);
		radarScrollView = (ScrollView) v.findViewById(R.id.radar_scrollview);
		radarScrollView.smoothScrollTo(0, 0);

		// 改變所有數據
		// chageTheAllData();
		// MissChildadapter.notifyDataSetChanged();

		missingChildNumTxt = (TextView) v
				.findViewById(R.id.radar_text_missed_number);
		unMissingChildNumTxt = (TextView) v
				.findViewById(R.id.radar_text_supervised_number);

		confirmRadarBtn = (TextView) v.findViewById(R.id.confirm_radar_btn);
		// 防丟器
		openAntiTheft = (LinearLayout) v
				.findViewById(R.id.connection_status_btn);
		openAntiTheftTX = (TextView) v.findViewById(R.id.connection_status_txt);

		// bluetooth
		// connect device
		// btnConfirm = v.findViewById(R.id.btn_confirm);
		// btnCancel = v.findViewById(R.id.btn_cancel);
		btnStatus = v.findViewById(R.id.connection_status_btn);
	}

	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("onDestroy()");
		super.onDestroy();
		// 关闭循环扫描
		isWhileLoop = false;
		handler.removeCallbacksAndMessages(SCAN_CHILD_FOR_LIST);
		handler.removeCallbacksAndMessages(BEEPTIMEOUT);

		try {
			if (beepIntent != null)
				getActivity().stopService(beepIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopTimer();
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}

	// 下面為bluetooth
	Runnable autoScan = new Runnable() {
		@Override
		public void run() {
			// System.out.println("isWhileLoop==>" + isWhileLoop);
			while (isWhileLoop) {
				scanLeDevice(true);
			}

		}
	};

	@SuppressLint("NewApi")
	public void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.

			isLeScan = mBluetoothAdapter.startLeScan(mLeScanCallback);

			try {

				Thread.sleep(Constants.SCAN_INRERVAL_TIME);

				mBluetoothAdapter.stopLeScan(mLeScanCallback);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume()");
		// ScanedChildData.clear();
		// MissChildData.clear();
		// listItem.clear();
		// mLeDevices.clear();
		final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// // // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		// if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
		// openBluetooth();
		// }

		if (mBluetoothAdapter.isEnabled()) {
			// if (scan_flag) {
			// autoScanHandler.postDelayed(autoScan, Constants.POSTDELAYTIME);
			// } else {
			// new Thread(autoScan).start();
			// }

		}

		if (!isConfirmRadarBtn) {
			System.out.println("isWhileLoop = false;");
			isWhileLoop = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		} else {
			System.out.println("isWhileLoop = true;");
			isWhileLoop = true;
			if (scan_flag) {
				autoScanHandler.postDelayed(autoScan, Constants.POSTDELAYTIME);
			} else {
				new Thread(autoScan).start();
			}
		}

		// clearAlltheDate();
	}

	@SuppressLint("NewApi")
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop()");

		isWhileLoop = false;
		// handler.removeCallbacksAndMessages(SCAN_CHILD_FOR_LIST);
		// handler.removeCallbacksAndMessages(BEEPTIMEOUT);
		try {
			if (beepIntent != null)
				getActivity().stopService(beepIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// stopTimer();

	}

	public void btnConfirmConnect() {

		confirmRadarBtn.setBackground(getResources().getDrawable(
				R.drawable.ic_selected));

		// checkIsBluetoothOpen 判斷藍芽是否打開 如果沒打開則返回到初始界面
		boolean checkIsBluetoothOpen = false;
		try {
			checkIsBluetoothOpen = checkIsBluetooth();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (checkIsBluetoothOpen) {
			// 阻止底部button點擊
			isClickConnection = true;
			// 開始循環掃描
			isWhileLoop = true;

			// 確定點擊開啟button
			isConfirmRadarBtn = true;

			// 重新啟動timetask
			// timer.schedule(task, 500, 5000);
			if (buttonCancel) {
				buttonCancel = false;
				TimerTask task = new TimerTask() {

					public void run() {
						// System.out.println("AAAAAAAAAAA");
						Message msg = handler.obtainMessage();
						msg.what = SCAN_CHILD_FOR_LIST;
						handler.sendMessage(msg);

					}

				};
				timer = new Timer();

				if (timer != null && task != null)
					// System.out.println("bbbbbbbbbbbbbb");
					timer.schedule(task, Constants.DELAY, Constants.PERIOD);

			}

			// 顯示list
			ChildlistView.setVisibility(View.VISIBLE);
			MissChildlistView.setVisibility(View.VISIBLE);
			// list透明為正常
			ChildlistView.setAlpha(1);
			MissChildlistView.setAlpha(1);
			RadarView.setAlpha(1);

			radarAnim();

			// device status
			SharePrefsUtils.setDeviceConnectStatus(getActivity(),
					Constants.DEVICE_CONNECT_STATUS_DEFAULT);

			// bluetooth

			if (scan_flag) {
				// System.out
				// .println("autoScanHandler.postDelayed(autoScan, POSTDELAYTIME);");
				autoScanHandler.postDelayed(autoScan, Constants.POSTDELAYTIME);
			} else {
				new Thread(autoScan).start();
			}
		} else {
			confirmRadarBtn.setBackground(getResources().getDrawable(
					R.drawable.ic_selected_off));
		}

	}

	@SuppressLint("NewApi")
	private void btnCancelConnect() {
		isConfirmRadarBtn = false;
		confirmRadarBtn.setBackground(getResources().getDrawable(
				R.drawable.ic_selected_off));
		// timer control
		buttonCancel = true;
		// 清除time task
		stopTimer();
		// 删除 radar上的头像
		// removeInitImageHead();

		// listview消失
		ChildlistView.setVisibility(View.GONE);
		MissChildlistView.setVisibility(View.GONE);
		ChildlistView.setAlpha((float) 0.3);
		MissChildlistView.setAlpha((float) 0.3);
		RadarView.setAlpha((float) 0.3);
		scan_flag = false;
		// autoScanHandler.removeCallbacks(autoScan);
		// mHandler.removeCallbacks(scanLeDeviceRunable);
		// mBluetoothAdapter.stopLeScan(mLeScanCallback);
		// 關閉循環掃描
		isWhileLoop = false;
		// 清除動畫
		radar_rotate.clearAnimation();

		// 清除頭像 清除數據 清除數量
		removeImageHead(ScanedChildDataHeadNum);
		removeMissImageHead(MissChildDataHeadNum);

		clearAlltheDate();
		missingChildNumTxt.setText(MissChildData.size() + "");
		unMissingChildNumTxt.setText(ScanedTempChildData.size() + "");

		mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("onStart()");

	}

	private void chageTheAllData(ArrayList<Child> scanedTempChildData,
			ArrayList<Child> missChildData) {

//		Childadapter = new RadarKidsListViewAdapter(getActivity(),
//				scanedTempChildData);
		Childadapter.setCallback(RadarTrackingFragmentInstance);
		ChildlistView.setAdapter(Childadapter);

		MissChildadapter = new MissRadarKidsListViewAdapter(getActivity(),
				missChildData);
		MissChildlistView.setAdapter(MissChildadapter);

	}

	@SuppressWarnings("unchecked")
	@SuppressLint("ResourceAsColor")
	private int addImageHead(ArrayList<Child> scanedChildData2) {
		// 手动添加imageview
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();

		for (int i = 0; i < scanedChildData2.size(); i++) {
			Child child = scanedChildData2.get(i);
			mainLayout = (RelativeLayout) v.findViewById(R.id.miss_radar_view);
			CircleImageView cim = new CircleImageView(getActivity());
			HeadPosition(cim);

			mainLayout.addView(cim);
			// AsyncImageLoader.setImageViewFromUrl(child.getIcon(), cim);
			imageLoader.displayImage(child.getIcon(), cim, options, null);

		}

		return scanedChildData2.size();

	}

	private void removeImageHead(int scanedChildDataNum) {

		try {
			mainLayout.removeViewsInLayout(1, scanedChildDataNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// mainLayout.removeViewsInLayout(1, scanedChildDataNum);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private int addMissImageHead(ArrayList<Child> missChildData2) {
		// 手动添加imageview
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();

		for (int i = 0; i < missChildData2.size(); i++) {
			// System.out.println("MissTempHeadImageChildData isChange===> "
			// + missChildData2.size());
			Child child = missChildData2.get(i);
			MissMainLayout = (RelativeLayout) v
					.findViewById(R.id.miss_radar_view);
			CircleImageView MissCim = new CircleImageView(getActivity());

			// 初始化頭像方位
			int initHeadPosition = 0;
			switch (i % 4) {
			case 0:
				initHeadPosition = 0;
				break;

			case 1:
				initHeadPosition = 1;
				break;

			case 2:
				initHeadPosition = 2;
				break;

			case 3:
				initHeadPosition = 3;
				break;
			}
			missHeadPosition(MissCim, initHeadPosition);

			MissMainLayout.addView(MissCim);
			// AsyncImageLoader.setImageViewFromUrl(child.getIcon(), cim);
			imageLoader.displayImage(child.getIcon(), MissCim, options, null);

		}

		return missChildData2.size();

	}

	private void removeMissImageHead(int missChildDataNum) {

		try {
			// .out.println("number=> " + missChildDataNum);
			MissMainLayout.removeViews(1, missChildDataNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化頭像位置
	 * 
	 * @param imMiss
	 * @param cim
	 * @param getPeople
	 */
	// @SuppressLint("NewApi")
	// private void HeadPosition(CircleImageView cim) {
	// // // 初始化為中心
	// RelativeLayout.LayoutParams layoutParams = new
	// RelativeLayout.LayoutParams(
	// DensityUtil.dip2px(getActivity(), 32), DensityUtil.dip2px(
	// getActivity(), 32));
	//
	// cim.setX(DensityUtil.dip2px(getActivity(), 160));
	//
	// getScreenWidth = getScreenInfo();
	// // 24 = 8邊框 + 16ImageView
	// DipGetScreenWidth = getScreenWidth / 2
	// - DensityUtil.dip2px(getActivity(), 24);
	// // 得到整個手機的dp 三星為360 centrl 為 DipGetScreenWidth/2 -120
	// // int te = DensityUtil.px2dip(getActivity(), getScreenWidth);
	// // System.out.println("tete" + te);
	// initX = DipGetScreenWidth;
	// initY = DensityUtil.dip2px(getActivity(), 120 - 16);
	//
	// int RightorLeft = 1 + (int) (Math.random() * 2);
	// int toporBottom = 1 + (int) (Math.random() * 2);
	// cim.setBorderColor(getResources().getColor(R.color.white));
	//
	// cim.setBorderWidth(DensityUtil.dip2px(getActivity(), 2));
	//
	// addX = DensityUtil.dip2px(getActivity(), (int) (Math.random() * 100));
	// addY = DensityUtil.dip2px(getActivity(), (int) (Math.random() * 100));
	//
	// if ((addX * addX + addY * addY) <= DensityUtil.dip2px(getActivity(),
	// 7225)) {
	// if (toporBottom == 1) {
	// initY = initY - addY;
	//
	// if (RightorLeft == 1) {
	// initX = initX + addX;
	// } else if (RightorLeft == 2) {
	// initX = initX - addX;
	// }
	// } else if (toporBottom == 2) {
	// initY = initY + addY;
	//
	// if (RightorLeft == 1) {
	// initX = initX + addX;
	// } else if (RightorLeft == 2) {
	// initX = initX - addX;
	// }
	// }
	//
	// // System.out.println("initX + initY :" + initX + " " + initY);
	// cim.setX(initX);
	// cim.setY(initY);
	//
	// } else {
	// HeadPosition(cim);
	// }
	//
	// cim.setLayoutParams(layoutParams);
	// }
	//
	// private void missHeadPosition(CircleImageView cim, int initHeadPosition)
	// {
	// RelativeLayout.LayoutParams layoutParams = new
	// RelativeLayout.LayoutParams(
	// DensityUtil.dip2px(getActivity(), 32), DensityUtil.dip2px(
	// getActivity(), 32));
	//
	// cim.setX(DensityUtil.dip2px(getActivity(), 160));
	//
	// getScreenWidth = getScreenInfo();
	// // 24 = 8邊框 + 16ImageView
	// DipGetScreenWidth = getScreenWidth / 2
	// - DensityUtil.dip2px(getActivity(), 24);
	// // 得到整個手機的dp 三星為360 centrl 為 DipGetScreenWidth/2 -120
	// // int te = DensityUtil.px2dip(getActivity(), getScreenWidth);
	// // System.out.println("tete" + te);
	// initX = DipGetScreenWidth;
	// initY = DensityUtil.dip2px(getActivity(), 120 - 16);
	//
	// int RightorLeft = 1;
	// int toporBottom = 1;
	// // int RightorLeft = 1;
	// // int toporBottom = 1;
	// switch (initHeadPosition) {
	// case 0:
	// RightorLeft = 1;
	// toporBottom = 1;
	// break;
	//
	// case 1:
	// RightorLeft = 2;
	// toporBottom = 1;
	// break;
	//
	// case 2:
	// RightorLeft = 1;
	// toporBottom = 2;
	// break;
	//
	// case 3:
	// RightorLeft = 2;
	// toporBottom = 2;
	// break;
	// }
	//
	// cim.setBorderColor(getResources().getColor(R.color.red));
	// cim.setBorderWidth(DensityUtil.dip2px(getActivity(), 2));
	//
	// int missX = (int) (Math.random() * 120);
	// int missY = (int) (Math.random() * 120);
	//
	// // System.out.println("addY + addx :" + missX * missX + " " + missY
	// // * missY + " " + DensityUtil.dip2px(getActivity(), 14400)
	// // + " " + DensityUtil.dip2px(getActivity(), 10000) + "  "
	// // + 90 + (int) (Math.random() * 30));
	// if ((missX * missX + missY * missY) < 14000
	// && (missX * missX + missY * missY) > 10000) {
	// addX = DensityUtil.dip2px(getActivity(), missX);
	// addY = DensityUtil.dip2px(getActivity(), missY);
	// // System.out.println("啊啊啊啊啊啊 ");
	//
	// if (toporBottom == 1) {
	// initY = initY - addY;
	//
	// if (RightorLeft == 1) {
	// initX = initX + addX;
	// } else if (RightorLeft == 2) {
	// initX = initX - addX;
	// }
	// } else if (toporBottom == 2) {
	// initY = initY + addY;
	//
	// if (RightorLeft == 1) {
	// initX = initX + addX;
	// } else if (RightorLeft == 2) {
	// initX = initX - addX;
	// }
	// }
	//
	// // System.out.println("initX + initY :" + initX + " " + initY);
	//
	// cim.setX(initX);
	// cim.setY(initY);
	//
	// } else {
	// missHeadPosition(cim, initHeadPosition);
	// }
	//
	// cim.setLayoutParams(layoutParams);
	// }

	@SuppressLint("NewApi")
	private void HeadPosition(CircleImageView cim) {
		// // 初始化為中心
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				DensityUtil.dip2px(getActivity(), 32), DensityUtil.dip2px(
						getActivity(), 32));

		cim.setX(DensityUtil.dip2px(getActivity(), 160));

		getScreenWidth = getScreenInfo();
		// 24 = 8邊框 + 16ImageView
		DipGetScreenWidth = getScreenWidth / 2
				- DensityUtil.dip2px(getActivity(), 24);
		// 得到整個手機的dp 三星為360 centrl 為 DipGetScreenWidth/2 -120
		// int te = DensityUtil.px2dip(getActivity(), getScreenWidth);
		// System.out.println("tete" + te);
		initX = DipGetScreenWidth;
		initY = DensityUtil.dip2px(getActivity(), 120 - 16);

		int RightorLeft = 1 + (int) (Math.random() * 2);
		int toporBottom = 1 + (int) (Math.random() * 2);
		cim.setBorderColor(getResources().getColor(R.color.white));

		cim.setBorderWidth(DensityUtil.dip2px(getActivity(), 2));

		addX = DensityUtil.dip2px(getActivity(), (int) (Math.random() * 100));
		addY = DensityUtil.dip2px(getActivity(), (int) (Math.random() * 100));

		if ((addX * addX + addY * addY) <= DensityUtil.dip2px(getActivity(),
				7225)) {
			if (toporBottom == 1) {
				initY = initY - addY;

				if (RightorLeft == 1) {
					initX = initX + addX;
				} else if (RightorLeft == 2) {
					initX = initX - addX;
				}
			} else if (toporBottom == 2) {
				initY = initY + addY;

				if (RightorLeft == 1) {
					initX = initX + addX;
				} else if (RightorLeft == 2) {
					initX = initX - addX;
				}
			}

			// System.out.println("initX + initY :" + initX + " " + initY);
			cim.setX(initX);
			cim.setY(initY);

		} else {
			HeadPosition(cim);
		}

		cim.setLayoutParams(layoutParams);
	}

	private void missHeadPosition(CircleImageView cim, int initHeadPosition) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				DensityUtil.dip2px(getActivity(), 32), DensityUtil.dip2px(
						getActivity(), 32));

		cim.setX(DensityUtil.dip2px(getActivity(), 160));

		getScreenWidth = getScreenInfo();
		// 24 = 8邊框 + 16ImageView
		DipGetScreenWidth = getScreenWidth / 2
				- DensityUtil.dip2px(getActivity(), 24);
		// 得到整個手機的dp 三星為360 centrl 為 DipGetScreenWidth/2 -120
		// int te = DensityUtil.px2dip(getActivity(), getScreenWidth);
		// System.out.println("tete" + te);
		initX = DipGetScreenWidth;
		initY = DensityUtil.dip2px(getActivity(), 120 - 16);

		int RightorLeft = 1;
		int toporBottom = 1;
		int finalMissX = 100;
		int finalMissY = 10;
		switch (initHeadPosition) {
		case 0:
			RightorLeft = 1;
			toporBottom = 1;
			zeroMissX = zeroMissX + 10;
			zeroMissY = zeroMissY + 10;
			finalMissX = zeroMissX;
			finalMissY = zeroMissY;
			break;

		case 1:
			RightorLeft = 2;
			toporBottom = 1;
			oneMissX = oneMissX + 10;
			oneMissY = oneMissY + 10;
			finalMissX = oneMissX;
			finalMissY = oneMissY;
			break;

		case 2:
			RightorLeft = 1;
			toporBottom = 2;
			twoMissX = twoMissX + 10;
			twoMissY = twoMissY + 10;
			finalMissX = twoMissX;
			finalMissY = twoMissY;
			break;

		case 3:
			RightorLeft = 2;
			toporBottom = 2;
			threeMissX = threeMissX + 10;
			threeMissY = threeMissY + 10;
			finalMissX = threeMissX;
			finalMissY = threeMissY;
			break;
		}

		cim.setBorderColor(getResources().getColor(R.color.red));
		cim.setBorderWidth(DensityUtil.dip2px(getActivity(), 2));

		// int missX = (int) (Math.random() * 120);
		// int missY = (int) (Math.random() * 120);

		// System.out.println("addY + addx :" + missX * missX + " " + missY
		// * missY + " " + DensityUtil.dip2px(getActivity(), 14400)
		// + " " + DensityUtil.dip2px(getActivity(), 10000) + "  "
		// + 90 + (int) (Math.random() * 30));
		if ((finalMissX * finalMissX + finalMissY * finalMissY) < 14000
				&& (finalMissX * finalMissX + finalMissY * finalMissY) > 10000) {
			addX = DensityUtil.dip2px(getActivity(), finalMissX);
			addY = DensityUtil.dip2px(getActivity(), finalMissY);
			// System.out.println("啊啊啊啊啊啊 ");

			if (toporBottom == 1) {
				initY = initY - addY;

				if (RightorLeft == 1) {
					// zero right top
					initX = initX + addX;
				} else if (RightorLeft == 2) {
					// one left top
					initX = initX - addX;
				}
			} else if (toporBottom == 2) {
				initY = initY + addY;

				if (RightorLeft == 1) {
					// two right bottom
					initX = initX + addX;
				} else if (RightorLeft == 2) {
					// three left bottom
					initX = initX - addX;
				}
			}

			// System.out.println("initX + initY :" + initX + " " + initY);

			cim.setX(initX);
			cim.setY(initY);

		} else {
			zeroMissX = 100;
			zeroMissY = 10;

			oneMissX = 10;
			oneMissY = 100;

			twoMissX = 10;
			twoMissY = 100;

			threeMissX = 100;
			threeMissY = 10;

			finalMissX = 100;
			finalMissY = 10;
			missHeadPosition(cim, initHeadPosition);
		}

		cim.setLayoutParams(layoutParams);
	}

	private int getScreenInfo() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		// System.out.println("widthwidthwidthwidth=>" + width);
		return width;
	}

	public Bitmap getBitmapFromUri(Uri uri) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity()
					.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			Log.e("[Android]", e.getMessage());
			Log.e("[Android]", "目录为：" + uri);
			e.printStackTrace();
			return null;
		}
	}

	private int getVersionAPI() throws Exception {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		return currentapiVersion;
	}

	@SuppressLint("NewApi")
	public boolean checkIsBluetooth() throws NotFoundException, Exception {
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (getVersionAPI() >= BLE_VERSION) {
			// System.out.println("checkIsBluetooth");
			if (!getActivity().getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				Toast.makeText(getActivity(), R.string.text_ble_not_supported,
						Toast.LENGTH_SHORT).show();
				return false;
			}

			// 初始化 Bluetooth adapter,
			// 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
			final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
					.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();

			// 检查设备上是否支持蓝牙
			if (mBluetoothAdapter == null) {
				Toast.makeText(getActivity(),
						R.string.text_error_bluetooth_not_supported,
						Toast.LENGTH_SHORT).show();
				return false;

			}

			// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
			if (!mBluetoothAdapter.isEnabled()) {
				openBluetooth();
				return false;
			}

			return true;
		}
		return false;
	}

	private void radarAnim() {

		radar_rotate = (ImageView) getActivity().findViewById(
				R.id.bg_radar_rotate_img);
		final Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_anim);
		anim.setFillAfter(true);
		radar_rotate.startAnimation(anim);
	}

	private void openBluetooth() {
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	private void closeBluetooth() {
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.disable();// 关闭蓝牙

		}

	}

	public static void removeDuplicate(ArrayList<Child> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {
					list.remove(j);
				}
			}
		}
		// System.out.println(list);
	}

	public static void removeDuplicateList(ArrayList<Child> list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
		// System.out.println(list);
	}

	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BEEPTIMEOUT:
				if (BleServicesService.intentToChara != null)
					getActivity().stopService(BleServicesService.intentToChara);
				getActivity().stopService(beepIntent);
				SharePrefsUtils.setfinishBeep(getActivity(), false);

				beepTime = 0;
				BeepCheckTimeOutTask.cancel();

				switch (SharePrefsUtils.DeviceConnectStatus(getActivity())) {
				case Constants.DEVICE_CONNECT_STATUS_SUCCESS:
					// device status
					SharePrefsUtils.setDeviceConnectStatus(getActivity(),
							Constants.DEVICE_CONNECT_STATUS_DEFAULT);
					break;

				default:
					// device status
					SharePrefsUtils.setDeviceConnectStatus(getActivity(),
							Constants.DEVICE_CONNECT_STATUS_ERROR);
					break;
				}
				break;

			case SCAN_CHILD_FOR_LIST:
				// device status
				if (SharePrefsUtils.DeviceConnectStatus(getActivity()) == Constants.DEVICE_CONNECT_STATUS_ERROR) {

					if (deviceStatusError == 1) {
						SharePrefsUtils.setDeviceConnectStatus(getActivity(),
								Constants.DEVICE_CONNECT_STATUS_DEFAULT);
						deviceStatusError = 0;
					}
					deviceStatusError++;
				}

				ScanedTempChildData = (ArrayList<Child>) ScanedChildData
						.clone();

				// removeDuplicate(ScanedTempChildData);
				try {
					removeDuplicateList(ScanedTempChildData);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// removeDuplicateList(ScanedTempChildData);
					e.printStackTrace();
				}

				if (ScanedTempChildData.size() >= 0) {
					BeepTempChildData = (ArrayList<Child>) ScanedTempChildData
							.clone();
					// beep all the device
					BeepAllTempChildData = (ArrayList<Child>) ScanedTempChildData
							.clone();
				}

				if (SharePrefsUtils.isInitHead(getActivity())) {
					SharePrefsUtils.setInitHead(getActivity(), false);
				} else {

					if (ScanedChildDataHeadNum >= 0) {
						removeImageHead(ScanedChildDataHeadNum);
					}

				}
				//
				// System.out.println("ScanedChildData1=>"
				// + ScanedTempChildData.size());
				//
				if (ScanedTempChildData != null) {
					unMissingChildNumTxt.setText(ScanedTempChildData.size()
							+ "");
				}
				if (ScanedTempChildData != null) {
					addImageHead(ScanedTempChildData);
					ScanedChildDataHeadNum = ScanedTempChildData.size();

				}

				// System.out.println("ScanedChildData2=>"
				// + ScanedTempChildData.size());

				MissChildData = DBChildren.getChildrenList(getActivity());
				for (int x = 0; x < ScanedTempChildData.size(); x++) {
					for (int y = 0; y < MissChildData.size(); y++) {
						if (ScanedTempChildData.get(x).getChildId() == MissChildData
								.get(y).getChildId()) {
							MissChildData.remove(y);
							break;
						}

					}
				}

				// System.out.println("MissChildDataMissChildData=>"
				// + MissChildData.size());

				// System.out.println("ScanedChildData3=>"
				// + ScanedTempChildData.size());

				if (MissChildData != null) {
					missingChildNumTxt.setText(MissChildData.size() + "");
				}

				if (SharePrefsUtils.isInitHead(getActivity())) {
					SharePrefsUtils.setInitHead(getActivity(), false);
				} else {
					if (MissChildDataHeadNum >= 0) {
						removeMissImageHead(MissChildDataHeadNum);
					}

				}

				if (MissChildData != null) {
					addMissImageHead(MissChildData);
					MissChildDataHeadNum = MissChildData.size();

				}

				chageTheAllData(ScanedTempChildData, MissChildData);
				// radar頭像

				clearAlltheDate();

			}

			// case SCAN_CHILD_FOR_LIST:
			// // device status
			// if (SharePrefsUtils.DeviceConnectStatus(getActivity()) ==
			// Constants.DEVICE_CONNECT_STATUS_ERROR) {
			// deviceStatusError++;
			// if (deviceStatusError == 2) {
			// SharePrefsUtils.setDeviceConnectStatus(getActivity(),
			// Constants.DEVICE_CONNECT_STATUS_DEFAULT);
			// deviceStatusError = 0;
			// }
			// }
			//
			// ScanedTempChildData = (ArrayList<Child>) ScanedChildData
			// .clone();
			//
			// // removeDuplicate(ScanedTempChildData);
			// try {
			// removeDuplicateList(ScanedTempChildData);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// // removeDuplicateList(ScanedTempChildData);
			// e.printStackTrace();
			// }
			//
			// if (ScanedTempChildData.size() >= 0) {
			// BeepTempChildData = (ArrayList<Child>) ScanedTempChildData
			// .clone();
			// // beep all the device
			// BeepAllTempChildData = (ArrayList<Child>) ScanedTempChildData
			// .clone();
			// }
			//
			// if (SharePrefsUtils.isInitHead(getActivity())) {
			// SharePrefsUtils.setInitHead(getActivity(), false);
			// } else {
			//
			// if (ScanedChildDataHeadNum >= 0) {
			// removeImageHead(ScanedChildDataHeadNum);
			// }
			//
			// }
			// //
			// // System.out.println("ScanedChildData1=>"
			// // + ScanedTempChildData.size());
			// //
			// if (ScanedTempChildData != null) {
			// unMissingChildNumTxt.setText(ScanedTempChildData.size()
			// + "");
			// }
			//
			// // System.out.println("ScanedChildData2=>"
			// // + ScanedTempChildData.size());
			//
			// MissChildData = DBChildren.getChildrenList(getActivity());
			// for (int x = 0; x < ScanedTempChildData.size(); x++) {
			// for (int y = 0; y < MissChildData.size(); y++) {
			// if (ScanedTempChildData.get(x).getChildId() == MissChildData
			// .get(y).getChildId()) {
			// MissChildData.remove(y);
			// break;
			// }
			//
			// }
			// }
			//
			// // System.out.println("MissChildDataMissChildData=>"
			// // + MissChildData.size());
			//
			// // System.out.println("ScanedChildData3=>"
			// // + ScanedTempChildData.size());
			//
			// if (MissChildData != null) {
			// missingChildNumTxt.setText(MissChildData.size() + "");
			// }
			//
			// if (SharePrefsUtils.isInitHead(getActivity())) {
			// SharePrefsUtils.setInitHead(getActivity(), false);
			// } else {
			// if (MissChildDataHeadNum >= 0) {
			// removeMissImageHead(MissChildDataHeadNum);
			// }
			//
			// }
			//
			// if (inFirstSetHeadImage) {
			// inFirstSetHeadImage = false;
			// if (ScanedTempChildData != null) {
			// addImageHead(ScanedTempChildData);
			// ScanedChildDataHeadNum = ScanedTempChildData.size();
			//
			// }
			//
			// if (MissChildData != null) {
			// addMissImageHead(MissChildData);
			// MissChildDataHeadNum = MissChildData.size();
			//
			// }
			//
			// scanedHeadChildData = (ArrayList<Child>)
			// ScanedTempChildData.clone();
			// missedHeadChildData = (ArrayList<Child>) MissChildData.clone();
			//
			// }else{
			// //先對比有什麼變化
			//
			//
			// }
			//
			// // }
			//
			// chageTheAllData(ScanedTempChildData, MissChildData);
			// // radar頭像
			//
			// clearAlltheDate();
			//
			// }
		}
	};

	private void clearAlltheDate() {
		if (ScanedTempChildData != null) {
			Iterator<Child> it = ScanedTempChildData.iterator();
			for (; it.hasNext();) {
				it.next();
				it.remove();
			}
		}
		// if (BeepAllTempChildData != null) {
		// Iterator<Child> it1 = BeepAllTempChildData.iterator();
		// for (; it1.hasNext();) {
		// it1.next();
		// it1.remove();
		// }
		// }
		if (MissChildData != null) {
			Iterator<Child> it2 = MissChildData.iterator();
			for (; it2.hasNext();) {
				it2.next();
				it2.remove();
			}
		}
		if (listItem != null) {
			Iterator<HashMap<String, Object>> it3 = listItem.iterator();
			for (; it3.hasNext();) {
				it3.next();
				it3.remove();
			}
		}
		if (mLeDevices != null) {
			Iterator<BluetoothDevice> it4 = mLeDevices.iterator();
			for (; it4.hasNext();) {
				it4.next();
				it4.remove();
			}
		}
		if (ScanedChildData != null) {
			Iterator<Child> it5 = ScanedChildData.iterator();
			for (; it5.hasNext();) {
				it5.next();
				it5.remove();
			}
		}
	}

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			// System.out.println("mLeScanCallback");
			BLEScanThread = new Thread(new Runnable() {

				@SuppressWarnings("unchecked")
				@Override
				public void run() {

					// System.out.println("rssi=>" + rssi);
					//
					// System.out.println("rssi=>" + rssi + " device = >"
					// + device.getName() + " " + device.getAddress());
					// onStartToBeepClicked();
					// startToBeep();
					// System.out
					// .println(" BluetoothAdapter.LeScanCallback mLeScanCallback ");

					Device newDevice = new Device();
					newDevice.setAddress(device.getAddress());
					newDevice.setMajor(scanRecord[25] * 256 + scanRecord[26]);
					newDevice.setMinor(scanRecord[27] * 256 + scanRecord[28]);
					newDevice.setName(device.getName());
					newDevice.setUuid(bytesToHex(scanRecord, 9, 16));
					newDevice.setRssi(rssi);
					// if (bytesToHex(scanRecord, 9, 16).equals(
					// Constants.OURDEVICEUUID)) {

					if (bytesToHex(scanRecord, 9, 16).equals(
							Constants.DEVICE_UUID_VERSON_1)
							|| bytesToHex(scanRecord, 9, 16).substring(8, 32)
									.equals(Constants.DEVICE_UUID_VERSON_2)) {
						if (deviceMap.put(device.getAddress(), newDevice) != null) {
							Iterator<Entry<String, Device>> it = deviceMap
									.entrySet().iterator();
							listItem.clear();

							// MissChildData.clear();
							while (it.hasNext()) {
								HashMap<String, Object> map = new HashMap<String, Object>();

								Map.Entry<String, Device> entry = it.next();

								// System.out.println("device.getAddress()"
								// + device.getAddress() + " "
								// + ChildData.size());

								for (int i = 0; i < ChildData.size(); i++) {
									Child child = ChildData.get(i);
									// System.out.println("ChildData.size()>"
									// + ChildData.size());
									// System.out.println("child.getMacAddress()=>"
									// + child.getMacAddress());

									if (child.getMacAddress().equals(
											device.getAddress())) {

										if (openAnti) {
											RSSIforBeep(rssi, device);
										}

										// System.out
										// .println("child.getMacAddress()=>"
										// + child.getMacAddress());
										map.put("image", R.drawable.ble_icon);
										map.put("title", entry.getValue()
												.getName());
										map.put("text", entry.getValue()
												.getAddress()
												+ " UUID:"
												+ entry.getValue().getUuid()
												+ " MajorID:"
												+ entry.getValue().getMajor()
												+ " MinorID:"
												+ entry.getValue().getMinor()
												+ " RSSI:"
												+ entry.getValue().getRssi());
										// System.out.println("childchildchild+"
										// + child.getMacAddress());
										ScanedChildData.add(child);

										listItem.add(map);
										mLeDevices.add(device);

									}

								}

							}

						}

					}

				}

			});
			BLEScanThread.start();
		}
	};

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeDuplicateWithOrder(List list) {
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		list.clear();
		list.addAll(newList);
		// System.out.println(" remove duplicate " + list);
	}

	public static String bytesToHex(byte[] bytes, int begin, int length) {
		StringBuilder sbuf = new StringBuilder();
		for (int idx = begin; idx < begin + length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10)
				sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase());
		}
		return sbuf.toString();
	}

	/**
	 * 如果beeptime等于10秒则停止连接service
	 */
	@Override
	public void onStartToBeepClicked(int position) {
		// TODO Auto-generated method stub
		// startToBeepThread.start();
		BeepCheckTimeOutTask = new TimerTask() {
			public void run() {
				beepTime++;
				System.out.println("beepTime==>" + beepTime);
				if (beepTime == 10) {
					Message msg = handler.obtainMessage();
					msg.what = BEEPTIMEOUT;
					handler.sendMessage(msg);

				}
			}
		};

		if (isFirstBeep) {
			timer.schedule(BeepCheckTimeOutTask, 0, 1000);
		} else {
			if (SharePrefsUtils.isfinishBeep(getActivity())) {
				getActivity().stopService(BleServicesService.intentToChara);
				getActivity().stopService(beepIntent);

			}

			Timer timer = new Timer();

			timer.schedule(BeepCheckTimeOutTask, 0, 1000);

		}
		// 重置为0
		beepTime = 0;
		startToBeep(position);
	}

	/**
	 * 現在少一層device address判斷 會有bug
	 * 
	 * @param position
	 * 
	 * @param position
	 */
	@SuppressLint("NewApi")
	public void startToBeep(int position) {

		System.out.println("BeepTempChildData.size()==>"
				+ BeepTempChildData.size());
		if (BeepTempChildData.size() > 0) {
			isFirstBeep = false;

			beepIntent = new Intent();

			beepIntent.putExtra(BleServicesService.EXTRAS_DEVICE_NAME,
					"Macaron");
			beepIntent.putExtra(BleServicesService.EXTRAS_DEVICE_ADDRESS,
					BeepTempChildData.get(position).getMacAddress());
			System.out
					.println("BeepTempChildData.get(position).getMacAddress()==>"
							+ BeepTempChildData.get(position).getMacAddress());
			beepIntent
					.setAction("com.twinly.eyebb.service.BLE_SERVICES_SERVICES");
			// if (scan_flag) {
			// scanLeDevice(false);
			// }
			SharePrefsUtils.setConnectBleService(getActivity(), 1);
			SharePrefsUtils.setfinishBeep(getActivity(), true);

			// device status
			SharePrefsUtils.setDeviceConnectStatus(getActivity(),
					Constants.DEVICE_CONNECT_STATUS_LOADING);

			SharePrefsUtils.setKeepDeviceConnectStatus(getActivity(),
					BeepTempChildData.get(position).getName());

			getActivity().startService(beepIntent);

		}

	}

	// 應該加入頭像

	private void RSSIforBeep(int rssi, BluetoothDevice device) {
		// TODO Auto-generated method stub

		if (rssi < Constants.BEEP_RSSI) {

			beepAllTime++;
			// System.out.println("beepAllTime=>" + beepAllTime);
			if (beepAllTime == 10) {

				Intent beepForAntiIntent = new Intent();

				beepForAntiIntent.setClass(getActivity(),
						RadarOutOfRssiBeepDialog.class);

				startActivity(beepForAntiIntent);

			}

		} else {

			beepAllTime = 0;

			if (RadarOutOfRssiBeepDialog.isStart) {
				if (RadarOutOfRssiBeepDialog.instance != null) {
					RadarOutOfRssiBeepDialog.instance.finish();

					RadarOutOfRssiBeepDialog.isStart = false;
				}

			}

		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		System.out.println("This is:" + thread.getName() + ",Message:"
				+ ex.getMessage());
		ex.printStackTrace();
	}
}