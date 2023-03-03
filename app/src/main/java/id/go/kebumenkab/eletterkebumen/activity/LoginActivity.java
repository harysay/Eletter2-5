package id.go.kebumenkab.eletterkebumen.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.desa.GantiPassword;
import id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah;
import id.go.kebumenkab.eletterkebumen.activity.lurah.LoginLurahActivity;
import id.go.kebumenkab.eletterkebumen.activity.operator.DashboardOperator;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.intro.WelcomeActivity;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.AppController;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PAGE;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PESAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_STATUS_SUKSES;

public class LoginActivity extends AppCompatActivity {

    Button buttonLogin;
    EditText editUsername, editPassword;
    private TextView version, message;
    ProgressBar progressBar;

    private ConnectivityManager conMgr;

    private static final String TAG     = LoginActivity.class.getSimpleName();

    private Context mContext;
    private BroadcastReceiver broadcastReceiver;
    private String newtoken="";

    private PrefManager prefManager;
    private NotifikasiDialog notifikasiDialog;

    private Logger logger;
    private String versionName="";
    private int versionCode= 0;

    String[] arrayTipe = {"3", "4", "5", "6", "7", "8", "9"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        // reset angka
        int badgeCount = 0;
        ShortcutBadger.applyCount(getApplicationContext(), badgeCount); //for 1.1.4+

        // Pengecekan session dilakukan sebelum memanggil setContentView()
        prefManager = new PrefManager(getApplicationContext());
        mContext = getApplicationContext();
        logger      = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), LoginActivity.this);

        logger.d(Tag.SESSION_NAMA, prefManager.getSessionNama());
        logger.d("Sudah masuk", String.valueOf(prefManager.isLoggedIn()));

        /**  Seting dan inisiasi tampilan dan Firebase **/
        setup();


            // Bila bukan pertama kali dijalankan

            if (prefManager.isLoggedIn()) {
                // Bila sudah berhasil login maka tutup halaman Login dan Pindah ke halaman utama
                Intent intent= new Intent(getApplicationContext(), Dashboard.class);
                logger.d("IDPerangkat", String.valueOf(prefManager.getIdPerangkat()));
                logger.d("JabatanLurah", prefManager.getJabatan());

                if(prefManager.getStatusJabatan().equals(Tag.TAG_AJUDAN)){
                    // jika status jabatan adalah ajudan
                    intent= new Intent(getApplicationContext(), id.go.kebumenkab.eletterkebumen.ajudan.Dashboard.class);
                }else {
                    if(prefManager.getIdPerangkat().length()>0){

                        // kepala desa atau lurah
                        logger.d("URL", prefManager.getUrlDesa());
                        intent= new Intent(getApplicationContext(), id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);

                        if(prefManager.getJabatan().toLowerCase().contains("lurah")){
                            intent= new Intent(getApplicationContext(), DashboardLurah.class);
                        }
                    }else{
                        // operator akses
                        if(ArrayUtils.contains(arrayTipe, prefManager.getType()))
                        {
                            // pindah ke Dashboard Operator
                            intent= new Intent(getApplicationContext(), DashboardOperator.class);
                            startActivity(intent);
                        }
                    }
                }

                intent.putExtra(TAG_PAGE, 0);
                startActivity(intent);
                finish();

            }else {

                editUsername.setText(prefManager.getUsername());
                editUsername.setSelection(editUsername.getText().length());
                editPassword.setText("");
            }
        }



    /**  Setting view **/
    private void setup(){
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;//Version Name
            versionCode = pInfo.versionCode;//Version Code
            version         = (TextView)findViewById(R.id.version);
            version.setText("Versi :"+ versionName +" >> Selengkapnya");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        buttonLogin     = (Button) findViewById(R.id.buttonLogin);
        editUsername    = (EditText) findViewById(R.id.editUsername);
        editPassword    = (EditText) findViewById(R.id.editPassword);
        progressBar     = (ProgressBar)findViewById(R.id.progressBar);
        message         = (TextView) findViewById(R.id.message);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(prefManager.getTokenFirebase() != null){
            newtoken = prefManager.getTokenFirebase();
        }else{
            /**  Awal inisiasi Firebase **/
            FirebaseApp.initializeApp(this);

//            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,
//                    new OnSuccessListener<InstanceIdResult>() {
//                        @Override
//                        public void onSuccess(InstanceIdResult instanceIdResult) {
//                            newtoken= instanceIdResult.getToken();
//                            prefManager.storeTokenFirebase(instanceIdResult.getToken());
//
//                            logger.d("Token Firebase",newtoken);
//                           // Toast.makeText(getApplicationContext(), "Token :"+ newtoken, Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                newtoken = prefManager.getTokenFirebase();
//            }
//        };
//
//
//        registerReceiver(broadcastReceiver,new IntentFilter("mytokenbroadcast"));


            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());

                                // Get new FCM registration token
                                if(task.getResult()!=null){
                                    String token = task.getResult();

                                    prefManager.storeTokenFirebase(token);

                                    // Log and toast
                                    String msg = getString(R.string.msg_token_fmt) +" "+ token;
                                    logger.d(TAG, msg);
                                    // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }


                            }else{
                                Log.e("Installations", "Unable to get Installation auth token");
                            }


                        }
                    });


            /**  Akhir inisiasi Firebase **/

        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cekUsernamePassword()){
                    show_dialog_setuju();
                }

            }
        });
    }


    /** Fungsi untuk menampilkan pesan error **/
    private boolean cekUsernamePassword(){
        /**  Tampilkan error jika edit username masih kosong **/
        if (editUsername.getText().toString().trim().equalsIgnoreCase("")){
            editUsername.requestFocus();
            editUsername.setError(getString(R.string.message_username_diisi));
            return false;
        }
        /**  Tampilkan error jika edit password masih kosong **/
        else if (editPassword.getText().toString().trim().equalsIgnoreCase("")){
            editPassword.requestFocus();
            editPassword.setError(getString(R.string.message_password_diisi));
            return false;
        }
        return true;
    }

    public void show_dialog_lupa(View view){
        notifikasiDialog.showDialogError(7, "");
    }

    /** Menampilkan progress dialog **/
    private void showDialog() {
        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
    }

    /** Menutup progress dialog **/
    private void hideDialog() {
        progressBar.setVisibility(View.GONE);
        buttonLogin.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        /**  **/
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**  **/
    private void postLoginDesa(final String username, String password) {

        showDialog();

        ApiInterfaceDesa apiService =
                ApiClientDesa.loginRequest(getApplicationContext()).create(ApiInterfaceDesa.class);

        Call<ResponseBody> call = apiService.login(Config.AUTHORIZATION, username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();

                logger.d("Respon", response.toString());
                try {
                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                    String status = jsonRESULTS.getString(Tag.SESSION_STATUS);

                    logger.d("debug", "jsonResults  "+ jsonRESULTS.toString());
                    logger.d("debug", "status "+ status);

                    if(status.equalsIgnoreCase(TAG_STATUS_SUKSES)){
                        String isUserBaru = jsonRESULTS.getString("user_baru");

                        if(jsonRESULTS.has("user_baru")){

                        }

                        String urlBaru = jsonRESULTS.getString("url");
                        String idPerangkat = jsonRESULTS.getString("id_perangkat");
                        String nama = jsonRESULTS.getString("nama");
                        String jabatan = jsonRESULTS.getString("jabatan");

                        logger.d("debug", "url "+ urlBaru);
                        logger.d("debug", "perangkat "+ idPerangkat);
                        logger.d("debug", "jabatan "+ jabatan);

                        prefManager.simpanUsername(username);
                        prefManager.simpanNIK(username);
                        prefManager.setIsLoggedIn(true);
                        prefManager.setNama(nama);
                        prefManager.setJabatan(jabatan);

                        /** Khusus Desa **/
                        prefManager.setIdPerangkat(idPerangkat);
                        prefManager.setUrlDesa(urlBaru);

                        logger.d("debug", "url 2"+ prefManager.getUrlDesa());
                        logger.d("debug", "perangkat 2 "+ prefManager.getIdPerangkat());

                        Intent intent= new Intent(getApplicationContext(), id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);

                        if(isUserBaru.equalsIgnoreCase("1")){
                            intent= new Intent(getApplicationContext(), GantiPassword.class);
                        }
                        startActivity(intent);
                        finish();
                    }else{
                        String error_message = jsonRESULTS.getString(TAG_PESAN);
                        message.setText(error_message);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
//                *  Tampilkan error *
                logger.d("error", t.getLocalizedMessage());
//                notifikasiDialog.showDialogError(2, t.getLocalizedMessage());
                message.setText(t.getLocalizedMessage());
            }
        });
    }

    /**  **/
    private void postLogin(final String username, String password) {

        showDialog();

        ApiInterface apiService =
                ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

        /*Call<List<ResultLogin>> call =  apiService.login(username,password);
        call.enqueue(new Callback<List<ResultLogin>>() {
            @Override
            public void onResponse(Call<List<ResultLogin>> call, Response<List<ResultLogin>> response) {
                logger.d("login", response.toString());
            }

            @Override
            public void onFailure(Call<List<ResultLogin>> call, Throwable t) {
                logger.d("login", t.getLocalizedMessage());
            }
        });*/

        Call<ResponseBody> call = apiService.loginRequest(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                logger.d("Respon", response.toString());

                if (response.isSuccessful()){

                    logger.d("debug", "onResponse: BERHASIL");

                    hideDialog();

                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        logger.d("debug", jsonRESULTS.toString());

                        if (jsonRESULTS.getString(Tag.SESSION_STATUS).equals(Tag.TAG_STATUS_SUKSES)){
                            logger.d("debug", "Cek JsonResult");

                            String nik              = "";
                            String nip              = "";
                            String jabatan          = "";
                            String id_pns           = "";
                            String nama             = "";
                            String token            = "";
                            String status_jabatan   = "";
                            String id_jabatan       = "";
                            String unit_kerja       = "";
                            String id_unit_kerja    = "";
                            String tipe             = "";
                            String arsip_aris       = "";

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_STATUS_JABATAN)){
                                status_jabatan = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_STATUS_JABATAN);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_ID_JABATAN)){
                                id_jabatan = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_ID_JABATAN);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_UNIT)){
                                unit_kerja = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_UNIT);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_ID_UNIT)){
                                id_unit_kerja = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_ID_UNIT);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_NAMA)){
                                nama = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_NAMA);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_ID_PNS)){
                                id_pns = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_ID_PNS);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_NIK)){
                                nik = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_NIK);
                                prefManager.simpanNIK(nik);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_NIP)){
                                nip = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_NIP);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_JABATAN)){
                                jabatan = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_JABATAN);
                            }

                            if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_TYPE)){
                                tipe = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_TYPE);

                                if(jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).has(Tag.SESSION_ARSIPARIS)){
                                    arsip_aris = jsonRESULTS.getJSONObject(Tag.SESSION_DETAIL).getString(Tag.SESSION_ARSIPARIS);


                                }

                                prefManager.simpanTipeDanArsipAris(tipe, arsip_aris);

                                logger.d("debug", "tipe " + tipe + " Arsip aris " + arsip_aris);

                            }else{
                                logger.d("debug", "Tidak ada tipe");

                            }



                            if(jsonRESULTS.has(Tag.SESSION_TOKEN)){
                                token   = jsonRESULTS.getString(Tag.SESSION_TOKEN);
                            }

                            logger.d(Tag.SESSION_DETAIL, id_pns+"/nip "+nip+"/nama "+nama+"/id unit kerja"+id_unit_kerja+"/jabatan " + jabatan + "/ tipe "+tipe);

                            String device_info = getDeviceName();

                            prefManager.setIsLoggedIn(true);
                            prefManager.simpanUsername(username);


                            prefManager.setSession(nama, nip, id_pns, id_unit_kerja,
                                    unit_kerja, id_jabatan, jabatan, status_jabatan,token, device_info);

                            if(jabatan.contains("Lurah")){

                                // Buka halaman login untuk Desa Online
                                Intent intent= new Intent(getApplicationContext(), LoginLurahActivity.class);
                                intent.putExtra(Tag.SESSION_NIK, nik);
                                intent.putExtra(TAG_PESAN, status_jabatan +" di "+unit_kerja);
                                startActivity(intent);
                                finish();

                            }else{

                                logger.d("debug-tipe-cek", prefManager.getType()+"/"+String.valueOf(ArrayUtils.contains(arrayTipe, prefManager.getType())));


                                if(ArrayUtils.contains(arrayTipe, prefManager.getType())){
                                    // pindah ke Dashboard Operator
                                    Intent intent= new Intent(getApplicationContext(), DashboardOperator.class);
                                    startActivity(intent);
                                    finish();
                                }else{

                                    /**  Kirim username dan token firebase ke server **/
                                    if(prefManager.getTokenFirebase() == null){
                                        notifikasiDialog.showToast("Maaf, aplikasi tidak berhasil memperoleh data untuk servis notifikasi real time.");
                                    }else if(prefManager.getTokenFirebase().length()==0) {
                                        notifikasiDialog.showToast("Maaf, aplikasi tidak berhasil memperoleh data untuk servis notifikasi real time.");
                                    }else{
                                        sendTokenToFirebase();
                                    }
                                }


                            }

                        } else {
                            String error_message = jsonRESULTS.getString(TAG_PESAN);
                            message.setText(error_message);
                        }
                    } catch (JSONException e) {
                        hideDialog();
                        e.printStackTrace();
                        message.setText(e.getLocalizedMessage());
                    } catch (IOException e) {
                        hideDialog();
                        e.printStackTrace();
                        message.setText(e.getLocalizedMessage());
                    }
                } else {
                    hideDialog();
                    Headers headers = response.headers();
                    String cookie = response.headers().get("Set-Cookie");
                    String message_ = response.headers().get("message");
                    message.setText(message_);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                logger.d("error", t.getLocalizedMessage());
                message.setText(t.getLocalizedMessage());
            }
        });
    }


    /** Mengirim username dan token firebase ke server **/
    private void sendTokenToFirebase() {

        showDialog();

        ApiInterface apiService =
                ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

        String token_authorization = prefManager.getSessionToken();
        String token_firebase = prefManager.getTokenFirebase();

        Call<ResponseBody> call = apiService.sendTokenFirebase(token_authorization, token_firebase,
                getDeviceName(), versionName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                logger.d("Respon", response.body().toString());
                if (response.isSuccessful()){
                    logger.d("debug", "onResponse: BERHASIL");

                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString(Tag.SESSION_STATUS).equals("success")){

                            hideDialog();
                            message.setVisibility(View.GONE);

                            Intent intent= new Intent(getApplicationContext(), Dashboard.class);
                            if(prefManager.getStatusJabatan().equals(Tag.TAG_AJUDAN)){
                                intent= new Intent(getApplicationContext(), id.go.kebumenkab.eletterkebumen.ajudan.Dashboard.class);
                            }

                            startActivity(intent);
                            finish();

                        } else {
                            hideDialog();
                            String error_message = jsonRESULTS.getString(TAG_PESAN);
                            notifikasiDialog.showDialogError(100, error_message);
                        }
                    } catch (JSONException e) {
                        hideDialog();
                        e.printStackTrace();
                        notifikasiDialog.showDialogError(100, e.getLocalizedMessage());
                    } catch (IOException e) {
                        hideDialog();
                        e.printStackTrace();
                        notifikasiDialog.showDialogError(100, e.getLocalizedMessage());
                    }
                } else {
                    hideDialog();
                    logger.d("debug", "onResponse: GA BERHASIL");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                /**  Tampilkan error **/
                notifikasiDialog.showDialogError(100, t.getLocalizedMessage());
            }
        });
    }

    public void show_dialog_setuju(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning_password, null);
        TextView  messageText = dialogView.findViewById(R.id.message_text);
        messageText.setText(getString(R.string.message_syarat_dan_ketentuan));
        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.setuju), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        /** Cek username dan password ke server**/


                        if(username.length()==16){ // by NIK untuk kades
                            postLoginDesa(username, password);
                            prefManager.simpanUsername(username);
                            // login(username, password);

                        }else{
                            postLogin(username, password);
                            prefManager.simpanUsername(username);
                        }


                    } else {
                        /** Tampilkan pesan error **/
                        notifikasiDialog.showDialogError(1, "");
                    }

            }
        });

        builder.setNegativeButton(getString(R.string.lihat), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("https://eletter.kebumenkab.go.id/panduan/SK-Aplikasi.pdf"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        builder.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    public void bukaVersiWeb(View view){
        String url = "https://eletter.kebumenkab.go.id";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void bukaRiwayatDev(View view){
        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        startActivity(intent);
    }

    public void bukaFAQ(View view){
        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        intent.putExtra("faq", "1");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
   }

    private boolean login(final String username, final String password){
        final String token = Config.AUTHORIZATION;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_LOGIN_DESA,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        logger.d("Login", response.toString());

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        logger.d("Error Login",error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization",  token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

        return true;
    }
}
