package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ResponDetailPermintaanTandatangan {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private PermintaanTandatangan data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PermintaanTandatangan getData() {
        return data;
    }

    public void setData(PermintaanTandatangan data) {
        this.data = data;
    }

}