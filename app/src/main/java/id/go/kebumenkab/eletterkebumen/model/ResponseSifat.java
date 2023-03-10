package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseSifat{

	@SerializedName("sifat")
	private List<String> sifat;

	public List<String> getSifat(){
		return sifat;
	}
}