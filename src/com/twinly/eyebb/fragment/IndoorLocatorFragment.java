package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eyebb.R;
import com.twinly.eyebb.activity.KidsListActivity;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.adapter.IndoorLocatorAdapter;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBChildren;
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
	private CallbackInterface callback;
	private Map<String, Child> childrenMap;
	private SerializableChildrenMap myMap;
	private Map<Location, ArrayList<String>> indoorLocatorData;
	private IndoorLocatorAdapter adapter;

	private ToggleButton autoUpdateButton;
	private boolean autoUpdateFlag;
	private AutoUpdateTask autoUpdateTask;
	private boolean isSort = false;

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
		hint.setVisibility(View.INVISIBLE);
		autoUpdateButton = (ToggleButton) v.findViewById(R.id.autoUpdateButton);

		indoorLocatorData = new HashMap<Location, ArrayList<String>>();
		childrenMap = DBChildren.getChildrenMap(getActivity());
		myMap = new SerializableChildrenMap();
		setUpListener(v);
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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

	/**
	 * beepall will post children data (ID and macAddress) to the server
	 * @param v
	 */
	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						/*if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									BeepDialog.class);
							startActivity(intent);
						}*/

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
								indoorLocatorData, childrenMap, isSort);
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
					// refresh time
					long refreshTime = 5;
					try {
						if (Long.parseLong(SharePrefsUtils
								.refreshTime(getActivity())) > 0) {
							refreshTime = Long.parseLong(SharePrefsUtils
									.refreshTime(getActivity())) * 1000;
						} else {
							SharePrefsUtils.setRefreshTime(getActivity(),
									refreshTime + "");
							refreshTime = refreshTime * 1000;
						}
					} catch (NumberFormatException e) {
						SharePrefsUtils.setRefreshTime(getActivity(),
								refreshTime + "");
						refreshTime = refreshTime * 1000;
						e.printStackTrace();
					}
					Thread.sleep(refreshTime);
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
			progressBar.setVisibility(View.VISIBLE);
			hint.setVisibility(View.INVISIBLE);
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
				JSONObject json = new JSONObject(result);
				getAllLocation(json);
				getAllChild(json);
				adapter = new IndoorLocatorAdapter(getActivity(),
						indoorLocatorData, childrenMap, isSort);
				listView.setAdapter(adapter);

			} catch (JSONException e) {
				System.out.println("reportService/api/childrenList ---->> "
						+ e.getMessage());
			}
			progressBar.setVisibility(View.INVISIBLE);
			if (indoorLocatorData.size() <= 1) {
				hint.setVisibility(View.VISIBLE);
			} else {
				hint.setVisibility(View.INVISIBLE);
			}

			callback.resetProgressBar();
		}

	}

	/**
	 * Parse json data get all locations
	 * @param json
	 * @throws JSONException 
	 */
	private void getAllLocation(JSONObject json) throws JSONException {
		if (indoorLocatorData != null) {
			indoorLocatorData.clear();
		}
		JSONArray locationJSONList = json
				.getJSONArray(HttpConstants.JSON_KEY_LOCATION_ALL);

		Location location = new Location();
		location.setId(0);
		location.setName("kindergarten");
		location.setType("title");
		indoorLocatorData.put(location, new ArrayList<String>());

		for (int i = 0; i < locationJSONList.length(); i++) {
			JSONObject object = (JSONObject) locationJSONList.get(i);
			location = new Location();
			location.setId(object.getLong(HttpConstants.JSON_KEY_LOCATION_ID));
			location.setName(object
					.getString(HttpConstants.JSON_KEY_LOCATION_NAME));
			location.setType(object
					.getString(HttpConstants.JSON_KEY_LOCATION_TYPE));
			indoorLocatorData.put(location, new ArrayList<String>());
		}
	}

	/**
	 * Parse json data get all children informations and location
	 * @param json
	 * @throws JSONException 
	 */
	private void getAllChild(JSONObject json) throws JSONException {
		childrenMap = DBChildren.getChildrenMap(getActivity());
		JSONArray childJSONList = json
				.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_LIST);
		for (int i = 0; i < childJSONList.length(); i++) {
			JSONObject object = (JSONObject) childJSONList.get(i);
			insertChild(object);

			String childId = object.getString(HttpConstants.JSON_KEY_CHILD_ID);
			if (CommonUtils.isNotNull(object
					.getString(HttpConstants.JSON_KEY_LOCATION_TIME))) {
				JSONObject locationTime = object
						.getJSONObject(HttpConstants.JSON_KEY_LOCATION_TIME);
				childrenMap
						.get(childId)
						.setLastAppearTime(
								locationTime
										.getLong(HttpConstants.JSON_KEY_LOCATION_LAST_APPEAR_TIME));

				JSONObject locationJSON = locationTime
						.getJSONObject(HttpConstants.JSON_KEY_LOCATION);
				Location location = new Location();
				location.setId(locationJSON
						.getLong(HttpConstants.JSON_KEY_LOCATION_ID));
				location.setName(locationJSON
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME));
				location.setType(HttpConstants.JSON_KEY_LOCATION_TYPE);
				childrenMap.get(childId).setLocationName(location.getName());
				updateLocationData(childId, location);
			}

		}
	}

	private void insertChild(JSONObject object) throws JSONException {
		Child child = new Child(object.getInt(HttpConstants.JSON_KEY_CHILD_ID),
				object.getString(HttpConstants.JSON_KEY_CHILD_NAME),
				object.getString(HttpConstants.JSON_KEY_CHILD_ICON));
		child.setMacAddress(object
				.getString(HttpConstants.JSON_KEY_CHILD_MAC_ADDRESS));
		// get parents' phone
		if (CommonUtils.isNotNull(object
				.getString(HttpConstants.JSON_KEY_PARENTS))) {
			JSONArray parents = object
					.getJSONArray(HttpConstants.JSON_KEY_PARENTS);
			if (parents != null) {
				JSONObject parent = (JSONObject) parents.get(0);
				child.setPhone(parent
						.getString(HttpConstants.JSON_KEY_PARENTS_PHONE));
			}
		}

		DBChildren.insert(getActivity(), child);
	}

	private void updateLocationData(String childId, Location location) {
		ArrayList<String> childrenIdList = null;
		Iterator<Location> Iterator = indoorLocatorData.keySet().iterator();
		boolean locationExist = false;
		Location preLocation = null;
		while (Iterator.hasNext()) {
			preLocation = Iterator.next();
			if (preLocation.getId() == location.getId()) {
				childrenIdList = indoorLocatorData.get(preLocation);
				locationExist = true;
				break;
			}
		}
		if (locationExist == false) {
			childrenIdList = new ArrayList<String>();
		}
		childrenIdList.add(childId);
		if (locationExist) {
			indoorLocatorData.put(preLocation, childrenIdList);
		} else {
			indoorLocatorData.put(location, childrenIdList);
		}
	}
}
