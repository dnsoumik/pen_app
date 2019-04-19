package in.xlayer.f2h.driver.other;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class HttpResponseBuilder implements Parcelable {

    public final static Creator<HttpResponseBuilder> CREATOR = new Creator<HttpResponseBuilder>() {


        @NonNull
        @SuppressWarnings({
                "unchecked"
        })
        public HttpResponseBuilder createFromParcel(Parcel in) {
            return new HttpResponseBuilder(in);
        }

        @NonNull
        @Contract(pure = true)
        public HttpResponseBuilder[] newArray(int size) {
            return (new HttpResponseBuilder[size]);
        }

    };
    @SerializedName("resp_msg")
    @Expose
    private String respMsg;
    @SerializedName("data")
    @Expose
    private List<Object> data = null;
    @SerializedName("resp_code")
    @Expose
    private Boolean respCode;
    @SerializedName("error_code")
    @Expose
    private int errorCode;

    protected HttpResponseBuilder(Parcel in) {
        this.respMsg = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.data, (Object.class.getClassLoader()));
        this.respCode = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.errorCode = ((int) in.readValue((int.class.getClassLoader())));
    }

    public HttpResponseBuilder() {
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public Boolean getRespCode() {
        return respCode;
    }

    public void setRespCode(Boolean respCode) {
        this.respCode = respCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("respMsg", respMsg).append("data", data).append("respCode", respCode).append("errorCode", errorCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(respCode).append(errorCode).append(data).append(respMsg).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof HttpResponseBuilder) == false) {
            return false;
        }
        HttpResponseBuilder rhs = ((HttpResponseBuilder) other);
        return new EqualsBuilder().append(respCode, rhs.respCode).append(errorCode, rhs.errorCode).append(data, rhs.data).append(respMsg, rhs.respMsg).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(respMsg);
        dest.writeList(data);
        dest.writeValue(respCode);
        dest.writeValue(errorCode);
    }

    public int describeContents() {
        return 0;
    }

}
