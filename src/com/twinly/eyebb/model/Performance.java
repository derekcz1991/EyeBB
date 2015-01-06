package com.twinly.eyebb.model;

import java.util.HashMap;
import java.util.Map;

import com.twinly.eyebb.utils.CommonUtils;

public class Performance {
	private long childId;
	private String jsonData;
	private String lastUpdateTime;

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		if (CommonUtils.isNotNull(jsonData))
			this.jsonData = jsonData;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	private Map<String, Integer> getEntrySet(String value) {
		if (CommonUtils.isNull(value)) {
			return null;
		}
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] items = value.split(",");
		for (int i = 0; i < items.length; i++) {
			String[] item = items[i].split(":");
			map.put(item[0], (int) Double.parseDouble(item[1]));
		}
		return map;
	}
}
