package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableChildrenList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6L;
	private ArrayList<Child> list;

	public ArrayList<Child> getList() {
		return list;
	}

	public void setList(ArrayList<Child> list) {
		this.list = list;
	}
}
