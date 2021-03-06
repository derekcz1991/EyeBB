package com.twinly.eyebb.model;

public class ChildSelectable extends Child {

	/**
	 * 
	 */
	private static final long serialVersionUID = 315922863765346884L;
	private boolean isSelected;

	public ChildSelectable(Child child, boolean isSelected) {
		super(child.getChildId(), child.getName(), child.getIcon(), child
				.getLocalIcon(), child.getPhone(), child.getMacAddress());
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
