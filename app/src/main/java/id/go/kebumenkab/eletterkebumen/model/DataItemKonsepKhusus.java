package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataItemKonsepKhusus implements Serializable {

	@SerializedName("app_name")
	private String appName;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("title2")
	private String title2;

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	public String getAppName(){
		return appName;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getTitle2(){
		return title2;
	}

	public String getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}
}