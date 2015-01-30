package com.twinly.eyebb.model;

import java.io.Serializable;

public class Device implements Serializable {
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
	private boolean isMissed;

	public Device(String macAddress) {
		this.macAddress = macAddress;
		this.isMissed = true;
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

	public boolean isMissed() {
		return isMissed;
	}

	public void setMissed(boolean isMissed) {
		this.isMissed = isMissed;
	}

	@Override
	public String toString() {
		return "Macaron [macAddress=" + macAddress + "]";
	}
}
