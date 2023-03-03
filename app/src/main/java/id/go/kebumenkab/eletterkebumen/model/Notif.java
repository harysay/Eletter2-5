package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notif {

    @SerializedName("konsep")
    @Expose
    private int konsep;
    @SerializedName("masuk")
    @Expose
    private int masuk;

    public int getKonsep() {
        return konsep;
    }

    public void setKonsep(int konsep) {
        this.konsep = konsep;
    }

    public int getMasuk() {
        return masuk;
    }

    public void setMasuk(int masuk) {
        this.masuk = masuk;
    }

}