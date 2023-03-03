package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultPemberitahuan {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Pemberitahuan> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Pemberitahuan> getData() {
        return data;
    }

    public void setData(List<Pemberitahuan> data) {
        this.data = data;
    }
}
