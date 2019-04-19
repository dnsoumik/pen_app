package in.xlayer.f2h.driver.ws.sub;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Datum {

    @SerializedName("v_id")
    @Expose
    private String vId;
    @SerializedName("location")
    @Expose
    private List<Location> location = null;
    @SerializedName("type_id")
    @Expose
    private String typeId;

    public String getVId() {
        return vId;
    }

    public void setVId(String vId) {
        this.vId = vId;
    }

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("vId", vId).append("location", location).append("typeId", typeId).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(location).append(vId).append(typeId).toHashCode();
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
        return new EqualsBuilder().append(location, rhs.location).append(vId, rhs.vId).append(typeId, rhs.typeId).isEquals();
    }

}
