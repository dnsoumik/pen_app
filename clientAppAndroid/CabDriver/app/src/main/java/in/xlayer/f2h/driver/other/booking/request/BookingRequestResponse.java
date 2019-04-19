package in.xlayer.f2h.driver.other.booking.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class BookingRequestResponse {

    @SerializedName("resp_msg")
    @Expose
    private String respMsg;
    @SerializedName("error_code")
    @Expose
    private Long errorCode;
    @SerializedName("resp_code")
    @Expose
    private Boolean respCode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getRespCode() {
        return respCode;
    }

    public void setRespCode(Boolean respCode) {
        this.respCode = respCode;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("respMsg", respMsg).append("errorCode", errorCode).append("respCode", respCode).append("data", data).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(respCode).append(data).append(errorCode).append(respMsg).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof BookingRequestResponse) == false) {
            return false;
        }
        BookingRequestResponse rhs = ((BookingRequestResponse) other);
        return new EqualsBuilder().append(respCode, rhs.respCode).append(data, rhs.data).append(errorCode, rhs.errorCode).append(respMsg, rhs.respMsg).isEquals();
    }

}
