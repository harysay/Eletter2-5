package id.go.kebumenkab.eletterkebumen.network.pns;

import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.model.ResponDetailPermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.model.ResponMessage;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResponsePenerimaDispo;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDiajukan;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDikoreksi;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDisetujui;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDitindaklanjuti;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipLain;
import id.go.kebumenkab.eletterkebumen.model.ResultHistoriArsip;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsep;
import id.go.kebumenkab.eletterkebumen.model.ResultBawahan;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.model.ResultHistory;
import id.go.kebumenkab.eletterkebumen.model.ResultLogin;
import id.go.kebumenkab.eletterkebumen.model.ResultNotif;
import id.go.kebumenkab.eletterkebumen.model.ResultPemberitahuan;
import id.go.kebumenkab.eletterkebumen.model.ResultPermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.model.ResultSuratMasuk;
import id.go.kebumenkab.eletterkebumen.model.ResultTindakan;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("index.php/authandroid/login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("index.php/authandroid/login")
    Call<List<ResultLogin>> login(@Field("username") String username,
                                 @Field("password") String password);

    @FormUrlEncoded
    @POST("index.php/auth/login")
    Call<ResponseBody> loginOperator(@Field("username") String username,
                                  @Field("password") String password);

    // Request untuk kirim token
    @FormUrlEncoded
    @POST("index.php/androiddevice/add")
    Call<ResponseBody> sendTokenFirebase(@Header("Authorization") String authorization,
                                    @Field("token") String token, @Field("device") String device,
                                         @Field("versi") String versiApp);


    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/konsep")
    Call<ResultKonsep> getKonsep(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @Headers("Content-Type: application/json")
    @GET("index.php/suratinternalget/masuk")
    Call<ResultSuratMasuk> getMasuk(@Header("Authorization") String authorization);

    // Untuk menampilkan pemberitahuan yang belum diberi aksi
    @GET("index.php/suratinternalget/pemberitahuan")
    Call<ResultPemberitahuan> getPemberitahuan(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/diajukan")
    Call<ResultKonsep> getArsipDiajukan(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/dikoreksi")
    Call<ResultKonsep> getArsipDikoreksi(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/disetujui")
    Call<ResultKonsep> getArsipDisetujui(@Header("Authorization") String authorization);


    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/didisposisi")
    Call<ResultSuratMasuk> getArsipDisposisi(@Header("Authorization") String authorization);


    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/dikerjakan")
    Call<ResultSuratMasuk> getArsipDikerjakan(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/ditindaklanjuti")
    Call<ResultSuratMasuk> getArsipDitindaklanjuti(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/ditelaah")
    Call<ResultSuratMasuk> getArsipDitelaah(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/arsiplain")
    Call<ResultSuratMasuk> getArsipLainnya(@Header("Authorization") String authorization);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/arsip/tembusan")
    Call<ResultSuratMasuk> getArsipTembusan(@Header("Authorization") String authorization);


    // Untuk menampilkan surat yang didisposisi
    @GET("index.php/arsip/disposisi/index/{page}")
    Call<ResultHistoriArsip> getArsipDisposisiLengkap(@Header("Authorization") String authorization,
                                                      @Path("page") String page);

    // Untuk menampilkan surat yang didisposisi versi 2
    @GET("index.php/arsip/disposisi/index2/{page}")
    Call<ResultHistoriArsip> getArsipDisposisi(@Header("Authorization") String authorization,
                                                      @Path("page") String page);

    // Untuk menampilkan arsip surat yang diajukan
    @GET("index.php/arsip/diajukan/index/{page}")
    Call<ResultArsipDiajukan> getArsipDiajukanLengkap(@Header("Authorization") String authorization,
                                                      @Path("page") String page);

    // Untuk menampilkan surat yang didisposisi versi 2
    @GET("index.php/arsip/diajukan/index2/{page}")
    Call<ResultArsipDiajukan> getArsipDiajukan(@Header("Authorization") String authorization,
                                               @Path("page") String page);

    // Untuk menampilkan arsip surat yang dikoreksi
    @GET("index.php/arsip/dikoreksi/index/{page}")
    Call<ResultArsipDikoreksi> getArsipDikoreksiLengkap(@Header("Authorization") String authorization,
                                                        @Path("page") String page);

    // Untuk menampilkan surat yang telah disetujui
    @GET("index.php/arsip/disetujui/index/{page}")
    Call<ResultArsipDisetujui> getArsipDisetujuiLengkap(@Header("Authorization") String authorization,
                                                        @Path("page") String page);

    // Untuk menampilkan arsip surat yang diajukan
    @GET("index.php/arsip/ditelaah/index/{page}")
    Call<ResultArsipDiajukan> getArsipDitelaahLengkap(@Header("Authorization") String authorization,
                                                      @Path("page") String page);

    // Untuk menampilkan surat yang telah disetujui
    @GET("index.php/arsip/arsiplain/index/{page}")
    Call<ResultArsipLain> getArsipLainLengkap(@Header("Authorization") String authorization,
                                              @Path("page") String page);

    // Untuk menampilkan surat yang telah disetujui
    @GET("index.php/arsip/ditindaklanjuti/index/{page}")
    Call<ResultArsipDitindaklanjuti> getArsipDitindaklanjtiLengkap(@Header("Authorization") String authorization,
                                                                   @Path("page") String page);


    // Untuk menampilkan permintaan tanda  tangan
    @GET("index.php/permohonan_ttd")
    Call<ResultPermintaanTandatangan> getPermintaanTandatangan(@Header("Authorization") String authorization);

    // Untuk menampilkan detail permintaan tanda tangan
    @GET("index.php/permohonan_ttd/detail/{id}")
    Call<ResponDetailPermintaanTandatangan> getDetailPermintaanTandatangan(@Header("Authorization") String authorization,
                                                                           @Path("id") String id);

    // Untuk memberi tanda tangan
    @FormUrlEncoded
    @POST("index.php/permohonan_ttd/tandatangan/{id_surat}")
    Call<ResponStandar> sendTandatangan(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Field("passphrase") String passphrase);

    // Request detail surat
    @GET("index.php/suratinternalget/detail/{id_surat}/{id_histori}")
    Call<ResultDetail> getDetailKonsep(
            @Header("Authorization") String authorization,
            @Path("id_surat") String id_surat,
            @Path("id_histori") String id_histori);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/keluarinternalhistory/{id_surat}")
    Call<ResultHistory> getHistori(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat);

    // Untuk menampilkan surat yang belum diberi aksi
    @GET("index.php/suratinternalget/masukhistory/{id_surat}/{id_unit_kerja}/masukinternal")
    Call<ResultHistory> getHistoriSuratMasuk(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                             @Path("id_unit_kerja") String id_unit_kerja);

    // Untuk ajukan surat
    // http://10.28.11.18/eletter2/api/suratinternalprocess/ajukan/36/149

    @GET("index.php/suratinternalprocess/read_notif/{id_pemberitahuan}")
    Call<ResponStandar> sendDibaca(@Header("Authorization") String authorization,
                                   @Path("id_pemberitahuan") String id_pemberitahuan);


    @GET("index.php/suratinternalprocess/ajukan/{id_surat}/{id_histori}")
    Call<ResponStandar> sendAjukanOld(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Path("id_histori") String id_histori);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/ajukan/{id_surat}/{id_histori}")
    Call<ResponStandar> sendAjukan(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Path("id_histori") String id_histori,
                                   @Field("pesan") String pesan);

    // Untuk koreksi surat
    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/koreksi/{id_surat}/{id_histori}")
    Call<ResponStandar> sendKoreksi(@Header("Authorization") String authorization,
                                 @Field("pesan") String pesan,
                                 @Path("id_surat") String id_surat,
                                 @Path("id_histori") String id_histori);

    // Untuk disposisi surat
    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/disposisi/{id_surat}/{id_history}")
    Call<ResponStandar> sendDisposisi (@Header("Authorization") String authorization,
                                       @Field("pesan") String pesan,
                                       @Field("bawahan[]") ArrayList<String>  bawahan,
                                       @Field("tindakan[]") ArrayList<String> tindakan,
                                       @Path("id_surat") String id_surat,
                                       @Path("id_history") String id_history);

    // Untuk disposisi surat
    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/order_ajudan/")
    Call<ResponStandar> sendOrderAjudan (@Header("Authorization") String authorization,
                                       @Field("history[]") ArrayList<String>  histori);


    // Untuk disposisi surat
    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/disposisi/{id_surat}/{id_history}/{nip_bawahan}")
    Call<ResponMessage> sendDisposisiBackup (@Header("Authorization") String authorization,
                                       @Field("pesan") String pesan,
                                       @Path("id_surat") String id_surat,
                                       @Path("id_histori") String id_histori);
    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/setujui/{id_surat}/{id_histori}")
    Call<ResponStandar> sendSetuju(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Path("id_histori") String id_histori,
                                   @Field("passphrase") String pesan);

    @GET("index.php/suratinternalprocess/kirim/{id_surat}/{id_histori}")
    Call<ResponStandar> sendKirim(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                  @Path("id_histori") String id_histori);
    /** **/



    @GET("index.php/suratinternalprocess/arsipkan/{id_surat}/{id_histori}")
    Call<ResponStandar> sendArsipTembusan(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Path("id_histori") String id_histori);

    @GET("index.php/suratinternalprocess/arsiplain/{id_surat}/{id_histori}")
    Call<ResponStandar> sendArsipkan(@Header("Authorization") String authorization,
                                     @Path("id_surat") String id_surat,
                                     @Path("id_histori") String id_histori);

    @GET("index.php/arsip/disposisi/penerima_disposisi/{id_histori}")
    Call<ResponsePenerimaDispo> getPenerimaDispo(@Header("Authorization") String authorization,
                                                 @Path("id_histori") String id_histori);

    @GET("index.php/arsip/diajukan/penerima_ajuan/{id_histori}")
    Call<ResponsePenerimaDispo> getPenerimaAjuan(@Header("Authorization") String authorization,
                                                 @Path("id_histori") String id_histori);

    /** TELAAH POST **/

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/telaah/{id_surat}/{id_histori}")
    Call<ResponStandar> sendTelaah(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Path("id_histori") String id_histori,
                                   @Field("pesan") String pesan);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/telaah_kasubbagtu/{id_surat}/{id_history}/{asisten}")
    Call<ResponStandar> sendTelaahKasubbagtu(@Header("Authorization") String authorization,
                                     @Path("id_surat") String id_surat,
                                     @Path("id_history") String id_histori,
                                             @Path("asisten") String asisten,
                                             @Field("pesan") String pesan);
    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/telaah_asisten/{id_surat}/{id_history}/")
    Call<ResponStandar> sendTelaahAsisten(@Header("Authorization") String authorization,
                                             @Path("id_surat") String id_surat,
                                             @Path("id_history") String id_histori,
                                          @Field("pesan") String pesan);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/telaah_sekda/{id_surat}/{id_history}/{asisten}")
    Call<ResponStandar> sendTelaahSekda(@Header("Authorization") String authorization,
                                           @Path("id_surat") String id_surat,
                                           @Path("id_history") String id_histori,
                                        @Field("pesan") String pesan);


    /** TELAAH OLD **/
    @GET("index.php/suratinternalprocess/telaah_kasubbagtu/{id_surat}/{id_history}/{asisten}")
    Call<ResponStandar> sendTelaahKasubbagtuOld(@Header("Authorization") String authorization,
                                             @Path("id_surat") String id_surat,
                                             @Path("id_history") String id_histori,
                                             @Path("asisten") String asisten);


    @GET("index.php/suratinternalprocess/telaah/{id_surat}/{id_histori}")
    Call<ResponStandar> sendTelaahOld(@Header("Authorization") String authorization,
                                      @Path("id_surat") String id_surat,
                                      @Path("id_histori") String id_histori);

    @GET("index.php/suratinternalprocess/telaah_asisten/{id_surat}/{id_history}/")
    Call<ResponStandar> sendTelaahAsistenOld(@Header("Authorization") String authorization,
                                         @Path("id_surat") String id_surat,
                                         @Path("id_history") String id_histori);

    @GET("index.php/suratinternalprocess/telaah_sekda/{id_surat}/{id_history}/{asisten}")
    Call<ResponStandar> sendTelaahSekdaOld(@Header("Authorization") String authorization,
                                         @Path("id_surat") String id_surat,
                                         @Path("id_history") String id_histori);


    @GET("index.php/pegawai/bawahan")
    Call<ResultBawahan> getBawahan(@Header("Authorization") String authorization);

    @GET("index.php/suratinternalget/tindakan")
    Call<ResultTindakan> getTindakan(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/disposisi/{id_surat}/{id_histori}/{nip_bawahan}")
    Call<ResponStandar> sendDispo(@Header("Authorization") String authorization,
                                  @Path("id_surat") String id_surat,
                                  @Path("id_histori") String id_histori,
                                  @Path("nip_bawahan") String nip_bawahan,
                                  @Field("pesan") String pesan);


    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/tarik_disposisi/{id_surat}/{id_histori}")
    Call<ResponStandar> tarikDispo(@Header("Authorization") String authorization,
                                  @Path("id_surat") String id_surat,
                                  @Path("id_histori") String id_histori,
                                  @Field("pesan") String pesan);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/tarik/{id_surat}")
    Call<ResponStandar> tarikSurat(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Field("pesan") String pesan);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/tarik_disposisi_android/{id_surat}/{id_pemberitahuan}")
    Call<ResponStandar> tarikSuratDispoPemberitahuan(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                   @Path("id_pemberitahuan") String id_pemberitahuan,
                                   @Field("pesan") String pesan);

    @FormUrlEncoded
    @POST("index.php/suratinternalprocess/kembalikan_disposisi/{id_surat}/{id_history}")
    Call<ResponStandar> sendKembalikan(@Header("Authorization") String authorization,
                                   @Path("id_surat") String id_surat,
                                       @Path("id_history") String id_history,
                                   @Field("pesan") String pesan);

    @GET("index.php/suratinternalprocess/kerjakan/{id_surat}/{id_histori}")
    Call<ResponStandar> sendKerjakan(@Header("Authorization") String authorization,
                                  @Path("id_surat") String id_surat,
                                  @Path("id_histori") String id_histori);

    @GET("index.php/suratinternalprocess/tindaklanjuti/{id_surat}/{id_histori}")
    Call<ResponStandar> sendTindakLanjuti(@Header("Authorization") String authorization,
                                     @Path("id_surat") String id_surat,
                                     @Path("id_histori") String id_histori);

    @GET("index.php/suratinternalprocess/tandai/{id_surat}/{status}")
    Call<ResponStandar> sendTandai(@Header("Authorization") String authorization,
                                          @Path("id_surat") String id_surat,
                                          @Path("status") String status); // 1 atau 0

    @GET("index.php/suratinternalget/count_inbox/{nip}")
    Call<ResultNotif> getNotifikasi(@Header("Authorization") String authorization,
                                    @Path("nip") String nip); //

}
