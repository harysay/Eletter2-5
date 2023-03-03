package id.go.kebumenkab.eletterkebumen.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResponsePenerimaDispo implements Serializable {

	@SerializedName("data")
	private List<DataPenerimaDispo> data;

	@SerializedName("status")
	private String status;

	public void setData(List<DataPenerimaDispo> data){
		this.data = data;
	}

	public List<DataPenerimaDispo> getData(){
		return data;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}
