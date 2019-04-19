package in.xlayer.f2h.driver.other.dinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VehicleInfo {

    @SerializedName("reg_num")
    @Expose
    private String regNum;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("id")
    @Expose
    private String id;

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("regNum", regNum).append("model", model).append("make", make).append("id", id).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(model).append(regNum).append(make).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VehicleInfo) == false) {
            return false;
        }
        VehicleInfo rhs = ((VehicleInfo) other);
        return new EqualsBuilder().append(id, rhs.id).append(model, rhs.model).append(regNum, rhs.regNum).append(make, rhs.make).isEquals();
    }

}
