package in.xlayer.f2h.driver.other.booking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DropInfo {

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("addr")
    @Expose
    private String addr;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("lat", lat).append("lng", lng).append("addr", addr).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(addr).append(lng).append(lat).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DropInfo) == false) {
            return false;
        }
        DropInfo rhs = ((DropInfo) other);
        return new EqualsBuilder().append(addr, rhs.addr).append(lng, rhs.lng).append(lat, rhs.lat).isEquals();
    }

}
