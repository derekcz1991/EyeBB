package com.twinly.eyebb.activity;

import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.widget.Toast;

public class WriteBeepDialog extends WriteToBeaconDialog {
	public static final String EXTRAS_DEVICE_BEEP = "DEVICE_BEEP";
	private String beep;

	@Override
	public void onPreWritePrepareUi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreWriteGetData() {
		beep = intent.getStringExtra(EXTRAS_DEVICE_BEEP);
	}

	@Override
	public void writeToMacaron(List<BluetoothGattService> gattServices) {
		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			System.out.println("Service == >> " + uuid);
			if (uuid.equals(BEEP_SERVICE_UUID)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					System.out.println("Characteristic == >> " + uuid);
					if (uuid.equals(BEEP_CHARACTERISTICS_BEEP_UUID)) {
						gattCharacteristic
								.setValue(super.HexString2Bytes(beep));
						if (mBluetoothLeService
								.writeCharacteristic(gattCharacteristic)) {
							Toast.makeText(WriteBeepDialog.this, "true",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(WriteBeepDialog.this, "false",
									Toast.LENGTH_SHORT).show();
						}
						finish();
					}
				}
				finish();
			}
		}
	}

}
