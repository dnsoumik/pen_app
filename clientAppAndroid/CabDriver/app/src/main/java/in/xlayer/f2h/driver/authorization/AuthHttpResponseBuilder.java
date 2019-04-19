package in.xlayer.f2h.driver.authorization;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AuthHttpResponseBuilder implements Serializable
{

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private Long code;
    @SerializedName("result")
    @Expose
    private List<Object> result = null;
    private final static long serialVersionUID = -5936858096052875381L;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public List<Object> getResult() {
        return result;
    }

    public void setResult(List<Object> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("message", message).append("code", code).append("result", result).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(message).append(result).append(status).append(code).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AuthHttpResponseBuilder) == false) {
            return false;
        }
        AuthHttpResponseBuilder rhs = ((AuthHttpResponseBuilder) other);
        return new EqualsBuilder().append(message, rhs.message).append(result, rhs.result).append(status, rhs.status).append(code, rhs.code).isEquals();
    }

}