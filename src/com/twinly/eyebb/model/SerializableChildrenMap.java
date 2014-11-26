package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.Map;

public class SerializableChildrenMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Long, Child> map;

	public Map<Long, Child> getMap() {
		return map;
	}

	public void setMap(Map<Long, Child> childrenMap) {
		this.map = childrenMap;
	}
}
