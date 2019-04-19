package in.xlayer.f2h.driver.other.dinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class EntityInfo {

    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("contact_info")
    @Expose
    private List<ContactInfo> contactInfo = null;
    @SerializedName("loc_info")
    @Expose
    private List<LocInfo> locInfo = null;
    @SerializedName("name")
    @Expose
    private String name;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<ContactInfo> getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(List<ContactInfo> contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<LocInfo> getLocInfo() {
        return locInfo;
    }

    public void setLocInfo(List<LocInfo> locInfo) {
        this.locInfo = locInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("logo", logo).append("contactInfo", contactInfo).append("locInfo", locInfo).append("name", name).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(logo).append(contactInfo).append(name).append(locInfo).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof EntityInfo) == false) {
            return false;
        }
        EntityInfo rhs = ((EntityInfo) other);
        return new EqualsBuilder().append(logo, rhs.logo).append(contactInfo, rhs.contactInfo).append(name, rhs.name).append(locInfo, rhs.locInfo).isEquals();
    }

}
