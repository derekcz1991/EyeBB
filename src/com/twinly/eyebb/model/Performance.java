package com.twinly.eyebb.model;

import java.util.HashMap;
import java.util.Map;

import com.twinly.eyebb.utils.CommonUtils;

public class Performance {
	private long childId;
	private String daily;
	private String weekly;
	private String monthly;
	private String lastUpdateDate;

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

	public String getDaily() {
		return daily;
	}

	public Map<String, Integer> getDailyMap() {
		return getEntrySet(daily);
	}

	public void setDaily(String daily) {
		if (CommonUtils.isNotNull(daily))
			this.daily = daily;
	}

	public String getWeekly() {
		return weekly;
	}

	public Map<String, Integer> getWeeklyMap() {
		return getEntrySet(weekly);
	}

	public void setWeekly(String weekly) {
		if (CommonUtils.isNotNull(weekly))
			this.weekly = weekly;
	}

	public String getMonthly() {
		return monthly;
	}

	public void setMonthly(String monthly) {
		if (CommonUtils.isNotNull(monthly))
			this.monthly = monthly;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
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
