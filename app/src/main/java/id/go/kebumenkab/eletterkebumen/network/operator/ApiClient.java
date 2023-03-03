package id.go.kebumenkab.eletterkebumen.network.operator;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.network.ConnectivityInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    //public static String DOMAIN = "http://10.28.7.3/eletter2";
    public static String DOMAIN = "https://eletter.kebumenkab.go.id";
    public static final String ELETTER_PDFJS = DOMAIN + "/index.php/viewer/index/";
    public static final String ELETTER_PDF = DOMAIN + "/index.php/web/surat/view/index/";
    public static String MY_URL = DOMAIN + "/api/";
    public static String URL_TELAAH = DOMAIN + "/api/index.php";

    public static Retrofit loginRequest(Context context) {
        Retrofit retrofit = null;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new ConnectivityInterceptor(context))
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(MY_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // https://stackoverflow.com/questions/36960627/android-retrofit-design-patterns/36963162#36963162
    // https://stackoverflow.com/questions/39918814/use-jsonreader-setlenienttrue-to-accept-malformed-json-at-line-1-column-1-path
}

