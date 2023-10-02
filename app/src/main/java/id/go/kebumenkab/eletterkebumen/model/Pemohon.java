package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pemohon implements Serializable {

	@SerializedName("id_jabatan")
	private String idJabatan;

	@SerializedName("unit_kerja")
	private String unitKerja;

	@SerializedName("telp")
	private String telp;

	@SerializedName("nama")
	private String nama;

	@SerializedName("nip")
	private String nip;

	@SerializedName("masa_kerja")
	private String masaKerja;

	@SerializedName("jabatan")
	private String jabatan;

	@SerializedName("id_unit_kerja")
	private String idUnitKerja;

	public void setIdJabatan(String idJabatan){
		this.idJabatan = idJabatan;
	}

	public String getIdJabatan(){
		return idJabatan;
	}

	public void setUnitKerja(String unitKerja){
		this.unitKerja = unitKerja;
	}

	public String getUnitKerja(){
		return unitKerja;
	}

	public void setTelp(String telp){
		this.telp = telp;
	}

	public String getTelp(){
		return telp;
	}

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setNip(String nip){
		this.nip = nip;
	}

	public String getNip(){
		return nip;
	}

	public void setMasaKerja(String masaKerja){
		this.masaKerja = masaKerja;
	}

	public String getMasaKerja(){
		return masaKerja;
	}

	public void setJabatan(String jabatan){
		this.jabatan = jabatan;
	}

	public String getJabatan(){
		return jabatan;
	}

	public void setIdUnitKerja(String idUnitKerja){
		this.idUnitKerja = idUnitKerja;
	}

	public String getIdUnitKerja(){
		return idUnitKerja;
	}
}