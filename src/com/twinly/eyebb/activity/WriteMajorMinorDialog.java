package com.twinly.eyebb.activity;

import java.util.List;

import com.twinly.eyebb.constant.ActivityConstants;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.widget.Toast;

public abstract class WriteMajorMinorDialog extends WriteToBeaconDialog {
	public static final String EXTRAS_DEVICE_MAJOR = "DEVICE_MAJOR";
	public static final String EXTRAS_DEVICE_MINOR = "DEVICE_MINOR";
	
	private String major;
	private String minor;

	/**
	 * To post data to server if write major and minor success
	 */
	public abstract void postToServer();

	@Override
	public void onPreWritePrepareUi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreWriteGetData() {
		major = intent.getStringExtra(EXTRAS_DEVICE_MAJOR);
		minor = intent.getStringExtra(EXTRAS_DEVICE_MAJOR);
	}

	@Override
	public void writeToMacaron(List<BluetoothGattService> gattServices) {
		BluetoothGattCharacteristic majorGattCharacteristic = null;
		BluetoothGattCharacteristic minorGattCharacteristic = null;

		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			System.out.println("Service == >> " + uuid);
			if (uuid.equals(BEEP_SERVICE_UUID)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(BEEP_CHARACTERISTICS_MAJOR_UUID)) {
						System.out.println("Characteristic == >> " + uuid);
						majorGattCharacteristic = gattCharacteristic;
					} else if (uuid.equals(BEEP_CHARACTERISTICS_MINOR_UUID)) {
						System.out.println("Characteristic == >> " + uuid);
						minorGattCharacteristic = gattCharacteristic;
					}
				}
				break;
			}
		}

		if (majorGattCharacteristic != null) {
			majorGattCharacteristic.setValue(HexString2Bytes(major));
			if (mBluetoothLeService
					.writeCharacteristic(majorGattCharacteristic)) {
			} else {
				Toast.makeText(WriteMajorMinorDialog.this, "write major fail",
						Toast.LENGTH_SHORT).show();
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				finish();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (minorGattCharacteristic != null) {
			minorGattCharacteristic.setValue(HexString2Bytes(minor));
			if (mBluetoothLeService
					.writeCharacteristic(minorGattCharacteristic)) {
			} else {
				Toast.makeText(WriteMajorMinorDialog.this, "write minor fail",
						Toast.LENGTH_SHORT).show();
				setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				finish();
			}
		}

		postToServer();
	}

}
