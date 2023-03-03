package id.go.kebumenkab.eletterkebumen.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataPenerimaDispo implements Serializable {

	@SerializedName("unit_kerja")
	private String unitKerja;

	@SerializedName("nip")
	private String nip;

	@SerializedName("nama")
	private String nama;

	@SerializedName("jabatan")
	private String jabatan;

	@SerializedName("proses")
	private String proses;

	public void setUnitKerja(String unitKerja){
		this.unitKerja = unitKerja;
	}

	public String getUnitKerja(){
		return unitKerja;
	}

	public void setNip(String nip){
		this.nip = nip;
	}

	public String getNip(){
		return nip;
	}

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setJabatan(String jabatan){
		this.jabatan = jabatan;
	}

	public String getJabatan(){
		return jabatan;
	}

	public void setProses(String proses){
		this.proses = proses;
	}

	public String getProses(){
		return proses;
	}
}
