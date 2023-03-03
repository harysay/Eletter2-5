package id.go.kebumenkab.eletterkebumen.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Histori {

    @SerializedName("alur")
    @Expose
    private String alur;
    @SerializedName("penerima")
    @Expose
    private String penerima;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("pesan")
    @Expose
    private String pesan;
    @SerializedName("pengirim")
    @Expose
    private String pengirim;

    public String getAlur() {
        return alur;
    }

    public void setAlur(String alur) {
        this.alur = alur;
    }

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

}
