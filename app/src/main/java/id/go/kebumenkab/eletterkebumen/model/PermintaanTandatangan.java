package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PermintaanTandatangan implements Serializable {

    @SerializedName("id_permohonan_ttd")
    @Expose
    private String idPermohonanTtd;
    @SerializedName("aplikasi")
    @Expose
    private String aplikasi;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("nip_penerima")
    @Expose
    private String nipPenerima;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("pdf")
    @Expose
    private String pdf;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp_update")
    @Expose
    private String timestampUpdate;
    @SerializedName("timestamp_insert")
    @Expose
    private String timestampInsert;

    public String getIdPermohonanTtd() {
        return idPermohonanTtd;
    }

    public void setIdPermohonanTtd(String idPermohonanTtd) {
        this.idPermohonanTtd = idPermohonanTtd;
    }

    public String getAplikasi() {
        return aplikasi;
    }

    public void setAplikasi(String aplikasi) {
        this.aplikasi = aplikasi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getNipPenerima() {
        return nipPenerima;
    }

    public void setNipPenerima(String nipPenerima) {
        this.nipPenerima = nipPenerima;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestampUpdate() {
        return timestampUpdate;
    }

    public void setTimestampUpdate(String timestampUpdate) {
        this.timestampUpdate = timestampUpdate;
    }

    public String getTimestampInsert() {
        return timestampInsert;
    }

    public void setTimestampInsert(String timestampInsert) {
        this.timestampInsert = timestampInsert;
    }

}