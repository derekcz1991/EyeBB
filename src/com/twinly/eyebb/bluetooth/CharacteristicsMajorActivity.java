package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent.OnFinished;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;
import com.twinly.eyebb.activity.CheckBeaconActivity;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.BLEUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class CharacteristicsMajorActivity extends Activity {

    SimpleAdapter listItemAdapter; // ListView的适配器
    ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
    ListView myList; // ListView控件

    TextView status_text;
    public static CharacteristicsMajorActivity instance;
    int servidx, charaidxMajor, charaidxMinor;
    String name = null;
    String name2 = null;
    BluetoothGattService gattService;
    BluetoothGattService gattService2;
    ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> gattCharacteristicGroupData2 = new ArrayList<HashMap<String, String>>();
    ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
    ArrayList<BluetoothGattCharacteristic> charas2 = new ArrayList<BluetoothGattCharacteristic>();
    private String uuid;
    private String uuid2;

    private boolean majorFlag = false;
    private boolean minorFlag = false;

    private TimerTask TimeOutTask = null;
    private int intentTime = 0;
    Timer timer = new Timer();
    private Dialog dialog;
    public static boolean majorFinished = false;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ble_characteristics);

        BaseApp.getInstance().addActivity(this);
        instance = this;
        status_text = (TextView) findViewById(R.id.characteristics_status);
        dialog = LoadingDialog.createLoadingDialogCanCancel(
                CharacteristicsMajorActivity.this,
                getString(R.string.toast_write_major));
        dialog.show();
        final Intent intent = getIntent();
        servidx = intent.getIntExtra("servidx", -1);

        if (servidx == -1) {
            Toast.makeText(this, "Characteristics Index Error!",
                    Toast.LENGTH_LONG).show();
            CharacteristicsMajorActivity.this.finish();
        }

        SharePrefsUtils.setBleServiceIndex(CharacteristicsMajorActivity.this,
                servidx);
        SharePrefsUtils
                .setOpenBindingDevice(CharacteristicsMajorActivity.this, true);

        listItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this, listItem,
                R.layout.ble_characteristics_listview, new String[] { "title",
                        "text" }, new int[] { R.id.characteristics_ItemTitle,
                        R.id.characteristics_ItemText });
        myList = (ListView) findViewById(R.id.characteristics_listView);
        myList.setAdapter(listItemAdapter);

        // final BluetoothGattCharacteristic characteristic = charas.get(0);
        // uuid = characteristic.getUuid().toString();
        // uuid = uuid.substring(4,8);
        // charaidx = 0;
        // Constans.mBluetoothLeService.readCharacteristic(characteristic);

        gattService = BleDeviceConstants.gattServiceObject.get(servidx);
        // gattService2 = Constants.gattServiceObject.get(servidx);
        registerReceiver(mGattUpdateReceiver, new IntentFilter(
                BluetoothLeService.ACTION_DATA_AVAILABLE));
        Thread disconverThread = new Thread() {
            @SuppressLint("NewApi")
            public void run() {

                // System.out.println("disconverThread ");
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                        .getCharacteristics();

                // System.out.println("disconverThread. ");
                // Loops through available Characteristics.
                if (gattCharacteristics == null) {
                    System.out.println("gattCharacteristics NULL");
                }

                for (int i = 0; i < gattCharacteristics.size(); i++) {
                    charas.add(gattCharacteristics.get(i));
                    // System.out.println("disconverThread1 ");
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    // System.out.println("disconverThread2 ");
                    uuid = gattCharacteristics.get(i).getUuid().toString();
                    // System.out.println("disconverThread3 ");
                    uuid = uuid.substring(4, 8);
                    // System.out.println("uuid char==>" + uuid);

                    name = SampleGattAttributes.lookup(uuid, "Unknow");
                    currentCharaData.put("NAME", name);
                    currentCharaData.put("UUID", uuid);
                    gattCharacteristicGroupData.add(currentCharaData);
                    addItem(name, uuid);
                    // System.out.println("gattCharacteristic=>" +
                    // gattCharacteristics.get(i).toString());
                    if (uuid.equals(BleDeviceConstants.BEEP_CHAR_MAJOR)) {
                        final BluetoothGattCharacteristic characteristic = gattCharacteristics
                                .get(i);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // uuid =
                            // characteristic.getUuid().toString();
                            System.out.println(" charas uuid ==>" + uuid + "  "
                                    + i);
                            // uuid = uuid.substring(4, 8);
                            // uuid = uuid;
                            charaidxMajor = i;
                            BleDeviceConstants.mBluetoothLeService
                                    .readCharacteristic(characteristic);

                            majorFlag = true;

                            break;

                        }
                    }

                }

            }
        };
        disconverThread.start();

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            System.out.println("action = " + action);
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent
                        .getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("data========>" + data);

                if (uuid.equals("1008")) {
                    System.out.println("this is 1008" + data + " "
                            + Integer.parseInt(data, 16));
                    modify1008();
                }
            }
        }

        @SuppressLint("NewApi")
        private void modify1008() {
            // TODO Auto-generated method stub
            // String data = "0033";
        

            TimeOutTask = new TimerTask() {
                public void run() {
                    intentTime++;
                    System.out.println("MajorTime==>" + intentTime);
                    if (intentTime == 5) {
                        
                        if (BluetoothLeService.writeMajorSuccess) {
                            intentTime = 6;
                            Intent intent = new Intent(
                                    CharacteristicsMajorActivity.this,
                                    ServicesActivity.class);

                            intent.putExtra(
                                    ServicesActivity.EXTRAS_DEVICE_NAME,
                                    "Macaron");
                            intent.putExtra(
                                    ServicesActivity.EXTRAS_DEVICE_ADDRESS,
                                    SharePrefsUtils
                                            .isMacAddress(CharacteristicsMajorActivity.this));
                            try {

                                dialog.dismiss();

                                TimeOutTask.cancel();
                                TimeOutTask = null;
                                timer.cancel();
                                timer.purge();
                                timer = null;
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            CharacteristicsMajorActivity.majorFinished = true;

                            Message msg = handler.obtainMessage();
                            msg.what = BleDeviceConstants.FINISH_WRITE_MAJOR_CHARA;
                            handler.sendMessage(msg);
                            startActivity(intent);
                            // CharacteristicsActivity.this.finish();
                        }

                    }
                }
            };

            timer.schedule(TimeOutTask, 0, 1000);

            
            try {
                String data = SharePrefsUtils
                        .signUpDeviceMajor(CharacteristicsMajorActivity.this);
                BluetoothGattCharacteristic characteristic = charas
                        .get(charaidxMajor);

                characteristic.setValue(BLEUtils.HexString2Bytes(data));

                BleDeviceConstants.mBluetoothLeService
                        .wirteCharacteristic(characteristic);
                System.out.println("---->finish1008");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    };

    Handler handler = new Handler() {

        @SuppressLint("ShowToast")
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BleDeviceConstants.FINISH_WRITE_MAJOR_CHARA:

                // unregisterReceiver(mGattUpdateReceiver);
                finish();
                System.out.println("chara1008 handler");
                break;

            }
        }
    };

    // private final BroadcastReceiver mGattUpdateReceiver2 = new
    // BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // final String action = intent.getAction();
    // System.out.println("action = " + action);
    // if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
    // String data = intent
    // .getStringExtra(BluetoothLeService.EXTRA_DATA);
    // System.out.println("data========>" + data);
    //
    // if (uuid2.equals("1009")) {
    // System.out.println("this is 1009" + data + " "
    // + Integer.parseInt(data, 16));
    // modify1009();
    // }
    // }
    // }
    //
    // @SuppressLint("NewApi")
    // private void modify1009() {
    // // TODO Auto-generated method stub
    //
    // String data2 = "0333";
    //
    // BluetoothGattCharacteristic characteristic2 = charas2
    // .get(charaidxMinor);
    //
    // characteristic2.setValue(BLEUtils.HexString2Bytes(data2));
    //
    // Constants.mBluetoothLeService.wirteCharacteristic2(characteristic2);
    // System.out.println("---->finish1009");
    //
    // }
    // };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, new IntentFilter(
                BluetoothLeService.ACTION_DATA_AVAILABLE));
        // registerReceiver(mGattUpdateReceiver2, new IntentFilter(
        // BluetoothLeService.ACTION_DATA_AVAILABLE));
        System.out.println("chara1008 onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);

        try {
            TimeOutTask.cancel();
            TimeOutTask = null;

            timer.cancel();
            timer.purge();

            if (dialog.isShowing() && dialog != null)
                dialog.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // unregisterReceiver(mGattUpdateReceiver2);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // unregisterReceiver(mGattUpdateReceiver);
        System.out.println("1008 destroy");
        try {
            TimeOutTask.cancel();
            TimeOutTask = null;

            timer.cancel();
            timer.purge();

            // if (dialog.isShowing() && dialog != null)
            dialog.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void addItem(String devname, String address) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", devname);
        map.put("text", address);
        listItem.add(map);
        listItemAdapter.notifyDataSetChanged();
    }

    private void deleteItem() {
        int size = listItem.size();
        if (size > 0) {
            listItem.remove(listItem.size() - 1);
            listItemAdapter.notifyDataSetChanged();
        }
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // // TODO Auto-generated method stub
    // if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
    // CharacteristicsActivity.this.finish();
    // System.out.println("=========>onKeyDown");
    // return true;
    // }
    // return super.onKeyDown(keyCode, event);
    // }

}