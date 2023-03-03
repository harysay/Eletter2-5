package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pemberitahuan implements Serializable {

    @SerializedName("id_pemberitahuan")
    @Expose
    private String idPemberitahuan;
    @SerializedName("id_surat")
    @Expose
    private String idSurat;
    @SerializedName("jenis")
    @Expose
    private String jenis;
    @SerializedName("nama_pelaksana")
    @Expose
    private String namaPelaksana;
    @SerializedName("jabatan_pelaksana")
    @Expose
    private String jabatanPelaksana;
    @SerializedName("unit_kerja_pelaksana")
    @Expose
    private String unitKerjaPelaksana;

    @SerializedName("nama_surat")
    @Expose
    private String namaSurat;

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    @SerializedName("pesan")
    @Expose
    private String pesan;

    public String getIdPemberitahuan() {
        return idPemberitahuan;
    }

    public void setIdPemberitahuan(String idPemberitahuan) {
        this.idPemberitahuan = idPemberitahuan;
    }

    public String getIdSurat() {
        return idSurat;
    }

    public void setIdSurat(String idSurat) {
        this.idSurat = idSurat;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getNamaPelaksana() {
        return namaPelaksana;
    }

    public void setNamaPelaksana(String namaPelaksana) {
        this.namaPelaksana = namaPelaksana;
    }

    public String getJabatanPelaksana() {
        return jabatanPelaksana;
    }

    public void setJabatanPelaksana(String jabatanPelaksana) {
        this.jabatanPelaksana = jabatanPelaksana;
    }

    public String getUnitKerjaPelaksana() {
        return unitKerjaPelaksana;
    }

    public void setUnitKerjaPelaksana(String unitKerjaPelaksana) {
        this.unitKerjaPelaksana = unitKerjaPelaksana;
    }

    public String getNamaSurat() {
        return namaSurat;
    }

    public void setNamaSurat(String namaSurat) {
        this.namaSurat = namaSurat;
    }

}
