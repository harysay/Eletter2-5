package id.go.kebumenkab.eletterkebumen.model;


import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ResponMessage {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("pesan")
    @Expose
    private String pesan;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
