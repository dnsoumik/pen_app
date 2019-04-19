package in.xlayer.f2h.driver.handler;

import static in.xlayer.f2h.driver.authorization.AuthStaticElements.APP_SIGN_IN_CACHE_MEMORY_TAG;

/**
 * Created by Soumik Debnath on 10-05-2018.
 */

public class AppConstants {

    //    public static final String MARKER_WAY_POINT = "WAY_POINT_MARK";
//    public static final String MARKER_ENTITY = "EMARK";
//    public static final String MARKER_VEHICLE = "VMARK";
//
//    // web socket response type
//    public static final String TRIP_STARTED = "TRIP_STARTED";
//    public static final String TRIP_STOPPED = "TRIP_STOPPED";
//    public static final String UPDATE_CHANGES = "UPDATE_CHANGES";
//    public static final String CONN_READY = "conn_ready";
//    public static final String LIVE_LOCATION = "live_location";
//    public static final String SUB = "SUB";
//    public static final String UNSUB = "UNSUB";
//
//    // fcm notifications
//    public static final String NOTICE = "NOTICE";
//    public static final String APPROACH = "APPROACH";
//    public static final String RESUMED = "RESUMED";
//
//    // settings
//    public static final String MAP_GESTURES = "Settings -> MapGestures";
    public static final String SETTINGS_LOCATION_UPDATE_INTERVAL = "Settings -> LocationUpdateInterval";
    public static final String SETTINGS_TRACKING_BUTTON = "Settings -> TrackingButton";
    public static final String BOOKING_REQUEST_LOG = "Request -> RequestLog";

    // app default cache tag
    public static final String APP_DEFAULT_CACHE = "$SET#APPDEFAULT#CACHE";
    // server url tag
    public static final String APP_SERVER_URL_TAG = APP_DEFAULT_CACHE + "$SET#SERVER#URL";
    // fire base key tag
    public static final String FIRE_BASE_KEY_TAG = APP_DEFAULT_CACHE + "$SET#FIREBASEKEY";

    public static final String ENTITY_INFO_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetENTITY#INFO#DETAILS";
    public static final String DRIVER_INFO_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetDRIVER#INFO#DETAILS";
    public static final String BOOKING_REQUEST_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetBOOKING#REQUEST#RESPONSE";
    public static final String YOUR_BOOKING_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetYOUR#BOOKING#RESPONSE";
    public static final String YOUR_BOOKING_REQUEST = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetYOUR#BOOKING#RESPONSE";
//    public static final String CURRENT_TRIP_INFO_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetCURRENT#TRIP#INFO#RESPONSE";;
//    public static final String ROUTE_INFO_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetROUTE#INFO#RESPONSE";;
//    public static final String VEHICLE_LAST_LOC_INFO_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetVEHICLE#LAST#LOCATION";
//    public static final String NOTICE_BOARD_INFO_RESPONSE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetNOTICEBOARD#INFO#RESPONSE";
//
//    public static final String CALCULATE_DISTANCE_OF_VEHICLE = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetCALCULATE#DISTANCE#VEHICLE";


    public static final String BACKGROUND_SERVICE_STATUS = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetBackground#Status";
    public static final String DEVICE_LAST_LOCATION = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetDevice#Location[INFO]";
    public static final String LAST_UPDATED_LOCATION = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SetLast#Update#Location[DETAILS]";

    public static final String DRIVER_TRIP_ACTIVE_TRACKING_ID = APP_DEFAULT_CACHE + "$Set#Trip#Active#Status[HEX]";
    public static final String DRIVER_TRIP_ACTIVE_TRACKING_DISTANCE = APP_DEFAULT_CACHE + "$Set#Trip#Active#Distance[DISTANCE]";
    public static String REQUEST_REQUEST_LOG_STATUS = "StatusUpdate";

}
