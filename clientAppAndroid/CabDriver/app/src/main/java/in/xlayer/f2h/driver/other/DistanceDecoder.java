package in.xlayer.f2h.driver.other;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class DistanceDecoder {

    public static double getDistanceInKM(LatLng startPoint, LatLng endPoint) {
        Location org = new Location("fused");
        org.setLatitude(startPoint.latitude);
        org.setLongitude(startPoint.longitude);
        Location dest = new Location("fused");
        dest.setLatitude(endPoint.latitude);
        dest.setLongitude(endPoint.longitude);
        return org.distanceTo(dest) / 1000;
    }

    public static double getDistanceOldInKM(LatLng startPoint, LatLng endPoint) {

        double lat1 = startPoint.latitude;
        double lon1 = startPoint.longitude;
        double lat2 = endPoint.latitude;
        double lon2 = endPoint.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
