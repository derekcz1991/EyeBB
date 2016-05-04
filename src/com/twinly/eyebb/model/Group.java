package com.twinly.eyebb.model;

import java.util.ArrayList;

public class Group {
	private long groupId;
	private String groupName;
	private long initiatorId;
	private String initiatorName;
	private ArrayList<ChildForLocator> childList;

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getInitiatorId() {
		return initiatorId;
	}

	public void setInitiatorId(long initiatorId) {
		this.initiatorId = initiatorId;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public ArrayList<ChildForLocator> getChildList() {
		return childList;
	}

	public void setChildList(ArrayList<ChildForLocator> childList) {
		this.childList = childList;
	}

}
