package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.Map;

public class SerializableChildrenMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Child> map;

	public Map<String, Child> getMap() {
		return map;
	}

	public void setMap(Map<String, Child> childrenMap) {
		this.map = childrenMap;
	}
}
