package com.twinly.eyebb.model;

public class Area {
	private Long areaId;
	private String name;
	private String nameTc;
	private String nameSc;
	private String icon;

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameTc() {
		return nameTc;
	}

	public void setNameTc(String nameTc) {
		this.nameTc = nameTc;
	}

	public String getNameSc() {
		return nameSc;
	}

	public void setNameSc(String nameSc) {
		this.nameSc = nameSc;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "Area [areaId=" + areaId + ", name=" + name + ", nameTc="
				+ nameTc + ", nameSc=" + nameSc + ", icon=" + icon + "]";
	}

}
