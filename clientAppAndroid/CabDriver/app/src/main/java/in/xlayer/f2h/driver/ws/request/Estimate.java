
package in.xlayer.f2h.driver.ws.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class Estimate implements Serializable
{

    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("distance")
    @Expose
    private Long distance;
    @SerializedName("price")
    @Expose
    private Double price;
    private final static long serialVersionUID = 3873855427574689057L;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("duration", duration).append("distance", distance).append("price", price).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(distance).append(duration).append(price).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Estimate) == false) {
            return false;
        }
        Estimate rhs = ((Estimate) other);
        return new EqualsBuilder().append(distance, rhs.distance).append(duration, rhs.duration).append(price, rhs.price).isEquals();
    }

}
