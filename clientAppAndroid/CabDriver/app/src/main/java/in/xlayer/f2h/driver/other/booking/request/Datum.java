package in.xlayer.f2h.driver.other.booking.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Datum {

    @SerializedName("end_point")
    @Expose
    private List<EndPoint> endPoint = null;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("eta_price")
    @Expose
    private Double etaPrice;
    @SerializedName("v_type_info")
    @Expose
    private List<VTypeInfo> vTypeInfo = null;
    @SerializedName("start_point")
    @Expose
    private List<StartPoint> startPoint = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("booking_code")
    @Expose
    private Long bookingCode;

    public List<EndPoint> getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(List<EndPoint> endPoint) {
        this.endPoint = endPoint;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getEtaPrice() {
        return etaPrice;
    }

    public void setEtaPrice(Double etaPrice) {
        this.etaPrice = etaPrice;
    }

    public List<VTypeInfo> getVTypeInfo() {
        return vTypeInfo;
    }

    public void setVTypeInfo(List<VTypeInfo> vTypeInfo) {
        this.vTypeInfo = vTypeInfo;
    }

    public List<StartPoint> getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(List<StartPoint> startPoint) {
        this.startPoint = startPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(Long bookingCode) {
        this.bookingCode = bookingCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("endPoint", endPoint).append("paymentMode", paymentMode).append("etaPrice", etaPrice).append("vTypeInfo", vTypeInfo).append("startPoint", startPoint).append("id", id).append("bookingCode", bookingCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(paymentMode).append(endPoint).append(etaPrice).append(vTypeInfo).append(bookingCode).append(startPoint).toHashCode();
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
        return new EqualsBuilder().append(id, rhs.id).append(paymentMode, rhs.paymentMode).append(endPoint, rhs.endPoint).append(etaPrice, rhs.etaPrice).append(vTypeInfo, rhs.vTypeInfo).append(bookingCode, rhs.bookingCode).append(startPoint, rhs.startPoint).isEquals();
    }

}
