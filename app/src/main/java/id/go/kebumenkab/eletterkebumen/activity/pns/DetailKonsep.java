package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.HistoriActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranActivity;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
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
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISPENERIMA;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISTUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSILANGSUNG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class DetailKonsep extends AppBaseActivity implements View.OnClickListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_telaah;
    private LinearLayout btn_riwayat;
    private LinearLayout btn_lampiran;
    private LinearLayout btn_koreksi;

    private TextView tanggal, instansi, pengirim, subjek, pesan, pesan_atas;
    private TextView t_inisiator, t_nomor, t_tandatangan, t_tujuan, t_deskripsi, t_pesan;

    private String  idSurat;
    private String  idHistori;
    private String  alurSurat;
    private String  pejabatTandaTangan;
    private String  jenisSurat;
    private String  strPesan;
    private String  strLampiran;

    private String  strArsip="";
    private PrefManager prefManager;
    private String token;
    private ProgressDialog pDialog;

    private String ditandai ="";
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private ActivityResultLauncher<Intent> webViewLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        registerBaseActivityReceiver();

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DetailKonsep.this);

        logger = new Logger();

        /**  Inisiasi progress dialog **/
        pDialog     = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));

        /**  Menyiapkan header dan title **/
       // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_detail));

        /** inisialisasi untuk menutup activity dari WebViewActivity **/
        webViewLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultIntent = new Intent();
                        if (result.getData() != null) {
                            String successMessage = result.getData().getStringExtra("successMessage");
                            resultIntent.putExtra("successMessage", successMessage);
                        }
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish(); // Tutup DetailKonsep
                        overridePendingTransition(0, 0); // Tidak ada animasi keluar
                    }
                });
//        webViewLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // Tutup DetailKonsep setelah WebViewActivity selesai
//                        finish();
//                    }
//                }
//        );

        /**  Inisiasi komponen tampilan **/
        setupView();

        /**  Menerima data dari activity lain **/
        Intent intent = getIntent();
        Konsep konsep = (Konsep)intent.getSerializableExtra("object");
        int posisi = intent.getIntExtra("position", 0);
        idSurat = konsep.getIdSurat();
        idHistori = konsep.getIdHistory();
        alurSurat = konsep.getStatus();
        String subjek = konsep.getSubject();
        strArsip        = intent.getStringExtra(TAG_ARSIP);

        logger.d("PindahHalamanKonsep", idSurat+"/"+idHistori+"/"+alurSurat+"/"+strArsip+"/"+subjek+"/posisi: "+posisi);

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
        pesan_atas               = (TextView)findViewById(R.id.pesan_atas); // untuk menampilkan ada info lampiran

        t_inisiator         = (TextView)findViewById(R.id.text_inisiator);
        t_deskripsi         = (TextView)findViewById(R.id.text_deskripsi);
        t_nomor             = (TextView)findViewById(R.id.text_nomor);
        t_tandatangan       = (TextView)findViewById(R.id.text_tandatangan);
        t_tujuan            = (TextView)findViewById(R.id.text_tujuan);

        btn_telaah          = (LinearLayout)findViewById(R.id.btn_telaah);
        btn_riwayat         = (LinearLayout)findViewById(R.id.btn_riwayat);
        btn_lampiran        = (LinearLayout)findViewById(R.id.btn_lampiran);
        btn_koreksi       = (LinearLayout)findViewById(R.id.btn_editkoreksi);

        btn_telaah.setOnClickListener(this);
        btn_riwayat.setOnClickListener(this);
        btn_lampiran.setOnClickListener(this);
        btn_koreksi.setOnClickListener(this);
        //penambahan syarat untuk bupati, wakil dan sekda tidak ada menu koreksi
        if(prefManager.getStatusJabatan().equalsIgnoreCase("bupati")||prefManager.getStatusJabatan().equalsIgnoreCase("wakilbupati")||prefManager.getStatusJabatan().equalsIgnoreCase("sekda")){
            btn_koreksi.setVisibility(View.GONE);
        }

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
                intent.putExtra(Tag.TAG_ALUR_SURAT, alurSurat );
                intent.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intent.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intent.putExtra(Tag.TAG_PEJABAT_TANDATANGAN, pejabatTandaTangan);
                intent.putExtra(Tag.TAG_JENISSURAT, jenisSurat);
                intent.putExtra(Tag.TAG_PESAN, strPesan);
                intent.putExtra(TAG_ARSIP, strArsip);
                intent.putExtra(TAG_JENISPENERIMA, ""); // instansi, sekda, bupati, wakilbupati
                intent.putExtra(Tag.TAG_TANDAI, ditandai);
                intent.putExtra(TAG_JENISTUJUAN, "");
                /**  Pindah halaman **/;
                logger.d("Jenis Surat", jenisSurat);
                webViewLauncher.launch(intent);
//                startActivity(intent);
//                finish(); // Tutup DetailKonsep karena kalau tidak dia akan tetap ada di stack walaupun sudah dari WebViewActivity dikembalikan ke dashboard
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
            case R.id.btn_editkoreksi:
                /**  Dikirimkan ke WebView  Activity **/
                Intent intentkoreksi = new Intent(getApplicationContext(), WebViewActivity.class);
                intentkoreksi.putExtra(Tag.TAG_ALUR_SURAT, alurSurat );
                intentkoreksi.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intentkoreksi.putExtra(Tag.TAG_JENISSURAT, jenisSurat);
                intentkoreksi.putExtra(TAG_KOREKSILANGSUNG, "koreksi_langsung");
                /**  Pindah halaman **/;
                logger.d("Jenis Surat", jenisSurat);
                startActivity(intentkoreksi);
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

                Logger logger = new Logger();
                logger.w5("Hasil Detail Konsep", response);

                /**  **/
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Detail detail = result.getDetail();
                        String inisiator = detail.getNamaInisiator();


                        if (detail != null) {

                            pejabatTandaTangan = detail.getPejabatTtd();
                            jenisSurat = detail.getJenisSurat();
                            ditandai    =  detail.getTandai();

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
                                showLampiran();
                                btn_lampiran.setVisibility(View.VISIBLE);
                            }

//                            if(pejabatTandaTangan.equals(prefManager.getSessionJabatan())){


                        } else {

                            notifikasiDialog.showDialogError(2, "");

                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<ResultDetail> call, Throwable t) {
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

    public void showLampiran(){
        // adding the color to be shown
        pesan_atas.setText(R.string.message_ada_lampiran);
        ObjectAnimator animator = ObjectAnimator.ofInt(pesan_atas, "backgroundColor", Color.WHITE, Color.YELLOW, Color.WHITE);

        // duration of one color
        animator.setDuration(3000);
        animator.setEvaluator(new ArgbEvaluator());

        // color will be show in reverse manner
        animator.setRepeatCount(Animation.REVERSE);

        // It will be repeated up to infinite time
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    /*public void showLampiran() {

        *//**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **//*

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_message, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit);

        *//** Konfirmasi arsipkan tidak perlu kolom pesan **//*
        *//**  untuk tombol ajuan **//*
        edt.setVisibility(GONE);
        dialogBuilder.setMessage(getString(R.string.message_lihatlampiran));


        dialogBuilder.setPositiveButton(getString(R.string.action_lihat), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                lihatLampiran();
            }
        });

        dialogBuilder.setNeutralButton(getString(R.string.action_tutup), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                *//** Tutup dialog **//*
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }*/

    public void lihatLampiran(){
        /**  Dikirimkan ke Lampiran  Activity **/
        Intent intentLampiran = new Intent(getApplicationContext(), LampiranActivity.class);
        intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );

        /**  Pindah halaman **/
        startActivity(intentLampiran);
    }

}
