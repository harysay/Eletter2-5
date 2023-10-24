package id.go.kebumenkab.eletterkebumen.activity.operator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.HistoriActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranActivity;
import id.go.kebumenkab.eletterkebumen.activity.pns.WebViewActivity;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.DataItemSuratMasuk;
import id.go.kebumenkab.eletterkebumen.model.Detail;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISPENERIMA;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISTUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PESAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;

public class DetailSuratMasuk extends AppBaseActivity implements View.OnClickListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_telaah;
    private LinearLayout btn_riwayat;
    private LinearLayout btn_arsip;
    private LinearLayout btn_lampiran;


    private TextView tanggal, instansi, pengirim, subjek, pesan, pesan_atas;
    private TextView t_nomor, t_pesan, t_deskripsi;

    private String  idSurat;
    private String  idHistori;
    private String  alurSurat;
    private String  timeStamp;
    private String  deskripsiSurat;
    private String  tautan_telaah;
    private String  dataPengirim;

    private String  pejabatTandaTangan;
    private String  jenisSurat;
    private String  strArsip;
    private String  strLampiran;
    private String  jenisArsip = "";
    private String  jenisPenerima;
    private String  jenisTujuan;

    private PrefManager prefManager;
    private String token;
    private RelativeLayout layoutPengirim;

    private Logger logger;
    private NotifikasiDialog notifikasiDialog;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_suratmasuk);
        registerBaseActivityReceiver();

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        logger      = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DetailSuratMasuk.this);

        /**  Menyiapkan header dan title **/
        // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_detail));

        /**  Menerima data dari activity lain
         *  seperti dari ArsipActivity, SuratMasukFragment **/
        cekIntent();

        /**  Inisiasi komponen tampilan **/
        setupView();

        /** Mengecek internet **/
        isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /** Bila memiliki koneksi internet maka panggil api detail **/
        if(isConnected){
            getDetail();
        }else{
            notifikasiDialog.showDialogError(1, "");
        }

    }


    private void cekIntent(){

        Intent intent = getIntent();
        DataItemSuratMasuk suratMasuk = (DataItemSuratMasuk) intent.getSerializableExtra("object");
        idSurat         = suratMasuk.getIdSuratInternal();
        tautan_telaah   = suratMasuk.getTelaah();
        dataPengirim    = suratMasuk.getPengirim();
        timeStamp       = suratMasuk.getTimestamp();
        strArsip        = intent.getStringExtra(TAG_ARSIP);


        if(intent.getStringExtra(TAG_JENISARSIP)!= null){
            jenisArsip = getIntent().getStringExtra(TAG_JENISARSIP); // untuk deteksi arsip ajuan, koreksi, setuju
        }

        if(intent.getStringExtra(TAG_JENISTUJUAN) != null){
            jenisTujuan = getIntent().getStringExtra(TAG_JENISTUJUAN);
        }

        logger.d("PindahHalamanSuratMasuk", idSurat+"/"+idHistori
                +"/"+alurSurat+"/"+strArsip+"/"+jenisSurat
                +"/"+jenisArsip+"/"+jenisTujuan);

    }

    /**  Fungsi menghubungkan komponen di layout dengan activity**/
    public void setupView(){
        tanggal             = (TextView)findViewById(R.id.txt_tanggal);
        instansi            = (TextView)findViewById(R.id.txt_instansi);
        pengirim            = (TextView)findViewById(R.id.from);
        subjek              = (TextView)findViewById(R.id.subject);
        pesan               = (TextView)findViewById(R.id.message);
        t_nomor             = (TextView)findViewById(R.id.text_nomor);
        t_deskripsi         = (TextView)findViewById(R.id.text_deskripsi);
        pesan_atas          = (TextView)findViewById(R.id.pesan_atas); // untuk menampilkan ada info lampiran

        btn_telaah          = (LinearLayout)findViewById(R.id.btn_telaah);
        btn_riwayat         = (LinearLayout)findViewById(R.id.btn_riwayat);
        btn_lampiran        = (LinearLayout)findViewById(R.id.btn_lampiran);

        layoutPengirim      =  (RelativeLayout)findViewById(R.id.layout_pengirim);

        /** default tombol arsip  disembunyikan **/
        btn_arsip         = (LinearLayout)findViewById(R.id.btn_arsip);

        btn_telaah.setOnClickListener(this);
        btn_riwayat.setOnClickListener(this);
        btn_arsip.setOnClickListener(this);
        btn_lampiran.setOnClickListener(this);

        /** Bila  surat tersebut adalah tembusan maka tampilkan tombol arsip **/
        if(jenisSurat.equalsIgnoreCase(Tag.TAG_TEMBUSAN)){
            // btn_arsip.setVisibility(View.VISIBLE);
        }

        if(strArsip.equals(TAG_ARSIP)) {
            btn_riwayat.setVisibility(View.VISIBLE);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lampiran:
                lihatLampiran();
                break;

            case R.id.btn_telaah:
                /**  **/
                //Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(Tag.TAG_ALUR_SURAT, alurSurat ); // telaah, disposisi
                intent.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intent.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intent.putExtra(Tag.TAG_PEJABAT_TANDATANGAN, pejabatTandaTangan);
                intent.putExtra(Tag.TAG_JENISSURAT, jenisSurat); // tujuan, tembusan, setuju
                intent.putExtra(TAG_ARSIP, strArsip);
                intent.putExtra(TAG_JENISPENERIMA, jenisPenerima); // instansi, sekda, bupati, wakilbupati
                intent.putExtra(TAG_PESAN,  pesan.getText().toString());
                intent.putExtra(TAG_JENISTUJUAN, jenisTujuan);

                startActivity(intent);
                break;

            case R.id.btn_riwayat:
                /**  **/
                Intent intentRiwayat = new Intent(getApplicationContext(), HistoriActivity.class);
                intentRiwayat.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intentRiwayat.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intentRiwayat.putExtra(Tag.TAG_JENISSURAT, Tag.TAG_SURATMASUK );

                startActivity(intentRiwayat);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }



    /** Fungsi untuk mengambil informasi detail surat/konsep **/
    private void getDetail() {

        ApiInterface apiService =   ApiClient.getClient().create(ApiInterface.class);

        /**  **/
        Call<ResultDetail> call = apiService.getDetailKonsep(token, idSurat, idHistori);

        call.enqueue(new Callback<ResultDetail>() {
            @Override
            public void onResponse(Call<ResultDetail> call, Response<ResultDetail> response) {
                /**  **/
                Logger logger = new Logger();
                logger.w5("Hasil Detail", response);

                final ResultDetail result = response.body();

                /**  **/
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Detail detail = result.getDetail();
                       // String inisiator = detail.getNamaInisiator();


                        if (detail != null) {

                            pejabatTandaTangan = detail.getPejabatTtd();
                            jenisSurat = detail.getJenisSurat();

                            t_nomor.setText("Nomor : " + detail.getNomorSurat());

                            String stringTujuan = "";
                            String[] arrayTujuan = detail.getTujuan().split(",");

                            String strPesan =  "";
                            strPesan = detail.getPesan();

                            String strDeskripsi = "";
                            strDeskripsi = "Deskripsi :  "+detail.getDeskripsiSurat();

                            for(int i=0; i< arrayTujuan.length -1 ; i++){
                                stringTujuan += "\n\n"+String.valueOf(i+1)+". "+arrayTujuan[i].trim();
                            }

                            pesan.setText(strPesan);
                            /**  Menampilkan data dari activity sebelumnya **/
                            tanggal.setText(konversiTanggal(detail.getTimestamp()));
                            jenisPenerima = detail.getJenis_penerima();

                            String pejabatPenandaTangan = detail.getPejabatTtd().toLowerCase();

                            if(jenisArsip.equalsIgnoreCase(TAG_AJUAN)){
                                layoutPengirim.setVisibility(GONE);
                            } else if(jenisArsip.equalsIgnoreCase(TAG_KOREKSI)){
                                layoutPengirim.setVisibility(GONE);
                            }else if(jenisArsip.equalsIgnoreCase(TAG_SETUJU)){
                                layoutPengirim.setVisibility(GONE);
                            }else{
                                if(pejabatPenandaTangan.equalsIgnoreCase("asisten1") ||
                                        pejabatPenandaTangan.equalsIgnoreCase("asisten2") ||
                                        pejabatPenandaTangan.equalsIgnoreCase("asisten3")){
                                    pengirim.setText("Sekretariat Daerah");
                                }else if(pejabatPenandaTangan.equalsIgnoreCase("bupati") ||
                                        pejabatPenandaTangan.equalsIgnoreCase("wakilbupati")){
                                    pengirim.setText("Bupati Kebumen");
                                }else{
                                    pengirim.setText(detail.getUnitKerjaInisiator());
                                }
                            }

                            /** Fitur untuk membantu kasubbagtu dalam menyortir surat**/

                            String keteranganJenisPenerima ="";
                            if(jenisPenerima.equalsIgnoreCase("sekda"))
                                keteranganJenisPenerima =" untuk Sekda";
                            else if(jenisPenerima.equalsIgnoreCase("bupati"))
                                keteranganJenisPenerima =" untuk Bupati";
                            else if(jenisPenerima.equalsIgnoreCase("wakilbupati"))
                                keteranganJenisPenerima =" untuk Wakil Bupati";

                            subjek.setText(detail.getNamaSurat() + keteranganJenisPenerima);

                            /** Selesai **/

                            t_deskripsi.setText(strDeskripsi);

                            if(detail.getLampiran().length()>0){
                                strLampiran = detail.getLampiran();
                                btn_lampiran.setVisibility(View.VISIBLE);
                                showLampiran();
                            }
                        } else {
                            notifikasiDialog.showDialogError(2,"");
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<ResultDetail> call, Throwable t) {
                notifikasiDialog.showDialogError(2,"");
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

    public void lihatLampiran(){
        /**  Dikirimkan ke Lampiran  Activity **/
        Intent intentLampiran = new Intent(getApplicationContext(), LampiranActivity.class);
        intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );

        /**  Pindah halaman **/
        startActivity(intentLampiran);
    }
}
