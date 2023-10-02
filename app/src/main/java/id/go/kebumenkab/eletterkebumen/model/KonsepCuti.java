package id.go.kebumenkab.eletterkebumen.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class KonsepCuti implements Serializable {

	@SerializedName("data")
	private List<DataItemCuti> data;

	@SerializedName("count")
	private int count;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<DataItemCuti> data){
		this.data = data;
	}

	public List<DataItemCuti> getData(){
		return data;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}
}