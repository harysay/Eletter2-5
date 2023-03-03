package id.go.kebumenkab.eletterkebumen.activity.desa;

import androidx.appcompat.app.AppCompatActivity;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class KonfirmasiTandaTanganActivity extends AppCompatActivity {

    TextView            _message;
    TextInputEditText   _passphrase;
    Button              _btnKirim, _btnTutup;
    ProgressBar         _progressBarLingkaran;
    PrefManager         prefManager;
    Logger              logger;
    ImageView           _close;
    LinearLayout        _linearLayoutPassphrase;

    String              messageLog = "";
    int                 jumlahDitandatangani = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_tanda_tangan);

        prefManager = new PrefManager(getApplicationContext());
        logger      = new Logger();

        _message    = (TextView)findViewById(R.id.message);
        _passphrase = (TextInputEditText)findViewById(R.id.input_passphrase);
        _btnKirim   = (Button) findViewById(R.id.btn_tandatangani);
        _btnTutup   = (Button) findViewById(R.id.btn_close);
        _progressBarLingkaran= (ProgressBar)findViewById(R.id.progressBarLingkaran);
        _close      = (ImageView) findViewById(R.id.close);
        _linearLayoutPassphrase = (LinearLayout)findViewById(R.id.layout_passphrase);

        hideProgressBarCircle();
        _progressBarLingkaran.setVisibility(View.INVISIBLE);

        ArrayList<String> infoDitandai = prefManager.ambilDataKonsepList();
        _message.setText("Anda akan menandatangani "+ infoDitandai.size() +" ajuan");

        _btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_passphrase.getText().length()==0) {
                    _passphrase.setError("Masukkan passphrase Anda");
                } else {
                    // tandaTangani(_passphrase.getText().toString());
                    showProgressBarCircle();
                    hideButtonDanPassphrase();


                    for(int i = 0; i<infoDitandai.size();i++){
                        String id           =  infoDitandai.get(i);
                        logger.d("tandatangan id", id);
                        boolean hasil       = prosesTandaTangan( id, _passphrase.getText().toString());

                        hideProgressBarCircle();
                        showButtonClose();
                    }
                }
            }
        });

        _close.setOnClickListener(view -> finish());
        _btnTutup.setOnClickListener(view -> finish());



    }

    private void hideButtonDanPassphrase(){
        _close.setVisibility(View.INVISIBLE);
        _linearLayoutPassphrase.setVisibility(View.GONE);
    }

    private void showButtonClose(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                _close.setVisibility(View.VISIBLE);
                _btnTutup.setVisibility(View.VISIBLE);
                _btnTutup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        });


    }

    private void showProgressBarCircle(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _message.setText(messageLog);
                _progressBarLingkaran.setVisibility(View.VISIBLE);
            }
        });

    }

    private void hideProgressBarCircle(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _message.setText(messageLog);
                _progressBarLingkaran.setVisibility(View.INVISIBLE);
            }
        });

    }


    private boolean prosesTandaTangan(String idSurat, String passphrase){

        showProgressBarCircle();

        boolean[] myStatus = {false};
        ApiInterfaceDesa apiService =
                ApiClientDesa.getClient(getApplicationContext()).create(ApiInterfaceDesa.class);

        Call<ResponStandar> call = apiService.sendSetuju(Config.AUTHORIZATION, idSurat, prefManager.getIdPerangkat(), prefManager.getNIK(), passphrase);

        logger.d("tanda_tanganku", idSurat+"/"+ prefManager.getNIK());

        if (call != null) {

            call.enqueue(new Callback<ResponStandar>() {
                @Override
                public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                    ResponStandar data = response.body();
                    logger.w4("debug_eletter_responsestandar", response);

                    if(data!=null){
                        if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                            prefManager.hilangkanDataKonsepList(idSurat);

                            jumlahDitandatangani=+1;

                            if(jumlahDitandatangani == prefManager.ambilDataKonsepList().size()){
                                logger.d("debug_eletter", jumlahDitandatangani+"/"+ prefManager.ambilDataKonsepList().size());
                                messageLog = "Ajuan telah ditandatangani semua";
                                _message.setText(messageLog);

                            }
                            showButtonClose();
                            myStatus[0] = true;

                        }else{
                            logger.w4("debug_eletter_responsestandar", response);
                            _message.setText(data.getPesan());
                            _close.setVisibility(View.VISIBLE);
                            _btnTutup.setVisibility(View.VISIBLE);
                            myStatus[0] = false;

                        }
                    }else{
                        messageLog = "data kosong";
                        logger.d("debug_eletter", messageLog);

                        _close.setVisibility(View.VISIBLE);
                        _btnTutup.setVisibility(View.VISIBLE);
                        _message.setText(messageLog);

                        myStatus[0] = false;
                    }
                }

                @Override
                public void onFailure(Call<ResponStandar> call, Throwable t) {
                    logger.d("debug_eletter", "failure " + t.getMessage());
                    messageLog = t.getLocalizedMessage();
                    _message.setText(messageLog);

                    _close.setVisibility(View.VISIBLE);
                    _btnTutup.setVisibility(View.VISIBLE);
                    myStatus[0] = false;
                }
            });

        }else{
            logger.d("debug_eletter", "call is null");
            messageLog = "Librari tidak sukses";
            _message.setText(messageLog);
            myStatus[0] = false;
        }

        return myStatus[0];
    }
}