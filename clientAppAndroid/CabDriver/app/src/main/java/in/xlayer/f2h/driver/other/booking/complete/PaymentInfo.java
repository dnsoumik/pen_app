package in.xlayer.f2h.driver.other.booking.complete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PaymentInfo {

    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("mode")
    @Expose
    private String mode;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("price", price).append("mode", mode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(price).append(mode).toHashCode();
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
        return new EqualsBuilder().append(price, rhs.price).append(mode, rhs.mode).isEquals();
    }

}
