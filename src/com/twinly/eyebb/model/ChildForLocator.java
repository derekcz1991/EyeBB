package com.twinly.eyebb.model;

import java.io.Serializable;

public class ChildForLocator extends Child implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8549868122420844984L;
	private String locationName;
	private long lastAppearTime;
	private boolean isInSchool;

	public ChildForLocator() {
	}

	public ChildForLocator(Child child) {
		super(child.getChildId(), child.getName(), child.getIcon(), child
				.getLocalIcon(), child.getPhone(), child.getMacAddress());
		this.locationName = "";
		this.lastAppearTime = 0;
		this.isInSchool = false;
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

	public boolean isInSchool() {
		return isInSchool;
	}

	public void setInSchool(boolean isInSchool) {
		this.isInSchool = isInSchool;
	}

}
