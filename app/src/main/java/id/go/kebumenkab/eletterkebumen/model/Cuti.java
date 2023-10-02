package id.go.kebumenkab.eletterkebumen.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cuti implements Serializable {

	@SerializedName("cuti_id")
	private String cutiId;

	@SerializedName("jeniscuti_id")
	private String jeniscutiId;

	@SerializedName("alamat_cuti")
	private String alamatCuti;

	@SerializedName("mulai_cuti")
	private String mulaiCuti;

	@SerializedName("jenis")
	private String jenis;

	@SerializedName("selesai_cuti")
	private String selesaiCuti;

	@SerializedName("alasan")
	private String alasan;

	@SerializedName("lama_hari")
	private int lamaHari;

	@SerializedName("lampiran")
	private String lampiran;

	public void setCutiId(String cutiId){
		this.cutiId = cutiId;
	}

	public String getCutiId(){
		return cutiId;
	}

	public void setJeniscutiId(String jeniscutiId){
		this.jeniscutiId = jeniscutiId;
	}

	public String getJeniscutiId(){
		return jeniscutiId;
	}

	public void setAlamatCuti(String alamatCuti){
		this.alamatCuti = alamatCuti;
	}

	public String getAlamatCuti(){
		return alamatCuti;
	}

	public void setMulaiCuti(String mulaiCuti){
		this.mulaiCuti = mulaiCuti;
	}

	public String getMulaiCuti(){
		return mulaiCuti;
	}

	public void setJenis(String jenis){
		this.jenis = jenis;
	}

	public String getJenis(){
		return jenis;
	}

	public void setSelesaiCuti(String selesaiCuti){
		this.selesaiCuti = selesaiCuti;
	}

	public String getSelesaiCuti(){
		return selesaiCuti;
	}

	public void setAlasan(String alasan){
		this.alasan = alasan;
	}

	public String getAlasan(){
		return alasan;
	}

	public void setLamaHari(int lamaHari){
		this.lamaHari = lamaHari;
	}

	public int getLamaHari(){
		return lamaHari;
	}

	public void setLampiran(String lampiran){
		this.lampiran = lampiran;
	}

	public String getLampiran(){
		return lampiran;
	}
}