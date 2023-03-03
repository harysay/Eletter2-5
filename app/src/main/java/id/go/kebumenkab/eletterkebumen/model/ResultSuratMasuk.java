package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultSuratMasuk {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<SuratMasuk> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SuratMasuk> getData() {
        return data;
    }

    public void setData(List<SuratMasuk> data) {
        this.data = data;
    }

}