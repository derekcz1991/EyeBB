package com.twinly.eyebb.model;

public class Child {
	private long childId;
	private String name;
	private String icon;
	private String phone;

	public Child(int childId, String name, String icon) {
		super();
		this.childId = childId;
		this.name = name;
		this.icon = icon;
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

}
