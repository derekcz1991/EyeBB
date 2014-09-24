package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.activity.KidsListActivity;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.adapter.IndoorLocatorAdapter;
import com.twinly.eyebb.bluetooth.DeviceListAcitivity;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.SerializableChildrenMap;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class IndoorLocatorFragment extends Fragment implements
		PullToRefreshListener {
	private PullToRefreshListView listView;
	private CallbackInterface callback;
	private Map<String, Child> childrenMap;
	private SerializableChildrenMap myMap;
	private Map<String, ArrayList<String>> indoorLocatorData;
	private IndoorLocatorAdapter adapter;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * @param value current progress
		 */
		public void updateProgressBar(int value);

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

		indoorLocatorData = new HashMap<String, ArrayList<String>>();
		childrenMap = DBChildren.getChildrenMap(getActivity());
		myMap = new SerializableChildrenMap();
		setUpListener(v);
		return v;
	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									BeepDialog.class);
							startActivity(intent);
						}

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

		v.findViewById(R.id.btn_kidslist).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								KidsListActivity.class);
//						Intent intent = new Intent(getActivity(),
//								DeviceListAcitivity.class);
						myMap.setMap(childrenMap);
						Bundle bundle = new Bundle();
						bundle.putSerializable("childrenMap", myMap);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
	}

	public void updateListView() {
		new UpdateView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void updateProgressBar(int value) {
		callback.updateProgressBar(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listView.setRefreshing(true);
			indoorLocatorData.clear();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return HttpRequestUtils.get("reportService/api/childrenList", null);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("childrenList = " + result);
			try {
				JSONObject json = new JSONObject(result);
				JSONArray list = json
						.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_LIST);
				for (int i = 0; i < list.length(); i++) {
					JSONObject object = (JSONObject) list.get(i);
					String childId = object
							.getString(HttpConstants.JSON_KEY_CHILD_ID);
					String locationName = object.getJSONObject(
							HttpConstants.JSON_KEY_LOCATION).getString(
							HttpConstants.JSON_KEY_LOCATION_NAME);
					updateLocationData(childId, locationName);
				}
				adapter = new IndoorLocatorAdapter(getActivity(),
						indoorLocatorData, childrenMap);
				listView.setAdapter(adapter);

				listView.setRefreshing(false);
				callback.resetProgressBar();
			} catch (JSONException e) {
				System.out.println("reportService/api/childrenList ---->> "
						+ e.getMessage());
				listView.setRefreshing(false);
				callback.resetProgressBar();
			}
		}

	}

	private void updateLocationData(String childId, String locationName) {
		ArrayList<String> childrenIdList = null;
		if (indoorLocatorData.keySet().contains(locationName)) {
			childrenIdList = indoorLocatorData.get(locationName);
		} else {
			childrenIdList = new ArrayList<String>();
		}
		childrenIdList.add(childId);
		indoorLocatorData.put(locationName, childrenIdList);
	}
}
