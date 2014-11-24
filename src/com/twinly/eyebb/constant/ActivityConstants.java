package com.twinly.eyebb.constant;

public class ActivityConstants {
	// share_preference item name
	public static final String SHARE_PREFERENCES_NAME = "EyeBB";
	public static final String SHARE_PREFS_ITEM_APP_VERSION = "app_version";
	public static final String SHARE_PREFS_ITEM_IS_LOGIN = "is_login";
	public static final String SHARE_PREFS_ITEM_REGISTRATION_ID = "registrationId";
	public static final String SHARE_PREFS_ITEM_LOGIN_ACCOUNT = "login_account";
	public static final String SHARE_PREFS_ITEM_PASSWORD = "password";
	public static final String SHARE_PREFS_ITEM_LANGUAGE = "language";
	public static final String SHARE_PREFS_ITEM_USER_NAME = "user_name";
	public static final String SHARE_PREFS_ITEM_USER_TYPE = "user_type";
	public static final String SHARE_PREFS_ITEM_USER_PHONE = "user_phone";
	public static final String SHARE_PREFS_ITEM_KINDERGARTEN_ID = "kindergarten_id";
	public static final String SHARE_PREFS_ITEM_UPDATE_INDOOR_LOCATOR_FLAG = "update_indoor_locator_flag";
	public static final String SHARE_PREFS_ITEM_UPDATE_REPORT_PERFORMANCE_FLAG = "update_report_performance_flag";
	public static final String SHARE_PREFS_ITEM_UPDATE_REPORT_ACTIVITIES_FLAG = "update_report_activities_flag";
	public static final String SHARE_PREFS_ITEM_UPDATE_NOTICE_FLAG = "update_notice_flag";
	public static final String SHARE_PREFS_ITEM_AUDO_UPDATE = "auto_update";
	public static final String SHARE_PREFS_ITEM_SOUND = "sound";
	public static final String SHARE_PREFS_ITEM_START_BEEP = "isStartBeepDialog";
	public static final String SHARE_PREFS_ITEM_RUN_NUM_RADAR = "runNumRadar";
	public static final String SHARE_PREFS_ITEM_VIBRATE = "vibrate";
	public static final String SHARE_PREFS_INIT_HEAD = "init_head";
	public static final String SHARE_PREFS_FINISH_BEEP = "finish_beep";
	public static final String SHARE_PREFS_CONNECT_BLE_SERVICE = "connect_ble_service";
	public static final String SHARE_PREFS_REFRESH_TIME = "refresh_time";
	public static final String SHARE_PREFS_MAC_ADDRESSS = "device_mac_address";
	public static final String SHARE_PREFS_DEVICE_CONNECT_STATUS = "device_status_connect";
	public static final String SHARE_PREFS_KEEP_DEVICE_CONNECT_STATUS = "keep_device_status_connect";
	public static final String SHARE_PREFS_CHILID_BIRTHDAY = "child_birthday";
	public static final String SHARE_PREFS_BEEP_ALL_DEVICE = "all_device";
	public static final String SHARE_PREFS_SIGN_UP_USERNAME = "sign_username";
	public static final String SHARE_PREFS_SIGN_UP_PASSWORD = "sign_password";
	public static final String SHARE_PREFS_SIGN_UP_EMAIL = "sign_email";
	public static final String SHARE_PREFS_SIGN_UP_NICKNAME = "sign_nickname";
	public static final String SHARE_PREFS_SIGN_UP_PHONE_NUMBER = "sign_phone_number";
	public static final String SHARE_PREFS_BLE_SERVICE_RUN_ONCE_FLAG = "ble_service_run_once";
	public static final String SHARE_PREFS_DEVICE_MAJOR = "device_major";
	public static final String SHARE_PREFS_DEVICE_MINOR = "device_minor";
	public static final String SHARE_PREFS_GUARDIANID = "guardianId";
	public static final String SHARE_PREFS_CHILDID = "childID";
	public static final String SHARE_PREFS_BINDING_DEVICE = "binding_device_open";
	public static final String SHARE_PREFS_BLE_SERVICE_INDEX = "ble_service_index";
	public static final String SHARE_PREFS_GRANT_CHILD_ID = "grant_child_id";
	public static final String SHARE_PREFS_DEVICE_BATTERY = "device_battery";

	public static final String SHARE_PREFS_ITEM_LAST_UPDATE_TIME = "last_update_time";
	public static final String SHARE_PREFS_ITEM_REPORT_CHILD_ID = "report_child_id";

	public static final int REQUEST_GO_TO_WELCOME_ACTIVITY = 100;
	public static final int REQUEST_GO_TO_LOGIN_ACTIVITY = 101;
	public static final int REQUEST_GO_TO_SIGN_UP_ACTIVITY = 102;
	public static final int REQUEST_GO_TO_KINDERGARTEN_ACTIVITY = 103;
	public static final int REQUEST_GO_TO_SETTING_ACTIVITY = 104;
	public static final int REQUEST_GO_TO_CHANGE_KIDS_ACTIVITY = 105;
	public static final int REQUEST_GO_TO_BIRTHDAY_ACTIVITY = 106;
	public static final int REQUEST_GO_TO_KID_PROFILE_ACTIVITY = 107;
	public static final int REQUEST_GO_TO_UNBIND_ACTIVITY = 108;
	public static final int REQUEST_GO_TO_BEACON_LIST_ACTIVITY = 109;
	public static final int REQUEST_GO_TO_BIND_CHILD_MACARON_DIALOG = 110;
	public static final int REQUEST_GO_TO_BIND_CHILD_MACARON_ACTIVITY = 111;
	public static final int REQUEST_GO_TO_SIGNUP_ASK_TO_BIND_DIALOG = 112;
	public static final int REQUEST_GO_TO_CHILD_INFO_MATCHING_ACTIVITY = 113;
	public static final int REQUEST_GO_TO_CHECK_CHILD_TO_BIND_DIALOG = 114;

	public static final int RESULT_RESULT_OK = 200;
	public static final int RESULT_LOGOUT = 201;
	public static final int RESULT_AUTO_UPDATE_ON = 202;
	public static final int RESULT_AUTO_UPDATE_OFF = 203;
	public static final int RESULT_RESULT_BIRTHDAY_OK = 204;
	public static final int RESULT_UNBIND_SUCCESS = 205;
	public static final int RESULT_UNBIND_CANCEL = 206;
	public static final int RESULT_WRITE_MAJOR_MINOR_SUCCESS = 207;
	public static final int RESULT_WRITE_MAJOR_MINOR_FAIL = 208;

	public static final int FRAGMENT_REPORT_ACTIVITY = 1;
	public static final int FRAGMENT_PROFILE = 2;
	public static final int ACTIVITY_CHECK_CHILD_TO_BIND = 3;
	public static final int ACTIVITY_KID_PROFILE = 4;

	public static final String EXTRA_FROM = "FROM";
	public static final String EXTRA_USER_NAME = "USER_NAME";
	public static final String EXTRA_HASH_PASSWORD = "HASH_PASSWORD";
	public static final String EXTRA_GUARDIAN_ID = "GUARDIAN_ID";
	public static final String EXTRA_MAC_ADDRESS = "MAC_ADDRESS";
	public static final String EXTRA_CHILD_ID = "CHILD_ID";
}
