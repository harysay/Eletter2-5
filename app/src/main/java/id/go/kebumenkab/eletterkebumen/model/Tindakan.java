package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tindakan {

    @SerializedName("id_surat_disposisi_tindakan")
    @Expose
    private String idSuratDisposisiTindakan;
    @SerializedName("tindakan")
    @Expose
    private String tindakan;
    @SerializedName("tindakan_timestamp")
    @Expose
    private String tindakanTimestamp;

    public String getIdSuratDisposisiTindakan() {
        return idSuratDisposisiTindakan;
    }

    public void setIdSuratDisposisiTindakan(String idSuratDisposisiTindakan) {
        this.idSuratDisposisiTindakan = idSuratDisposisiTindakan;
    }

    public String getTindakan() {
        return tindakan;
    }

    public void setTindakan(String tindakan) {
        this.tindakan = tindakan;
    }

    public String getTindakanTimestamp() {
        return tindakanTimestamp;
    }

    public void setTindakanTimestamp(String tindakanTimestamp) {
        this.tindakanTimestamp = tindakanTimestamp;
    }

}