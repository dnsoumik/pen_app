package in.xlayer.f2h.driver.other.booking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Datum {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("vehicle_info")
    @Expose
    private List<VehicleInfo> vehicleInfo = null;
    @SerializedName("feedback")
    @Expose
    private List<Feedback> feedback = null;
    @SerializedName("user_info")
    @Expose
    private List<UserInfo> userInfo = null;
    @SerializedName("user_booking_id")
    @Expose
    private String userBookingId;
    @SerializedName("start_time")
    @Expose
    private Integer startTime;
    @SerializedName("payment_info")
    @Expose
    private List<PaymentInfo> paymentInfo = null;
    @SerializedName("pick_info")
    @Expose
    private List<PickInfo> pickInfo = null;
    @SerializedName("drop_info")
    @Expose
    private List<DropInfo> dropInfo = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("booking_code")
    @Expose
    private Integer bookingCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<VehicleInfo> getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(List<VehicleInfo> vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public List<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<Feedback> feedback) {
        this.feedback = feedback;
    }

    public List<UserInfo> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserBookingId() {
        return userBookingId;
    }

    public void setUserBookingId(String userBookingId) {
        this.userBookingId = userBookingId;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public List<PaymentInfo> getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(List<PaymentInfo> paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public List<PickInfo> getPickInfo() {
        return pickInfo;
    }

    public void setPickInfo(List<PickInfo> pickInfo) {
        this.pickInfo = pickInfo;
    }

    public List<DropInfo> getDropInfo() {
        return dropInfo;
    }

    public void setDropInfo(List<DropInfo> dropInfo) {
        this.dropInfo = dropInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(Integer bookingCode) {
        this.bookingCode = bookingCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("vehicleInfo", vehicleInfo).append("feedback", feedback).append("userInfo", userInfo).append("userBookingId", userBookingId).append("startTime", startTime).append("paymentInfo", paymentInfo).append("pickInfo", pickInfo).append("dropInfo", dropInfo).append("id", id).append("bookingCode", bookingCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(startTime).append(feedback).append(paymentInfo).append(userBookingId).append(status).append(dropInfo).append(userInfo).append(pickInfo).append(vehicleInfo).append(bookingCode).toHashCode();
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
        return new EqualsBuilder().append(id, rhs.id).append(startTime, rhs.startTime).append(feedback, rhs.feedback).append(paymentInfo, rhs.paymentInfo).append(userBookingId, rhs.userBookingId).append(status, rhs.status).append(dropInfo, rhs.dropInfo).append(userInfo, rhs.userInfo).append(pickInfo, rhs.pickInfo).append(vehicleInfo, rhs.vehicleInfo).append(bookingCode, rhs.bookingCode).isEquals();
    }

}
