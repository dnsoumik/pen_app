package in.xlayer.f2h.driver.other.booking.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VTypeInfo {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("rate_per_km")
    @Expose
    private Long ratePerKm;
    @SerializedName("hourly_rate")
    @Expose
    private Long hourlyRate;
    @SerializedName("image")
    @Expose
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(Long ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public Long getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Long hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("id", id).append("ratePerKm", ratePerKm).append("hourlyRate", hourlyRate).append("image", image).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(hourlyRate).append(name).append(image).append(ratePerKm).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VTypeInfo) == false) {
            return false;
        }
        VTypeInfo rhs = ((VTypeInfo) other);
        return new EqualsBuilder().append(id, rhs.id).append(hourlyRate, rhs.hourlyRate).append(name, rhs.name).append(image, rhs.image).append(ratePerKm, rhs.ratePerKm).isEquals();
    }

}
