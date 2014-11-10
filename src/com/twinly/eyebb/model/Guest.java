package com.twinly.eyebb.model;

public class Guest {
	private String guardianId;
	private String name;
	private String phoneNumber;
	private String icon;

	public String getGuardianId() {
		return guardianId;
	}

	public void setGuardianId(String guardianId) {
		this.guardianId = guardianId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
