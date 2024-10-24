package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArsipLainData implements Serializable {

    @SerializedName("IsImportant")
    @Expose
    private boolean isImportant;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("id_surat")
    @Expose
    private String idSurat;
    @SerializedName("id_surat_internal_tujuan")
    @Expose
    private String idSuratTujuan;
    @SerializedName("nomor")
    @Expose
    private String nomor;
    @SerializedName("id_history")
    @Expose
    private String idHistory;
    @SerializedName("lampiran")
    @Expose
    private String lampiran;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("isread")
    @Expose
    private String isread;
    @SerializedName("unit_kerja")
    @Expose
    private String unitKerja;
    @SerializedName("koreksi")
    @Expose
    private String koreksi;
    @SerializedName("jenis_tujuan")
    @Expose
    private String jenisTujuan;

    public boolean isIsImportant() {
        return isImportant;
    }

    public void setIsImportant(boolean isImportant) {
        this.isImportant = isImportant;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIdSurat() {
        return idSurat;
    }

    public String getIdSuratTujuan() {
        return idSuratTujuan;
    }
    public void setIdSuratTujuan(String idSuratTujuan) {
        this.idSuratTujuan = idSuratTujuan;
    }

    public void setIdSurat(String idSurat) {
        this.idSurat = idSurat;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(String idHistory) {
        this.idHistory = idHistory;
    }

    public String getLampiran() {
        return lampiran;
    }

    public void setLampiran(String lampiran) {
        this.lampiran = lampiran;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getUnitKerja() {
        return unitKerja;
    }

    public void setUnitKerja(String unitKerja) {
        this.unitKerja = unitKerja;
    }

    public String getKoreksi() {
        return koreksi;
    }

    public void setKoreksi(String koreksi) {
        this.koreksi = koreksi;
    }

    public String getJenisTujuan() {
        return jenisTujuan;
    }

    public void setJenisTujuan(String jenisTujuan) {
        this.jenisTujuan = jenisTujuan;
    }

}