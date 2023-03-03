package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KonsepDesa implements Serializable {


    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("nama_penduduk")
    @Expose
    private String namaPenduduk;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaPenduduk() {
        return namaPenduduk;
    }

    public void setNamaPenduduk(String namaPenduduk) {
        this.namaPenduduk = namaPenduduk;
    }

    public String getJenisSurat() {
        return jenisSurat;
    }

    public void setJenisSurat(String jenisSurat) {
        this.jenisSurat = jenisSurat;
    }

    public String getTanggalDibuat() {
        return tanggalDibuat;
    }

    public void setTanggalDibuat(String tanggalDibuat) {
        this.tanggalDibuat = tanggalDibuat;
    }

    @SerializedName("jenis_surat")
    @Expose
    private String jenisSurat;

    @SerializedName("tanggal_dibuat")
    @Expose
    private String tanggalDibuat;


    public KonsepDesa() {
    }

    public KonsepDesa(KonsepDesa konsep){
        this.id = konsep.getId();
        this.namaPenduduk = konsep.getNamaPenduduk();
        this.tanggalDibuat = konsep.getTanggalDibuat();
        this.jenisSurat = konsep.getJenisSurat();
    }
}
