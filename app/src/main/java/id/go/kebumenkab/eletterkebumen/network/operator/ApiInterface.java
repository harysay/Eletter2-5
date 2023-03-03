package id.go.kebumenkab.eletterkebumen.network.operator;

import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResponseSuratMasukOperator;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsepDesa;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiInterface {

    // Request untuk masuk aplikasi
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);


    // Untuk menampilkan surat masuk
    @GET("index.php/suratinternalget/masuk_operator")
    Call<ResponseSuratMasukOperator> getSuratMasuk(@Header("Authorization") String authorization);


    // Untuk menampilkan surat yang belum diberi aksi
    @GET
    Call<ResponStandar> telaahKonsep(@Header("Authorization") String authorization,
                                     @Url String path);

}
