package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataItemCuti implements Serializable {

	@SerializedName("cuti_id")
	private String cutiId;

	@SerializedName("pemohon")
	private Pemohon pemohon;

	@SerializedName("cuti")
	private Cuti cuti;

	@SerializedName("history_id")
	private String historyId;

	public void setCutiId(String cutiId){
		this.cutiId = cutiId;
	}

	public String getCutiId(){
		return cutiId;
	}

	public void setPemohon(Pemohon pemohon){
		this.pemohon = pemohon;
	}

	public Pemohon getPemohon(){
		return pemohon;
	}

	public void setCuti(Cuti cuti){
		this.cuti = cuti;
	}

	public Cuti getCuti(){
		return cuti;
	}

	public void setHistoryId(String historyId){
		this.historyId = historyId;
	}

	public String getHistoryId(){
		return historyId;
	}
}