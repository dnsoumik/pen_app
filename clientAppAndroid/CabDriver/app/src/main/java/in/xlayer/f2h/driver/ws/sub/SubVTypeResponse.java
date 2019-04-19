package in.xlayer.f2h.driver.ws.sub;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class SubVTypeResponse {

    @SerializedName("status_code")
    @Expose
    private Long statusCode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("msg_type")
    @Expose
    private String msgType;

    public Long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("statusCode", statusCode).append("data", data).append("response", response).append("msgType", msgType).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(response).append(statusCode).append(data).append(msgType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SubVTypeResponse) == false) {
            return false;
        }
        SubVTypeResponse rhs = ((SubVTypeResponse) other);
        return new EqualsBuilder().append(response, rhs.response).append(statusCode, rhs.statusCode).append(data, rhs.data).append(msgType, rhs.msgType).isEquals();
    }

}
