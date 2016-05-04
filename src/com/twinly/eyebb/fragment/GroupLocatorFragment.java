package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.GroupLocatorAdapter;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.Group;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class GroupLocatorFragment extends LocatorFragment implements
		OnClickListener {

	public static String TAG = "GroupLocatorFragment";
	private GroupLocatorAdapter groupLocatorAdapter;
	private ArrayList<Group> groupList;
	private ArrayList<Group> displayList;

	@Override
	protected void setResource() {
		layoutRes = R.layout.fragment_group_locator;
		requestUrl = "reportService/api/viewmygroup";
	}

	@Override
	protected void setUpView(View v) {
		super.setUpView(v);
		tag = "GroupLocatorFragment";
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		groupList = new ArrayList<Group>();
		displayList = new ArrayList<Group>();
		groupLocatorAdapter = new GroupLocatorAdapter(getActivity(),
				displayList);
		listView.setAdapter(groupLocatorAdapter);
	}

	@Override
	void setUpSecondMenu(View v) {
		v.findViewById(R.id.classLayout).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.classLayout:
			callback.switchFragment();
			break;
		}
	}

	@Override
	protected void handleResponse(String response) throws JSONException {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String locationNameKey;
		switch (SharePrefsUtils.getLanguage(getActivity())) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
			locationNameKey = "nameTc";
		case Constants.LOCALE_CN:
			locationNameKey = "nameSc";
		default:
			locationNameKey = "locationName";
		}
		JSONArray jsonArray = new JSONObject(response)
				.getJSONArray("groupBeans");
		groupList.clear();
		displayList.clear();
		childrenMap.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Group group = new Group();
			JSONArray childArray = jsonObject.getJSONArray("clList");
			ArrayList<ChildForLocator> childList = new ArrayList<ChildForLocator>();
			for (int j = 0; j < childArray.length(); j++) {
				JSONObject childObject = childArray.getJSONObject(j);
				ChildForLocator child = new ChildForLocator();
				child.setChildId(childObject.optLong("childId"));
				child.setName(childObject.optString("childName"));
				child.setLocationName(childObject.optString(locationNameKey));
				child.setIcon(childObject.optString("icon"));
				child.setLastAppearTime(childObject.optLong("lastAppearTime"));
				childList.add(child);
				childrenMap.put(child.getChildId(), child);
			}
			group.setChildList(childList);
			group.setGroupId(jsonObject.optLong("groupId"));
			group.setGroupName(jsonObject.optString("groupName"));
			group.setInitiatorId(jsonObject.optLong("initiatorId"));
			group.setInitiatorName(jsonObject.optString("initiatorName"));
			groupList.add(group);
			displayList.add(group);
		}
	}

	@Override
	protected void handlePostResult(boolean result) {
		if (result) {
			groupLocatorAdapter.notifyDataSetChanged();
		}
		if (groupList.size() == 0) {
			hintText.setVisibility(View.VISIBLE);
		} else {
			hintText.setVisibility(View.INVISIBLE);
		}
	}

	public void switchViewAllRooms(boolean isViewAllRooms) {
		displayList.clear();
		displayList.addAll(groupList);
		groupLocatorAdapter.setViewAllRooms(isViewAllRooms);
	}

}
