package in.xlayer.f2h.driver.other.booking.complete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Datum {

    @SerializedName("payment_info")
    @Expose
    private List<PaymentInfo> paymentInfo = null;
    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("travelled_distance")
    @Expose
    private double travelledDistance;

    public List<PaymentInfo> getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(List<PaymentInfo> paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getTravelledDistance() {
        return travelledDistance;
    }

    public void setTravelledDistance(double travelledDistance) {
        this.travelledDistance = travelledDistance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("paymentInfo", paymentInfo).append("bookingId", bookingId).append("travelledDistance", travelledDistance).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(travelledDistance).append(bookingId).append(paymentInfo).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Datum) == false) {
            return false;
        }
        Datum rhs = ((Datum) other);
        return new EqualsBuilder().append(travelledDistance, rhs.travelledDistance).append(bookingId, rhs.bookingId).append(paymentInfo, rhs.paymentInfo).isEquals();
    }

}
