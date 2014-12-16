package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.KidsListActivity;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.adapter.IndoorLocatorAdapter;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Area;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Location;
import com.twinly.eyebb.model.SerializableChildrenMap;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class IndoorLocatorFragment extends Fragment implements
		PullToRefreshListener {
	private PullToRefreshListView listView;
	private ProgressBar progressBar;
	private TextView hint;
	private TextView areaName;
	private CallbackInterface callback;

	private SerializableChildrenMap myMap;
	private IndoorLocatorAdapter adapter;
	private long currentAreaId = -1L;
	private HashMap<Long, Area> areaMap; // <area_id, Area>
	private HashMap<Long, Location> locationMap; // <location_id, Location>
	private HashMap<Long, Child> childrenMap; // <child_id, Child>
	private HashMap<Long, ArrayList<Long>> areaMapLocaion; // <area_id, [location_id, location_id]>
	private HashMap<Long, ArrayList<Long>> locationMapChildren; // <location_id, [child_id, child_id]>
	private HashMap<Long, HashMap<Long, ArrayList<Long>>> areaMapLocaionMapChildren;
	private ArrayList<HashMap.Entry<Long, Area>> areaList;

	private ToggleButton autoUpdateButton;
	private boolean autoUpdateFlag;
	private AutoUpdateTask autoUpdateTask;
	private boolean isSort = false;
	private boolean isFirstUpdate = true;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * 
		 * @param value
		 *            current progress
		 */
		public void updateProgressBarForIndoorLocator(int value);

		/**
		 * Cancel update the progressBar when release the listView
		 */
		public void cancelProgressBar();

		/**
		 * Reset the progressBar when finishing to update listView
		 */
		public void resetProgressBar();
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_indoor_locator, container,
				false);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);

		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		hint = (TextView) v.findViewById(R.id.hint);
		areaName = (TextView) v.findViewById(R.id.area_name);
		hint.setVisibility(View.INVISIBLE);
		autoUpdateButton = (ToggleButton) v.findViewById(R.id.autoUpdateButton);

		myMap = new SerializableChildrenMap();
		setUpListener(v);
		return v;
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		areaMap = new HashMap<Long, Area>();
		locationMap = new HashMap<Long, Location>();
		childrenMap = new HashMap<Long, Child>();
		areaMapLocaion = new HashMap<Long, ArrayList<Long>>();
		locationMapChildren = new HashMap<Long, ArrayList<Long>>();
		areaMapLocaionMapChildren = new HashMap<Long, HashMap<Long, ArrayList<Long>>>();

		if (SharePrefsUtils.isAutoUpdate(getActivity())) {
			autoUpdateFlag = true;
			listView.setLockPullAction(true);
			autoUpdateTask = new AutoUpdateTask();
			autoUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			autoUpdateButton.setChecked(true);
		} else {
			updateView();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		autoUpdateFlag = false;
		if (autoUpdateTask != null) {
			autoUpdateTask.cancel(true);
		}
	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});

		v.findViewById(R.id.btn_shcool_bus).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SchoolBusTrackingActivity.class);
						startActivity(intent);

					}
				});

		// children dialog
		v.findViewById(R.id.btn_kidslist).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								KidsListActivity.class);
						myMap.setMap(childrenMap);
						Bundle bundle = new Bundle();
						bundle.putSerializable("childrenMap", myMap);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		v.findViewById(R.id.sortButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						isSort = !isSort;
						adapter = new IndoorLocatorAdapter(getActivity(),
								areaMapLocaionMapChildren.get(currentAreaId),
								locationMap, childrenMap, isSort);
						listView.setAdapter(adapter);
					}
				});

		v.findViewById(R.id.autoUpdateButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (autoUpdateFlag) {
							SharePrefsUtils.setAutoUpdate(getActivity(), false);
							autoUpdateFlag = false;
							listView.setLockPullAction(false);
							if (autoUpdateTask != null)
								autoUpdateTask.cancel(true);
						} else {
							SharePrefsUtils.setAutoUpdate(getActivity(), true);
							autoUpdateFlag = true;
							listView.setLockPullAction(true);
							autoUpdateTask = new AutoUpdateTask();
							autoUpdateTask.execute();
						}
					}
				});
		areaName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				areaList = new ArrayList<HashMap.Entry<Long, Area>>(areaMap
						.entrySet());
				String[] choices = new String[areaList.size()];
				for (int i = 0; i < areaList.size(); i++) {
					choices[i] = areaList.get(i).getValue()
							.getDisplayName(getActivity());
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setItems(choices,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentAreaId = areaList.get(which).getKey();
								adapter = new IndoorLocatorAdapter(
										getActivity(),
										areaMapLocaionMapChildren
												.get(currentAreaId),
										locationMap, childrenMap, isSort);
								listView.setAdapter(adapter);
							}
						});

				builder.create().show();
			}
		});
	}

	public void updateView() {
		new UpdateView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void updateProgressBar(int value) {
		if (callback != null)
			callback.updateProgressBarForIndoorLocator(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	private class AutoUpdateTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (autoUpdateFlag) {
				updateView();
				try {
					Thread.sleep(SharePrefsUtils.getAutoUpdateTime(
							getActivity(), 5) * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	private class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (autoUpdateFlag == false && isFirstUpdate) {
				progressBar.setVisibility(View.VISIBLE);
				hint.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String result = HttpRequestUtils.get(
					"reportService/api/childrenList", null);
			if (autoUpdateFlag == false) {
				try {
					new JSONObject(result);
				} catch (JSONException e) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					result = HttpRequestUtils.get(
							"reportService/api/childrenList", null);
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("childrenList = " + result);
			try {
				isFirstUpdate = false;

				JSONObject json = new JSONObject(result);
				getAllAreaLocation(json);
				getAllChild(json);
				adapter = new IndoorLocatorAdapter(getActivity(),
						areaMapLocaionMapChildren.get(currentAreaId),
						locationMap, childrenMap, isSort);
				listView.setAdapter(adapter);
				// set area name
				if (areaMap.get(currentAreaId) != null) {
					areaName.setText(areaMap.get(currentAreaId).getDisplayName(
							getActivity()));
				}

			} catch (JSONException e) {
				System.out.println("reportService/api/childrenList ---->> "
						+ e.getMessage());
			}
			progressBar.setVisibility(View.INVISIBLE);
			if (areaMapLocaionMapChildren.size() == 0) {
				hint.setVisibility(View.VISIBLE);
			} else {
				hint.setVisibility(View.INVISIBLE);
			}

			callback.resetProgressBar();
		}

	}

	/**
	 * Parse json data get all area and its locations
	 * 
	 * @param json
	 * @throws JSONException
	 */
	private void getAllAreaLocation(JSONObject json) throws JSONException {
		if (areaMap != null) {
			areaMap.clear();
		}
		if (locationMap != null) {
			locationMap.clear();
		}
		if (areaMapLocaion != null) {
			areaMapLocaion.clear();
		}
		if (locationMapChildren != null) {
			locationMapChildren.clear();
		}
		JSONArray allLocationsJSONList = json
				.getJSONArray(HttpConstants.JSON_KEY_LOCATION_ALL);

		for (int i = 0; i < allLocationsJSONList.length(); i++) {
			JSONObject object = (JSONObject) allLocationsJSONList.get(i);
			JSONObject areaObject = object
					.getJSONObject(HttpConstants.JSON_KEY_LOCATION_AREA);
			if (i == 0) {
				currentAreaId = areaObject
						.getLong(HttpConstants.JSON_KEY_LOCATION_AREA_ID);
			}
			Area area = new Area();
			area.setAreaId(areaObject
					.getLong(HttpConstants.JSON_KEY_LOCATION_AREA_ID));
			area.setIcon(areaObject
					.getString(HttpConstants.JSON_KEY_LOCATION_AREA_ICON));
			area.setName(areaObject
					.getString(HttpConstants.JSON_KEY_LOCATION_AREA_NAME));
			area.setNameTc(areaObject
					.getString(HttpConstants.JSON_KEY_LOCATION_AREA_NAME_TC));
			area.setNameSc(areaObject
					.getString(HttpConstants.JSON_KEY_LOCATION_AREA_NAME_SC));
			areaMap.put(area.getAreaId(), area);

			areaMapLocaion.put(area.getAreaId(), new ArrayList<Long>());

			JSONArray locationsJSONList = object
					.getJSONArray(HttpConstants.JSON_KEY_LOCATIONS);
			for (int j = 0; j < locationsJSONList.length(); j++) {
				JSONObject locationObject = (JSONObject) locationsJSONList
						.get(j);
				Location location = new Location();
				location.setId(locationObject
						.getLong(HttpConstants.JSON_KEY_LOCATION_ID));
				location.setName(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME));
				location.setType(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_TYPE));
				location.setIcon(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_ICON));
				locationMap.put(location.getId(), location);

				locationMapChildren
						.put(location.getId(), new ArrayList<Long>());
				areaMapLocaion.get(area.getAreaId()).add(location.getId());
			}
		}
	}

	/**
	 * Parse json data get all children informations and their location
	 * 
	 * @param json
	 * @throws JSONException
	 */
	private void getAllChild(JSONObject json) throws JSONException {
		if (childrenMap != null) {
			childrenMap.clear();
		}

		JSONArray childrenByAreaJSONList = json
				.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_BY_AREA);
		for (int i = 0; i < childrenByAreaJSONList.length(); i++) {
			JSONObject object = childrenByAreaJSONList.getJSONObject(i);

			long areaId = object
					.getLong(HttpConstants.JSON_KEY_LOCATION_AREA_ID);
			areaMapLocaionMapChildren.put(areaId, locationMapChildren);

			JSONArray childrenBeanJSONList = object
					.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_BEAN);
			for (int j = 0; j < childrenBeanJSONList.length(); j++) {
				JSONObject childrenBeanObject = childrenBeanJSONList
						.getJSONObject(j);
				long childId = insertChild(childrenBeanObject);
				// set the first child as default report child
				if (i == 0) {
					SharePrefsUtils.setReportChildId(getActivity(), childId);
				}
				// if the child is located by router, show his location
				if (CommonUtils.isNotNull(childrenBeanObject
						.getString(HttpConstants.JSON_KEY_CHILD_LOC_ID))) {
					long locationId = childrenBeanObject
							.getLong(HttpConstants.JSON_KEY_CHILD_LOC_ID);

					if (locationMapChildren.get(locationId) == null) {
						locationMapChildren.put(locationId,
								new ArrayList<Long>());
					}
					locationMapChildren.get(locationId).add(childId);
				}

			}
		}
	}

	private long insertChild(JSONObject childrenBeanObject)
			throws JSONException {
		JSONObject childRelObject = childrenBeanObject
				.getJSONObject(HttpConstants.JSON_KEY_CHILD_REL);
		JSONObject childObject = childRelObject
				.getJSONObject(HttpConstants.JSON_KEY_CHILD);

		Child child = new Child(
				childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID),
				childObject.getString(HttpConstants.JSON_KEY_CHILD_NAME),
				childObject.getString(HttpConstants.JSON_KEY_CHILD_ICON));
		child.setRelationWithUser(childRelObject
				.getString(HttpConstants.JSON_KEY_CHILD_RELATION));
		child.setMacAddress(childrenBeanObject
				.getString(HttpConstants.JSON_KEY_CHILD_MAC_ADDRESS));
		// get parents' phone
		if (CommonUtils.isNotNull(childrenBeanObject
				.getString(HttpConstants.JSON_KEY_PARENTS))) {
			JSONObject parentObject = childrenBeanObject
					.getJSONObject(HttpConstants.JSON_KEY_PARENTS);
			child.setPhone(parentObject
					.getString(HttpConstants.JSON_KEY_PARENTS_PHONE));
		}
		child.setLastAppearTime(childrenBeanObject
				.getLong(HttpConstants.JSON_KEY_CHILD_LAST_APPEAR_TIME));
		DBChildren.insert(getActivity(), child);

		childrenMap.put(child.getChildId(), child);
		return child.getChildId();
	}

}
