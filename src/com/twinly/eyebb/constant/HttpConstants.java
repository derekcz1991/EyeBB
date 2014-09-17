package com.twinly.eyebb.constant;

public class HttpConstants {

	public static final int CONNECT_TIMEOUT = 5000;
	public static final String SERVER_URL = "http://158.182.246.221:8089/";
	//public static final String SERVER_URL = "http://158.182.246.224:8080/";

	public static final String HTTP_POST_RESPONSE_URL_NULL = "Url Null";
	public static final String HTTP_POST_RESPONSE_EXCEPTION = "Failed to connect to server";

	public static final String LOGIN = "j_spring_security_check";
	public static final String GET_KINDERGARTEN_LIST = "kindergartenList";

	public static final String JSON_KEY_KINDERGARTENS_INFO = "allKindergartensInfo";
	public static final String JSON_KEY_KINDERGARTEN_id = "kindergartenId";
	public static final String JSON_KEY_KINDERGARTEN_NAME_EN = "name";
	public static final String JSON_KEY_KINDERGARTEN_NAME_TC = "nameTc";
	public static final String JSON_KEY_KINDERGARTEN_NAME_SC = "nameSc";

	public static final String JSON_KEY_CHILDREN_LIST = "childrenList";
	public static final String JSON_KEY_CHILD_ID = "childId";
	public static final String JSON_KEY_CHILD_NAME = "name";
	public static final String JSON_KEY_CHILD_ICON = "icon";
	public static final String JSON_KEY_CHILD_PHONE = "phoneNumber";
	public static final String JSON_KEY_CHILD_MAC_ADDRESS = "macAddress";

	public static final String JSON_KEY_LOCATION = "loc";
	public static final String JSON_KEY_LOCATION_NAME = "locationName";

	public static final String JSON_KEY_PARENTS = "parents";
	public static final String JSON_KEY_PARENTS_PHONE = "phoneNumber";

	public static final String JSON_KEY_LAST_UPDATE_TIME = "lastUpdateTime";

	public static final String JSON_KEY_REPORT_PERFORMANCE_DAILY = "dailyFigure";
	public static final String JSON_KEY_REPORT_PERFORMANCE_WEEKLY = "weeklyFigure";

	public static final String JSON_KEY_REPORT_ACTIVITY_INFO = "activityInfos";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_TITLE = "title";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC = "titleTc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC = "titleSc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_URL = "activity";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC = "activityTc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC = "activitySc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_ICON = "icon";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_DATE = "validUntil";
	
	public static final String UPDATE_DEVICE_FROM_CHILD_LIST = "childId";
}
