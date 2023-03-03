package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultHistoriArsip {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("total_page")
    @Expose
    private int totalPage;
    @SerializedName("data")
    @Expose
    private List<ArsipDisposisiData> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<ArsipDisposisiData> getData() {
        return data;
    }

    public void setData(List<ArsipDisposisiData> data) {
        this.data = data;
    }

}