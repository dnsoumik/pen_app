package in.xlayer.f2h.driver.other.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Distance {

    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("value")
    @Expose
    private Double value;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("unit", unit).append("value", value).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(unit).append(value).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Distance) == false) {
            return false;
        }
        Distance rhs = ((Distance) other);
        return new EqualsBuilder().append(unit, rhs.unit).append(value, rhs.value).isEquals();
    }

}
