package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.twinly.twinly.R;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.dialog.ChildDialog;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.GPSLocation;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class DisplayLocationBaiduActivity extends FragmentActivity {

	private SupportMapFragment mMap;
	private ChildForLocator childForLoator;
	private List<GPSLocation> gpsLocationList;
	private boolean showCurLocation = true;

	BitmapDescriptor bd = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_display_location);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		childForLoator = (ChildForLocator) getIntent().getExtras()
				.getSerializable(ChildDialog.EXTRA_CHILD);
		setTitle(childForLoator.getName());
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_display_location, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_options) {
			if (mMap != null) {
				mMap.getBaiduMap().clear();
				if (showCurLocation) {
					item.setTitle(R.string.btn_cur_location);
					displayTrajectory();
				} else {
					item.setTitle(R.string.btn_trajectory);
					displayCurLocation();
				}
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			MapStatus ms = new MapStatus.Builder().overlook(-20).zoom(15)
					.build();
			BaiduMapOptions bo = new BaiduMapOptions().mapStatus(ms)
					.compassEnabled(false).zoomControlsEnabled(false);
			mMap = SupportMapFragment.newInstance(bo);
			FragmentManager manager = getSupportFragmentManager();
			manager.beginTransaction().add(R.id.map, mMap, "map_fragment")
					.commit();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		//mMap.setMyLocationEnabled(true);
		new GetLocationTask().execute();
	}

	private void displayTrajectory() {
		CoordinateConverter converter = new CoordinateConverter();
		showCurLocation = false;
		for (int i = 0; i < gpsLocationList.size(); i++) {
			//Convert coordinate from WGS84 to BAIDU09
			converter.from(CoordType.GPS);
			converter.coord(new LatLng(gpsLocationList.get(i).getLatitude(),
					gpsLocationList.get(i).getLongitude()));
			LatLng currentLocation = converter.convert();
			if (i == 0) {

				MapStatus mMapStatus = new MapStatus.Builder()
						.target(currentLocation).zoom(16).build();
				MapStatusUpdate msu = MapStatusUpdateFactory
						.newMapStatus(mMapStatus);
				mMap.getBaiduMap().setMapStatus(msu);

				mMap.getBaiduMap().addOverlay(
						(new CircleOptions()
								.center(currentLocation)
								.radius((int) gpsLocationList.get(i)
										.getRadius()).fillColor(1409351437)
								.stroke(new Stroke(5, 0xFF88b5dc))));
			}
			mMap.getBaiduMap()
					.addOverlay(
							(new MarkerOptions().position(currentLocation)
									.icon(bd).title(CommonUtils
									.ConvertTimestampToDateFormat(gpsLocationList
											.get(i).getTimestamp()))));
		}
	}

	private void displayCurLocation() {
		CoordinateConverter converter = new CoordinateConverter();
		showCurLocation = true;

		if (gpsLocationList.size() > 0) {
			//Convert coordinate from WGS84 to BAIDU09			
			converter.from(CoordType.GPS);
			converter.coord(new LatLng(gpsLocationList.get(0).getLatitude(),
					gpsLocationList.get(0).getLongitude()));
			LatLng currentLocation = converter.convert();

			MapStatus mMapStatus = new MapStatus.Builder()
					.target(currentLocation).zoom(16).build();
			MapStatusUpdate msu = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			mMap.getBaiduMap().setMapStatus(msu);

			mMap.getBaiduMap()
					.addOverlay(
							(new MarkerOptions().position(currentLocation)
									.icon(bd).title(CommonUtils
									.ConvertTimestampToDateFormat(gpsLocationList
											.get(0).getTimestamp()))));

			mMap.getBaiduMap().addOverlay(
					(new CircleOptions().center(currentLocation)
							.radius((int) gpsLocationList.get(0).getRadius())
							.fillColor(1409351437).stroke(new Stroke(3,
							0xFF88b5dc))));
		}
	}

	private class GetLocationTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childForLoator.getChildId()));
			map.put("hoursBefore", "6");
			return HttpRequestUtils.post(HttpConstants.GET_GPS_LOCATIONS, map);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				System.out.println("HttpConstants.GET_GPS_LOCATIONS" + "==>>"
						+ result);
				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = jsonObject
						.getJSONArray(HttpConstants.JSON_KEY_GPS_LOCATION_LIST);
				gpsLocationList = new ArrayList<GPSLocation>();
				for (int i = 0; i < jsonArray.length(); i++) {
					gpsLocationList.add(new GPSLocation(jsonArray
							.getJSONObject(i).getDouble(
									HttpConstants.JSON_KEY_GPS_LATITUDE),
							jsonArray.getJSONObject(i).getDouble(
									HttpConstants.JSON_KEY_GPS_LONGITUDE),
							jsonArray.getJSONObject(i).getDouble(
									HttpConstants.JSON_KEY_GPS_RADIUS),
							jsonArray.getJSONObject(i).getLong(
									HttpConstants.JSON_KEY_GPS_TIMESTAMP)));
				}
				Collections.sort(gpsLocationList,
						new Comparator<GPSLocation>() {

							@Override
							public int compare(GPSLocation lhs, GPSLocation rhs) {
								return (int) (rhs.getTimestamp() - lhs
										.getTimestamp());
							}
						});
				displayCurLocation();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
