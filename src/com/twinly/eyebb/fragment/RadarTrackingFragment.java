package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.activity.ServicesActivity;
import com.twinly.eyebb.adapter.ChangeKidsListViewAdapter;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class RadarTrackingFragment extends Fragment {
	private SimpleAdapter mkidsListAdapter;
	ArrayList<HashMap<String, Object>> mKidsData;
	ImageView radar_rotate;
	private View radarBeepAllBtn;
	private View radarBeepBtn;
	private View radarBeepBtn1;
	private View radarBeepBtn2;

	private View btnConfirm;
	private View btnCancel;
	private View btnStatus;
	private View radarView;
	private BluetoothAdapter mBluetoothAdapter;
	private final static int BLE_VERSION = 18;
	private static final int REQUEST_ENABLE_BT = 1;
	private boolean isFirstDialog = true;

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
	private String UUID;
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
	List<String> UUID_temp = new ArrayList<String>();
	private int if_has = 1; // 1为有
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件
	private String BeepDeviceMACAddress = "";
	private String BeepDeviceUUID = "4D616361726F6E202020202020202020";
	private int findDevice = 100;
	private ArrayList<Device> device;
	private ListView listView;
	private RadarKidsListViewAdapter adapter;

	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");
		View v = inflater.inflate(R.layout.fragment_radar_tracking, container,
				false);
		listView = (ListView) v.findViewById(R.id.radar_children_list);
		adapter = new RadarKidsListViewAdapter(getActivity(),
				DBChildren.getChildrenList(getActivity()));
		listView.setAdapter(adapter);
		// radarBeepBtn1 = v.findViewById(R.id.radar_beep_btn1);
		// radarBeepBtn2 = v.findViewById(R.id.radar_beep_btn2);

		mHandler = new Handler();
		autoScanHandler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();

		if (SharePrefsUtils.getRole(getActivity()) == false) {
			// radarBeepBtn1.setVisibility(View.GONE);
			// radarBeepBtn2.setVisibility(View.GONE);
			v.findViewById(R.id.avatar1).setVisibility(View.GONE);
			v.findViewById(R.id.avatar2).setVisibility(View.GONE);
			// ((TextView) v.findViewById(R.id.radar_text_missed_number))
			// .setText("1");
			// v.findViewById(R.id.newslist_item_layout_1)
			// .setVisibility(View.GONE);
			// v.findViewById(R.id.newslist_item_layout2).setVisibility(View.GONE);
		} else {
			((TextView) v.findViewById(R.id.radar_text_missed_number))
					.setText("3");
		}
		// radarBeepBtn1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getActivity(), BeepDialog.class);
		// startActivity(intent);
		// }
		// });
		// radarBeepBtn2.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getActivity(), BeepDialog.class);
		// startActivity(intent);
		// }
		// });

		// bluetooth
		// connect device
		btnConfirm = v.findViewById(R.id.btn_confirm);
		btnCancel = v.findViewById(R.id.btn_cancel);
		btnStatus = v.findViewById(R.id.connection_status_btn);
		radarView = v.findViewById(R.id.radar);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					checkIsBluetooth();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getActivity().findViewById(R.id.connect_device_layout)
						.setVisibility(View.GONE);
				radarView.setAlpha(1);

				// if (scan_flag) {
				// autoScanHandler.postDelayed(autoScan, POSTDELAYTIME);
				// } else {
				// new Thread(autoScan).start();
				// }

				// bluetooth

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().findViewById(R.id.connect_device_layout)
						.setVisibility(View.GONE);

			}
		});

		btnStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().findViewById(R.id.connect_device_layout)
						.setVisibility(View.VISIBLE);

			}
		});

	
		return v;

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
			radarAnim();

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
				radarAnim();
				// if (isFirstDialog) {
				// Intent intent = new Intent(getActivity(),
				// ConnectDeviceDialog.class);
				// startActivity(intent);
				// isFirstDialog = false;
				// }

			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreated");
	
		// radarBeepBtn = getActivity().findViewById(R.id.radar_beep_btn);

		// radarBeepBtn.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (CommonUtils.isFastDoubleClick()) {
		// return;
		// } else {
		// Intent intent = new Intent(getActivity(), BeepDialog.class);
		// startActivity(intent);
		// }
		// }
		// });

		radarBeepAllBtn = getActivity().findViewById(R.id.radar_beep_all_btn);

		radarBeepAllBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					Intent intent = new Intent(getActivity(), BeepDialog.class);
					startActivity(intent);
				}
			}
		});

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
			while (true) {
				// firstScan = true;
				if (scan_flag) {
					firstScan = true;
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

	@SuppressLint("NewApi")
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
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

			}, POSTDELAYTIME);

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

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int majorid, minorid;
					// System.out.println("scanRecordscanRecordscanRecordscanRecord"
					// + Arrays.toString(scanRecord));
					UUID = bytesToHex(scanRecord, 9, 16);

					majorid = scanRecord[25] * 256 + scanRecord[26];
					minorid = scanRecord[27] * 256 + scanRecord[28];
					// System.out.println("111111111firstScan======>" +
					// firstScan
					// + "  UUID======>" + bytesToHex(scanRecord, 9, 16));

					// record the UUID

					// if (is_autoConnection) {
					// new autoConnection().start();
					//
					// }
					System.out.println("BeepDeviceMACAddress=>"
							+ BeepDeviceMACAddress);
					if (BeepDeviceMACAddress.length() > 10) {
						System.out.println("mLeDevices.get().getAddress()=>"
								+ device.getAddress());
						System.out.println("UUID=>" + UUID);
						new autoConnection().start();
					}

					HashMap<String, String> hashMap = new HashMap<String, String>();
					if (firstScan) {

						addItem(device.getName(), device.getAddress()
								+ " UUID:" + bytesToHex(scanRecord, 9, 16)
								+ " MajorID:" + majorid + " MinorID:" + minorid
								+ " RSSI:" + rssi);
						System.out
								.println("firstScanfirstScanfirstScanfirstScanfirstScan");
						mLeDevices.add(device);
						firstScan = false;

						UUID_temp.add(UUID);

						if (mLeDevices.size() > 0) {
							if (bytesToHex(scanRecord, 9, 16).equals(
									BeepDeviceUUID)) {
								BeepDeviceMACAddress = device.getAddress();
								System.out.println("BeepDeviceMACAddress22=>"
										+ BeepDeviceMACAddress);
							}
						}

					} else {

						for (int i = 0; i < UUID_temp.size(); i++) {
							if (UUID.equals(UUID_temp.get(i))) {
								// System.out.println(firstScan
								// +"   ==UUID_temp.size()======>" +
								// UUID_temp.size()
								// + "  UUID======>" + bytesToHex(scanRecord, 9,
								// 16)+"   UUIDTEMP==>" + UUID_temp.get(i) + i);
								if_has = 1;
								break;
							} else {
								if_has = 0;

								// System.out.println("111");
							}

						}

						if (if_has == 0) {
							UUID_temp.add(UUID);
							addItem(device.getName(), device.getAddress()
									+ " UUID:" + bytesToHex(scanRecord, 9, 16)
									+ " MajorID:" + majorid + " MinorID:"
									+ minorid + " RSSI:" + rssi);
							mLeDevices.add(device);

						}
					}

					// System.out.println("firstScan======>" + firstScan

				}
			});
		}
	};

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_SCAN:

				break;

			case STOP_SCAN:

				break;

			case DELETE_SCAN:

				int size = listItem.size();
				if (size > 0) {

					listItem.clear();
					UUID_temp.clear();
					listItemAdapter.notifyDataSetChanged();

				}
				firstScan = true;
				mLeDevices.clear();
				break;

			}
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

	private void addItem(String devname, String address) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("image", R.drawable.btn_arrow_go);
		map.put("title", devname);
		map.put("text", address);
		listItem.add(map);
		listItemAdapter.notifyDataSetChanged();

		System.out.println("listItemlistItemlistItelistItem=>"
				+ listItem.size());
	}

	public class autoConnection extends Thread {
		@Override
		public void run() {
			// 测试自动连接功能
			if (mLeDevices.size() > 0) {

				for (int i = 0; i < mLeDevices.size(); i++) {
					System.out.println(mLeDevices.get(i));
					if (mLeDevices.get(i).getAddress()
							.equals(BeepDeviceMACAddress)) {
						// System.out.println("mLeDevices.get(i).getUuids()=>"
						// + mLeDevices.get(i).getUuids());
						findDevice = i;
						break;
					}
				}
				if (findDevice != 100) {
					System.out.println("findDevice=====> " + findDevice);
					//
					BluetoothDevice device = mLeDevices.get(findDevice);
					System.out.println("device.getName()=====> "
							+ device.getName());
					System.out.println("device.getAddress()=====> "
							+ device.getAddress());
					Intent intent = new Intent();
					intent.setClass(getActivity(), ServicesActivity.class);
					intent.putExtra(ServicesActivity.EXTRAS_DEVICE_NAME,
							device.getName());
					intent.putExtra(ServicesActivity.EXTRAS_DEVICE_ADDRESS,
							device.getAddress());
					if (scan_flag) {
						scanLeDevice(false);
					}
					findDevice = 100;
					startActivity(intent);
				}

			}

		}
	}

}
