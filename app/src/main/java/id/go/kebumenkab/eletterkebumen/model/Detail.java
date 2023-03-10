package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Detail {

    @SerializedName("hide_surat")
    @Expose
    private boolean hideSurat;
    @SerializedName("id_surat")
    @Expose
    private String idSurat;
    @SerializedName("id_history")
    @Expose
    private String idHistory;
    @SerializedName("surat_alur")
    @Expose
    private String suratAlur;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("nomor_surat")
    @Expose
    private String nomorSurat;
    @SerializedName("jenis_surat")
    @Expose
    private String jenisSurat;
    @SerializedName("nama_inisiator")
    @Expose
    private String namaInisiator;
    @SerializedName("jabatan_inisiator")
    @Expose
    private String jabatanInisiator;
    @SerializedName("unit_kerja_inisiator")
    @Expose
    private String unitKerjaInisiator;
    @SerializedName("pejabat_ttd")
    @Expose
    private String pejabatTtd;
    @SerializedName("pejabat_ttd_melalui")
    @Expose
    private String pejabatTtdMelalui;
    @SerializedName("nama_surat")
    @Expose
    private String namaSurat;
    @SerializedName("deskripsi_surat")
    @Expose
    private String deskripsiSurat;
    @SerializedName("jumlah_koreksi")
    @Expose
    private String jumlahKoreksi;
    @SerializedName("pesan")
    @Expose
    private String pesan;
    @SerializedName("tujuan")
    @Expose
    private String tujuan;

    @SerializedName("lampiran")
    @Expose
    private String lampiran;


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @SerializedName("from")
    @Expose
    private String from;


    @SerializedName("jenis_penerima")
    @Expose
    private String jenis_penerima;

    @SerializedName("tandai")
    @Expose
    private String tandai;

    public String getTandai() {
        return tandai;
    }

    public void setTandai(String tandai) {
        this.tandai = tandai;
    }

    public String getJenis_penerima() {
        return jenis_penerima;
    }

    public void setJenis_penerima(String jenis_penerima) {
        this.jenis_penerima = jenis_penerima;
    }

    public boolean getIsHideSurat() {
        return hideSurat;
    }

    public void setIsHideSurat(boolean hideSurat) {
        this.hideSurat = hideSurat;
    }


    public String getLampiran() {
        return lampiran;
    }

    public void setLampiran(String lampiran) {
        this.lampiran = lampiran;
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

    public String getSuratAlur() {
        return suratAlur;
    }

    public void setSuratAlur(String suratAlur) {
        this.suratAlur = suratAlur;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNomorSurat() {
        return nomorSurat;
    }

    public void setNomorSurat(String nomorSurat) {
        this.nomorSurat = nomorSurat;
    }

    public String getJenisSurat() {
        return jenisSurat;
    }

    public void setJenisSurat(String jenisSurat) {
        this.jenisSurat = jenisSurat;
    }

    public String getNamaInisiator() {
        return namaInisiator;
    }

    public void setNamaInisiator(String namaInisiator) {
        this.namaInisiator = namaInisiator;
    }

    public String getJabatanInisiator() {
        return jabatanInisiator;
    }

    public void setJabatanInisiator(String jabatanInisiator) {
        this.jabatanInisiator = jabatanInisiator;
    }

    public String getUnitKerjaInisiator() {
        return unitKerjaInisiator;
    }

    public void setUnitKerjaInisiator(String unitKerjaInisiator) {
        this.unitKerjaInisiator = unitKerjaInisiator;
    }

    public String getPejabatTtd() {
        return pejabatTtd;
    }

    public void setPejabatTtd(String pejabatTtd) {
        this.pejabatTtd = pejabatTtd;
    }

    public String getPejabatTtdMelalui() {
        return pejabatTtdMelalui;
    }

    public void setPejabatTtdMelalui(String pejabatTtdMelalui) {
        this.pejabatTtdMelalui = pejabatTtdMelalui;
    }

    public String getNamaSurat() {
        return namaSurat;
    }

    public void setNamaSurat(String namaSurat) {
        this.namaSurat = namaSurat;
    }

    public String getDeskripsiSurat() {
        return deskripsiSurat;
    }

    public void setDeskripsiSurat(String deskripsiSurat) {
        this.deskripsiSurat = deskripsiSurat;
    }

    public String getJumlahKoreksi() {
        return jumlahKoreksi;
    }

    public void setJumlahKoreksi(String jumlahKoreksi) {
        this.jumlahKoreksi = jumlahKoreksi;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

}