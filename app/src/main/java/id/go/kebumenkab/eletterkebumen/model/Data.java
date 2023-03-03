package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("pesan")
    @Expose
    private String pesan;
    @SerializedName("bawahan")
    @Expose
    private List<String> bawahan = null;
    @SerializedName("tindakan")
    @Expose
    private List<String> tindakan = null;

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public List<String> getBawahan() {
        return bawahan;
    }

    public void setBawahan(List<String> bawahan) {
        this.bawahan = bawahan;
    }

    public List<String> getTindakan() {
        return tindakan;
    }

    public void setTindakan(List<String> tindakan) {
        this.tindakan = tindakan;
    }

}