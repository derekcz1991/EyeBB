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
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twinly.twinly.R;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.dialog.ChildDialog;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.GPSLocation;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class DisplayLocationActivity extends FragmentActivity {
	private GoogleMap mMap;
	private ChildForLocator childForLoator;
	private List<GPSLocation> gpsLocationList;
	private boolean showCurLocation = true;

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
				mMap.clear();
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
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
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
		showCurLocation = false;
		for (int i = 0; i < gpsLocationList.size(); i++) {
			if (i == 0) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						gpsLocationList.get(i).getLatitude(), gpsLocationList
								.get(i).getLongitude()), 16));
				mMap.addCircle(new CircleOptions()
						.center(new LatLng(
								gpsLocationList.get(i).getLatitude(),
								gpsLocationList.get(i).getLongitude()))
						.radius(gpsLocationList.get(i).getRadius())
						.strokeWidth(1).strokeColor(0xFF88b5dc)
						.fillColor(1409351437));
			}
			mMap.addMarker(new MarkerOptions()
					.alpha(1 - (i / 20))
					.position(
							new LatLng(gpsLocationList.get(i).getLatitude(),
									gpsLocationList.get(i).getLongitude()))
					.title(CommonUtils
							.ConvertTimestampToDateFormat(gpsLocationList
									.get(i).getTimestamp())));
		}
	}

	private void displayCurLocation() {
		showCurLocation = true;
		if (gpsLocationList.size() > 0) {
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					gpsLocationList.get(0).getLatitude(), gpsLocationList
							.get(0).getLongitude()), 16));
			mMap.addMarker(new MarkerOptions().position(
					new LatLng(gpsLocationList.get(0).getLatitude(),
							gpsLocationList.get(0).getLongitude())).title(
					CommonUtils.ConvertTimestampToDateFormat(gpsLocationList
							.get(0).getTimestamp())));
			mMap.addCircle(new CircleOptions()
					.center(new LatLng(gpsLocationList.get(0).getLatitude(),
							gpsLocationList.get(0).getLongitude()))
					.radius(gpsLocationList.get(0).getRadius()).strokeWidth(1)
					.strokeColor(0xFF88b5dc).fillColor(1409351437));
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
