package in.xlayer.f2h.driver.other.update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class UpdateResponseInfo {

    @SerializedName("error_code")
    @Expose
    private Long errorCode;
    @SerializedName("resp_code")
    @Expose
    private Boolean respCode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;

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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("errorCode", errorCode).append("respCode", respCode).append("data", data).append("errorMsg", errorMsg).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(respCode).append(data).append(errorCode).append(errorMsg).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof UpdateResponseInfo) == false) {
            return false;
        }
        UpdateResponseInfo rhs = ((UpdateResponseInfo) other);
        return new EqualsBuilder().append(respCode, rhs.respCode).append(data, rhs.data).append(errorCode, rhs.errorCode).append(errorMsg, rhs.errorMsg).isEquals();
    }

}
