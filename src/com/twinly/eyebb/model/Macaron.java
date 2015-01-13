package com.twinly.eyebb.model;

import java.io.Serializable;

public class Macaron implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6L;
	private String macAddress;
	private int rssi;
	private int preRssi;
	private long lastAppearTime;
	private int axisX;
	private int axisY;
	private boolean isAntiLostOpen;
	private boolean isAntiLostWriten;

	public Macaron(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public int getPreRssi() {
		return preRssi;
	}

	public void setPreRssi(int preRssi) {
		this.preRssi = preRssi;
	}

	public long getLastAppearTime() {
		return lastAppearTime;
	}

	public void setLastAppearTime(long lastAppearTime) {
		this.lastAppearTime = lastAppearTime;
	}

	public int getAxisX() {
		return axisX;
	}

	public void setAxisX(int axisX) {
		this.axisX = axisX;
	}

	public int getAxisY() {
		return axisY;
	}

	public void setAxisY(int axisY) {
		this.axisY = axisY;
	}

	public boolean isAntiLostOpen() {
		return isAntiLostOpen;
	}

	public void setAntiLostOpen(boolean isAntiLostOpen) {
		this.isAntiLostOpen = isAntiLostOpen;
	}

	public boolean isAntiLostWriten() {
		return isAntiLostWriten;
	}

	public void setAntiLostWriten(boolean isAntiLostWriten) {
		this.isAntiLostWriten = isAntiLostWriten;
	}

	@Override
	public String toString() {
		return "Macaron [macAddress=" + macAddress + ", isAntiLostOpen="
				+ isAntiLostOpen + ", isAntiLostWriten=" + isAntiLostWriten
				+ "]";
	}

}
