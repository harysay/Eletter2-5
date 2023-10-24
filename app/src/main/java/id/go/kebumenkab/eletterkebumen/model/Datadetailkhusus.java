package id.go.kebumenkab.eletterkebumen.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Datadetailkhusus{

	@SerializedName("app_name")
	private String appName;

	@SerializedName("preview_url")
	private String previewUrl;

	@SerializedName("preview_lampiran")
	private String previewLampiran;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("title2")
	private String title2;

	@SerializedName("id")
	private String id;

	@SerializedName("detail")
	private List<DetailItemKhusus> detail;

	@SerializedName("title")
	private String title;

	@SerializedName("aksi")
	private List<AksiItemKhusus> aksi;

	public String getAppName(){
		return appName;
	}

	public String getPreviewUrl(){
		return previewUrl;
	}

	public String getPreviewLampiran(){
		return previewLampiran;
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

	public List<DetailItemKhusus> getDetail(){
		return detail;
	}

	public String getTitle(){
		return title;
	}

	public List<AksiItemKhusus> getAksi(){
		return aksi;
	}
}