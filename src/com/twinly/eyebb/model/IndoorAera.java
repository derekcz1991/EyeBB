package com.twinly.eyebb.model;

public class IndoorAera {
	private long aeraId;
	private String name;

	public IndoorAera() {

	}

	public IndoorAera(long aeraId, String name) {
		super();
		this.aeraId = aeraId;
		this.name = name;
	}

	public long getAeraId() {
		return aeraId;
	}

	public void setAeraId(long aeraId) {
		this.aeraId = aeraId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
