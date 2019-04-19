package in.xlayer.f2h.driver.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by dnsou on 23-01-2018.
 */

public class DeviceLocInfo {

    @SerializedName("class")
    @Expose
    private String _class;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("accuracy")
    @Expose
    private double accuracy;
    @SerializedName("altitude")
    @Expose
    private double altitude;
    @SerializedName("bearing")
    @Expose
    private double bearing;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("time")
    @Expose
    private long time;
    @SerializedName("speed")
    @Expose
    private double speed;
    @SerializedName("elapsed_realtime_nanos")
    @Expose
    private long elapsedRealtimeNanos;
    @SerializedName("extras")
    @Expose
    private String extras;

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("_class", _class).append("latitude", latitude).append("longitude", longitude).append("accuracy", accuracy).append("altitude", altitude).append("bearing", bearing).append("provider", provider).append("time", time).append("speed", speed).append("elapsedRealtimeNanos", elapsedRealtimeNanos).append("extras", extras).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(time).append(_class).append(speed).append(altitude).append(elapsedRealtimeNanos).append(bearing).append(provider).append(longitude).append(latitude).append(accuracy).append(extras).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DeviceLocInfo) == false) {
            return false;
        }
        DeviceLocInfo rhs = ((DeviceLocInfo) other);
        return new EqualsBuilder().append(time, rhs.time).append(_class, rhs._class).append(speed, rhs.speed).append(altitude, rhs.altitude).append(elapsedRealtimeNanos, rhs.elapsedRealtimeNanos).append(bearing, rhs.bearing).append(provider, rhs.provider).append(longitude, rhs.longitude).append(latitude, rhs.latitude).append(accuracy, rhs.accuracy).append(extras, rhs.extras).isEquals();
    }

}
