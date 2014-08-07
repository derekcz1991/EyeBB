package com.twinly.eyebb.constant;

public class HttpConstants {

	public static final int CONNECT_TIMEOUT = 5000;
	public static final String SERVER_URL = "http://158.182.246.221:8089/";

	public static final String HTTP_POST_RESPONSE_URL_NULL = "Url Null";
	public static final String HTTP_POST_RESPONSE_EXCEPTION = "连接服务器失败";

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

	public static final String JSON_KEY_LOCATION = "loc";
	public static final String JSON_KEY_LOCATION_NAME = "locationName";

	public static final String JSON_KEY_PARENTS = "parents";
	public static final String JSON_KEY_PARENTS_PHONE = "phoneNumber";
}
