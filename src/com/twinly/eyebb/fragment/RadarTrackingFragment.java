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
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.BeepAllForRadarDialog;
import com.twinly.eyebb.activity.RadarOutOfRssiBeepDialog;
import com.twinly.eyebb.activity.RadarShowAllMissImageDialog;
import com.twinly.eyebb.activity.RadarShowAllScanImageDialog;
import com.twinly.eyebb.adapter.MissRadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter.RadarKidsListViewAdapterCallback;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.service.BleServicesService;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class RadarTrackingFragment extends Fragment implements
		RadarKidsListViewAdapterCallback, UncaughtExceptionHandler {

	ArrayList<HashMap<String, Object>> mKidsData;
	ImageView radar_rotate;
	private View radarBeepAllBtn;

	private BluetoothAdapter mBluetoothAdapter;
	private final static int BLE_VERSION = 18;

	private final static int BEEPTIMEOUT = 7;
	private final static int SCAN_CHILD_FOR_LIST = 8;

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

	private TextView missingChildNumTxt;
	private TextView unMissingChildNumTxt;
	private Boolean isClickConnection = false;
	private Boolean isWhileLoop = false;
	private ToggleButton confirmRadarBtn;
	// 判斷confirmRadarBtn是否被點擊
	private Boolean isConfirmRadarBtn = false;

	// device map
	private HashMap<String, Device> deviceMap = new HashMap<String, Device>();;

	private ArrayList<Child> ChildData;
	private ArrayList<Child> ScanedChildData;
	private static ArrayList<Child> ScanedTempChildData;
	private ArrayList<Child> BeepTempChildData;
	private ArrayList<Child> BeepAllTempChildData;

	private ArrayList<Device> myDevice;
	// 檢測頭像是否有改變
	private ArrayList<Child> openAntiData;
	private boolean openAntiCurrentDataFlag;

	// private Child child;
	private ArrayList<Child> MissChildData;

	private View v;
	private static RadarTrackingFragment RadarTrackingFragmentInstance = null;
	RelativeLayout InitMainLayout = null;
	RelativeLayout MissMainLayout = null;
	RelativeLayout mainLayout = null;
	private TimerTask BeepCheckTimeOutTask = null;

	private boolean isFirstBeep = true;

	Timer timer = null;
	TimerTask task = null;

	Thread BLEScanThread;

	public static Intent beepIntent;
	private int beepTime = 0;

	// 開啟防丟器
	private TextView openAntiTheft;
	// private TextView openAntiTheftTX;
	private boolean openAnti = false;

	private int deviceStatusError = 0;

	private CircleImageView scanImg1;
	private CircleImageView scanImg2;
	private CircleImageView scanImg3;
	private CircleImageView scanImg4;
	private CircleImageView scanImg5;
	private CircleImageView scanImg6;
	private CircleImageView scanImg7;
	private CircleImageView scanImg8;
	private CircleImageView scanImg9;

	private CircleImageView missImg1;
	private CircleImageView missImg2;
	private CircleImageView missImg3;
	private CircleImageView missImg4;
	private CircleImageView missImg5;
	private CircleImageView missImg6;

	private RelativeLayout connectDeviceLayout;

	ArrayList<Child> showAllScanImageData;
	ArrayList<Child> showAllMissImageData;

	private UpdateDb updateDb;
	private TextView hint_nodata;
	private boolean first_start_scan_thread_flag = true;
	private boolean get_bluetooth_status;
	private boolean is_support_ble = false;
	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	@SuppressWarnings("static-access")
	@SuppressLint({ "NewApi", "CutPasteId" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");

		v = inflater.inflate(R.layout.fragment_radar_tracking_verson_2,
				container, false);
		RadarTrackingFragmentInstance = this;
		initView(v);

		// 點擊這裡i
		confirmRadarBtn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (BLEUtils.isSupportBle(getActivity())) {
							confirmRadarBtn.setChecked(isChecked);
							// System.out.println("isChecked>" + isChecked);

							if (isChecked) {

								btnConfirmConnect();
								SharePrefsUtils.setfinishBeep(getActivity(),
										false);
							} else {
								btnCancelConnect();
								closeBluetooth();
							}
						} else {
							Message msg = handler.obtainMessage();
							msg.what = BleDeviceConstants.CAN_NOT_SUPPORT_BLE;
							handler.sendMessage(msg);
							System.out.println("AAAAAAAAA");
						}

					}
				});

		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (BLEUtils.isSupportBle(getActivity())) {

			final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
					.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();

			// for (int i = 0; i < ChildData.size(); i++) {
			// System.out.println("child_date-------->"
			// + ChildData.get(i).getMacAddress());
			// }

			updateDb = new UpdateDb();
			// device status
			getActivity().registerReceiver(bluetoothState,
					new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

			listItem = new ArrayList<HashMap<String, Object>>();
			myDevice = new ArrayList<Device>();
			radarBeepAllBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isClickConnection) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									BeepAllForRadarDialog.class);
							intent.putExtra(BleDeviceConstants.BEEP_ALL_DEVICE,
									(Serializable) BeepAllTempChildData);

							SharePrefsUtils.setStartBeepDialog(getActivity(),
									true);
							startActivity(intent);

							BeepAllTempChildData.clear();
						}
					}
				}
			});

			OnClickListener showAllScanImage = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (CommonUtils.isFastDoubleClick()) {
						return;
					} else {
						Intent intent = new Intent(getActivity(),
								RadarShowAllScanImageDialog.class);
						// scanedTempChildData

						System.out.println("ScanedTempChildData=>"
								+ showAllScanImageData.size());
						intent.putExtra("showAllScanImage",
								showAllScanImageData);
						intent.putExtra("showAllScanImageAnti", openAntiData);
						startActivity(intent);

						// showAllScanImageData.clear();

					}

				}
			};

			OnClickListener showAllMissImage = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (CommonUtils.isFastDoubleClick()) {
						return;
					} else {
						Intent intent = new Intent(getActivity(),
								RadarShowAllMissImageDialog.class);
						// missChildData
						System.out.println("MissChildData=>"
								+ showAllMissImageData.size());
						intent.putExtra("showAllMissImage",
								showAllMissImageData);
						intent.putExtra("showAllMissImageAnti", openAntiData);
						startActivity(intent);

						// showAllMissImageData.clear();
					}

				}
			};

			openAntiTheft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (openAnti) {
						closeAntiMode();
					} else {
						openAnti = true;
						openAntiCurrentDataFlag = true;
						openAntiTheft.setBackground(getResources().getDrawable(
								R.drawable.ic_selected));
						// openAntiTheftTX.setText(getResources().getString(
						// R.string.text_radar_status_disconnection));

					}
				}
			});

			/*
			 * initial the children head image postion
			 */
			scanImg1.setOnClickListener(showAllScanImage);
			scanImg2.setOnClickListener(showAllScanImage);
			scanImg3.setOnClickListener(showAllScanImage);
			scanImg4.setOnClickListener(showAllScanImage);
			scanImg5.setOnClickListener(showAllScanImage);
			scanImg6.setOnClickListener(showAllScanImage);
			scanImg7.setOnClickListener(showAllScanImage);
			scanImg8.setOnClickListener(showAllScanImage);
			scanImg9.setOnClickListener(showAllScanImage);
			missImg1.setOnClickListener(showAllMissImage);
			missImg2.setOnClickListener(showAllMissImage);
			missImg3.setOnClickListener(showAllMissImage);
			missImg4.setOnClickListener(showAllMissImage);
			missImg5.setOnClickListener(showAllMissImage);
			missImg6.setOnClickListener(showAllMissImage);

			mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

				@Override
				public void onLeScan(final BluetoothDevice device,
						final int rssi, final byte[] scanRecord) {
					// System.out.println("mLeScanCallback");
					BLEScanThread = new Thread(new Runnable() {

						@SuppressWarnings("unchecked")
						@Override
						public void run() {

							// System.out.println("rssi=>" + rssi);
							//
							// System.out.println("rssi=>" + rssi +
							// " device = >"
							// + device.getName() + " " + device.getAddress());
							// onStartToBeepClicked();
							// startToBeep();
							// System.out
							// .println(" BluetoothAdapter.LeScanCallback mLeScanCallback ");

							Device newDevice = new Device();
							newDevice.setAddress(device.getAddress());
							newDevice.setMajor(scanRecord[25] * 256
									+ scanRecord[26]);
							newDevice.setMinor(scanRecord[27] * 256
									+ scanRecord[28]);
							newDevice.setName(device.getName());
							newDevice.setUuid(bytesToHex(scanRecord, 9, 16));
							newDevice.setRssi(rssi);
							// if (bytesToHex(scanRecord, 9, 16).equals(
							// Constants.OURDEVICEUUID)) {

							if (bytesToHex(scanRecord, 9, 16).equals(
									BleDeviceConstants.DEVICE_UUID_VERSON_1)
									|| bytesToHex(scanRecord, 9, 16)
											.substring(8, 32)
											.equals(BleDeviceConstants.DEVICE_UUID_VERSON_2)) {
								if (deviceMap.put(device.getAddress(),
										newDevice) != null) {
									Iterator<Entry<String, Device>> it = deviceMap
											.entrySet().iterator();
									listItem.clear();

									// MissChildData.clear();
									while (it.hasNext()) {
										HashMap<String, Object> map = new HashMap<String, Object>();

										Map.Entry<String, Device> entry = it
												.next();

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

												// System.out
												// .println("child.getMacAddress()=>"
												// + child.getMacAddress());
												map.put("image",
														R.drawable.ble_icon);
												map.put("title", entry
														.getValue().getName());
												map.put("text", entry
														.getValue()
														.getAddress()
														+ " UUID:"
														+ entry.getValue()
																.getUuid()
														+ " MajorID:"
														+ entry.getValue()
																.getMajor()
														+ " MinorID:"
														+ entry.getValue()
																.getMinor()
														+ " RSSI:"
														+ entry.getValue()
																.getRssi());
												// System.out.println("childchildchild+"
												// + child.getMacAddress());
												ScanedChildData.add(child);

												listItem.add(map);
												// mLeDevices.add(device);
												myDevice.add(newDevice);

												if (openAnti) {
													// System.out.println("openAnti");
													if (openAntiData != null) {
														// System.out.println("openAnti1");
														if (openAntiData.size() > 0) {
															// System.out.println("openAnti2");
															RSSIforBeep(rssi,
																	device,
																	openAntiData);
														}
													}
												}

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
		} else {
			// ble not support
			btnCancelConnect();
		}

		return v;

	}

	@SuppressLint("NewApi")
	private void closeAntiMode() {
		openAnti = false;
		openAntiCurrentDataFlag = false;
		// openAntiTheftTX.setText(getResources().getString(
		// R.string.text_radar_status_start_connected));
		if (openAntiData != null) {
			if (openAntiData.size() > 0) {
				openAntiData.clear();
			}
		}
		scanImg1.setBorderColor(getResources().getColor(R.color.white));
		scanImg2.setBorderColor(getResources().getColor(R.color.white));
		scanImg3.setBorderColor(getResources().getColor(R.color.white));
		scanImg4.setBorderColor(getResources().getColor(R.color.white));
		scanImg5.setBorderColor(getResources().getColor(R.color.white));
		scanImg6.setBorderColor(getResources().getColor(R.color.white));
		scanImg7.setBorderColor(getResources().getColor(R.color.white));
		scanImg8.setBorderColor(getResources().getColor(R.color.white));
		scanImg9.setBorderColor(getResources().getColor(R.color.white));
		missImg1.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg2.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg3.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg4.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg5.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg6.setBorderColor(getResources().getColor(R.color.dark_grey));
		openAntiTheft.setBackground(getResources().getDrawable(
				R.drawable.ic_selected_off));
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
		showAllScanImageData = new ArrayList<Child>();
		// ScanedChildData.clear();
		// MissChildData.clear();

		hint_nodata = (TextView) v.findViewById(R.id.hint_no_data);
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

		openAntiTheft = (TextView) v.findViewById(R.id.confirm_anti_lost_btn);
		// 防丟器
		confirmRadarBtn = (ToggleButton) v
				.findViewById(R.id.connection_status_btn);
		// openAntiTheftTX = (TextView)
		// v.findViewById(R.id.connection_status_txt);

		// bluetooth
		// connect device

		scanImg1 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img1);
		scanImg2 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img2);
		scanImg3 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img3);
		scanImg4 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img4);
		scanImg5 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img5);
		scanImg6 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img6);
		scanImg7 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img7);
		scanImg8 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img8);
		scanImg9 = (CircleImageView) v
				.findViewById(R.id.radar_child_scan_head_img9);

		missImg1 = (CircleImageView) v
				.findViewById(R.id.radar_child_miss_head_img1);
		missImg2 = (CircleImageView) v
				.findViewById(R.id.radar_child_miss_head_img2);
		missImg3 = (CircleImageView) v
				.findViewById(R.id.radar_child_miss_head_img3);
		missImg4 = (CircleImageView) v
				.findViewById(R.id.radar_child_miss_head_img4);
		missImg5 = (CircleImageView) v
				.findViewById(R.id.radar_child_miss_head_img5);
		missImg6 = (CircleImageView) v
				.findViewById(R.id.radar_child_miss_head_img6);

		connectDeviceLayout = (RelativeLayout) v
				.findViewById(R.id.connect_device_layout);

	}

	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("onDestroy()");
		super.onDestroy();
		// 关闭循环扫描
		isWhileLoop = false;
		// mBluetoothAdapter.stopLeScan(mLeScanCallback);

		try {
			handler.removeCallbacksAndMessages(SCAN_CHILD_FOR_LIST);
			handler.removeCallbacksAndMessages(BEEPTIMEOUT);

			if (beepIntent != null)
				getActivity().stopService(beepIntent);
			getActivity().unregisterReceiver(updateDb);
			getActivity().unregisterReceiver(bluetoothState);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopTimer();

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

		if (BLEUtils.isSupportBle(getActivity())) {
			final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
					.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();

			// // // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
			// if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null)
			// {
			// openBluetooth();
			// }

			if (mBluetoothAdapter.isEnabled()) {

			}

			if (!isConfirmRadarBtn) {
				System.out.println("isWhileLoop = false;");
				isWhileLoop = false;
				// .stopLeScan(mLeScanCallback);
			} else {
				System.out.println("isWhileLoop = true;");
				isWhileLoop = true;
				Thread autoScan = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						while (isWhileLoop) {

							try {
								scanLeDevice();
								Thread.sleep(BleDeviceConstants.SCAN_INRERVAL_TIME);

								Message msg = handler.obtainMessage();
								msg.what = SCAN_CHILD_FOR_LIST;
								handler.sendMessage(msg);

								System.out
										.println("scanLeDevice repeat start !!!!!!!!");
								mBluetoothAdapter.stopLeScan(mLeScanCallback);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				if (first_start_scan_thread_flag) {
					autoScan.start();
					first_start_scan_thread_flag = false;
				}
				// mBluetoothAdapter.startLeScan(mLeScanCallback);
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
		first_start_scan_thread_flag = true;
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
		// 確定點擊開啟button

		isConfirmRadarBtn = true;
		try {
			get_bluetooth_status = checkIsBluetooth();
			if (get_bluetooth_status) {
				clickConfirmRadarButton();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void clickConfirmRadarButton() {
		// hint and get db data
		if (DBChildren.getChildrenList(getActivity()) != null) {
			ChildData = DBChildren.getChildrenList(getActivity());
			Message msg = handler.obtainMessage();
			msg.what = SCAN_CHILD_FOR_LIST;
			handler.sendMessage(msg);
		}

		if (ChildData != null && ChildData.size() > 0) {
			hint_nodata.setVisibility(View.GONE);
		} else {
			hint_nodata.setVisibility(View.VISIBLE);
		}

		getActivity().registerReceiver(updateDb,
				new IntentFilter(BleDeviceConstants.BROADCAST_FINISH_BIND));

		// if (checkIsBluetoothOpen) {
		// 阻止底部button點擊

		isClickConnection = true;

		// 顯示list
		ChildlistView.setVisibility(View.VISIBLE);
		MissChildlistView.setVisibility(View.VISIBLE);
		// list透明為正常
		ChildlistView.setAlpha(1);
		MissChildlistView.setAlpha(1);
		RadarView.setAlpha(1);
		connectDeviceLayout.setAlpha(1);

		radarAnim();

		// device status
		SharePrefsUtils.setDeviceConnectStatus(getActivity(),
				BleDeviceConstants.DEVICE_CONNECT_STATUS_DEFAULT);

		// bluetooth
		if (first_start_scan_thread_flag) {
			autoScan.start();
			first_start_scan_thread_flag = false;
		}
		isWhileLoop = true;
	}

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	private void btnCancelConnect() {
		// 没有点击的flag
		isConfirmRadarBtn = false;
		// 还原为没有点击
		confirmRadarBtn.setChecked(false);
		autoScan.currentThread().interrupt();

		// 清除time task
		// stopTimer();

		// listview消失
		ChildlistView.setVisibility(View.GONE);
		MissChildlistView.setVisibility(View.GONE);
		ChildlistView.setAlpha((float) 0.3);
		MissChildlistView.setAlpha((float) 0.3);
		RadarView.setAlpha((float) 0.3);
		connectDeviceLayout.setAlpha((float) 0.3);

		// 關閉循環掃描
		isWhileLoop = false;
		// mBluetoothAdapter.stopLeScan(mLeScanCallback);

		// 清除動畫
		if (radar_rotate != null) {
			radar_rotate.clearAnimation();
		}

		// 清除頭像 清除數據 清除數量

		clearAlltheDate();
		missingChildNumTxt.setText(MissChildData.size() + "");
		unMissingChildNumTxt.setText(ScanedTempChildData.size() + "");

		scanImg1.setVisibility(View.INVISIBLE);
		scanImg2.setVisibility(View.INVISIBLE);
		scanImg3.setVisibility(View.INVISIBLE);
		scanImg4.setVisibility(View.INVISIBLE);
		scanImg5.setVisibility(View.INVISIBLE);
		scanImg6.setVisibility(View.INVISIBLE);
		scanImg7.setVisibility(View.INVISIBLE);
		scanImg8.setVisibility(View.INVISIBLE);
		scanImg9.setVisibility(View.INVISIBLE);
		missImg1.setVisibility(View.INVISIBLE);
		missImg2.setVisibility(View.INVISIBLE);
		missImg3.setVisibility(View.INVISIBLE);
		missImg4.setVisibility(View.INVISIBLE);
		missImg5.setVisibility(View.INVISIBLE);
		missImg6.setVisibility(View.INVISIBLE);

		scanImg1.setBorderColor(getResources().getColor(R.color.white));
		scanImg2.setBorderColor(getResources().getColor(R.color.white));
		scanImg3.setBorderColor(getResources().getColor(R.color.white));
		scanImg4.setBorderColor(getResources().getColor(R.color.white));
		scanImg5.setBorderColor(getResources().getColor(R.color.white));
		scanImg6.setBorderColor(getResources().getColor(R.color.white));
		scanImg7.setBorderColor(getResources().getColor(R.color.white));
		scanImg8.setBorderColor(getResources().getColor(R.color.white));
		scanImg9.setBorderColor(getResources().getColor(R.color.white));
		missImg1.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg2.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg3.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg4.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg5.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg6.setBorderColor(getResources().getColor(R.color.dark_grey));

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("onStart()");

	}

	private void changeTheAllData(ArrayList<Child> scanedTempChildData,
			ArrayList<Child> missChildData, ArrayList<Child> openAntiData2,
			ArrayList<Device> myDevice2) {

		Childadapter = new RadarKidsListViewAdapter(getActivity(),
				scanedTempChildData, openAntiData2, myDevice2);
		Childadapter.setCallback(RadarTrackingFragmentInstance);
		ChildlistView.setAdapter(Childadapter);

		MissChildadapter = new MissRadarKidsListViewAdapter(getActivity(),
				missChildData, openAntiData2);
		MissChildlistView.setAdapter(MissChildadapter);

	}

	@SuppressWarnings("unchecked")
	@SuppressLint("ResourceAsColor")
	private int addImageHead(ArrayList<Child> scanedChildData2,
			ArrayList<Child> openAntiData2) {
		// // 手动添加imageview
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_location_default)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();

		scanImg1.setVisibility(View.INVISIBLE);
		scanImg2.setVisibility(View.INVISIBLE);
		scanImg3.setVisibility(View.INVISIBLE);
		scanImg4.setVisibility(View.INVISIBLE);
		scanImg5.setVisibility(View.INVISIBLE);
		scanImg6.setVisibility(View.INVISIBLE);
		scanImg7.setVisibility(View.INVISIBLE);
		scanImg8.setVisibility(View.INVISIBLE);
		scanImg9.setVisibility(View.INVISIBLE);
		scanImg1.setBorderColor(getResources().getColor(R.color.white));
		scanImg2.setBorderColor(getResources().getColor(R.color.white));
		scanImg3.setBorderColor(getResources().getColor(R.color.white));
		scanImg4.setBorderColor(getResources().getColor(R.color.white));
		scanImg5.setBorderColor(getResources().getColor(R.color.white));
		scanImg6.setBorderColor(getResources().getColor(R.color.white));
		scanImg7.setBorderColor(getResources().getColor(R.color.white));
		scanImg8.setBorderColor(getResources().getColor(R.color.white));
		scanImg9.setBorderColor(getResources().getColor(R.color.white));

		if (scanedChildData2.size() > 9) {
			for (int i = 0; i < 9; i++) {
				Child child = scanedChildData2.get(i);
				switch (i) {

				case 0:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(0).getChildId()) {
								scanImg1.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg1,
							options, null);
					scanImg1.setVisibility(View.VISIBLE);
					break;

				case 1:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(1).getChildId()) {
								scanImg2.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg2,
							options, null);
					scanImg2.setVisibility(View.VISIBLE);
					break;
				case 2:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(2).getChildId()) {
								scanImg3.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg3,
							options, null);
					scanImg3.setVisibility(View.VISIBLE);
					break;

				case 3:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(3).getChildId()) {
								scanImg4.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg4,
							options, null);
					scanImg4.setVisibility(View.VISIBLE);
					break;
				case 4:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(4).getChildId()) {
								scanImg5.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg5,
							options, null);
					scanImg5.setVisibility(View.VISIBLE);
					break;
				case 5:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(5).getChildId()) {
								scanImg6.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg6,
							options, null);
					scanImg6.setVisibility(View.VISIBLE);
					break;
				case 6:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(6).getChildId()) {
								scanImg7.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg7,
							options, null);
					scanImg7.setVisibility(View.VISIBLE);
					break;
				case 7:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(7).getChildId()) {
								scanImg8.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg8,
							options, null);
					scanImg8.setVisibility(View.VISIBLE);
					break;
				case 8:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(8).getChildId()) {
								scanImg9.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg9,
							options, null);
					scanImg9.setVisibility(View.VISIBLE);
					break;

				}

			}
		} else {
			for (int i = 0; i < scanedChildData2.size(); i++) {
				Child child = scanedChildData2.get(i);
				switch (i) {

				case 0:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(0).getChildId()) {
								scanImg1.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg1,
							options, null);
					scanImg1.setVisibility(View.VISIBLE);
					break;

				case 1:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(1).getChildId()) {
								scanImg2.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg2,
							options, null);
					scanImg2.setVisibility(View.VISIBLE);
					break;
				case 2:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(2).getChildId()) {
								scanImg3.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg3,
							options, null);
					scanImg3.setVisibility(View.VISIBLE);
					break;

				case 3:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(3).getChildId()) {
								scanImg4.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg4,
							options, null);
					scanImg4.setVisibility(View.VISIBLE);
					break;
				case 4:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(4).getChildId()) {
								scanImg5.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg5,
							options, null);
					scanImg5.setVisibility(View.VISIBLE);
					break;
				case 5:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(5).getChildId()) {
								scanImg6.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg6,
							options, null);
					scanImg6.setVisibility(View.VISIBLE);
					break;
				case 6:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(6).getChildId()) {
								scanImg7.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg7,
							options, null);
					scanImg7.setVisibility(View.VISIBLE);
					break;
				case 7:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(7).getChildId()) {
								scanImg8.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg8,
							options, null);
					scanImg8.setVisibility(View.VISIBLE);
					break;
				case 8:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == scanedChildData2
									.get(8).getChildId()) {
								scanImg9.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), scanImg9,
							options, null);
					scanImg9.setVisibility(View.VISIBLE);
					break;

				}
			}
		}

		return scanedChildData2.size();

	}

	@SuppressWarnings("unchecked")
	private int addMissImageHead(ArrayList<Child> missChildData2,
			ArrayList<Child> openAntiData2) {
		// 手动添加imageview
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_location_default)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();

		missImg1.setVisibility(View.INVISIBLE);
		missImg2.setVisibility(View.INVISIBLE);
		missImg3.setVisibility(View.INVISIBLE);
		missImg4.setVisibility(View.INVISIBLE);
		missImg5.setVisibility(View.INVISIBLE);
		missImg6.setVisibility(View.INVISIBLE);
		missImg1.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg2.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg3.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg4.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg5.setBorderColor(getResources().getColor(R.color.dark_grey));
		missImg6.setBorderColor(getResources().getColor(R.color.dark_grey));

		if (missChildData2.size() > 6) {
			for (int i = 0; i < 6; i++) {
				Child child = missChildData2.get(i);
				switch (i) {
				case 0:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(0).getChildId()) {
								missImg1.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}

					imageLoader.displayImage(child.getIcon(), missImg1,
							options, null);

					missImg1.setVisibility(View.VISIBLE);
					break;

				case 1:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(1).getChildId()) {
								missImg2.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg2,
							options, null);
					missImg2.setVisibility(View.VISIBLE);
					break;
				case 2:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(2).getChildId()) {
								missImg3.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg3,
							options, null);
					missImg3.setVisibility(View.VISIBLE);
					break;

				case 3:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(3).getChildId()) {
								missImg4.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg4,
							options, null);
					missImg4.setVisibility(View.VISIBLE);
					break;
				case 4:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(4).getChildId()) {
								missImg5.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg5,
							options, null);
					missImg5.setVisibility(View.VISIBLE);
					break;
				case 5:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(5).getChildId()) {
								missImg6.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg6,
							options, null);
					missImg6.setVisibility(View.VISIBLE);
					break;

				}

			}
		} else {
			for (int i = 0; i < missChildData2.size(); i++) {
				Child child = missChildData2.get(i);
				switch (i) {

				case 0:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(0).getChildId()) {
								missImg1.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}

					imageLoader.displayImage(child.getIcon(), missImg1,
							options, null);

					missImg1.setVisibility(View.VISIBLE);
					break;

				case 1:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(1).getChildId()) {
								missImg2.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg2,
							options, null);
					missImg2.setVisibility(View.VISIBLE);
					break;
				case 2:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(2).getChildId()) {
								missImg3.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg3,
							options, null);
					missImg3.setVisibility(View.VISIBLE);
					break;

				case 3:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(3).getChildId()) {
								missImg4.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg4,
							options, null);
					missImg4.setVisibility(View.VISIBLE);
					break;
				case 4:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(4).getChildId()) {
								missImg5.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg5,
							options, null);
					missImg5.setVisibility(View.VISIBLE);
					break;
				case 5:
					if (openAntiData2 != null) {
						for (int y = 0; y < openAntiData2.size(); y++) {
							if (openAntiData2.get(y).getChildId() == missChildData2
									.get(5).getChildId()) {
								missImg6.setBorderColor(getResources()
										.getColor(R.color.red));
							}
						}
					}
					imageLoader.displayImage(child.getIcon(), missImg6,
							options, null);
					missImg6.setVisibility(View.VISIBLE);
					break;
				}
			}
		}

		return missChildData2.size();

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
				startActivityForResult(enableBtIntent,
						Constants.REQUEST_ENABLE_BT);
			}
		}
	}

	private void closeBluetooth() {
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		System.out.println("mBluetoothAdapter.disable()");
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

	public static void removeChildDuplicateList(ArrayList<Child> list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
		// System.out.println(list);
	}

	public static void removeDeviceDuplicateList(ArrayList<Device> list) {

		// System.out.println(tempList);
		for (int m = 0; m < list.size(); m++) {
			for (int n = m + 1; n < list.size();) {
				if (list.get(m).getAddress().equals(list.get(n).getAddress())) {
					list.remove(n);
					n++;
				} else {
					n++;
				}
			}
		}
	}

	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BleDeviceConstants.CAN_NOT_SUPPORT_BLE:
				Toast.makeText(getActivity(), R.string.text_ble_not_supported,
						Toast.LENGTH_LONG).show();
				break;

			case BEEPTIMEOUT:
				if (BleServicesService.intentToChara != null)
					getActivity().stopService(BleServicesService.intentToChara);
				getActivity().stopService(beepIntent);
				SharePrefsUtils.setfinishBeep(getActivity(), false);

				beepTime = 0;
				if (BeepCheckTimeOutTask != null) {
					BeepCheckTimeOutTask.cancel();
				}

				switch (SharePrefsUtils.DeviceConnectStatus(getActivity())) {
				case BleDeviceConstants.DEVICE_CONNECT_STATUS_SUCCESS:
					// device status
					SharePrefsUtils.setDeviceConnectStatus(getActivity(),
							BleDeviceConstants.DEVICE_CONNECT_STATUS_DEFAULT);
					break;

				default:
					// device status
					SharePrefsUtils.setDeviceConnectStatus(getActivity(),
							BleDeviceConstants.DEVICE_CONNECT_STATUS_ERROR);
					break;
				}
				break;

			case SCAN_CHILD_FOR_LIST:

				if (SharePrefsUtils.DeviceConnectStatus(getActivity()) == BleDeviceConstants.DEVICE_CONNECT_STATUS_ERROR) {

					if (deviceStatusError == 1) {
						SharePrefsUtils
								.setDeviceConnectStatus(
										getActivity(),
										BleDeviceConstants.DEVICE_CONNECT_STATUS_DEFAULT);
						deviceStatusError = 0;
					}
					deviceStatusError++;
				}
				// for (int i = 0; i < ScanedChildData.size(); i++) {
				// System.out.println("------->" +
				// ScanedChildData.get(i).getMacAddress());
				//
				// }
				// System.out.println("-----------------------------");
				ScanedTempChildData = (ArrayList<Child>) ScanedChildData
						.clone();

				try {
					removeChildDuplicateList(ScanedTempChildData);
					// for (int i = 0; i < ScanedTempChildData.size(); i++) {
					// System.out.println("------->" +
					// ScanedTempChildData.get(i).getMacAddress());
					//
					// }
					// System.out.println("-----------------------------");
					removeDeviceDuplicateList(myDevice);
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
					// show all
					if (showAllScanImageData != null) {
						showAllScanImageData.clear();
					}
					showAllScanImageData = (ArrayList<Child>) ScanedTempChildData
							.clone();

				}

				if (openAnti) {
					if (openAntiCurrentDataFlag) {
						openAntiData = (ArrayList<Child>) ScanedTempChildData
								.clone();
						if (openAntiData.size() > 0) {
							openAntiCurrentDataFlag = false;
						}

						// System.out.println("openAntiData==>"
						// + openAntiData.size());
					}

					// RSSIforBeep(rssi, device);
				}

				if (ScanedTempChildData != null) {
					unMissingChildNumTxt.setText(ScanedTempChildData.size()
							+ "");
				}
				if (ScanedTempChildData != null) {
					addImageHead(ScanedTempChildData, openAntiData);

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

				if (MissChildData != null) {
					missingChildNumTxt.setText(MissChildData.size() + "");
				}

				if (MissChildData != null) {
					addMissImageHead(MissChildData, openAntiData);

					if (showAllMissImageData != null) {
						showAllMissImageData.clear();
					}
					// show all
					showAllMissImageData = (ArrayList<Child>) MissChildData
							.clone();

				}

				changeTheAllData(ScanedTempChildData, MissChildData,
						openAntiData, myDevice);
				// radar頭像

				clearAlltheDate();

			}

		}
	};

	// 下面為bluetooth
	Thread autoScan = new Thread(new Runnable() {

		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (isWhileLoop) {

				try {
					scanLeDevice();
					Thread.sleep(BleDeviceConstants.SCAN_INRERVAL_TIME);

					Message msg = handler.obtainMessage();
					msg.what = SCAN_CHILD_FOR_LIST;
					handler.sendMessage(msg);

					System.out.println("scanLeDevice repeat start !!!!!!!!");
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});

	/**
	 * scan funciton
	 */
	@SuppressLint("NewApi")
	public void scanLeDevice() {
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	/**
	 * broadcast
	 */
	BroadcastReceiver bluetoothState = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String stateExtra = BluetoothAdapter.EXTRA_STATE;
			int state = intent.getIntExtra(stateExtra, -1);
			switch (state) {
			case BluetoothAdapter.STATE_TURNING_ON:
				break;
			case BluetoothAdapter.STATE_ON:

				if (isConfirmRadarBtn) {
					if (!get_bluetooth_status) {
						clickConfirmRadarButton();
					}
				}

				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				System.out.println("STATE_TURNING_OFF");
				btnCancelConnect();
				closeBluetooth();
				closeAntiMode();
				break;
			case BluetoothAdapter.STATE_OFF:
				System.out.println("STATE_OFF");
				btnCancelConnect();
				closeBluetooth();
				closeAntiMode();
				break;

			}

		}
	};

	private class UpdateDb extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BleDeviceConstants.BROADCAST_FINISH_BIND)) {
				ChildData = DBChildren.getChildrenList(getActivity());
			}
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
		// if (mLeDevices != null) {
		// Iterator<BluetoothDevice> it4 = mLeDevices.iterator();
		// for (; it4.hasNext();) {
		// it4.next();
		// it4.remove();
		// }
		// }
		if (ScanedChildData != null) {
			Iterator<Child> it5 = ScanedChildData.iterator();
			for (; it5.hasNext();) {
				it5.next();
				it5.remove();
			}
		}

		if (myDevice != null) {
			Iterator<Device> it6 = myDevice.iterator();
			for (; it6.hasNext();) {
				it6.next();
				it6.remove();
			}
		}
	}

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
			timer = new Timer();
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
					Constants.DB_NAME);
			beepIntent.putExtra(BleServicesService.EXTRAS_DEVICE_ADDRESS,
					BeepTempChildData.get(position).getMacAddress());
			System.out
					.println("BeepTempChildData.get(position).getMacAddress()==>"
							+ BeepTempChildData.get(position).getMacAddress());
			beepIntent
					.setAction("com.twinly.eyebb.service.BLE_SERVICES_SERVICES");
			beepIntent.putExtra(BleDeviceConstants.BLE_SERVICE_COME_FROM,
					"radar");

			SharePrefsUtils.setConnectBleService(getActivity(), 1);
			SharePrefsUtils.setfinishBeep(getActivity(), true);

			// device status
			SharePrefsUtils.setDeviceConnectStatus(getActivity(),
					BleDeviceConstants.DEVICE_CONNECT_STATUS_LOADING);

			SharePrefsUtils.setKeepDeviceConnectStatus(getActivity(),
					BeepTempChildData.get(position).getName());

			getActivity().startService(beepIntent);

		}

	}

	// 應該加入頭像

	private void RSSIforBeep(int rssi, BluetoothDevice device,
			ArrayList<Child> openAntiData2) {
		// TODO Auto-generated method stub

		for (int i = 0; i < openAntiData2.size(); i++) {
			// System.out.println("beepAllTimeaaa=>" + device.getAddress() + " "
			// + openAntiData2.get(i).getMacAddress());
			if (device.getAddress()
					.equals(openAntiData2.get(i).getMacAddress())) {
				if (rssi < BleDeviceConstants.BEEP_RSSI) {

					Intent beepForAntiIntent = new Intent();

					beepForAntiIntent.setClass(getActivity(),
							RadarOutOfRssiBeepDialog.class);
					beepForAntiIntent.putExtra("child_information",
							openAntiData2.get(i));

					startActivity(beepForAntiIntent);

					// }

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
