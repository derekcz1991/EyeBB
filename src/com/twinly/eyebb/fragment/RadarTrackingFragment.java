package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
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
import com.google.android.gms.drive.internal.v;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter.RadarKidsListViewAdapterCallback;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.bluetooth.RadarServicesActivity;
import com.twinly.eyebb.bluetooth.ServicesActivity;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.customview.LoadingDialog;

import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.DensityUtil;
import com.twinly.eyebb.utils.SharePrefsUtils;

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
	private static final int SCANTIME = 5000;
	private static final int POSTDELAYTIME = 4500;
	private Handler mHandler;
	private Handler autoScanHandler;
	private final static int START_SCAN = 4;
	private final static int STOP_SCAN = 5;
	private final static int DELETE_SCAN = 6;
	private final static int STATERTOBEEP = 7;
	private String UUID;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
	List<String> UUID_temp = new ArrayList<String>();

	// SimpleAdapter Childadapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件

	private LinearLayoutForListView ChildlistView;
	private RadarKidsListViewAdapter Childadapter;
	private ScrollView radarScrollView;
	// private CircleImageView circleImageView;
	private View RadarView;

	private ArrayList<Child> ChildData;
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

	private boolean startToScan = false;
	private Device newDevice;

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");

		MajorAndMinorPreferences = getActivity().getSharedPreferences(
				"MajorAndMinor", getActivity().MODE_PRIVATE);
		editor = MajorAndMinorPreferences.edit();

		View v = inflater.inflate(R.layout.fragment_radar_tracking, container,
				false);

		ChildlistView = (LinearLayoutForListView) v
				.findViewById(R.id.radar_children_list);
		Childadapter = new RadarKidsListViewAdapter(getActivity(),
				DBChildren.getChildrenList(getActivity()));
		Childadapter.setCallback(this);

		ChildlistView.setAdapter(Childadapter);
		ChildlistView.setClickable(false);

		RadarView = v.findViewById(R.id.radar_view);

		radarBeepAllBtn = v.findViewById(R.id.radar_beep_all_btn);

		radarScrollView = (ScrollView) v.findViewById(R.id.radar_scrollview);
		radarScrollView.smoothScrollTo(0, 0);

		missingChildNumTxt = (TextView) v
				.findViewById(R.id.radar_text_missed_number);
		unMissingChildNumTxt = (TextView) v
				.findViewById(R.id.radar_text_supervised_number);

		confirmRadarBtn = (TextView) v.findViewById(R.id.confirm_radar_btn);

		mHandler = new Handler();
		autoScanHandler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();

		if (SharePrefsUtils.getRole(getActivity()) == false) {

		} else {
			((TextView) v.findViewById(R.id.radar_text_missed_number))
					.setText("3");
		}

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
					isClickConnection = true;
					isWhileLoop = true;

					ChildlistView.setAlpha(1);
					RadarView.setAlpha(1);

					ChildlistView.setClickable(true);

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

					ChildlistView.setAlpha((float) 0.3);
					RadarView.setAlpha((float) 0.3);
					scan_flag = false;
					// autoScanHandler.removeCallbacks(autoScan);
					// mHandler.removeCallbacks(scanLeDeviceRunable);
					// mBluetoothAdapter.stopLeScan(mLeScanCallback);
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

		addImageHead(v);
		return v;

	}

	@SuppressLint("ResourceAsColor")
	private void addImageHead(View v) {
		// 手动添加imageview
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();
		ChildData = DBChildren.getChildrenList(getActivity());
		for (int i = 0; i < ChildData.size(); i++) {
			Child child = ChildData.get(i);

			RelativeLayout mainLayout = (RelativeLayout) v
					.findViewById(R.id.radar_view);

			CircleImageView cim = new CircleImageView(getActivity());

			// 0 is missing 1 is unmiss
			int imMiss = 0;
			HeadPosition(imMiss, cim);

			// Uri uri = Uri.parse(child.getIcon());
			// ImageView cim = new ImageView(getActivity());
			// layoutParams.leftMargin = 30;
			// layoutParams.topMargin = 100;

			mainLayout.addView(cim);
			// AsyncImageLoader.setImageViewFromUrl(child.getIcon(), cim);
			imageLoader.displayImage(child.getIcon(), cim, options, null);

		}

	}

	@SuppressLint("NewApi")
	private void HeadPosition(int imMiss, CircleImageView cim) {
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
			missingChildNum = "2";
			missingChildNumTxt.setText(missingChildNum);

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
				HeadPosition(imMiss, cim);
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

				System.out.println("initX + initY :" + initX + " " + initY);
				cim.setX(initX);
				cim.setY(initY);
			} else {
				HeadPosition(imMiss, cim);
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

		System.out.println("onResume");
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

	// private void addItem(String devname, String address) {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("image", R.drawable.ble_icon);
	// map.put("title", devname);
	// map.put("text", address);
	// listItem.add(map);
	// Childadapter.notifyDataSetChanged();
	// }

	private void deleteItem() {
		int size = listItem.size();
		if (size > 0) {

			listItem.remove(listItem.size() - 1);
			Childadapter.notifyDataSetChanged();
		}
		// mLeDevices.clear();
	}

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
				Childadapter.notifyDataSetChanged();
				mLeDevices.clear();
				break;

			}
		}
	};

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					int majorid, minorid;
					System.out.println("rssi=>" + rssi);

					// System.out.println("device = >" + device.getName() + " "
					// + device.getAddress());
					// onStartToBeepClicked();
					// startToBeep();
					startToScan = true;
					RSSIforBeep(rssi, device);

					newDevice = new Device();
					newDevice.setAddress(device.getAddress());
					newDevice.setMajor(scanRecord[25] * 256 + scanRecord[26]);
					newDevice.setMinor(scanRecord[27] * 256 + scanRecord[28]);
					newDevice.setName(device.getName());
					newDevice.setUuid(bytesToHex(scanRecord, 9, 16));
					newDevice.setRssi(rssi);
					// if (bytesToHex(scanRecord, 9, 16).equals(
					// Constants.OURDEVICEUUID)) {
					if (deviceMap.put(device.getAddress(), newDevice) != null) {
						Iterator<Entry<String, Device>> it = deviceMap
								.entrySet().iterator();
						// listItem.clear();
						while (it.hasNext()) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							Map.Entry<String, Device> entry = it.next();
							map.put("image", R.drawable.ble_icon);
							map.put("title", entry.getValue().getName());
							map.put("text", entry.getValue().getAddress()
									+ " UUID:" + entry.getValue().getUuid()
									+ " MajorID:" + entry.getValue().getMajor()
									+ " MinorID:" + entry.getValue().getMinor()
									+ " RSSI:" + rssi);
							listItem.add(map);
							mLeDevices.add(device);
						}
						Childadapter.notifyDataSetChanged();
					}
				}

				// }

			});
			thread.start();
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

	private void startToBeep(int position) {

		// System.out
		// .println("deviceMap.get(mLeDevices.get(position).getAddress()).getRssi()=>"
		// + deviceMap.get(mLeDevices.get(position).getAddress())
		// .getRssi());
		// System.out.println("startToScan=>" + startToScan);

		if (startToScan) {
			startToScan = false;
			editor.putInt("runNumRadar", 1);
			editor.commit();

			Intent beepIntent = new Intent();

			beepIntent.setClass(getActivity(), RadarServicesActivity.class);

			beepIntent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_NAME,
					"Macaron");
			beepIntent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_ADDRESS,
					"44:A6:E5:00:04:E2");
			if (scan_flag) {
				scanLeDevice(false);
			}

			startActivity(beepIntent);

		}

	}

	@SuppressWarnings("static-access")
	private void RSSIforBeep(int rssi, BluetoothDevice device) {
		// TODO Auto-generated method stub
		SandVpreferences = getActivity().getSharedPreferences(
				"soundAndVibrate", getActivity().MODE_PRIVATE);
		Boolean isStart = false;
		isStart = SandVpreferences.getBoolean("isStartBeepDialog", false);

		if (rssi < Constants.BEEP_RSSI) {
			if (device.getAddress().equals("44:A6:E5:00:04:E2")) {
				editor.putInt("runNumRadar", 1);
				editor.commit();

				final Intent intent = new Intent();

				// intent.setClass(getActivity(), RadarServicesActivity.class);
				intent.setClass(getActivity(), BeepDialog.class);

				intent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_NAME,
						device.getName());
				intent.putExtra(RadarServicesActivity.EXTRAS_DEVICE_ADDRESS,
						device.getAddress());

				if (scan_flag) {
					scanLeDevice(false);
				}

				startActivity(intent);
			}
		} else {
			if (isStart) {
				BeepDialog.instance.finish();
				editor = SandVpreferences.edit();
				editor.putBoolean("isStartBeepDialog", false);
				editor.commit();
			}

		}
	}

}
