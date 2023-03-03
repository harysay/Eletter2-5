package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponUrl {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("url")
    @Expose
    private String url;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String data) {
        this.url = url;
    }


}
