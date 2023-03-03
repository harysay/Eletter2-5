package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArsipSuratDitandatanganiDesa implements Serializable {


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

    public String getTanggalSurat() {
        return tanggalSurat;
    }

    public void setTanggalSurat(String tanggalDibuat) {
        this.tanggalSurat = tanggalSurat;
    }

    @SerializedName("jenis_surat")
    @Expose
    private String jenisSurat;

    @SerializedName("tanggal_surat")
    @Expose
    private String tanggalSurat;



    public ArsipSuratDitandatanganiDesa(){

    }

    public ArsipSuratDitandatanganiDesa(ArsipSuratDitandatanganiDesa suratMasuk){
        this.id = suratMasuk.getId();
        this.namaPenduduk = suratMasuk.getNamaPenduduk();
        this.tanggalSurat = suratMasuk.getTanggalSurat();
        this.jenisSurat = suratMasuk.getJenisSurat();
    }


}