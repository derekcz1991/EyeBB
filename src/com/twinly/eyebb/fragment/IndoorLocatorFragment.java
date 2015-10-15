package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.KidsListActivity;
import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.adapter.IndoorLocatorAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.dialog.IndoorLocatorOptionsDialog;
import com.twinly.eyebb.model.Area;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.Location;
import com.twinly.eyebb.model.SerializableChildrenMap;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

@SuppressLint("UseSparseArrays")
public class IndoorLocatorFragment extends Fragment implements
		PullToRefreshListener,
		IndoorLocatorAdapter.IndoorLocatorAdapterCallback {
	private final int TYPE_ENTER = 1;
	private final int TYPE_LEAVE = 2;

	private PullToRefreshListView listView;
	private ProgressBar progressBar;
	private LinearLayout secondMenu;
	private Spinner mSpinner;
	private TextView hint;
	private TextView areaName;
	private CallbackInterface callback;

	private SerializableChildrenMap myMap;
	private IndoorLocatorAdapter mIndoorLocatorAdapter;
	private long currentAreaId = -1L;
	private long otherID = 0L;
	// <area_id, Area>
	private HashMap<Long, Area> areaMap;
	// <location_id, Location>
	private HashMap<Long, Location> locationMap;
	// <child_id, Child>
	private HashMap<Long, ChildForLocator> childrenMap;
	// <area_id, <location_id, [child_id, child_id]>>
	private HashMap<Long, HashMap<Long, ArrayList<Long>>> curAreaMapLocaionMapChildren;
	private HashMap<Long, HashMap<Long, ArrayList<Long>>> preAreaMapLocaionMapChildren;
	private ArrayList<HashMap.Entry<Long, Area>> areaList;
	private List<HashMap.Entry<Long, ArrayList<Long>>> mList;		//long location ID, Arraylist ID
	private List<Long> locMonitoringList;
	
	private boolean autoUpdateFlag;
	private AutoUpdateTask autoUpdateTask;
	private boolean isViewAllRooms = false;
	private boolean isFirstUpdate = true;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder alertNotificationbuilder;
	private Notification alertNotificaion;
	private boolean isSoundOn;
	private boolean isVirbrateOn;

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
		secondMenu = (LinearLayout) v.findViewById(R.id.second_menu);
		secondMenu.setVisibility(View.INVISIBLE);
		mSpinner = (Spinner) v.findViewById(R.id.spinner);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);

		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		hint = (TextView) v.findViewById(R.id.hint);
		areaName = (TextView) v.findViewById(R.id.area_name);
		hint.setVisibility(View.INVISIBLE);

		myMap = new SerializableChildrenMap();
		setUpListener(v);
		buildNotification();
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		areaMap = new HashMap<Long, Area>();
		locationMap = new HashMap<Long, Location>();
		childrenMap = new HashMap<Long, ChildForLocator>();
		locMonitoringList = new ArrayList<Long>();
		curAreaMapLocaionMapChildren = new HashMap<Long, HashMap<Long, ArrayList<Long>>>();
		preAreaMapLocaionMapChildren = new HashMap<Long, HashMap<Long, ArrayList<Long>>>();

		mList = new ArrayList<Map.Entry<Long, ArrayList<Long>>>();
		mIndoorLocatorAdapter = new IndoorLocatorAdapter(getActivity(), mList,
				locationMap, childrenMap, isViewAllRooms, locMonitoringList,
				this);
		listView.setAdapter(mIndoorLocatorAdapter);

		if (SharePrefsUtils.isAutoUpdate(getActivity())) {
			autoUpdateFlag = true;
			listView.setLockPullAction(true);
			autoUpdateTask = new AutoUpdateTask();
			autoUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
						if (childrenMap.size() != 0)
							startActivity(intent);
					}
				});
		v.findViewById(R.id.btn_option).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								IndoorLocatorOptionsDialog.class);
						intent.putExtra(
								IndoorLocatorOptionsDialog.EXTRA_VIEW_ALL_ROOMS,
								isViewAllRooms);
						startActivityForResult(intent,
								ActivityConstants.REQUEST_GO_TO_OPTIONS_DIALOG);

					}
				});
		/*Switch kinder Garden*/
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {	

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				currentAreaId = areaList.get(position).getKey();
				areaName.setText(areaMap.get(currentAreaId).getDisplayName(
						getActivity()));
				mList.clear();
				Iterator<Entry<Long, ArrayList<Long>>> it = curAreaMapLocaionMapChildren
						.get(currentAreaId).entrySet().iterator();
				 
				while (it.hasNext()) {						
						mList.add(it.next());	
				}
				if(mList.isEmpty()){				
				}
				mIndoorLocatorAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	public void updateView() {
		new UpdateViewTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
							getActivity(), 5) * 1000) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
	/* Parse JSON to view*/
	private class UpdateViewTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (autoUpdateFlag == false && isFirstUpdate) {
				progressBar.setVisibility(View.VISIBLE);
				hint.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String result = HttpRequestUtils.get(
					HttpConstants.GET_CHILDREN_LOC_LIST, null);
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
							HttpConstants.GET_CHILDREN_LOC_LIST, null);
				}
			}

			System.out.println("childrenList = " + result);
			try {
				JSONObject json = new JSONObject(result);
				getAllAreaLocation(json);
				getAllChild(json);
				getUnlocatedChildren(json);
				checkMonitroingLoc();
				// copy curAreaMapLocaionMapChildren to mList
				preAreaMapLocaionMapChildren
						.put(currentAreaId,
								new HashMap<Long, ArrayList<Long>>(
										curAreaMapLocaionMapChildren
												.get(currentAreaId)));
				mList.clear();
				Iterator<Entry<Long, ArrayList<Long>>> it = curAreaMapLocaionMapChildren
						.get(currentAreaId).entrySet().iterator();
				while (it.hasNext()) {
					mList.add(it.next());
				}
				System.out.println("here");
			} catch (JSONException e) {
				System.out.println(HttpConstants.GET_CHILDREN_LOC_LIST
						+ " ---->> " + e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				mIndoorLocatorAdapter.notifyDataSetChanged();
				
				// set area name
				if (areaMap.get(currentAreaId) != null) {
					secondMenu.setVisibility(View.VISIBLE);
					setAeraSpinner();
					areaName.setText(areaMap.get(currentAreaId).getDisplayName(
							getActivity()));
				}
			}
			progressBar.setVisibility(View.INVISIBLE);
			if (curAreaMapLocaionMapChildren.size() == 0) {
				hint.setVisibility(View.VISIBLE);
			} else {
				hint.setVisibility(View.INVISIBLE);
			}
			callback.resetProgressBar();
			isFirstUpdate = false;
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
		JSONArray allLocationsJSONList = json
				.getJSONArray(HttpConstants.JSON_KEY_LOCATION_ALL);
		
		for (int i = 0; i < allLocationsJSONList.length(); i++) {
			System.out.println("start parse");
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
			System.out.println("AreaID:"+areaObject.getLong(HttpConstants.JSON_KEY_LOCATION_AREA_ID));
			System.out.println(areaMap.entrySet().toArray().length);
			/* get all locations */
			HashMap<Long, ArrayList<Long>> locationMapChildren;
			if (curAreaMapLocaionMapChildren.keySet()		//assign children to corresponding area
					.contains(area.getAreaId())) {
				locationMapChildren = curAreaMapLocaionMapChildren.get(area
						.getAreaId());
			} else {
				locationMapChildren = new HashMap<Long, ArrayList<Long>>();
			}
			JSONArray locationsJSONList = object
					.getJSONArray(HttpConstants.JSON_KEY_LOCATIONS);
			if(locationsJSONList.length() == 0){
				System.out.println("it is empty!!");
				otherID = area.getAreaId();
				Location location = new Location();
				location.setId(0L);
				location.setIcon(null);
				location.setName("for Testing");location.setNameSc("for Testing");location.setNameTc("for Testing");
				location.setType("O");
				locationMap.put(location.getId(), location);
				locationMapChildren.put(location.getId(), new ArrayList<Long>());
			}
				
			for (int j = 0; j < locationsJSONList.length(); j++) {
				JSONObject locationObject = (JSONObject) locationsJSONList
						.get(j);
				Location location = new Location();
				location.setId(locationObject
						.getLong(HttpConstants.JSON_KEY_LOCATION_ID));
				location.setName(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME));
				location.setNameTc(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME_TC));
				location.setNameSc(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_NAME_SC));
				location.setType(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_TYPE));
				location.setIcon(locationObject
						.getString(HttpConstants.JSON_KEY_LOCATION_ICON));

				locationMap.put(location.getId(), location);
				
				// clear previous location
				locationMapChildren
						.put(location.getId(), new ArrayList<Long>());
			}
			curAreaMapLocaionMapChildren.put(area.getAreaId(),
					locationMapChildren);
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
			HashMap<Long, ArrayList<Long>> locationMapChildren = curAreaMapLocaionMapChildren
					.get(areaId);

			JSONArray childrenBeanJSONList = object
					.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_BEAN);
			for (int j = 0; j < childrenBeanJSONList.length(); j++) {
				JSONObject childrenBeanObject = childrenBeanJSONList
						.getJSONObject(j);
				long childId = insertChild(childrenBeanObject);
				// set the first child as default report child
				if (i == 0) {
					if (SharePrefsUtils.getReportChildId(getActivity(), -1L) == -1L) {
						SharePrefsUtils
								.setReportChildId(getActivity(), childId);
					}
				}
				// if the child is located by router, show his location
				if (CommonUtils.isNotNull(childrenBeanObject
						.getString(HttpConstants.JSON_KEY_CHILD_LOC_ID))) {
					long locationId = childrenBeanObject
							.getLong(HttpConstants.JSON_KEY_CHILD_LOC_ID);
					if (locationMapChildren.containsKey(locationId))
						locationMapChildren.get(locationId).add(childId);
				}
			}
		}
	}
	/**
	 *  Show unlocated children
	 * */
	private void getUnlocatedChildren(JSONObject json) throws JSONException {
		JSONArray unLocatedChildrenJSONList = json
				.getJSONArray(HttpConstants.JSON_KEY_UNLOCATED_CHILDREN);
		HashMap<Long, ArrayList<Long>> locationMapChildren = curAreaMapLocaionMapChildren
				.get(otherID);
		
		System.out.println("area:"+otherID);
		for(int i = 0; i < unLocatedChildrenJSONList.length(); i++){
			JSONObject childObject = unLocatedChildrenJSONList.getJSONObject(i);
			
			Child child = DBChildren.getChildById(getActivity(),
					childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID));
			if (child == null) {
				child = new Child(
						childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID),
						
						childObject.getString(HttpConstants.JSON_KEY_CHILD_NAME),
						childObject.getString(HttpConstants.JSON_KEY_CHILD_ICON));
			System.out.println(childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID));
			}
			DBChildren.insert(getActivity(), child);
			ChildForLocator childForLocator = new ChildForLocator(child);
			//childForLocator.setLastAppearTime(0);
			childrenMap.put(child.getChildId(), childForLocator);		
			long id = 0L;
			if (locationMapChildren.containsKey(id))
				locationMapChildren.get(id).add(child.getChildId());			
		}
	}
	private long insertChild(JSONObject childrenBeanObject)
			throws JSONException {
		JSONObject childRelObject = childrenBeanObject
				.getJSONObject(HttpConstants.JSON_KEY_CHILD_REL);
		JSONObject childObject = childRelObject
				.getJSONObject(HttpConstants.JSON_KEY_CHILD);

		Child child = DBChildren.getChildById(getActivity(),
				childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID));
		if (child == null) {
			child = new Child(
					childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID),
					childObject.getString(HttpConstants.JSON_KEY_CHILD_NAME),
					childObject.getString(HttpConstants.JSON_KEY_CHILD_ICON));
		}
		child.setRelationWithUser(childRelObject
				.getString(HttpConstants.JSON_KEY_CHILD_RELATION));
		child.setMacAddress(childrenBeanObject
				.getString(HttpConstants.JSON_KEY_CHILD_MAC_ADDRESS));
		DBChildren.insert(getActivity(), child);
		
		ChildForLocator childForLocator = new ChildForLocator(child);
		childForLocator
				.setLastAppearTime(CommonUtils.isNull(childrenBeanObject
						.getString(HttpConstants.JSON_KEY_CHILD_LAST_APPEAR_TIME)) ? 0
						: childrenBeanObject
								.getLong(HttpConstants.JSON_KEY_CHILD_LAST_APPEAR_TIME));

		childrenMap.put(child.getChildId(), childForLocator);
		return child.getChildId();
	}

	private void checkMonitroingLoc() {
		if (preAreaMapLocaionMapChildren.size() > 0) {

			Iterator<Entry<Long, ArrayList<Long>>> it = curAreaMapLocaionMapChildren
					.get(currentAreaId).entrySet().iterator();
			//ArrayList<Long> enterChildren = new ArrayList<Long>();
			//ArrayList<Long> exitChildren = new ArrayList<Long>();
			while (it.hasNext()) {
				Entry<Long, ArrayList<Long>> entry = it.next();
				ArrayList<Long> curChildren = entry.getValue();
				ArrayList<Long> preChildren = new ArrayList<Long>(
						preAreaMapLocaionMapChildren.get(currentAreaId).get(
								entry.getKey()));
				if (locMonitoringList.contains(entry.getKey())) {
					for (int i = 0; i < curChildren.size(); i++) {
						if (preChildren.remove(curChildren.get(i)) == false) {
							// new children show in this location
							//enterChildren.add(curChildren.get(i));
							updateAlertNotification(entry.getKey(),
									curChildren.get(i), TYPE_ENTER);
						}
					}
					//exitChildren.addAll(preChildren);
					for (int i = 0; i < preChildren.size(); i++) {
						updateAlertNotification(entry.getKey(),
								preChildren.get(i), TYPE_LEAVE);
					}
				}
			}
			//System.out.println("enterChildren = " + enterChildren.size());
			//System.out.println("exitChildren = " + exitChildren.size());
		}
	}

	/**
	 * Set aera spinner
	 */
	private void setAeraSpinner() {
		areaList = new ArrayList<HashMap.Entry<Long, Area>>(areaMap.entrySet());
		String[] choices = new String[areaList.size()];
		for (int i = 0; i < areaList.size(); i++) {
			choices[i] = areaList.get(i).getValue()
					.getDisplayName(getActivity());
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				getActivity(), R.layout.item_spinner, choices);
		adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
		mSpinner.setAdapter(adapter);
	}

	private void updateAlertNotification(long locId, long childId, int type) {
		if (type == TYPE_ENTER) {
			alertNotificationbuilder.setContentText(childrenMap.get(childId)
					.getName()
					+ getString(R.string.text_enter)
					+ locationMap.get(locId).getDisplayName(getActivity()));
		} else if (type == TYPE_LEAVE) {
			alertNotificationbuilder.setContentText(childrenMap.get(childId)
					.getName()
					+ getString(R.string.text_leave)
					+ locationMap.get(locId).getDisplayName(getActivity()));
		}

		alertNotificaion = alertNotificationbuilder.build();
		alertNotificaion.defaults |= Notification.DEFAULT_LIGHTS;
		alertNotificaion.flags = Notification.FLAG_AUTO_CANCEL;
		if (isSoundOn) {
			alertNotificaion.defaults |= Notification.DEFAULT_SOUND;
		}
		if (isVirbrateOn) {
			alertNotificaion.defaults |= Notification.DEFAULT_VIBRATE;
		}
		// Issue the notification
		mNotificationManager.notify(Integer.parseInt(String.valueOf(
				System.currentTimeMillis()).substring(5)), alertNotificaion);
	}

	private void buildNotification() {
		mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		alertNotificationbuilder = new NotificationCompat.Builder(getActivity());
		alertNotificationbuilder.setSmallIcon(R.drawable.ic_location_default);
		alertNotificationbuilder
				.setContentTitle(getString(R.string.text_indoor_locator));

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(getActivity(), LancherActivity.class);
		resultIntent.setAction("android.intent.action.MAIN");
		resultIntent.addCategory("android.intent.category.LAUNCHER");
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				getActivity(), 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alertNotificationbuilder.setContentIntent(resultPendingIntent);

		isSoundOn = SharePrefsUtils.isSoundOn(getActivity());
		isVirbrateOn = SharePrefsUtils.isVibrateOn(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_OPTIONS_DIALOG) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				if (autoUpdateFlag != data.getBooleanExtra(
						IndoorLocatorOptionsDialog.EXTRA_AUTO_REFRESH,
						autoUpdateFlag)) {
					autoUpdateFlag = !autoUpdateFlag;
					if (autoUpdateFlag) {
						listView.setLockPullAction(true);
						autoUpdateTask = new AutoUpdateTask();
						autoUpdateTask.execute();
					} else {
						listView.setLockPullAction(false);
						if (autoUpdateTask != null)
							autoUpdateTask.cancel(true);
					}
				}

				if (isViewAllRooms != data.getBooleanExtra(
						IndoorLocatorOptionsDialog.EXTRA_VIEW_ALL_ROOMS,
						isViewAllRooms)) {
					isViewAllRooms = !isViewAllRooms;

					mList.clear();
					Iterator<Entry<Long, ArrayList<Long>>> it = curAreaMapLocaionMapChildren
							.get(currentAreaId).entrySet().iterator();
					while (it.hasNext()) {
						mList.add(it.next());
					}
					mIndoorLocatorAdapter.setViewAllRooms(isViewAllRooms);
					mIndoorLocatorAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onMonitoringSwitch(boolean checked, long locId) {
		if (checked) {
			if (!locMonitoringList.contains(locId)) {
				locMonitoringList.add(locId);
			}
		} else {
			locMonitoringList.remove(locId);
		}
	}

}
