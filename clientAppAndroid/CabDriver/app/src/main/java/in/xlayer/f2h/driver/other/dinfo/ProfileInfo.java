package in.xlayer.f2h.driver.other.dinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProfileInfo {

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("cover_pic")
    @Expose
    private String coverPic;
    @SerializedName("rating")
    @Expose
    private Long rating;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("profilePic", profilePic).append("coverPic", coverPic).append("rating", rating).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(coverPic).append(profilePic).append(rating).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ProfileInfo)) {
            return false;
        }
        ProfileInfo rhs = ((ProfileInfo) other);
        return new EqualsBuilder().append(coverPic, rhs.coverPic).append(profilePic, rhs.profilePic).append(rating, rhs.rating).isEquals();
    }

}
