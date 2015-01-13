package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.Map;

public class SerializableMacaron implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private Map<String, Macaron> map;

	public Map<String, Macaron> getMap() {
		return map;
	}

	public void setMap(Map<String, Macaron> macaronMap) {
		this.map = macaronMap;
	}
}
