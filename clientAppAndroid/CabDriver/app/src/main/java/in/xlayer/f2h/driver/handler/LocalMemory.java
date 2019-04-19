package in.xlayer.f2h.driver.handler;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.authorization.AuthStaticElements;
import in.xlayer.f2h.driver.other.FormatDecoder;
import in.xlayer.f2h.driver.other.booking.request.BookingRequestResponse;
import in.xlayer.f2h.driver.other.booking.response.DriverBookingResponse;
import in.xlayer.f2h.driver.other.dinfo.DriverInfo;
import in.xlayer.f2h.driver.other.location.DeviceLocationData;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dnsou on 20-01-2018.
 */

public class LocalMemory {

    private Context context;

    private SharedPreferences mShare;

    public LocalMemory(Context context) {
        this.context = context;
        mShare = context.getSharedPreferences(AuthStaticElements.APP_SIGN_IN_CACHE_MEMORY_TAG, MODE_PRIVATE);
    }

    public String getString(String Key) {
        return mShare.getString(Key, "");
    }

    public int getInt(String Key) {
        return mShare.getInt(Key, -1);
    }

    public boolean getBoolean(String Key) {
        return mShare.getBoolean(Key, false);
    }

    public double getDouble(String Key) {
        return Double.parseDouble(String.valueOf(mShare.getString(Key, "0.0")));
    }

    public long getLong(String Key) {
        return mShare.getLong(Key, 0L);
    }

    public void putString(String Key, String stringValue) {
        SharedPreferences.Editor mEditor = mShare.edit();
        mEditor.putString(Key, stringValue);
        mEditor.apply();
    }

    public void putInt(String Key, int intValue) {
        SharedPreferences.Editor mEditor = mShare.edit();
        mEditor.putInt(Key, intValue);
        mEditor.apply();
    }

    public void putBoolean(String Key, boolean booleanValue) {
        SharedPreferences.Editor mEditor = mShare.edit();
        mEditor.putBoolean(Key, booleanValue);
        mEditor.apply();
    }

    public void putDouble(String Key, double doubleValue) {
        SharedPreferences.Editor mEditor = mShare.edit();
        mEditor.putString(Key, String.valueOf(doubleValue));
        mEditor.apply();
    }

    public void putLong(String Key, long longValue) {
        SharedPreferences.Editor mEditor = mShare.edit();
        mEditor.putLong(Key, longValue);
        mEditor.apply();
    }

    //    public EntityInfo getFromEntityInfo() throws NullPointerException {
//        return new Gson().fromJson(getString(AppConstants.ENTITY_INFO_RESPONSE), EntityInfo.class);
//    }
//
    public DriverInfo getFromDriverInfo() throws NullPointerException {
        return new Gson().fromJson(getString(AppConstants.DRIVER_INFO_RESPONSE), DriverInfo.class);
    }

    public BookingRequestResponse getFromBookingRequest() throws NullPointerException {
        return new Gson().fromJson(getString(AppConstants.BOOKING_REQUEST_RESPONSE), BookingRequestResponse.class);
    }

    public DriverBookingResponse getFromDriverBookingsResponse() throws NullPointerException {
        return new Gson().fromJson(getString(AppConstants.YOUR_BOOKING_RESPONSE), DriverBookingResponse.class);
    }

    public DeviceLocationData getFromMyLocation() throws NullPointerException {
        return new Gson().fromJson(getString(AppConstants.DEVICE_LAST_LOCATION), DeviceLocationData.class);
    }

    public String getActiveTripId() throws NullPointerException {
        return getString(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_ID);
    }

    public double getActiveTripDistance() throws NullPointerException {
        try {
            return FormatDecoder.threeDecimalPoint(getDouble(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_DISTANCE));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return -1;
        }
    }

//
//    public DeviceLocationData getFromMyLocation() throws NullPointerException {
//        return new Gson().fromJson(getString(AppConstants.DEVICE_LAST_LOCATION), DeviceLocationData.class);
//    }
//
//    public LastLocationUpdateInfo getLastLocationUpdateInfo() throws NullPointerException {
//        return new Gson().fromJson(getString(AppConstants.LAST_UPDATED_LOCATION), LastLocationUpdateInfo.class);
//    }

    public void clearAllUserData() {
        SharedPreferences preferences = context.getSharedPreferences(AuthStaticElements.APP_SIGN_IN_CACHE_MEMORY_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    // stored in default cache
    public void changeServerUrl(String serverUrl) {
        SharedPreferences share = context.getSharedPreferences(AppConstants.APP_DEFAULT_CACHE, MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(AppConstants.APP_SERVER_URL_TAG, serverUrl);
        editor.apply();
    }

    public String getServerUrl() throws NullPointerException {
        SharedPreferences share = context.getSharedPreferences(AppConstants.APP_DEFAULT_CACHE, MODE_PRIVATE);
        return share.getString(AppConstants.APP_SERVER_URL_TAG, context.getResources().getString(R.string.default_server_url));
    }

    public String getFireBaseToken() throws NullPointerException {
        SharedPreferences share = context.getSharedPreferences(AppConstants.APP_DEFAULT_CACHE, Context.MODE_PRIVATE);
        return share.getString(AppConstants.FIRE_BASE_KEY_TAG, null);
    }

    public Long getLocationUpdateInterval() {
        return mShare.getLong(AppConstants.SETTINGS_LOCATION_UPDATE_INTERVAL, 10000L);
    }

    public boolean getTrackingButtonState() {
        return mShare.getBoolean(AppConstants.SETTINGS_TRACKING_BUTTON, true);
    }
}
