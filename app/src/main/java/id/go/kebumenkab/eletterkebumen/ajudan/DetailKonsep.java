package id.go.kebumenkab.eletterkebumen.ajudan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.HistoriActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranActivity;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Detail;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;

public class DetailKonsep extends AppCompatActivity implements View.OnClickListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_telaah;
    private LinearLayout btn_riwayat;
    private LinearLayout btn_lampiran;

    private TextView tanggal, instansi, pengirim, subjek, pesan;
    private TextView t_inisiator, t_nomor, t_tandatangan, t_tujuan, t_deskripsi, t_pesan;

    private String  idSurat;
    private String  idHistori;
    private String  alurSurat;
    private String  pejabatTandaTangan;
    private String  jenisSurat;
    private String  strPesan;
    private String  strLampiran;

    private PrefManager prefManager;
    private String token;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

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
        Konsep konsep = (Konsep)intent.getSerializableExtra("object");
        idSurat = konsep.getIdSurat();
        idHistori = konsep.getIdHistory();
        alurSurat = konsep.getStatus();

        /** Mengecek internet **/
        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /** Bila memiliki koneksi internet maka panggil api detail **/
        if(isConnected){
            getDetail();
        }else{
            showDialogError(1);
        }

    }

    /**  Fungsi menghubungkan komponen di layout dengan activity**/
    public void setupView(){
        tanggal             = (TextView)findViewById(R.id.txt_tanggal);
        instansi            = (TextView)findViewById(R.id.txt_instansi);
        pengirim            = (TextView)findViewById(R.id.from);
        subjek              = (TextView)findViewById(R.id.subject);
        pesan               = (TextView)findViewById(R.id.message);

        t_inisiator         = (TextView)findViewById(R.id.text_inisiator);
        t_deskripsi         = (TextView)findViewById(R.id.text_deskripsi);
        t_nomor             = (TextView)findViewById(R.id.text_nomor);
        t_tandatangan       = (TextView)findViewById(R.id.text_tandatangan);
        t_tujuan            = (TextView)findViewById(R.id.text_tujuan);

        btn_telaah          = (LinearLayout)findViewById(R.id.btn_telaah);
        btn_riwayat         = (LinearLayout)findViewById(R.id.btn_riwayat);
        btn_lampiran         = (LinearLayout)findViewById(R.id.btn_lampiran);


        btn_telaah.setOnClickListener(this);
        btn_riwayat.setOnClickListener(this);
        btn_lampiran.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            /** Ketika ditekan tombol panah di pojok kiri atas maka tutup activity **/
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_telaah:
                /**  Dikirimkan ke WebView  Activity **/
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(Tag.TAG_ALUR_SURAT, alurSurat );
                intent.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intent.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intent.putExtra(Tag.TAG_PEJABAT_TANDATANGAN, pejabatTandaTangan);
                intent.putExtra(Tag.TAG_JENISSURAT, jenisSurat);
                intent.putExtra(Tag.TAG_PESAN, strPesan);
                intent.putExtra(TAG_ARSIP, "");

                /**  Pindah halaman **/
                startActivity(intent);
                break;

            case R.id.btn_lampiran:
                /**  Dikirimkan ke WebView  Activity **/
                Intent intentLampiran = new Intent(getApplicationContext(), LampiranActivity.class);
                intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );

                /**  Pindah halaman **/
                startActivity(intentLampiran);
                break;


            case R.id.btn_riwayat:
                /** Dikirimkan ke HistoriActivity **/
                Intent intentRiwayat = new Intent(getApplicationContext(), HistoriActivity.class);
                intentRiwayat.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intentRiwayat.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intentRiwayat.putExtra(Tag.TAG_JENISSURAT, Tag.TAG_KONSEP );

                /**  Pindah halaman **/
                startActivity(intentRiwayat);
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
        Call<ResultDetail> call = apiService.getDetailKonsep(token, idSurat, idHistori);

        call.enqueue(new Callback<ResultDetail>() {
            @Override
            public void onResponse(Call<ResultDetail> call, Response<ResultDetail> response) {
                /**  **/
                hideDialog();

                final ResultDetail result = response.body();

                /**  **/
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Detail detail = result.getDetail();
                        String inisiator = detail.getNamaInisiator();


                        if (detail != null) {

                            pejabatTandaTangan = detail.getPejabatTtd();
                            jenisSurat = detail.getJenisSurat();

                            t_inisiator.setText("Inisiator : " + detail.getNamaInisiator());
                            t_nomor.setText("Nomor : " + detail.getNomorSurat());
                            t_tandatangan.setText("Tanda tangan : " + detail.getPejabatTtd());
                            t_deskripsi.setText("Deskripsi : " +detail.getDeskripsiSurat());

                            strPesan = detail.getPesan().toString();
                            pesan.setText(strPesan);

                            String stringTujuan ="";
                            String[] arrayTujuan = detail.getTujuan().split(",");

                            for(int i=0; i< arrayTujuan.length -1 ; i++){
                                stringTujuan += "\n\n"+String.valueOf(i+1)+". "+arrayTujuan[i].trim();
                            }

                            t_tujuan.setText("Tujuan :"+ stringTujuan);

                            tanggal.setText(konversiTanggal(detail.getTimestamp()));
                            instansi.setText(detail.getUnitKerjaInisiator());
                            pengirim.setText(detail.getFrom());
                            subjek.setText(detail.getNamaSurat());


                            if(detail.getLampiran().length()>0){
                                Log.d("Lampiran", detail.getLampiran());
                                strLampiran = detail.getLampiran().toString();
                                btn_lampiran.setVisibility(View.VISIBLE);
                            }

                        } else {

                            Log.d("tes", "null");

                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<ResultDetail> call, Throwable t) {
                hideDialog();
                showDialogError(2);
            }
        });
    }

    /**  Fungsi memparsing tanggal */
    public String konversiTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm");

        return timeFormat.format(date);
    }

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/

    public void showDialogError(int i){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = null;

        if(i==1){
            dialogView = inflater.inflate(R.layout.dialog_warning, null);
        }else if(i==2){
            dialogView = inflater.inflate(R.layout.dialog_warning_server, null);
        }
        builder.setView(dialogView);

        builder.setPositiveButton(getString(R.string.action_tutup), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                finish();
            }
        });

        builder.show();
    }

}