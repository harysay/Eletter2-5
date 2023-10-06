package id.go.kebumenkab.eletterkebumen.network.pns;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import id.go.kebumenkab.eletterkebumen.network.ConnectivityInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    public static String DOMAIN = "https://eletter.kebumenkab.go.id";
//     public static String DOMAIN = "http://10.28.11.26/eletter2"; //jika di lapotope lukman
//    public static String DOMAIN = "http://103.147.218.90:8182/eletter";
//    public static String DOMAINCUTI = "http://103.147.218.90:8182"; //jika di development
//    public static final String ELETTER_CUTI = DOMAINCUTI + "/eletter/cutionline/index.php/surat/preview/"; //jika didevelopment

    public static final String ELETTER_URL = DOMAIN + "/api/";

    public static final String ELETTER_PDF = DOMAIN + "/index.php/web/surat/view/index/";
    public static final String ELETTER_PDF_KONSEP = DOMAIN + "/index.php/viewer/konsep/";
    public static final String ELETTER_KOREKSI = DOMAIN + "/index.php/koreksi/koreksi/index/";


    public static final String ELETTER_CUTI = DOMAIN + "/cutionline/index.php/surat/preview/";
    public static final String ELETTER_PDFJS = DOMAIN + "/index.php/viewer/index/";
    public static final String ELETTER_PDFJS_LAMPIRAN = DOMAIN + "/index.php/viewer/lampiran/";

    public static final String ELETTER_PDF_JADI = DOMAIN + "/suratpdf/";

    public static void setDomain(String domain){
        ApiClient.DOMAIN = domain;
    }

    public static String getDomain(){
        return DOMAIN;
    }

    private static Retrofit retrofit = null;


    
    public static Retrofit getClient() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

//            retrofit = new Retrofit.Builder()
//                    .baseUrl(ELETTER_URL)
//                    .client(okHttpClient)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ELETTER_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getDomainCuti() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit loginRequest(Context context) {
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
                    .baseUrl(ELETTER_URL)// "http://10.32.10.22/eletter2/api/"
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // https://stackoverflow.com/questions/36960627/android-retrofit-design-patterns/36963162#36963162
    // https://stackoverflow.com/questions/39918814/use-jsonreader-setlenienttrue-to-accept-malformed-json-at-line-1-column-1-path
}

