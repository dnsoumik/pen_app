package in.xlayer.f2h.driver.ws;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class SocketResponse implements Parcelable {

    public final static Creator<SocketResponse> CREATOR = new Creator<SocketResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SocketResponse createFromParcel(Parcel in) {
            return new SocketResponse(in);
        }

        public SocketResponse[] newArray(int size) {
            return (new SocketResponse[size]);
        }

    };
    @SerializedName("status_code")
    @Expose
    private Long statusCode;
    @SerializedName("data")
    @Expose
    private List<Object> data = null;
    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("msg_type")
    @Expose
    private String msgType;

    private SocketResponse(Parcel in) {
        this.statusCode = ((Long) in.readValue((Long.class.getClassLoader())));
        in.readList(this.data, (Object.class.getClassLoader()));
        this.response = ((String) in.readValue((String.class.getClassLoader())));
        this.msgType = ((String) in.readValue((String.class.getClassLoader())));
    }

    public SocketResponse() {
    }

    public Long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
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
        if (!(other instanceof SocketResponse)) {
            return false;
        }
        SocketResponse rhs = ((SocketResponse) other);
        return new EqualsBuilder().append(response, rhs.response).append(statusCode, rhs.statusCode).append(data, rhs.data).append(msgType, rhs.msgType).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(statusCode);
        dest.writeList(data);
        dest.writeValue(response);
        dest.writeValue(msgType);
    }

    public int describeContents() {
        return 0;
    }

}
