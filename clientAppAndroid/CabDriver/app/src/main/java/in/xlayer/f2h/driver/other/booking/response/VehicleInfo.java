package in.xlayer.f2h.driver.other.booking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VehicleInfo {

    @SerializedName("colour")
    @Expose
    private String colour;
    @SerializedName("reg_num")
    @Expose
    private String regNum;
    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("colour", colour).append("regNum", regNum).append("make", make).append("image", image).append("model", model).append("type", type).append("id", id).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(colour).append(model).append(regNum).append(image).append(type).append(make).toHashCode();
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
        return new EqualsBuilder().append(id, rhs.id).append(colour, rhs.colour).append(model, rhs.model).append(regNum, rhs.regNum).append(image, rhs.image).append(type, rhs.type).append(make, rhs.make).isEquals();
    }

}
