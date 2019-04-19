package in.xlayer.f2h.driver.other.booking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PaymentInfo {

    @SerializedName("duration")
    @Expose
    private Double duration;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("total_amount")
    @Expose
    private Double totalAmount;

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("duration", duration).append("status", status).append("mode", mode).append("totalAmount", totalAmount).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(duration).append(status).append(totalAmount).append(mode).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PaymentInfo) == false) {
            return false;
        }
        PaymentInfo rhs = ((PaymentInfo) other);
        return new EqualsBuilder().append(duration, rhs.duration).append(status, rhs.status).append(totalAmount, rhs.totalAmount).append(mode, rhs.mode).isEquals();
    }

}
