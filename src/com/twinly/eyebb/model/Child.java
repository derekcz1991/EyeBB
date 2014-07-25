package com.twinly.eyebb.model;

public class Child {
	private long childId;
	private String name;
	private String icon;
	private String phone;
	private String indoorAreaId; // dynamic changing

	public Child(long childId, String name, String icon, String phone) {
		super();
		this.childId = childId;
		this.name = name;
		this.icon = icon;
		this.phone = phone;
	}

	public long getChildId() {
		return childId;
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public String getPhone() {
		return phone;
	}

	public String getIndoorAreaId() {
		return indoorAreaId;
	}

	public void setIndoorAreaId(String indoorAreaId) {
		this.indoorAreaId = indoorAreaId;
	}

}
