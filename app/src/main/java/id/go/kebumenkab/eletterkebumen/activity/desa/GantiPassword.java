package id.go.kebumenkab.eletterkebumen.activity.desa;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import id.go.kebumenkab.eletterkebumen.R;
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

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PESAN;

public class GantiPassword extends AppCompatActivity {

    Button buttonLogin;
    EditText editPasswordLama, editPasswordBaru, editKonfirmasiPassword;
    private TextView version, message;
    private ProgressBar progressBar;
    private String username;

    private ConnectivityManager conMgr;

    private static final String TAG     = GantiPassword.class.getSimpleName();

    private Context mContext;


    private PrefManager prefManager;
    private NotifikasiDialog notifikasiDialog;

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pengecekan session dilakukan sebelum memanggil setContentView()
        prefManager         = new PrefManager(getApplicationContext());
        mContext            = getApplicationContext();
        logger              = new Logger();
        notifikasiDialog    = new NotifikasiDialog(getApplicationContext(), GantiPassword.this);
        username            = prefManager.getUsername();

        setup();

    }

    /**  Setting view **/
    private void setup(){
        setContentView(R.layout.layout_gantipassword);

        version                     = (TextView)findViewById(R.id.version);

        buttonLogin                 = (Button) findViewById(R.id.buttonLogin);
        editPasswordLama            = (EditText) findViewById(R.id.editPasswordLama);
        editPasswordBaru            = (EditText) findViewById(R.id.editPasswordBaru);
        editKonfirmasiPassword      = (EditText) findViewById(R.id.editKonfirmasiPassword);
        progressBar                 = (ProgressBar)findViewById(R.id.progressBar);
        message                     = (TextView) findViewById(R.id.message);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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
        if (editPasswordLama.getText().toString().trim().equalsIgnoreCase("")){
            editPasswordLama.requestFocus();
            editPasswordLama.setError(getString(R.string.message_password_diisi));
            return false;
        }
        /**  Tampilkan error jika edit password masih kosong **/
        else if (editPasswordBaru.getText().toString().trim().equalsIgnoreCase("")){
            editPasswordBaru.requestFocus();
            editPasswordBaru.setError(getString(R.string.message_password_diisi));
            return false;
        }/**  Tampilkan error jika edit password masih kosong **/
        else if (editKonfirmasiPassword.getText().toString().trim().equalsIgnoreCase("")){
            editKonfirmasiPassword.requestFocus();
            editKonfirmasiPassword.setError(getString(R.string.message_password_diisi));
            return false;
        }
        return true;
    }

    public void show_dialog_lupa(View view){
        notifikasiDialog.showDialogError(7, "");
    }

    public void skip(View view){
        prefManager.setIsLoggedIn(true);
        prefManager.simpanUsername(username);

        // desa =10
        notifikasiDialog.showDialogError(12, "Ganti Password lain waktu");
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
    private void updateGantiPassword(final String username, String passwordLama, String passwordBaru) {

        showDialog();

        ApiInterfaceDesa apiService =
                ApiClientDesa.loginRequest(getApplicationContext()).create(ApiInterfaceDesa.class);

        Call<ResponseBody> call = apiService.updateUser(Config.AUTHORIZATION,
                username, passwordLama, passwordBaru, passwordBaru);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideDialog();

                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("status").equals(Tag.TAG_STATUS_SUKSES)){

                            String pesan     = jsonRESULTS.getString("pesan");
                            Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();

//                            *  Simpan session *
                            prefManager.setIsLoggedIn(true);

                            // desa =10
                            notifikasiDialog.showDialogError(11, pesan);


                        } else {
                            String error_message = jsonRESULTS.getString(TAG_PESAN);
                            message.setText(error_message);
                        }
                    } catch (JSONException | IOException e) {
                        hideDialog();
                        e.printStackTrace();
                        // notifikasiDialog.showDialogError(100, e.getLocalizedMessage());
                        message.setText(e.getLocalizedMessage());
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

    public void show_dialog_setuju(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning_password, null);
        TextView  messageText = dialogView.findViewById(R.id.message_text);
        messageText.setText(getString(R.string.message_ganti_password));
        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.setuju), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String passwordLama = editPasswordLama.getText().toString();
                String passwordBaru = editPasswordBaru.getText().toString();
                String konfirmasiPassword = editKonfirmasiPassword.getText().toString();

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        /** Cek username dan password ke server**/

                        if(passwordLama.equalsIgnoreCase(passwordBaru)){
                            message.setText("Password Baru tidak boleh sama dengan password lama");
                            message.setVisibility(View.VISIBLE);
                        }else{
                            if(passwordBaru.equalsIgnoreCase(konfirmasiPassword)){
                                updateGantiPassword(username, passwordLama, passwordBaru);
                            }else{
                                message.setText("Password Baru dan konfirmasi harus sama");
                                message.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        /** Tampilkan pesan error **/
                        notifikasiDialog.showDialogError(1, "");
                    }

            }
        });

        builder.setNegativeButton(getString(R.string.action_tutup), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.show();
    }




    @Override
    protected void onResume() {
        super.onResume();
           }
}
