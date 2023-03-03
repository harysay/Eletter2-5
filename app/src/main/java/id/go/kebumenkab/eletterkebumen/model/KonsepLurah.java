package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KonsepLurah implements Serializable {



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;


    @SerializedName("id_surat")
    @Expose
    private String idSurat;
    @SerializedName("id_history")
    @Expose
    private String idHistory;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timestamp")

    @Expose
    private String isRead;
    @SerializedName("unit_kerja")
    @Expose
    private String unitKerja;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;


    public String getPernahDikoreksi() {
        return pernahDikoreksi;
    }

    public void setPernahDikoreksi(String pernahDikoreksi) {
        this.pernahDikoreksi = pernahDikoreksi;
    }

    @SerializedName("koreksi")
    @Expose
    private String pernahDikoreksi;

    public KonsepLurah() {
    }

    public KonsepLurah(KonsepLurah konsep){
        this.idSurat = konsep.getIdSurat();
        this.from = konsep.getFrom();
        this.idHistory = konsep.getIdHistory();
        this.pernahDikoreksi = konsep.getPernahDikoreksi();
    }


    public String getIdSurat() {
        return idSurat;
    }

    public void setIdSurat(String idSurat) {
        this.idSurat = idSurat;
    }

    public String getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(String idHistory) {
        this.idHistory = idHistory;
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


    public String getUnitKerja() {
        return unitKerja;
    }

    public void setUnitKerja(String unitKerja) {
        this.unitKerja = unitKerja;
    }

}
