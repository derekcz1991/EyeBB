package com.twinly.eyebb.activity;

import java.util.ArrayList;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.SupportMapFragment;


import com.twinly.eyebb.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

public class SchoolBusTrackingBaiduActivity extends FragmentActivity {
	private SupportMapFragment map;
	private BaiduMap mBaiduMap;
	
//	private ArrayList<LatLng> routePoints;
//	private ArrayList<LatLng> displayPoints;
//	private PolylineOptions polylineOptions;
//	private Polyline polyline;
//	private Marker marker;
	private int mProgress;
	private int speed = 5;
	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_bus);
		setTitle(getString(R.string.text_school_bus_tracking));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
		
		MapStatus ms = new MapStatus.Builder().overlook(-20).zoom(15).build();
		BaiduMapOptions bo = new BaiduMapOptions().mapStatus(ms)
				.compassEnabled(false).zoomControlsEnabled(false);
		map = SupportMapFragment.newInstance(bo);
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(R.id.map, map, "map_fragment").commit();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
