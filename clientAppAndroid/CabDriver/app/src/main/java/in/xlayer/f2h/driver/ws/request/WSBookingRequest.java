
package in.xlayer.f2h.driver.ws.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class WSBookingRequest implements Serializable
{

    @SerializedName("promo_code")
    @Expose
    private String promoCode;
    @SerializedName("end_point")
    @Expose
    private List<EndPoint> endPoint = null;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("request_id")
    @Expose
    private String requestId;
    @SerializedName("estimate")
    @Expose
    private List<Estimate> estimate = null;
    @SerializedName("start_point")
    @Expose
    private List<StartPoint> startPoint = null;
    @SerializedName("booking_code")
    @Expose
    private Long bookingCode;
    @SerializedName("time")
    @Expose
    private Long time;
    private final static long serialVersionUID = 2920281351761283476L;

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public List<EndPoint> getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(List<EndPoint> endPoint) {
        this.endPoint = endPoint;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Estimate> getEstimate() {
        return estimate;
    }

    public void setEstimate(List<Estimate> estimate) {
        this.estimate = estimate;
    }

    public List<StartPoint> getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(List<StartPoint> startPoint) {
        this.startPoint = startPoint;
    }

    public Long getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(Long bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("promoCode", promoCode).append("endPoint", endPoint).append("paymentType", paymentType).append("requestId", requestId).append("estimate", estimate).append("startPoint", startPoint).append("bookingCode", bookingCode).append("time", time).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(time).append(endPoint).append(requestId).append(paymentType).append(estimate).append(promoCode).append(bookingCode).append(startPoint).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WSBookingRequest) == false) {
            return false;
        }
        WSBookingRequest rhs = ((WSBookingRequest) other);
        return new EqualsBuilder().append(time, rhs.time).append(endPoint, rhs.endPoint).append(requestId, rhs.requestId).append(paymentType, rhs.paymentType).append(estimate, rhs.estimate).append(promoCode, rhs.promoCode).append(bookingCode, rhs.bookingCode).append(startPoint, rhs.startPoint).isEquals();
    }

}
