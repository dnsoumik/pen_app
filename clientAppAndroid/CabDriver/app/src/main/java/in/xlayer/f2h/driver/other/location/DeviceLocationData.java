package in.xlayer.f2h.driver.other.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class DeviceLocationData {

    @SerializedName("class")
    @Expose
    private String _class;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("accuracy")
    @Expose
    private Double accuracy;
    @SerializedName("altitude")
    @Expose
    private double altitude;
    @SerializedName("distance")
    @Expose
    private List<Distance> distance = null;
    @SerializedName("bearing")
    @Expose
    private double bearing;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("speed")
    @Expose
    private double speed;
    @SerializedName("elapsed_realtime_nanos")
    @Expose
    private double elapsedRealtimeNanos;
    @SerializedName("extras")
    @Expose
    private String extras;

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public List<Distance> getDistance() {
        return distance;
    }

    public void setDistance(List<Distance> distance) {
        this.distance = distance;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(double elapsedRealtimeNanos) {
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
        return new ToStringBuilder(this).append("_class", _class).append("latitude", latitude).append("longitude", longitude).append("accuracy", accuracy).append("altitude", altitude).append("distance", distance).append("bearing", bearing).append("provider", provider).append("time", time).append("speed", speed).append("elapsedRealtimeNanos", elapsedRealtimeNanos).append("extras", extras).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(speed).append(altitude).append(elapsedRealtimeNanos).append(provider).append(extras).append(time).append(distance).append(_class).append(bearing).append(longitude).append(latitude).append(accuracy).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DeviceLocationData) == false) {
            return false;
        }
        DeviceLocationData rhs = ((DeviceLocationData) other);
        return new EqualsBuilder().append(speed, rhs.speed).append(altitude, rhs.altitude).append(elapsedRealtimeNanos, rhs.elapsedRealtimeNanos).append(provider, rhs.provider).append(extras, rhs.extras).append(time, rhs.time).append(distance, rhs.distance).append(_class, rhs._class).append(bearing, rhs.bearing).append(longitude, rhs.longitude).append(latitude, rhs.latitude).append(accuracy, rhs.accuracy).isEquals();
    }

}
