package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailLogin {

    @SerializedName("pns")
    @Expose
    private int pns;
    @SerializedName("id_pns")
    @Expose
    private String idPns;
    @SerializedName("nip")
    @Expose
    private String nip;
    @SerializedName("nip_lama")
    @Expose
    private String nipLama;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("id_unit_kerja")
    @Expose
    private String idUnitKerja;
    @SerializedName("unit_kerja")
    @Expose
    private String unitKerja;
    @SerializedName("id_jabatan")
    @Expose
    private String idJabatan;
    @SerializedName("jabatan")
    @Expose
    private String jabatan;
    @SerializedName("status_jabatan")
    @Expose
    private String statusJabatan;

    public int getPns() {
        return pns;
    }

    public void setPns(int pns) {
        this.pns = pns;
    }

    public String getIdPns() {
        return idPns;
    }

    public void setIdPns(String idPns) {
        this.idPns = idPns;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNipLama() {
        return nipLama;
    }

    public void setNipLama(String nipLama) {
        this.nipLama = nipLama;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIdUnitKerja() {
        return idUnitKerja;
    }

    public void setIdUnitKerja(String idUnitKerja) {
        this.idUnitKerja = idUnitKerja;
    }

    public String getUnitKerja() {
        return unitKerja;
    }

    public void setUnitKerja(String unitKerja) {
        this.unitKerja = unitKerja;
    }

    public String getIdJabatan() {
        return idJabatan;
    }

    public void setIdJabatan(String idJabatan) {
        this.idJabatan = idJabatan;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getStatusJabatan() {
        return statusJabatan;
    }

    public void setStatusJabatan(String statusJabatan) {
        this.statusJabatan = statusJabatan;
    }

}