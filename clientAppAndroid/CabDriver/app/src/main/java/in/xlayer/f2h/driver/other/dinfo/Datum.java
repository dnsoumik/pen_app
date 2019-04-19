package in.xlayer.f2h.driver.other.dinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Datum {

    @SerializedName("vehicle_info")
    @Expose
    private List<VehicleInfo> vehicleInfo = null;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email_id")
    @Expose
    private String emailId;
    @SerializedName("entity_info")
    @Expose
    private List<EntityInfo> entityInfo = null;
    @SerializedName("active_booking_id")
    @Expose
    private String activeBookingId;
    @SerializedName("profile_info")
    @Expose
    private List<ProfileInfo> profileInfo = null;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    public List<VehicleInfo> getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(List<VehicleInfo> vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public List<EntityInfo> getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(List<EntityInfo> entityInfo) {
        this.entityInfo = entityInfo;
    }

    public String getActiveBookingId() {
        return activeBookingId;
    }

    public void setActiveBookingId(String activeBookingId) {
        this.activeBookingId = activeBookingId;
    }

    public List<ProfileInfo> getProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(List<ProfileInfo> profileInfo) {
        this.profileInfo = profileInfo;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("vehicleInfo", vehicleInfo).append("fullName", fullName).append("emailId", emailId).append("entityInfo", entityInfo).append("activeBookingId", activeBookingId).append("profileInfo", profileInfo).append("mobileNumber", mobileNumber).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(emailId).append(activeBookingId).append(profileInfo).append(entityInfo).append(fullName).append(mobileNumber).append(vehicleInfo).toHashCode();
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
        return new EqualsBuilder().append(emailId, rhs.emailId).append(activeBookingId, rhs.activeBookingId).append(profileInfo, rhs.profileInfo).append(entityInfo, rhs.entityInfo).append(fullName, rhs.fullName).append(mobileNumber, rhs.mobileNumber).append(vehicleInfo, rhs.vehicleInfo).isEquals();
    }

}
