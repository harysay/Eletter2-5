package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class KonsepKhusus{

	@SerializedName("data")
	private List<DataItemKonsepKhusus> data;

	@SerializedName("count")
	private int count;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public List<DataItemKonsepKhusus> getData(){
		return data;
	}

	public int getCount(){
		return count;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}