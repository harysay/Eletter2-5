package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultBawahan {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Bawahan> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Bawahan> getData() {
        return data;
    }

    public void setData(List<Bawahan> data) {
        this.data = data;
    }

}
