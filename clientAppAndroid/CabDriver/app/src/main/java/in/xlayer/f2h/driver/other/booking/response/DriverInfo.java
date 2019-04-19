package in.xlayer.f2h.driver.other.booking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DriverInfo {

    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("profilePicture", profilePicture).append("name", name).append("mobileNumber", mobileNumber).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(profilePicture).append(mobileNumber).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DriverInfo) == false) {
            return false;
        }
        DriverInfo rhs = ((DriverInfo) other);
        return new EqualsBuilder().append(name, rhs.name).append(profilePicture, rhs.profilePicture).append(mobileNumber, rhs.mobileNumber).isEquals();
    }

}
