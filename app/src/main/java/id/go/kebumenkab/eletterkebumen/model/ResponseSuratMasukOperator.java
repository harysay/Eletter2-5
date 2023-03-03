package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseSuratMasukOperator{

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("status")
	private String status;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}