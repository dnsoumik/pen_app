package in.xlayer.f2h.driver.handler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.xlayer.f2h.driver.other.booking.complete.BookingCompleteResponse;
import in.xlayer.f2h.driver.other.booking.response.Datum;
import in.xlayer.f2h.driver.ws.request.WSBookingRequest;

public class StaticMemory {
    public static int SCHEDULE_CONSTANT = 0;
    public static int BACKGROUND_SERVICE_STATUS = 0;
    public static int MAIN_ACTIVITY_STATUS = 0;
    public static int BOOKING_REQUEST_DIALOG_STATUS = 0;
    public static boolean BOOKINGS_ACTIVITY ;
    //    public static List<LatLng> DISTANCE_API_PL_LAT_LNG = null;
//
//    public static BookingPlace START_PLACE, END_PLACE;
    public static Datum SELECTED_BOOKING = null;
    public static in.xlayer.f2h.driver.other.booking.request.Datum SELECTED_BOOKING_REQUEST = null;
    public static BookingCompleteResponse TRIP_COMPLETE_RESPONSE = null;
    //
//    public static DirectionApiResponse DIRECTION_API_RESPONSE = null;
//    public static DistanceMatrixApiResponse DISTANCE_MATRIX_API_RESPONSE = null;
    public static long nTimer = 0L;
    public static String REJECT_BOOKING_ID = "";
    static String ENTITY_INFO_RESPONSE = null;
    static String USER_INFO_RESPONSE = null;
    public static List<WSBookingRequest> NEW_BOOKING_REQUEST = new ArrayList<>();
    public  static JSONObject CURRENT_BOOKING_REQUEST = null;

}
