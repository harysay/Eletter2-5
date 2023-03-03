package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PenerimaKonsep implements Serializable {

    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("proses")
    @Expose
    private String proses;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getProses() {
        return proses;
    }

    public void setProses(String proses) {
        this.proses = proses;
    }

}
