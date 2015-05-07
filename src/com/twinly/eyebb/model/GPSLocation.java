package com.twinly.eyebb.model;

public class GPSLocation {
	private double latitude;
	private double longitude;
	private double radius;
	private long timestamp;

	public GPSLocation(double latitude, double longitude, double radius,
			long timestamp) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.timestamp = timestamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getRadius() {
		return radius;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "GPSLocation [latitude=" + latitude + ", longitude=" + longitude
				+ ", radius=" + radius + ", timestamp=" + timestamp + "]";
	}

}
