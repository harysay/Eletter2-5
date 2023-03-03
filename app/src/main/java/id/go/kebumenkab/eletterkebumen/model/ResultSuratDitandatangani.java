package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultSuratDitandatangani {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<ArsipSuratDitandatanganiDesa> data = null;

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    @SerializedName("pesan")
    @Expose
    private String pesan;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ArsipSuratDitandatanganiDesa> getData() {
        return data;
    }

    public void setData(List<ArsipSuratDitandatanganiDesa> data) {
        this.data = data;
    }

}