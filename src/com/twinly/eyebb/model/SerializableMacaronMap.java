package com.twinly.eyebb.model;

import java.io.Serializable;
import java.util.HashMap;

public class SerializableMacaronMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private HashMap<String, Macaron> map;

	public HashMap<String, Macaron> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Macaron> macaronMap) {
		this.map = macaronMap;
	}
}
