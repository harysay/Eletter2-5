package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailItemKhusus implements Serializable {

	@SerializedName("label")
	private String label;

	@SerializedName("value")
	private String value;

	public String getLabel(){
		return label;
	}

	public String getValue(){
		return value;
	}
}