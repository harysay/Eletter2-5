package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.PermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.model.ResponDetailPermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ALUR_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISSURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PERMINTAANTANDATANGAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_URL_PDF;

public class DetailPermintaanTandatangan extends AppBaseActivity implements View.OnClickListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_telaah;

    private TextView tanggal, instansi, pengirim, subjek, pesan;

    private String  idSurat;
    private String  strPesan, strUrlPDF;

    private String  strArsip="";
    private PrefManager prefManager;
    private String token;
    private ProgressDialog pDialog;

    private String ditandai ="";
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_permintaantandatangan);
        registerBaseActivityReceiver();

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DetailPermintaanTandatangan.this);

        logger = new Logger();

        /**  Inisiasi progress dialog **/
        pDialog     = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));

        /**  Menyiapkan header dan title **/
       // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_detail));

        /**  Inisiasi komponen tampilan **/
        setupView();

        /**  Menerima data dari activity lain **/
        Intent intent = getIntent();
        PermintaanTandatangan konsep = (PermintaanTandatangan) intent.getSerializableExtra("object");
        idSurat = konsep.getIdPermohonanTtd();

        strArsip        = intent.getStringExtra(TAG_ARSIP);

        logger.d("Yusuf", intent.getStringExtra(Tag.TAG_PESAN)+"/"
                +intent.getStringExtra(Tag.TAG_ARSIP)+"/"
                +intent.getStringExtra(Tag.TAG_ALUR_SURAT)+"/"
                +intent.getStringExtra(Tag.TAG_ID_SURAT)+"/"
                +intent.getStringExtra(Tag.TAG_ID_HISTORI)+"/"
                +intent.getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN));

        /** Mengecek internet **/
        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /** Bila memiliki koneksi internet maka panggil api detail **/
        if(isConnected){
            getDetail();
        }else{
            notifikasiDialog.showDialogError(1, "");
        }

    }

    /**  Fungsi menghubungkan komponen di layout dengan activity**/
    public void setupView(){
        tanggal             = (TextView)findViewById(R.id.txt_tanggal);
        instansi            = (TextView)findViewById(R.id.txt_instansi);
        pengirim            = (TextView)findViewById(R.id.from);
        subjek              = (TextView)findViewById(R.id.subject);
        pesan               = (TextView)findViewById(R.id.message);

        btn_telaah          = (LinearLayout)findViewById(R.id.btn_telaah);

        btn_telaah.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            /** Ketika ditekan tombol panah di pojok kiri atas maka tutup activity **/
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_telaah:
                /**  Dikirimkan ke WebView  Activity **/
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(TAG_JENISSURAT, TAG_PERMINTAANTANDATANGAN);
                intent.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intent.putExtra(TAG_URL_PDF, strUrlPDF); // instansi, sekda, bupati, wakilbupati
                intent.putExtra(TAG_ALUR_SURAT, TAG_PERMINTAANTANDATANGAN);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /**  **/
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**  **/
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /** Fungsi untuk mengambil informasi detail surat/konsep **/
    private void getDetail() {
        /**  **/
        showDialog();

        ApiInterface apiService =   ApiClient.getClient().create(ApiInterface.class);

        /**  **/
        Call<ResponDetailPermintaanTandatangan> call = apiService.getDetailPermintaanTandatangan(token, idSurat);

        call.enqueue(new Callback<ResponDetailPermintaanTandatangan>() {
            @Override
            public void onResponse(Call<ResponDetailPermintaanTandatangan> call, Response<ResponDetailPermintaanTandatangan> response) {
                /**  **/
                hideDialog();

                final ResponDetailPermintaanTandatangan result = response.body();


                /**  **/
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        PermintaanTandatangan detail = result.getData();


                        if (detail != null) {

                            strPesan = detail.getDeskripsi();
                            pesan.setText(strPesan);

                            tanggal.setText(konversiTanggal(detail.getTimestampInsert()));
                            instansi.setText(detail.getStatus());
                            pengirim.setText(detail.getAplikasi());
                            subjek.setText(detail.getDeskripsi());
                            strUrlPDF = detail.getUrl();

                        } else {

                            notifikasiDialog.showDialogError(2, "");

                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<ResponDetailPermintaanTandatangan> call, Throwable t) {
                hideDialog();

                notifikasiDialog.showDialogError(100, t.getLocalizedMessage());
            }
        });
    }

    /**  Fungsi memparsing tanggal */
    public String konversiTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

        return timeFormat.format(date);
    }

}
