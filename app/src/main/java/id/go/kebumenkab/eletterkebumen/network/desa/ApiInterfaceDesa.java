package id.go.kebumenkab.eletterkebumen.network.desa;

import java.util.List;

import id.go.kebumenkab.eletterkebumen.model.ResponData;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResponUrl;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDikoreksi;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDisetujui;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.model.ResultHistory;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsep;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsepDesa;
import id.go.kebumenkab.eletterkebumen.model.ResultLogin;
import id.go.kebumenkab.eletterkebumen.model.ResultLoginDesa;
import id.go.kebumenkab.eletterkebumen.model.ResultSuratDitandatangani;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterfaceDesa {

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Header("Authorization") String auth,
                                @Field("username") String username,
                                @Field("password") String password);

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("update_user")
    Call<ResponseBody> updateUser(@Header("Authorization") String auth,
                                  @Field("username") String username,
                                  @Field("password_lama") String passwordLama,
                                  @Field("password_baru") String passwordBaru,
                                  @Field("password_baru_2") String passwordBaruDua);

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("updateuser")
    Call<List<ResponStandar>> updateuser(
                        @Header("Authorization") String auth,
                        @Field("username") String username,
                        @Field("password") String password);

    // Request untuk kirim token
    @FormUrlEncoded
    @POST("index.php/androiddevice/add")
    Call<ResponseBody> sendTokenFirebase(@Header("Authorization") String authorization,
                                         @Field("token") String token, @Field("device") String device,
                                         @Field("versi") String versiApp);


    // Untuk menampilkan surat yang belum diberi aksi
    @GET("surat_ajuan/{id_perangkat}")
    Call<ResultKonsepDesa> getKonsep(@Header("Authorization") String authorization,
                                     @Path("id_perangkat") String id_perangkat);

    @GET("preview_surat_ajuan/{id_surat}")
    Call<ResponData> getPreviewKonsep(@Header("Authorization") String authorization,
                               @Path("id_surat") String id_surat);

    @GET("surat_ttd/{id_perangkat}")
    Call<ResultSuratDitandatangani> getSurat(@Header("Authorization") String authorization,
                                             @Path("id_perangkat") String id_perangkat);

    @GET("preview_surat_ttd/{id_surat}")
    Call<ResponUrl> getPreviewDitandatangani(@Header("Authorization") String authorization,
                                             @Path("id_surat") String id_surat);


    @FormUrlEncoded
    @POST("ttd")
    Call<ResponStandar> sendSetuju(@Header("Authorization") String authorization,
                                   @Field("id_surat") String id_surat,
                                   @Field("id_perangkat") String id_perangkat,
                                   @Field("nik") String nik,
                                   @Field("passphrase") String passphrase);

    @FormUrlEncoded
    @POST("koreksi")
    Call<ResponStandar> sendKoreksi(@Header("Authorization") String authorization,
                                   @Field("id_surat") String id_surat,
                                   @Field("koreksi") String komentar);


}
