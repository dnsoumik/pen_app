
package in.xlayer.f2h.driver.ws.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class StartPoint implements Serializable
{

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("addr")
    @Expose
    private String addr;
    private final static long serialVersionUID = 6769824311526337376L;

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
        if ((other instanceof StartPoint) == false) {
            return false;
        }
        StartPoint rhs = ((StartPoint) other);
        return new EqualsBuilder().append(addr, rhs.addr).append(lng, rhs.lng).append(lat, rhs.lat).isEquals();
    }

}
