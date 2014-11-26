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

	private String relationWithUser;

	private String locationName; // dynamic changing
	private long lastAppearTime; // dynamic changing

	private boolean withAccess;
	private String totalQuota;
	private String quotaLeft;

	public String getQuotaLeft() {
		return quotaLeft;
	}

	public void setQuotaLeft(String quotaLeft) {
		this.quotaLeft = quotaLeft;
	}

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

	public String getRelationWithUser() {
		return relationWithUser;
	}

	public void setRelationWithUser(String relationWithUser) {
		this.relationWithUser = relationWithUser;
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

	public boolean isWithAccess() {
		return withAccess;
	}

	public void setWithAccess(boolean withAccess) {
		this.withAccess = withAccess;
	}

	public String getTotalQuota() {
		return totalQuota;
	}

	public void setTotalQuota(String totalQuota) {
		this.totalQuota = totalQuota;
	}

	@Override
	public String toString() {
		return "Child [childId=" + childId + ", name=" + name + ", icon="
				+ icon + ", phone=" + phone + ", macAddress=" + macAddress
				+ ", relationWithUser=" + relationWithUser + "]";
	}

}
