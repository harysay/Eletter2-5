package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Konsep implements Serializable {

    private int color = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    @SerializedName("IsImportant")
    @Expose
    private boolean isImportant;
    @SerializedName("picture")
    @Expose
    private String picture;
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
    private String timestamp;
    @SerializedName("isread")
    @Expose
    private String isRead;
    @SerializedName("unit_kerja")
    @Expose
    private String unitKerja;

    @SerializedName("tandai")
    @Expose
    private String tandai;

    public String getTandai() {
        return tandai;
    }

    public void setTandai(String tandai) {
        this.tandai = tandai;
    }



    public String getPernahDikoreksi() {
        return pernahDikoreksi;
    }

    public void setPernahDikoreksi(String pernahDikoreksi) {
        this.pernahDikoreksi = pernahDikoreksi;
    }

    @SerializedName("koreksi")
    @Expose
    private String pernahDikoreksi;

    public Konsep() {
    }

    public Konsep (Konsep konsep){
        this.idSurat = konsep.getIdSurat();
        this.from = konsep.getFrom();
        this.idHistory = konsep.getIdHistory();
        this.pernahDikoreksi = konsep.getPernahDikoreksi();
    }


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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getUnitKerja() {
        return unitKerja;
    }

    public void setUnitKerja(String unitKerja) {
        this.unitKerja = unitKerja;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
