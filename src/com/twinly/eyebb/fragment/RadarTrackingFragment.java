package com.twinly.eyebb.fragment;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.adapter.MissRadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter.RadarKidsListViewAdapterCallback;
import com.twinly.eyebb.bluetooth.RadarServicesActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.DensityUtil;

public class RadarTrackingFragment extends Fragment implements
		RadarKidsListViewAdapterCallback {
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
	// private static final int SCANTIME = 100000;
	// private static final int POSTDELAYTIME = 95000;
	private static final int SCANTIME = 5000;
	private static final int POSTDELAYTIME = 4500;
	private Handler mHandler;
	private Handler autoScanHandler;
	private final static int START_SCAN = 4;
	private final static int STOP_SCAN = 5;
	private final static int DELETE_SCAN = 6;
	private final static int SOS_TASK = 7;
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

	private SharedPreferences MajorAndMinorPreferences;
	private SharedPreferences SandVpreferences;
	private SharedPreferences.Editor editor;
	// device map
	private HashMap<String, Device> deviceMap = new HashMap<String, Device>();;

	private Device newDevice;
	private ArrayList<Child> ChildData;
	private ArrayList<Child> ScanedChildData;
	private ArrayList<Child> HeadImageChildData;
	private ArrayList<Child> TempHeadImageChildData;

	private ArrayList<String> ScanedChildDataID;
	private Child child;
	private ArrayList<Child> MissChildData;
	private ArrayList<String> MissChildDataID;
	private ArrayList<Child> MissHeadImageChildData;
	private ArrayList<Child> MissTempHeadImageChildData;
	private View v;
	private Boolean firstAddImageHead = true;
	private Boolean MissfirstAddImageHead = true;
	private static RadarTrackingFragment RadarTrackingFragmentInstance = null;
	RelativeLayout InitMainLayout = null;
	RelativeLayout MissMainLayout = null;
	RelativeLayout mainLayout = null;

	private boolean isInit = true;
	private boolean isInitHead = true;
	private int keepTim = 0;
	Timer timer = new Timer(true);

	@SuppressWarnings("static-access")
	@SuppressLint({ "NewApi", "CutPasteId" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");

		MajorAndMinorPreferences = getActivity().getSharedPreferences(
				"MajorAndMinor", getActivity().MODE_PRIVATE);
		editor = MajorAndMinorPreferences.edit();

		v = inflater
				.inflate(R.layout.fragment_radar_tracking, container, false);
		RadarTrackingFragmentInstance = this;

		ChildData = DBChildren.getChildrenList(getActivity());
		ChildlistView = (LinearLayoutForListView) v
				.findViewById(R.id.radar_children_list);
		MissChildlistView = (LinearLayoutForListView) v
				.findViewById(R.id.radar_children_list_miss);
		// listview初始化為看不見
		ChildlistView.setVisibility(View.GONE);
		MissChildlistView.setVisibility(View.GONE);
		ScanedChildData = new ArrayList<Child>();
		MissChildData = new ArrayList<Child>();

		ScanedChildDataID = new ArrayList<String>();
		MissChildDataID = new ArrayList<String>();

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

		mHandler = new Handler();
		autoScanHandler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();

		// bluetooth
		// connect device
		// btnConfirm = v.findViewById(R.id.btn_confirm);
		// btnCancel = v.findViewById(R.id.btn_cancel);
		btnStatus = v.findViewById(R.id.connection_status_btn);

		radarBeepAllBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isClickConnection) {
					if (CommonUtils.isFastDoubleClick()) {
						return;
					} else {
						Intent intent = new Intent(getActivity(),
								BeepDialog.class);
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
				if (!isConfirmRadarBtn) {
					isConfirmRadarBtn = true;
					confirmRadarBtn.setBackground(getResources().getDrawable(
							R.drawable.ic_selected));

					try {
						checkIsBluetooth();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 阻止底部button點擊
					isClickConnection = true;
					// 開始循環掃描
					isWhileLoop = true;

					//

					// 顯示list
					ChildlistView.setVisibility(View.VISIBLE);
					MissChildlistView.setVisibility(View.VISIBLE);
					// list透明為正常
					ChildlistView.setAlpha(1);
					MissChildlistView.setAlpha(1);
					RadarView.setAlpha(1);

					radarAnim();

					// bluetooth

					if (scan_flag) {
						System.out
								.println("autoScanHandler.postDelayed(autoScan, POSTDELAYTIME);");
						autoScanHandler.postDelayed(autoScan, POSTDELAYTIME);
					} else {
						new Thread(autoScan).start();
					}
				} else {
					isConfirmRadarBtn = false;
					confirmRadarBtn.setBackground(getResources().getDrawable(
							R.drawable.ic_selected_off));

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

				}

			}
		});

		btnStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().findViewById(R.id.connect_device_layout)
						.setVisibility(View.VISIBLE);
				isClickConnection = false;

			}
		});

		return v;

	}

	private void chageTheAllData() {

		for (int i = 0; i < ScanedChildData.size(); i++) {
			ScanedChildDataID.add(ScanedChildData.get(i).getChildId() + "");

		}

		for (int i = 0; i < MissChildData.size(); i++) {
			MissChildDataID.add(MissChildData.get(i).getChildId() + "");
		}

		Childadapter = new RadarKidsListViewAdapter(getActivity(),
				ScanedChildData, ScanedChildDataID);
		Childadapter.setCallback(RadarTrackingFragmentInstance);
		ChildlistView.setAdapter(Childadapter);
		MissChildadapter = new MissRadarKidsListViewAdapter(getActivity(),
				MissChildData);
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
			mainLayout = (RelativeLayout) v.findViewById(R.id.radar_view);
			CircleImageView cim = new CircleImageView(getActivity());
			// 0 is missing 1 is unmiss
			int imMiss = 1;
			HeadPosition(imMiss, cim, scanedChildData2.size());

			mainLayout.addView(cim);
			// AsyncImageLoader.setImageViewFromUrl(child.getIcon(), cim);
			imageLoader.displayImage(child.getIcon(), cim, options, null);

		}

		return scanedChildData2.size();

	}

	private void removeImageHead(int scanedChildDataNum) {

		try {
			mainLayout.removeViews(1, scanedChildDataNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			// 0 is missing 1 is unmiss
			int imMiss = 0;
			HeadPosition(imMiss, MissCim, missChildData2.size());

			MissMainLayout.addView(MissCim);
			// AsyncImageLoader.setImageViewFromUrl(child.getIcon(), cim);
			imageLoader.displayImage(child.getIcon(), MissCim, options, null);

		}

		return missChildData2.size();

	}

	private void removeMissImageHead(int missChildDataNum) {

		try {
			System.out.println("number=> " + missChildDataNum);
			MissMainLayout.removeViews(1, missChildDataNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void HeadPosition(int imMiss, CircleImageView cim, int getPeople) {
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

		if (imMiss == 0) {

			cim.setBorderColor(getResources().getColor(R.color.red));
			cim.setBorderWidth(DensityUtil.dip2px(getActivity(), 2));

			int missX = (int) (Math.random() * 120);
			int missY = (int) (Math.random() * 120);

			// System.out.println("addY + addx :" + missX * missX + " " + missY
			// * missY + " " + DensityUtil.dip2px(getActivity(), 14400)
			// + " " + DensityUtil.dip2px(getActivity(), 10000) + "  "
			// + 90 + (int) (Math.random() * 30));
			if ((missX * missX + missY * missY) < 14000
					&& (missX * missX + missY * missY) > 10000) {
				addX = DensityUtil.dip2px(getActivity(), missX);
				addY = DensityUtil.dip2px(getActivity(), missY);
				// System.out.println("啊啊啊啊啊啊 ");

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

				System.out.println("initX + initY :" + initX + " " + initY);

				cim.setX(initX);
				cim.setY(initY);
			} else {
				HeadPosition(imMiss, cim, getPeople);
			}

		} else if (imMiss == 1) {
			cim.setBorderColor(getResources().getColor(R.color.white));
			cim.setBorderWidth(DensityUtil.dip2px(getActivity(), 2));

			addX = DensityUtil.dip2px(getActivity(),
					(int) (Math.random() * 100));
			addY = DensityUtil.dip2px(getActivity(),
					(int) (Math.random() * 100));

			if ((addX * addX + addY * addY) <= DensityUtil.dip2px(
					getActivity(), 7225)) {
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
				HeadPosition(imMiss, cim, getPeople);
			}

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

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// ScanedChildData.clear();
		// MissChildData.clear();
		listItem.clear();
		mLeDevices.clear();
		final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		// if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
		// openBluetooth();
		// }

		if (mBluetoothAdapter.isEnabled()) {
			// radarAnim();

		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("onStart");

	}

	private int getVersionAPI() throws Exception {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		return currentapiVersion;
	}

	@SuppressLint("NewApi")
	public void checkIsBluetooth() throws NotFoundException, Exception {
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (getVersionAPI() >= BLE_VERSION) {
			System.out.println("checkIsBluetooth");
			if (!getActivity().getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				Toast.makeText(getActivity(), R.string.text_ble_not_supported,
						Toast.LENGTH_SHORT).show();

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

			}

			// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
			if (!mBluetoothAdapter.isEnabled()) {
				openBluetooth();
			} else {

				// if (isFirstDialog) {
				// Intent intent = new Intent(getActivity(),
				// ConnectDeviceDialog.class);
				// startActivity(intent);
				// isFirstDialog = false;
				// }

			}
		}
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

	// 下面為bluetooth
	Runnable autoScan = new Runnable() {
		@Override
		public void run() {
			while (isWhileLoop) {
				if (scan_flag) {

					scanLeDevice(false);

				} else {

					scanLeDevice(true);
					try {
						Thread.sleep(SCANTIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	};

	Runnable scanLeDeviceRunable = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			// scan_flag = false;

			// HANDLER
			Message msg = handler.obtainMessage();
			msg.what = START_SCAN;
			handler.sendMessage(msg);

			// new autoConnection().start();
			// autoScan.start();

		}
	};

	@SuppressLint("NewApi")
	public void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(scanLeDeviceRunable, POSTDELAYTIME);

			mBluetoothAdapter.startLeScan(mLeScanCallback);

			scan_flag = true;

			Message msg = handler.obtainMessage();
			msg.what = STOP_SCAN;
			handler.sendMessage(msg);
			// scanBtn.setText(R.string.stop_scan);

		} else {

			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			scan_flag = false;

			Message msg = handler.obtainMessage();
			msg.what = DELETE_SCAN;
			handler.sendMessage(msg);

			// autoScan.start();
		}

	}

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_SCAN:
				// scanBtn.setText("開始");
				// scanBtn.setText("start scan");
				break;

			case STOP_SCAN:
				// scanBtn.setText("結束");
				// scanBtn.setText("stop scan");
				break;

			case DELETE_SCAN:
				listItem.clear();
				mLeDevices.clear();
				// ScanedChildData.clear();
				// MissChildData.clear();
				// 改變所有數據
				// chageTheAllData();

				break;

			case SCAN_CHILD_FOR_LIST:

				removeDuplicateWithOrder(ScanedChildData);
				for (int i = 0; i < ScanedChildData.size(); ++i)
					for (int j = i + 1; j < ScanedChildData.size(); ++j) {
						if (ScanedChildData.get(i).equals(
								ScanedChildData.get(j)))
							ScanedChildData.remove(j);
					}

				MissChildData = DBChildren.getChildrenList(getActivity());

				for (int x = 0; x < ScanedChildData.size(); x++) {
					for (int y = 0; y < MissChildData.size(); y++) {
						if (ScanedChildData.get(x).getChildId() == MissChildData
								.get(y).getChildId()) {
							MissChildData.remove(y);
							break;
						}

					}
				}

				System.out.println("MissChildDataMissChildData=>"
						+ MissChildData.size());

				if (isInitHead) {

				} else {
					if (MajorAndMinorPreferences.getInt("MissChildData", 0) > 0)
						removeMissImageHead(MajorAndMinorPreferences.getInt(
								"MissChildData", 0));
					if (MajorAndMinorPreferences.getInt("ScanedChildData", 0) > 0)
						removeImageHead(MajorAndMinorPreferences.getInt(
								"ScanedChildData", 0));

				}

				isInitHead = false;

				if (MissChildData.size() >= 0)
					missingChildNumTxt.setText(MissChildData.size() + "");
				if (ScanedChildData.size() >= 0)
					unMissingChildNumTxt.setText(ScanedChildData.size() + "");

				if (ScanedChildData.size() > 0) {
					addImageHead(ScanedChildData);
					editor.putInt("ScanedChildData", ScanedChildData.size());
					// System.out.println("AAA=>" + MissChildData.size() + " "
					// + MajorAndMinorPreferences.getInt("MissChildData", 0));
				}
				if (MissChildData.size() > 0) {
					addMissImageHead(MissChildData);
					editor.putInt("MissChildData", MissChildData.size());
				}

				editor.commit();
				chageTheAllData();
				// radar頭像
				ScanedChildData.clear();
				MissChildData.clear();

			}
		}
	};

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {

			Thread thread = new Thread(new Runnable() {

				@SuppressWarnings("unchecked")
				@Override
				public void run() {

					int majorid, minorid;
					// System.out.println("rssi=>" + rssi);
					Child radarChild = new Child();
					// System.out.println("device = >" + device.getName() + " "
					// + device.getAddress());
					// onStartToBeepClicked();
					// startToBeep();
					// System.out
					// .println(" BluetoothAdapter.LeScanCallback mLeScanCallback ");

					newDevice = new Device();
					newDevice.setAddress(device.getAddress());
					newDevice.setMajor(scanRecord[25] * 256 + scanRecord[26]);
					newDevice.setMinor(scanRecord[27] * 256 + scanRecord[28]);
					newDevice.setName(device.getName());
					newDevice.setUuid(bytesToHex(scanRecord, 9, 16));
					newDevice.setRssi(rssi);
					// if (bytesToHex(scanRecord, 9, 16).equals(
					// Constants.OURDEVICEUUID)) {

					if (bytesToHex(scanRecord, 9, 16).equals(
							Constants.OURDEVICEUUID)) {
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
									child = ChildData.get(i);
									// System.out.println("ChildData.size()>"
									// + ChildData.size());
									// System.out.println("child.getMacAddress()=>"
									// + child.getMacAddress());

									if (child.getMacAddress().equals(
											entry.getValue().getAddress())) {

										RSSIforBeep(rssi, device);
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
										ScanedChildData.add(child);

										listItem.add(map);
										mLeDevices.add(device);

									}

								}

							}

						}
					}
					if (isInit) {
						// 0 秒钟后开始 5 秒钟为周期 重复执行
						timer.schedule(task, 500, 5000);
						isInit = false;
					}

				}

				// }

			});
			thread.start();
		}
	};

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

	TimerTask task = new TimerTask() {

		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = SCAN_CHILD_FOR_LIST;
			handler.sendMessage(msg);

		}

	};

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

	@Override
	public void onStartToBeepClicked(int position) {
		// TODO Auto-generated method stub
		// startToBeepThread.start();
		startToBeep(position);
	}

	/**
	 * 現在少一層device address判斷 會有bug
	 * 
	 * @param position
	 * 
	 * @param position
	 */
	public void startToBeep(int position) {

		if (ScanedChildData.size() > 0) {

			editor.putInt("runNumRadar", 1);
			editor.commit();

			editor.putBoolean("writeCharaSuccess", false);
			editor.commit();

			Intent beepIntent = new Intent();

			beepIntent.setClass(getActivity(), RadarServicesActivity.class);

			beepIntent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_NAME,
					"Macaron");
			beepIntent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_ADDRESS,
					ScanedChildData.get(position).getMacAddress());
			if (scan_flag) {
				scanLeDevice(false);
			}

			startActivity(beepIntent);
		}

	}

	// 應該加入頭像
	@SuppressWarnings("static-access")
	private void RSSIforBeep(int rssi, BluetoothDevice device) {
		// TODO Auto-generated method stub

		SandVpreferences = getActivity().getSharedPreferences(
				"soundAndVibrate", getActivity().MODE_PRIVATE);
		Boolean isStart = false;
		isStart = SandVpreferences.getBoolean("isStartBeepDialog", false);

		if (rssi < Constants.BEEP_RSSI) {

			keepTim++;
			System.out.println("keepTim=>" + keepTim);
			if (keepTim == 2) {
				if (scan_flag) {
					scanLeDevice(false);
				}
				editor.putInt("runNumRadar", 1);
				editor.commit();
				final Intent SOSintent = new Intent();
				// intent.setClass(getActivity(), RadarServicesActivity.class);
				SOSintent.setClass(getActivity(), BeepDialog.class);

				SOSintent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_NAME,
						device.getName());
				SOSintent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_ADDRESS,
						device.getAddress());

				startActivity(SOSintent);
			}

		} else {

			keepTim = 0;

			if (isStart) {
				if (BeepDialog.instance != null) {
					BeepDialog.instance.finish();
					editor = SandVpreferences.edit();
					editor.putBoolean("isStartBeepDialog", false);
					editor.commit();
				}

			}

		}
	}
}
