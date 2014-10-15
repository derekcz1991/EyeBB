package com.twinly.eyebb.model;

import java.io.Serializable;

import com.twinly.eyebb.utils.CommonUtils;

public class Child implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private long childId;
	private String name;
	private String icon;
	private String phone;

	private String macAddress;
	private boolean isMissing;
	private String locationName; // dynamic changing
	private long lastAppearTime; // dynamic changing

	public Child() {

	}

	public Child(long childId, String name, String icon) {
		super();
		this.childId = childId;
		this.name = name;
		this.icon = icon;
	}

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		if (CommonUtils.isNotNull(macAddress)) {
			this.macAddress = macAddress;
		} else {
			this.macAddress = "";
		}
	}

	public boolean isMissing() {
		return isMissing;
	}

	public void setMissing(boolean isMissing) {
		this.isMissing = isMissing;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public long getLastAppearTime() {
		return lastAppearTime;
	}

	public void setLastAppearTime(long lastAppearTime) {
		this.lastAppearTime = lastAppearTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
