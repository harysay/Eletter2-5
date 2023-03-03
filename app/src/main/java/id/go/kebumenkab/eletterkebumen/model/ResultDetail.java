package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultDetail {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("detail")
    @Expose
    private Detail detail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

}
