package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eyebb.R;

public class CharacteristicsActivity extends Activity {	
	
	SimpleAdapter listItemAdapter; // ListView的适配器
	ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表
	ListView myList; // ListView控件
	
	TextView status_text;
	
	int servidx,charaidx;
	
	BluetoothGattService gattService;
    ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
	ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
	
	private String uuid;
	
    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ble_characteristics);
      
		BaseApp.getInstance().addActivity(this);
		
		status_text = (TextView)findViewById(R.id.characteristics_status);
		
		final Intent intent = getIntent();
		servidx =intent.getIntExtra("servidx",-1);
		
		if(servidx == -1){
			Toast.makeText(this, "Characteristics Index Error!", Toast.LENGTH_LONG).show();
			CharacteristicsActivity.this.finish();
		}
		
		listItem = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItem, R.layout.ble_characteristics_listview,
		new String[]{"title", "text"},
		new int[]{R.id.characteristics_ItemTitle, R.id.characteristics_ItemText});
		myList = (ListView)findViewById(R.id.characteristics_listView);
		myList.setAdapter(listItemAdapter);
		
		
		
//		final BluetoothGattCharacteristic characteristic = charas.get(0);
//		uuid = characteristic.getUuid().toString();
//		uuid = uuid.substring(4,8);
//		charaidx = 0;
//		Constans.mBluetoothLeService.readCharacteristic(characteristic);
		
		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				final BluetoothGattCharacteristic characteristic = charas.get(arg2);
				System.out.println("arg2=========>" + arg2);
				final int charaProp = characteristic.getProperties();
				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
					uuid = characteristic.getUuid().toString();
					uuid = uuid.substring(4,8);
					charaidx = arg2;
					Constans.mBluetoothLeService.readCharacteristic(characteristic);
				}
			}
		});	
		
		gattService = Constans.gattServiceObject.get(servidx);
		
		Thread disconverThread = new Thread() {     
			   public void run() {   
				   status_text.setText(Constans.gattServiceData.get(servidx).get("NAME")+": Discovering Characteristics...");
				   List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

				   String uuid = null;
				   String name = null;
				   // Loops through available Characteristics.
				   for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				       charas.add(gattCharacteristic);
					   HashMap<String, String> currentCharaData = new HashMap<String, String>();
					   uuid = gattCharacteristic.getUuid().toString();
					   uuid = uuid.substring(4,8);
					   boolean exist = false;
						for(HashMap<String, String> sItem:gattCharacteristicGroupData){
							if(sItem.get("UUID").equals(uuid)){
								exist = true;
								break;
							}
						}
						if(exist){
							continue;
						}
						name = SampleGattAttributes.lookup(uuid, "Unknow");
					    currentCharaData.put("NAME", name);
					    currentCharaData.put("UUID", uuid);
					    gattCharacteristicGroupData.add(currentCharaData);
					    addItem(name, uuid);
				   }
				   status_text.setText(Constans.gattServiceData.get(servidx).get("NAME")+": Discovered");
			   }   
			};   
		disconverThread.start();
		
	    registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE));
    }
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			final String action = intent.getAction();
 			System.out.println("action = " + action);
 			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
 				String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
 				System.out.println("data========>" + data);
 			    
// 				if(uuid.equals("ffb0")){
// 					modifyFFB0(data);
// 				}else if(uuid.equals("ffb1")){
// 					modifyFFB1(data);
// 				}else if(uuid.equals("ffb2")){
// 					modifyFFB2(data);
// 				}else if(uuid.equals("ffb3")){
// 					modifyFFB3(data);
// 				}else if(uuid.equals("ffb4")){
// 					modifyFFB4(data);
// 				}else if(uuid.equals("ffb5")){
// 					modifyFFB5(data);
// 				}else if(uuid.equals("ffb6")){
// 					getFFB6(data);
// 				}else if(uuid.equals("ffb7")){
// 					getFFB7(data);
// 				}else if(uuid.equals("ffb8")){
// 					modifyFFB8(data);
// 				}else 
 				if(uuid.equals("2a19")){
 					System.out.println("this is battary life!!!!!!!!" + data + "=>16/ " + Integer.parseInt(data,16));
 					  Toast.makeText(CharacteristicsActivity.this, "Battery level :" +  Integer.parseInt(data,16) + "%",Toast.LENGTH_LONG).show();
 				}else if(uuid.equals("2a05")){
 					System.out.println("this is 2a05" + data + "=>16/ " + Integer.parseInt(data,16));
					  Toast.makeText(CharacteristicsActivity.this, "2a05:" +  Integer.parseInt(data,16),Toast.LENGTH_LONG).show();
 				}else if(uuid.equals("ffe1")){
 					System.out.println("this is ffe1" + data + "=>16/ " + Integer.parseInt(data,16));
 				}
 			}
 		}
 	};
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE));
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}
    private void addItem(String devname,String address)
    {
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	map.put("title", devname);
    	map.put("text", address);
    	listItem.add(map);
    	listItemAdapter.notifyDataSetChanged();
    }
    private void deleteItem()
    {
    	int size = listItem.size();
    	if( size > 0 )
    	{
    		listItem.remove(listItem.size() - 1);
    		listItemAdapter.notifyDataSetChanged();
    	}
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {			
			CharacteristicsActivity.this.finish();
			System.out.println("=========>onKeyDown");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
//    private void modifyFFB0(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb0,(ViewGroup)findViewById(R.id.ffb0_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("Pair Key");
//	    String keystr = "";
//	    for(int i=0;i<6;i++){
//	    	keystr += Integer.parseInt(data.substring(i*2,i*2+2),16) - 0x30;
//	    }
//	    final EditText key1 = (EditText)layout.findViewById(R.id.key1);
//	    key1.setText(keystr);
//	    final EditText key2 = (EditText)layout.findViewById(R.id.key2);
//	    key2.setText(keystr);
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = false;
//			  int key1_value = Integer.parseInt(key1.getText().toString());
//			  int key2_value = Integer.parseInt(key2.getText().toString());
//			  if(key1_value < 100000 || key1_value > 999999 || key2_value < 100000 || key2_value > 999999){
//				  Toast.makeText(CharacteristicsActivity.this, "The key must be 100000-999999",Toast.LENGTH_LONG).show();
//			  }else if(key1_value != key2_value){
//				  Toast.makeText(CharacteristicsActivity.this, "Input different",Toast.LENGTH_LONG).show();
//			  }else{
//				  flag = true;
//				  String data = ""+key1_value;
//				  BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//				  characteristic.setValue(data.getBytes());
//				  Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//			  }
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
//    private void modifyFFB1(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb1,(ViewGroup)findViewById(R.id.ffb1_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("Major&Minor ID");
//	    final EditText majorid = (EditText)layout.findViewById(R.id.majorid);
//	    majorid.setText(""+Integer.parseInt(data.substring(0,4),16));
//	    final EditText minorid = (EditText)layout.findViewById(R.id.minorid);
//	    minorid.setText(""+Integer.parseInt(data.substring(4,8),16));
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = false;
//		 	  int maid = Integer.parseInt(majorid.getText().toString());
//			  int miid = Integer.parseInt(minorid.getText().toString());
//			  if(maid < 0 || maid > 65535 || miid < 0 || miid > 65535){ 							
//			 	 Toast.makeText(CharacteristicsActivity.this, "MajorID or MinorID must be 0-65535",Toast.LENGTH_LONG).show();
//			  }else{
//				 flag = true;
//				 byte[] data = new byte[4];
//				 data[0] = (byte)(maid >> 8);
//				 data[1] = (byte)(maid & 0x00ff);
//				 data[2] = (byte)(miid >> 8);
//				 data[3] = (byte)(miid & 0x00ff);
//				 BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//				 characteristic.setValue(data);
//				 Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//			  }
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
//    private void modifyFFB2(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb2,(ViewGroup)findViewById(R.id.ffb2_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("iBeacon UUID");
//	    final EditText uuid1 = (EditText)layout.findViewById(R.id.uuid_1);
//	    uuid1.setText(data.substring(0,8));
//	    final EditText uuid2 = (EditText)layout.findViewById(R.id.uuid_2);
//	    uuid2.setText(data.substring(8,12));
//	    final EditText uuid3 = (EditText)layout.findViewById(R.id.uuid_3);
//	    uuid3.setText(data.substring(12,16));
//	    final EditText uuid4 = (EditText)layout.findViewById(R.id.uuid_4);
//	    uuid4.setText(data.substring(16,20));
//	    final EditText uuid5 = (EditText)layout.findViewById(R.id.uuid_5);
//	    uuid5.setText(data.substring(20,32));
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = false;
//		 	  String u1,u2,u3,u4,u5;
//		 	  u1 = uuid1.getText().toString();
//		 	  u2 = uuid2.getText().toString();
//		 	  u3 = uuid3.getText().toString();
//		 	  u4 = uuid4.getText().toString();
//		 	  u5 = uuid5.getText().toString();
//		 	  if(u1.length() != 8 || u2.length() != 4 || u3.length() != 4 || u4.length() != 4 || u5.length() != 12){
//		 		  Toast.makeText(CharacteristicsActivity.this, "UUID's size error",Toast.LENGTH_LONG).show();
//		 	  }else{
//		 		  flag = true;
//		 		  byte[] data = new byte[16];
//		 		  data[0] = (byte)Integer.parseInt(u1.substring(0,2),16);
//		 		  data[1] = (byte)Integer.parseInt(u1.substring(2,4),16);
//		 		  data[2] = (byte)Integer.parseInt(u1.substring(4,6),16);
//		 		  data[3] = (byte)Integer.parseInt(u1.substring(6,8),16);
//		 		  data[4] = (byte)Integer.parseInt(u2.substring(0,2),16);
//		 		  data[5] = (byte)Integer.parseInt(u2.substring(2,4),16);
//		 		  data[6] = (byte)Integer.parseInt(u3.substring(0,2),16);
//		 		  data[7] = (byte)Integer.parseInt(u3.substring(2,4),16);
//		 		  data[8] = (byte)Integer.parseInt(u4.substring(0,2),16);
//		 		  data[9] = (byte)Integer.parseInt(u4.substring(2,4),16);
//		 		  data[10] = (byte)Integer.parseInt(u5.substring(0,2),16);
//		 		  data[11] = (byte)Integer.parseInt(u5.substring(2,4),16);
//		 		  data[12] = (byte)Integer.parseInt(u5.substring(4,6),16);
//		 		  data[13] = (byte)Integer.parseInt(u5.substring(6,8),16);
//		 		  data[14] = (byte)Integer.parseInt(u5.substring(8,10),16);
//		 		  data[15] = (byte)Integer.parseInt(u5.substring(10,12),16);
//		 		 BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//				 characteristic.setValue(data);
//				 Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//		 	  }
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
//    private void modifyFFB3(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb3,(ViewGroup)findViewById(R.id.ffb3_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("Advertising Interval");
//	    final EditText interval = (EditText)layout.findViewById(R.id.interval);
//	    interval.setText(""+Integer.parseInt(data.substring(0,2),16));
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = false;
//			  int sec = Integer.parseInt(interval.getText().toString());
//			  if(sec < 1 || sec > 100){
//				  Toast.makeText(CharacteristicsActivity.this, "The value must be 1-100",Toast.LENGTH_LONG).show();
//			  }else{
//				  flag = true;
//				  byte[] data = new byte[1];
//				  data[0] = (byte)sec;
//				  BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//				  characteristic.setValue(data);
//				  Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//			  }
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
//    private void modifyFFB4(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb4,(ViewGroup)findViewById(R.id.ffb4_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("Device ID");
//	    byte[] didstr = new byte[data.length()/2];
//	    for(int i=0;i<data.length()/2;i++){
//	    	didstr[i] = (byte)Integer.parseInt(data.substring(i*2,i*2+2),16);
//	    }
//	    final EditText deviceid = (EditText)layout.findViewById(R.id.devid);
//	    deviceid.setText(new String(didstr));
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = false;
//			  String editstr = deviceid.getText().toString();
//			  if(editstr.length() < 1 || editstr.length() > 15){
//				  Toast.makeText(CharacteristicsActivity.this, "The string length must be 1-15",Toast.LENGTH_LONG).show();
//			  }else{
//				  flag = true;
//				  editstr = editstr + "#";
//				  BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//				  characteristic.setValue(editstr.getBytes());
//				  Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//			  }
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
//    private void modifyFFB5(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb5,(ViewGroup)findViewById(R.id.ffb5_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("Deployment Mode");
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = true;
//			  byte[] data = new byte[1];
//			  data[0] = 0;
//			  BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//			  characteristic.setValue(data);
//			  Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
//    private void getFFB6(String data){
//    	System.out.println("data==========>" + data);
//    	int x = Integer.parseInt(data.substring(0,2),16) + Integer.parseInt(data.substring(2,4),16)*256;
//    	int y = Integer.parseInt(data.substring(4,6),16) + Integer.parseInt(data.substring(6,8),16)*256;
//    	int z = Integer.parseInt(data.substring(8,10),16) + Integer.parseInt(data.substring(10,12),16)*256;
//    	int gx = Integer.parseInt(data.substring(16,18),16) + Integer.parseInt(data.substring(18,20),16)*256;
//    	int gy = Integer.parseInt(data.substring(20,22),16) + Integer.parseInt(data.substring(22,24),16)*256;
//    	int gz = Integer.parseInt(data.substring(24,26),16) + Integer.parseInt(data.substring(26,28),16)*256;
//    	if(x >= 0x8000){
//    		x = x - 0x10000;
//    	}
//    	if(y >= 0x8000){
//    		y = y - 0x10000;
//    	}
//    	if(z >= 0x8000){
//    		z = z - 0x10000;
//    	}
//    	if(gx >= 0x8000){
//    		gx = gx - 0x10000;
//    	}
//    	if(gy >= 0x8000){
//    		gy = gy - 0x10000;
//    	}
//    	if(gz >= 0x8000){
//    		gz = gz - 0x10000;
//    	}
//        Toast.makeText(CharacteristicsActivity.this, "x: "+x+" y: "+y+" z: "+z+" gx: "+gx+" gy: "+gy+" gz: "+gz,Toast.LENGTH_LONG).show();
//     }
//    private void getFFB7(String data){
//    	System.out.println("data==========>" + data);
//       int temperature = Integer.parseInt(data.substring(0,2),16) + Integer.parseInt(data.substring(2,4),16)*0x100 + Integer.parseInt(data.substring(4,6),16)*0x10000 + Integer.parseInt(data.substring(6,8),16)*0x1000000;
//       int pressure = Integer.parseInt(data.substring(8,10),16) + Integer.parseInt(data.substring(10,12),16)*0x100 + Integer.parseInt(data.substring(12,14),16)*0x10000 + Integer.parseInt(data.substring(14,16),16)*0x1000000;
//       float ftemp = (float)(temperature / 10.0);
//       Toast.makeText(CharacteristicsActivity.this, "Temp: "+ftemp+"  Pressure: "+pressure,Toast.LENGTH_LONG).show();
//    }
//    private void modifyFFB8(String data){
//    	LayoutInflater inflater = getLayoutInflater();
//	    View layout = inflater.inflate(R.layout.ffb8,(ViewGroup)findViewById(R.id.ffb8_dialog));
//
//	    AlertDialog.Builder builder = new AlertDialog.Builder(CharacteristicsActivity.this);
//	    builder.setView(layout);
//	    builder.setTitle("TxPower");
//	    int txp = Integer.parseInt(data.substring(0,2),16);
//	    if(txp >= 0x80){
//	    	txp = txp - 0x100;
//	    }
//	    final EditText txpower = (EditText)layout.findViewById(R.id.txpower);
//	    txpower.setText(""+txp);
//	    builder.setPositiveButton("Confirm", new OnClickListener(){
//	 	   @Override
//		   public void onClick(DialogInterface arg0, int arg1) {
//			  boolean flag = false;
//			  int tp = Integer.parseInt(txpower.getText().toString());
//			  if(tp > 0 || tp < -100){
//				  Toast.makeText(CharacteristicsActivity.this, "The value must be -1db ~ -100db",Toast.LENGTH_LONG).show();
//			  }else{
//				  flag = true;
//				  byte[] data = new byte[1];
//				  if(tp < 0){
//					  tp = tp + 0x100;
//				  }
//				  data[0] = (byte)tp;
//				  BluetoothGattCharacteristic characteristic = charas.get(charaidx);
//				  characteristic.setValue(data);
//				  Constans.mBluetoothLeService.wirteCharacteristic(characteristic);
//			  }
//			  try {//下面三句控制弹框的关闭
//                Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//                field.setAccessible(true);
//                field.set(arg0,flag);//true表示要关闭
//             } catch (Exception e) {
//                e.printStackTrace();
//             }	
//		  }
//	   });
//	   builder.setNegativeButton("Cancel", new OnClickListener(){
//		  @Override
//		  public void onClick(DialogInterface arg0, int arg1) {
//			try {//下面三句控制弹框的关闭
//               Field field = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
//               field.setAccessible(true);
//               field.set(arg0,true);//true表示要关闭
//            } catch (Exception e) {
//               e.printStackTrace();
//            }	
//		  }
//	   });
//	   builder.create().show();
//    }
}