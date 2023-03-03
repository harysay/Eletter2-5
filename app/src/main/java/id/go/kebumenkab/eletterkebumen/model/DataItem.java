package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataItem implements Serializable {

	@SerializedName("pengirim")
	private String pengirim;

	@SerializedName("nama_surat")
	private String namaSurat;

	@SerializedName("id_surat_internal")
	private String idSuratInternal;

	@SerializedName("deskripsi_surat")
	private String deskripsiSurat;

	@SerializedName("nomor")
	private String nomor;

	@SerializedName("timestamp")
	private String timestamp;

	public String getTelaah() {
		return telaah;
	}

	public void setTelaah(String telaah) {
		this.telaah = telaah;
	}

	@SerializedName("telaah")
	private String telaah;


	public void setPengirim(String pengirim){
		this.pengirim = pengirim;
	}

	public String getPengirim(){
		return pengirim;
	}

	public void setNamaSurat(String namaSurat){
		this.namaSurat = namaSurat;
	}

	public String getNamaSurat(){
		return namaSurat;
	}

	public void setIdSuratInternal(String idSuratInternal){
		this.idSuratInternal = idSuratInternal;
	}

	public String getIdSuratInternal(){
		return idSuratInternal;
	}

	public void setDeskripsiSurat(String deskripsiSurat){
		this.deskripsiSurat = deskripsiSurat;
	}

	public String getDeskripsiSurat(){
		return deskripsiSurat;
	}

	public void setNomor(String nomor){
		this.nomor = nomor;
	}

	public String getNomor(){
		return nomor;
	}

	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}

	public String getTimestamp(){
		return timestamp;
	}


}