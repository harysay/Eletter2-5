package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

public class AksiItemKhusus {

	@SerializedName("aksi_id")
	private int aksiId;

	@SerializedName("label")
	private String label;

	public int getAksiId(){
		return aksiId;
	}

	public String getLabel(){
		return label;
	}
}