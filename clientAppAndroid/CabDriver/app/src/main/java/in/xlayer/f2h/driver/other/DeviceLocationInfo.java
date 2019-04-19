package in.xlayer.f2h.driver.other;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DeviceLocationInfo implements Parcelable {

    public final static Creator<DeviceLocationInfo> CREATOR = new Creator<DeviceLocationInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DeviceLocationInfo createFromParcel(Parcel in) {
            return new DeviceLocationInfo(in);
        }

        public DeviceLocationInfo[] newArray(int size) {
            return (new DeviceLocationInfo[size]);
        }

    };
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
    private Double altitude;
    @SerializedName("bearing")
    @Expose
    private Long bearing;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("elapsed_realtime_nanos")
    @Expose
    private Long elapsedRealtimeNanos;
    @SerializedName("extras")
    @Expose
    private String extras;

    protected DeviceLocationInfo(Parcel in) {
        this._class = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.accuracy = ((Double) in.readValue((Double.class.getClassLoader())));
        this.altitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.bearing = ((Long) in.readValue((Long.class.getClassLoader())));
        this.provider = ((String) in.readValue((String.class.getClassLoader())));
        this.time = ((Long) in.readValue((Long.class.getClassLoader())));
        this.speed = ((Double) in.readValue((Double.class.getClassLoader())));
        this.elapsedRealtimeNanos = ((Long) in.readValue((Long.class.getClassLoader())));
        this.extras = ((String) in.readValue((String.class.getClassLoader())));
    }

    public DeviceLocationInfo() {
    }

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

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Long getBearing() {
        return bearing;
    }

    public void setBearing(Long bearing) {
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

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(Long elapsedRealtimeNanos) {
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
        if ((other instanceof DeviceLocationInfo) == false) {
            return false;
        }
        DeviceLocationInfo rhs = ((DeviceLocationInfo) other);
        return new EqualsBuilder().append(time, rhs.time).append(_class, rhs._class).append(speed, rhs.speed).append(altitude, rhs.altitude).append(elapsedRealtimeNanos, rhs.elapsedRealtimeNanos).append(bearing, rhs.bearing).append(provider, rhs.provider).append(longitude, rhs.longitude).append(latitude, rhs.latitude).append(accuracy, rhs.accuracy).append(extras, rhs.extras).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_class);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(accuracy);
        dest.writeValue(altitude);
        dest.writeValue(bearing);
        dest.writeValue(provider);
        dest.writeValue(time);
        dest.writeValue(speed);
        dest.writeValue(elapsedRealtimeNanos);
        dest.writeValue(extras);
    }

    public int describeContents() {
        return 0;
    }

}
