
package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultKonsepDesa {

    @SerializedName("status")
    @Expose
    private String status;

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    @SerializedName("pesan")
    @Expose
    private String pesan;

    @SerializedName("data")
    @Expose
    private List<KonsepDesa> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<KonsepDesa> getData() {
        return data;
    }

    public void setData(List<KonsepDesa> data) {
        this.data = data;
    }

}