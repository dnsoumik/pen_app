package in.xlayer.f2h.driver.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.xlayer.f2h.driver.handler.AppConstants;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.other.DistanceDecoder;
import in.xlayer.f2h.driver.other.location.DeviceLocationData;


/**
 * Created by dnsou on 09-02-2018.
 */

public class LocationService {

    public static boolean LOCATION_ACTIVE = false;
    public boolean SERVICE_STATUS = false;
    private String TAG = this.getClass().getSimpleName();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocalMemory sLocalMemory;
    private Context context;

    public LocationService(Context context) {
        this.context = context;
        sLocalMemory = new LocalMemory(context);
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
    }


    public void startLocationUpdates() {
        SERVICE_STATUS = true;
        final boolean[] isFistTime = {true};
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000L);
        locationRequest.setFastestInterval(500L);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        JSONObject locationObject = new JSONObject();
                        try {
                            locationObject.put("class",
                                    locationResult.getLastLocation().getClass());
                            locationObject.put("latitude",
                                    locationResult.getLastLocation().getLatitude());
                            locationObject.put("longitude",
                                    locationResult.getLastLocation().getLongitude());
                            locationObject.put("accuracy",
                                    (double) locationResult.getLastLocation().getAccuracy());
                            locationObject.put("altitude",
                                    locationResult.getLastLocation().getAltitude());
                            locationObject.put("bearing",
                                    locationResult.getLastLocation().getBearing());
                            locationObject.put("provider",
                                    locationResult.getLastLocation().getProvider());
                            locationObject.put("time",
                                    locationResult.getLastLocation().getTime() / 1000);
                            locationObject.put("speed",
                                    locationResult.getLastLocation().getSpeed());
                            locationObject.put("elapsed_realtime_nanos",
                                    locationResult.getLastLocation().getElapsedRealtimeNanos());
                            locationObject.put("extras",
                                    locationResult.getLastLocation().getExtras());
                        } catch (JSONException e) {
                            locationObject = null;
                        }
                        if (locationResult.getLastLocation() != null && locationObject != null &&
                                locationResult.getLastLocation().getAccuracy() < 30.00) {
                            try {
                                if (sLocalMemory.getFromMyLocation() != null) {
                                    try {
                                        JSONArray dtArr = new JSONArray();
                                        JSONObject dt = new JSONObject();
                                        DeviceLocationData lDt = sLocalMemory.getFromMyLocation();
                                        LatLng sPoint = new LatLng(lDt.getLatitude(), lDt.getLongitude());
                                        LatLng ePoint = new LatLng(locationResult.getLastLocation()
                                                .getLatitude(), locationResult.getLastLocation()
                                                .getLongitude());
                                        double currDistance = DistanceDecoder
                                                .getDistanceInKM(sPoint, ePoint);
                                        dt.put("unit", "KM");
                                        dt.put("value", currDistance);
                                        dtArr.put(dt);
                                        locationObject.put("distance", dtArr);
                                        if (!sLocalMemory.getActiveTripId().isEmpty()) {
                                            double lastDistance = sLocalMemory.getActiveTripDistance();
                                            double distance = lastDistance + currDistance;
                                            sLocalMemory.putDouble(AppConstants
                                                    .DRIVER_TRIP_ACTIVE_TRACKING_DISTANCE, distance);
                                        }
                                    } catch (NullPointerException |
                                            NumberFormatException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (NumberFormatException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!SERVICE_STATUS) {
                            SERVICE_STATUS = true;
                        }
                        assert locationObject != null;
                        sLocalMemory.putString(AppConstants.DEVICE_LAST_LOCATION,
                                locationObject.toString());
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                        LocationService.LOCATION_ACTIVE = locationAvailability.isLocationAvailable();
                    }
                }, Looper.myLooper());
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            SERVICE_STATUS = false;
            LocationService.LOCATION_ACTIVE = false;
        }
    }

}
