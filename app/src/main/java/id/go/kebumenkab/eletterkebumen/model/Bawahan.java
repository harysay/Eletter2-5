package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bawahan implements Serializable {

    @SerializedName("nip_baru")
    @Expose
    private String nipBaru;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("jabatan")
    @Expose
    private String jabatan;
    @SerializedName("unit_kerja")
    @Expose
    private String unitKerja;
    @SerializedName("id_unit_kerja")
    @Expose
    private String idUnitKerja;
    @SerializedName("kepala")
    @Expose
    private int kepala;

    private boolean isSelected;

    public String getNipBaru() {
        return nipBaru;
    }

    public void setNipBaru(String nipBaru) {
        this.nipBaru = nipBaru;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getUnitKerja() {
        return unitKerja;
    }

    public void setUnitKerja(String unitKerja) {
        this.unitKerja = unitKerja;
    }

    public String getIdUnitKerja() {
        return idUnitKerja;
    }

    public void setIdUnitKerja(String idUnitKerja) {
        this.idUnitKerja = idUnitKerja;
    }

    public int getKepala() {
        return kepala;
    }

    public void setKepala(int kepala) {
        this.kepala = kepala;
    }

    public boolean isSelect() {
        return isSelected;
    }

    public void setSelected(boolean status){
        this.isSelected = status;
    }
}
