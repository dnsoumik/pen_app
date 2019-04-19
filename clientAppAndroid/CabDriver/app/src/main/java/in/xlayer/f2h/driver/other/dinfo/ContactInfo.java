package in.xlayer.f2h.driver.other.dinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ContactInfo {

    @SerializedName("email_id")
    @Expose
    private String emailId;
    @SerializedName("technical")
    @Expose
    private String technical;
    @SerializedName("support")
    @Expose
    private String support;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String technical) {
        this.technical = technical;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("emailId", emailId).append("technical", technical).append("support", support).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(emailId).append(support).append(technical).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ContactInfo) == false) {
            return false;
        }
        ContactInfo rhs = ((ContactInfo) other);
        return new EqualsBuilder().append(emailId, rhs.emailId).append(support, rhs.support).append(technical, rhs.technical).isEquals();
    }

}
