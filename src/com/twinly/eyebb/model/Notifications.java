package com.twinly.eyebb.model;

public class Notifications {
	private long notificationId;
	private String title;
	private long date;
	private String icon;
	private String url;
	private boolean isRead;

	/**
	 * 
	 * @param notificationId
	 * @param title
	 * @param date
	 * @param icon
	 * @param url
	 * @param isRead
	 */
	public Notifications(long notificationId, String title, long date,
			String icon, String url, boolean isRead) {
		super();
		this.notificationId = notificationId;
		this.title = title;
		this.date = date;
		this.icon = icon;
		this.url = url;
		this.isRead = isRead;
	}

	public long getNotificationId() {
		return notificationId;
	}

	public String getTitle() {
		return title;
	}

	public long getDate() {
		return date;
	}

	public String getIcon() {
		return icon;
	}

	public String getUrl() {
		return url;
	}

	public boolean isRead() {
		return isRead;
	}

}