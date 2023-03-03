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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.activity.HistoriActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Detail;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.model.SuratMasuk;
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
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PESAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;

public class DetailSuratMasuk extends AppCompatActivity implements View.OnClickListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_telaah;
    private LinearLayout btn_riwayat;
    private LinearLayout btn_arsip;
    private LinearLayout btn_lampiran;


    private TextView tanggal, instansi, pengirim, subjek, pesan;
    private TextView t_nomor, t_pesan, t_deskripsi;

    private String  idSurat;
    private String  idHistori;
    private String  alurSurat;
    private String  pejabatTandaTangan;
    private String  jenisSurat;
    private String  strArsip;
    private String  strLampiran;
    private String  jenisArsip = "";

    private PrefManager prefManager;
    private String token;
    private ProgressDialog pDialog;
    private RelativeLayout layoutPengirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_suratmasuk);

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

        /**  Menerima data dari activity lain
         *  seperti dari ArsipActivity, SuratMasukFragment **/
        Intent intent = getIntent();
        SuratMasuk suratMasuk = (SuratMasuk)intent.getSerializableExtra("object");
        idSurat         = suratMasuk.getIdSurat();
        idHistori       = suratMasuk.getIdHistory();
        alurSurat       = suratMasuk.getStatus(); // telaah,  disposisi
        jenisSurat      = suratMasuk.getJenisTujuan(); // tujuan , tembusan

        strArsip        = intent.getStringExtra(TAG_ARSIP);

        if(getIntent().getStringExtra(TAG_JENISARSIP)!= null){
            jenisArsip = getIntent().getStringExtra(TAG_JENISARSIP); // untuk deteksi arsip ajuan, koreksi, setuju
        }

        /**  Inisiasi komponen tampilan **/
        setupView();

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
        t_nomor             = (TextView)findViewById(R.id.text_nomor);
        t_deskripsi         = (TextView)findViewById(R.id.text_deskripsi);

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
            btn_arsip.setVisibility(View.VISIBLE);
        }

        if(strArsip.equals(TAG_ARSIP)) {
            btn_riwayat.setVisibility(GONE);
        }


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
            case R.id.btn_lampiran:
                /**  Dikirimkan ke Lampiran  Activity **/
                Intent intentLampiran = new Intent(getApplicationContext(), LampiranActivity.class);
                intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );

                /**  Pindah halaman **/
                startActivity(intentLampiran);
                break;

            case R.id.btn_telaah:
                /**  **/
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(Tag.TAG_ALUR_SURAT, alurSurat ); // telaah, disposisi
                intent.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intent.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intent.putExtra(Tag.TAG_PEJABAT_TANDATANGAN, pejabatTandaTangan);
                intent.putExtra(Tag.TAG_JENISSURAT, jenisSurat); // tujuan, tembusan, setuju
                intent.putExtra(TAG_ARSIP, strArsip);
                intent.putExtra(TAG_PESAN,  pesan.getText().toString());

                /**  **/
                Logger logger = new Logger();
                logger.d("JENIS SURAT", jenisSurat);
                logger.d("ARSIP", strArsip);
                logger.d("ALUR SURAT", alurSurat);

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

            case R.id.btn_arsip:
                /**  **/
                    showDialogArsip();
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

                            String pejabatPenandaTangan = detail.getPejabatTtd().toLowerCase();

                            if(jenisArsip.equalsIgnoreCase(TAG_AJUAN)){
                                layoutPengirim.setVisibility(GONE);
                            } else if(jenisArsip.equalsIgnoreCase(TAG_KOREKSI)){
                                layoutPengirim.setVisibility(GONE);
                            }else if(jenisArsip.equalsIgnoreCase(TAG_SETUJU)){
                                layoutPengirim.setVisibility(GONE);
                                //
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




                            subjek.setText(detail.getNamaSurat());
                            t_deskripsi.setText(strDeskripsi);

                            if(detail.getLampiran().length()>0){
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
            }
        });

        builder.show();
    }


    public void showDialogArsip() {

        /**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_message, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(getString(R.string.action_arsip).toUpperCase());

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit);

        /** Konfirmasi arsipkan tidak perlu kolom pesan **/
            /**  untuk tombol ajuan **/
            edt.setVisibility(GONE);
            dialogBuilder.setMessage(getString(R.string.message_dialog_arsipkan));


        dialogBuilder.setPositiveButton(getString(R.string.action_arsip), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pesan = edt.getText().toString();

                postArsip();

            }
        });

        dialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /** Tutup dialog **/
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void postArsip() {

        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());
            ApiInterface apiService =
                    ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

            Call<ResponStandar> call = apiService.sendArsipkan(token, idSurat, idHistori);

            if (call != null) {
                showDialog();

                call.enqueue(new Callback<ResponStandar>() {
                    @Override
                    public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                        ResponStandar data = response.body();
                        Log.d("debug", "pesan "+ data.getPesan());

                        hideDialog();

                        if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){
                            /** Hasil sukses **/
                                // Tutup halaman dan buka halaman utama
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                finish();

                        }else{
                            /** Tidak sukses **/
                            showDialogError(2);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponStandar> call, Throwable t) {
                        hideDialog();
                        showDialogError(2);

                    }
                });

            }
        }



}
