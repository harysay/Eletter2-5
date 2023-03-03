package id.go.kebumenkab.eletterkebumen.activity.lurah;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.desa.GantiPassword;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.SESSION_STATUS;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PESAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_STATUS_SUKSES;

public class LoginLurahActivity extends AppCompatActivity {

    private Button buttonLogin;
    private EditText editUsername, editPassword;
    private TextView message;
    private ProgressBar progressBar;

    private ConnectivityManager conMgr;
    Context mContext;

    private PrefManager prefManager;
    private NotifikasiDialog notifikasiDialog;

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager     = new PrefManager(getApplicationContext());
        mContext        = getApplicationContext();
        logger          = new Logger();
        notifikasiDialog= new NotifikasiDialog(getApplicationContext(), LoginLurahActivity.this);

        logger.d("Halaman", "Login Lurah");

        setup();

        editUsername.setText(prefManager.getNIK());
        editUsername.setSelection(editUsername.getText().length());
        editPassword.setText("");
    }

    /**  Setting view **/
    private void setup(){
        setContentView(R.layout.layout_login_lurah);

        TextView version = (TextView) findViewById(R.id.version);
        version.setText("Masuk sebagai : "+getIntent().getStringExtra(TAG_PESAN));

        buttonLogin     = (Button) findViewById(R.id.buttonLogin);
        editUsername    = (EditText) findViewById(R.id.editUsername);
        editPassword    = (EditText) findViewById(R.id.editPassword);
        progressBar     = (ProgressBar)findViewById(R.id.progressBar);
        message         = (TextView) findViewById(R.id.message);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(getIntent().hasExtra(Tag.SESSION_NIK)){
            editUsername.setText(getIntent().getStringExtra(Tag.SESSION_NIK));
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cekUsernamePassword()){

                    String username = editUsername.getText().toString();
                    String password = editPassword.getText().toString();

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        username = username.replace("p","");
                        postLoginDesa(username, password);

                    } else {
                        /** Tampilkan pesan error **/
                        notifikasiDialog.showDialogError(1, "");
                    }
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
                    String status = jsonRESULTS.getString(SESSION_STATUS);

                    logger.d("debug", "jsonResults  "+ jsonRESULTS.toString());
                    logger.d("debug", "status "+ status);

                    if(status.equalsIgnoreCase(TAG_STATUS_SUKSES)){
                        String isUserBaru = jsonRESULTS.getString("user_baru");

                        if(jsonRESULTS.has("user_baru")){

                        }

                        String urlBaru = jsonRESULTS.getString("url");
                        String idPerangkat = jsonRESULTS.getString("id_perangkat");
                        String nama = jsonRESULTS.getString("nama");


                        logger.d("debug", "url "+ urlBaru);
                        logger.d("debug", "perangkat "+ idPerangkat);

                        prefManager.setIsLoggedIn(true);
                        prefManager.setNama(nama);

                        /** Khusus Desa **/
                        prefManager.setIdPerangkat(idPerangkat);
                        prefManager.setUrlDesa(urlBaru);

                        logger.d("debug", "url 2"+ prefManager.getUrlDesa());
                        logger.d("debug", "perangkat 2 "+ prefManager.getIdPerangkat());
                        logger.d("debug", "jabatan "+ prefManager.getJabatan());

                        Intent intent= new Intent(getApplicationContext(), DashboardLurah.class);

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
                logger.d("error", t.getLocalizedMessage());
                message.setText(t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
