package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.adapter.LeDeviceListAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class BeaconListActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 30 seconds.
	private static final long SCAN_PERIOD = 15000;

	private ListView listview;
	private ProgressBar progressBar;
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private ArrayList<Device> previousList;
	private ArrayList<Device> currentList;
	private long childId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkBLE();

		setTitle(getString(R.string.text_matching_device));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		mHandler = new Handler();
		previousList = new ArrayList<Device>();
		currentList = new ArrayList<Device>();

		setContentView(R.layout.ble_peripheral);
		childId = getIntent().getLongExtra("child_id", 0);
		listview = (ListView) findViewById(R.id.listView_peripheral);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				new GetMajorMinorTask().execute(mLeDeviceListAdapter.getItem(
						position).getAddress());

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter(this);
		previousList = mLeDeviceListAdapter.getDeviceList();
		listview.setAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_beacon_list, menu);
		menu.findItem(R.id.menu_search).setActionView(
				R.layout.actionbar_search_device);
		if (!mScanning) {
			menu.findItem(R.id.menu_scan).setTitle("scan");
		} else {
			menu.findItem(R.id.menu_scan).setTitle("stop");
		}

		MenuItem search = menu.findItem(R.id.menu_search);
		search.setActionView(R.layout.actionbar_search_device);

		final EditText etSearch = (EditText) search.getActionView()
				.findViewById(R.id.search_addr);

		search.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				etSearch.requestFocus();
				CommonUtils.switchSoftKeyboardstate(BeaconListActivity.this);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				etSearch.clearFocus();
				CommonUtils.hideSoftKeyboard(etSearch, BeaconListActivity.this);
				return true;
			}
		});

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				searchDevice(etSearch.getText().toString());
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_scan:
			if (mScanning) {
				scanLeDevice(false);
			} else {
				mLeDeviceListAdapter.clear();
				scanLeDevice(true);
			}
			break;
		}
		return true;
	}

	private class GetMajorMinorTask extends AsyncTask<String, Void, String> {
		private String address;

		@Override
		protected String doInBackground(String... params) {
			address = params[0];
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", address);

			return HttpRequestUtils.post(HttpConstants.CHECK_BEACON, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.CHECK_BEACON + result);

			if (result.length() > 0) {
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					return;
				}
				if (result.equals(HttpConstants.SERVER_RETURN_USED)) {
					Toast.makeText(BeaconListActivity.this,
							R.string.text_device_already_binded,
							Toast.LENGTH_SHORT).show();
				} else {
					String major;
					String minor;

					Intent intent = new Intent(BeaconListActivity.this,
							BindChildMacaronDialog.class);

					major = result.substring(0, result.indexOf(":"));
					minor = result.substring(result.indexOf(":") + 1,
							result.length());
					System.out.println("major = " + major + "  minor = "
							+ minor);
					intent.putExtra(
							BindChildMacaronDialog.EXTRAS_RECEIVER_MAJOR, major);
					intent.putExtra(
							BindChildMacaronDialog.EXTRAS_RECEIVER_MINOR, minor);

					major = checkMajorMinor(major);
					minor = checkMajorMinor(minor);

					System.out.println("major = " + major + "  minor = "
							+ minor);
					intent.putExtra("child_id", childId);
					intent.putExtra(WriteToBeaconDialog.EXTRAS_DEVICE_ADDRESS,
							address);
					intent.putExtra(WriteMajorMinorDialog.EXTRAS_DEVICE_MAJOR,
							major);
					intent.putExtra(WriteMajorMinorDialog.EXTRAS_DEVICE_MINOR,
							minor);
					startActivityForResult(
							intent,
							ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_DIALOG);
				}
			}
		}
	}

	private void checkBLE() {
		// Use this check to determine whether BLE is supported on the device.  Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "error_bluetooth_not_supported",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			progressBar.setVisibility(View.VISIBLE);
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
					progressBar.setVisibility(View.INVISIBLE);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	private void searchDevice(String deviceAddress) {
		if (!TextUtils.isEmpty(deviceAddress)) {
			deviceAddress = deviceAddress.toUpperCase(Locale.ENGLISH);
			currentList.clear();
			for (int i = 0; i < previousList.size(); i++) {
				String address = previousList.get(i).getAddress();
				if (address.replace(":", "").contains(deviceAddress)
						|| address.contains(deviceAddress)) {
					currentList.add(previousList.get(i));
				}
			}
			mLeDeviceListAdapter.setDeviceList(currentList);
		} else {
			mLeDeviceListAdapter.setDeviceList(previousList);
		}
		mLeDeviceListAdapter.notifyDataSetChanged();
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Device newDevice = new Device();
					newDevice.setAddress(device.getAddress());
					newDevice.setMajor(scanRecord[25] * 256 + scanRecord[26]);
					newDevice.setMinor(scanRecord[27] * 256 + scanRecord[28]);
					newDevice.setName(device.getName());
					newDevice.setRssi(rssi);
					newDevice.setUuid(BLEUtils.bytesToHex(scanRecord, 9, 16));
					if (newDevice.getUuid().equals(
							BleDeviceConstants.DEVICE_UUID_VERSON_1)
							|| newDevice
									.getUuid()
									.substring(8, 32)
									.equals(BleDeviceConstants.DEVICE_UUID_VERSON_2)) {
						mLeDeviceListAdapter.addDevice(newDevice);
						mLeDeviceListAdapter.notifyDataSetChanged();
					}

				}
			});
		}
	};

	private String checkMajorMinor(String value) {
		// TODO Auto-generated method stub
		switch (value.length()) {
		case 1:
			value = "000" + value;
			break;

		case 2:
			value = "00" + value;
			break;
		case 3:
			value = "0" + value;
			break;

		}
		return value;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		} else if (requestCode == ActivityConstants.REQUEST_GO_TO_BIND_CHILD_MACARON_DIALOG) {
			if (resultCode == ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS) {
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
				finish();
				return;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
