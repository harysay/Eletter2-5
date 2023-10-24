package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

public class KonsepKhususDetail{

	@SerializedName("message")
	private String message;

	@SerializedName("data")
	private Datadetailkhusus datadetailkhusus;

	@SerializedName("status")
	private String status;

	public String getMessage(){
		return message;
	}

	public Datadetailkhusus getDatadetailkhusus(){
		return datadetailkhusus;
	}

	public String getStatus(){
		return status;
	}
}