package id.go.kebumenkab.eletterkebumen.activity.pns;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISPENERIMA;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISTUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSILANGSUNG;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PERUBAHAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TANGGUHKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TELAAH;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TOLAK;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.HistoriActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranCutiActivity;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.DataItemCuti;
import id.go.kebumenkab.eletterkebumen.model.Detail;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKonsepCuti extends AppBaseActivity implements View.OnClickListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_lihatcuti,btn_perubahan, btn_tandatangani,btn_tangguhkan,btn_lampiran,btn_tolakcuti;

    private TextView tanggalcuti, instansi, pengirim, subjek, alasancuti, pesan_atas;
    private TextView t_inisiator, t_nomor, t_tandatangan, t_tujuan, t_deskripsi, t_pesan;

    private String  idcuti,idhistoricuti,namapemohoncuti,jabatanpemohon,unitkerjapemohon, masakerjapemohon,nohppemohon, jeniscuti,mulaicuit,selesaicuti, alamatcuti,lamaharicuti ;
//    private String  idSurat;
//    private String  idHistori;
//    private String  alurSurat;
//    private String  pejabatTandaTangan;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cuti);
        registerBaseActivityReceiver();

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DetailKonsepCuti.this);

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
        DataItemCuti konsepcuti = (DataItemCuti)intent.getSerializableExtra("object");
        idcuti = konsepcuti.getCutiId();
        idhistoricuti = konsepcuti.getHistoryId();
        namapemohoncuti = konsepcuti.getPemohon().getNama();

        logger.d("PindahHalamanKonsep", idcuti+"/"+idhistoricuti+"/"+namapemohoncuti);

        /** Mengecek internet **/
        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /** Bila memiliki koneksi internet maka panggil api detail **/
        if(isConnected){
            getDetailCuti();
        }else{
            notifikasiDialog.showDialogError(1, "");
        }

    }

    /**  Fungsi menghubungkan komponen di layout dengan activity**/
    public void setupView(){
        tanggalcuti             = (TextView)findViewById(R.id.txt_tanggal);
        instansi            = (TextView)findViewById(R.id.txt_instansi);
        pengirim            = (TextView)findViewById(R.id.from);
        subjek              = (TextView)findViewById(R.id.subject);
        alasancuti               = (TextView)findViewById(R.id.message);
        pesan_atas               = (TextView)findViewById(R.id.pesan_atas); // untuk menampilkan ada info lampiran

        t_inisiator         = (TextView)findViewById(R.id.text_inisiator);
        t_deskripsi         = (TextView)findViewById(R.id.text_deskripsi);
        t_nomor             = (TextView)findViewById(R.id.text_nomor);
        t_tandatangan       = (TextView)findViewById(R.id.text_tandatangan);
        t_tujuan            = (TextView)findViewById(R.id.text_tujuan);

        btn_lampiran        = (LinearLayout)findViewById(R.id.btn_lampiran);
        btn_lihatcuti       = (LinearLayout)findViewById(R.id.btn_lihatcuti);
        btn_tandatangani    = (LinearLayout)findViewById(R.id.btn_tandatangani);

        btn_perubahan    = (LinearLayout)findViewById(R.id.btn_perubahan);
        btn_tangguhkan      = (LinearLayout)findViewById(R.id.btn_tangguhkan);
        btn_tolakcuti       = (LinearLayout)findViewById(R.id.btn_tolakcuti);

        btn_lihatcuti.setOnClickListener(this);
        btn_tandatangani.setOnClickListener(this);
        btn_lampiran.setOnClickListener(this);

        btn_perubahan.setOnClickListener(this);
        btn_tangguhkan.setOnClickListener(this);
        btn_tolakcuti.setOnClickListener(this);
        //penambahan syarat untuk bupati, wakil dan sekda tidak ada menu koreksi
        if(prefManager.getStatusJabatan().equalsIgnoreCase("bupati")||prefManager.getStatusJabatan().equalsIgnoreCase("wakilbupati")||prefManager.getStatusJabatan().equalsIgnoreCase("sekda")){
            btn_tolakcuti.setVisibility(View.GONE);
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
            case R.id.btn_lihatcuti:
                /**  Dikirimkan ke WebView  Activity **/
                Intent intent = new Intent(getApplicationContext(), WebViewCutiActivity.class);
                intent.putExtra(Tag.TAG_CUTI_ID, idcuti );
                /**  Pindah halaman **/;
                startActivity(intent);
                break;
            case R.id.btn_tandatangani:
                showDialogAksi(Tag.TAG_SETUJU);
                break;
            case R.id.btn_lampiran:
//                /**  Dikirimkan ke WebView  Activity **/
//                Intent intentLampiran = new Intent(getApplicationContext(), LampiranActivity.class);
//                intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );
//
//                /**  Pindah halaman **/
//                startActivity(intentLampiran);
                lihatLampiran();
                break;
            case R.id.btn_perubahan:
                showDialogAksi(Tag.TAG_PERUBAHAN);
                break;
            case R.id.btn_tangguhkan:
                showDialogAksi(Tag.TAG_TANGGUHKAN);
                break;
            case R.id.btn_tolakcuti:
                showDialogAksi(Tag.TAG_TOLAK);
                break;
            default:
                break;
        }
    }


    public void showDialogAksi(final String stringStatus) {
        /**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **/
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_message, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(stringStatus.toUpperCase());
        // Koreksi
        final EditText edt = dialogView.findViewById(R.id.edit);
        edt.setSelection(edt.getText().length());
        // Passphrase
        final TextInputLayout textInputLayout = dialogView.findViewById(R.id.passphrase);
        final EditText editTextPassphrase = dialogView.findViewById(R.id.edit_passphrase);
        String strTombolEksekusi = "Kirim";

        /** Konfirmasi ajuan tidak perlu kolom pesan **/
        if(stringStatus.equalsIgnoreCase(Tag.TAG_AJUAN)) {
            /**  untuk tombol ajuan **/
            edt.setVisibility(View.VISIBLE);
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            dialogBuilder.setMessage(getString(R.string.message_dialog_ajukan));
            strTombolEksekusi = "Ajukan";

        }else if(stringStatus.equalsIgnoreCase(TAG_SETUJU)) {
            /**  sembunyikan inputan **/
            editTextPassphrase.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
            strTombolEksekusi = "TANDA TANGANI";
            dialogBuilder.setMessage(getString(R.string.message_dialog_setujui_kirim));
        }
        else if(stringStatus.equalsIgnoreCase(TAG_PERUBAHAN)) {
            /**  untuk tombol perubahan **/
            edt.setVisibility(View.VISIBLE);
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            dialogBuilder.setMessage(getString(R.string.message_dialog_perubahancuti));
            strTombolEksekusi = "Perubahan";
        }
        else if(stringStatus.equalsIgnoreCase(TAG_TANGGUHKAN)) {
            /**  untuk tombol tangguhkan **/
            edt.setVisibility(View.VISIBLE);
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            dialogBuilder.setMessage(getString(R.string.message_dialog_tangguhkancuti));
            strTombolEksekusi = "Tangguhkan";
        }
        else if(stringStatus.equalsIgnoreCase(TAG_TOLAK)) {
            /**  untuk tombol tolak **/
            edt.setVisibility(View.VISIBLE);
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            dialogBuilder.setMessage(getString(R.string.message_dialog_tolakcuti));
            strTombolEksekusi = "Tolak";
        }
        dialogBuilder.setPositiveButton(strTombolEksekusi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pesan        = edt.getText().toString();
                String myPassphrase = editTextPassphrase.getText().toString();
                if(stringStatus.equals(TAG_SETUJU)){
                    if(myPassphrase.length()< 7){
                        notifikasiDialog.showDialogError(6, "");
                    }else{
                        postAksi(stringStatus, myPassphrase);
                    }
                }else if(stringStatus.equals(TAG_TELAAH)){
                    if(pesan.length()< 5){
                        notifikasiDialog.showDialogError(5, "");
                    }else{
                        postAksi(stringStatus, pesan);
                    }
                }
                else if(stringStatus.equals(TAG_PERUBAHAN)){
                    if(pesan.length()< 1){
                        notifikasiDialog.showDialogError(5, "");
                    }else{
                        postAksi(stringStatus, pesan);
                    }
                }
                else if(stringStatus.equals(TAG_TANGGUHKAN)){
                    if(pesan.length()< 1){
                        notifikasiDialog.showDialogError(5, "");
                    }else{
                        postAksi(stringStatus, pesan);
                    }
                }else if(stringStatus.equals(TAG_TOLAK)){
                    if(pesan.length()< 1){
                        notifikasiDialog.showDialogError(5, "");
                    }else{
                        postAksi(stringStatus, pesan);
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /** Tutup dialog **/
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void postAksi(final String statusCuti, final String pesan) {

        if(statusCuti.length()==0 || statusCuti.isEmpty()){
            // notifikasiDialog.showToast("Status surat kosong, "+ statusSurat);
        }else {
            ApiInterface apiServiceCuti = ApiClient.getDomainCuti().create(ApiInterface.class);
            Call<ResponStandar> call = null;
            logger.d("status_surat", statusCuti+"/"+pesan+"/");

            /** Membedakan API  berdasarakan inputkan status surat / konsep **/

            if (statusCuti.equalsIgnoreCase(Tag.TAG_SETUJU)) {

                call = apiServiceCuti.sendSetujuCuti(token, idcuti, idhistoricuti, pesan);

            } else if (statusCuti.equalsIgnoreCase(TAG_PERUBAHAN)) {

              call = apiServiceCuti.sendAksiCuti(token,"perubahan", idcuti, idhistoricuti, pesan); //4=perubahan
            }else if (statusCuti.equalsIgnoreCase(TAG_TANGGUHKAN)) {

                call = apiServiceCuti.sendAksiCuti(token,"ditangguhkan", idcuti, idhistoricuti, pesan); //5=ditangguhkan
            }else if (statusCuti.equalsIgnoreCase(TAG_TOLAK)) {

                call = apiServiceCuti.sendAksiCuti(token,"ditolak", idcuti, idhistoricuti, pesan); //6=ditolak
            }
            else {
                // notifikasiDialog.showToast( "Status surat: " + statusSurat);
                return;
            }

            if (call != null) {
                showDialog();
                logger.d("debug_eletter_call", idcuti+"/"+idhistoricuti);
                call.enqueue(new Callback<ResponStandar>() {
                    @Override
                    public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {
                        ResponStandar data = response.body();
                        logger.w4("ResTandatanganiCuti ", response);
                        if(data!=null){
                            if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){
                                /** Hasil sukses **/
                                logger.d("debug_eletter", "proses "+ statusCuti+" Berhasil");
                                // Tutup halaman dan buka halaman utama
                                logger.d("debug WebView", " "+ statusCuti+" Selesai");
                                // Tutup halaman webview

                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                closeAllActivities();
                                startActivity(i);
                                notifikasiDialog.showDialogError(9, "");
                                // keluar
                            }else{
                                /** Tidak sukses **/
                                notifikasiDialog.showDialogError(10, data.getPesan());
                            }

                        }else{
                            logger.d("debug_eletter", "data kosong");
                            notifikasiDialog.showDialogError(4, data.getPesan());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponStandar> call, Throwable t) {
                        notifikasiDialog.showDialogError(10, t.getLocalizedMessage());
                        logger.d("debug_eletter", "failure " + t.getMessage());
                    }
                });

            }else{
                logger.d("debug_eletter", "call is null");
                notifikasiDialog.showDialogError(10, "Null Retrofit Builder");

            }
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

    private void getDetailCuti() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Intent intent = getIntent();
                DataItemCuti konsepcuti = (DataItemCuti)intent.getSerializableExtra("object");
//                idcuti = konsepcuti.getCutiId();
//                idhistoricuti = konsepcuti.getHistoryId();
//                namapemohoncuti = konsepcuti.getPemohon().getNama();


                if (konsepcuti != null) {

//                    pejabatTandaTangan = konsepcuti.getPemohon().getNama();
                    jenisSurat = konsepcuti.getCuti().getJenis();

                    t_inisiator.setText("Pemohon : " + konsepcuti.getPemohon().getNama());
                    t_nomor.setText("Jabatan : " + konsepcuti.getPemohon().getJabatan());
                    t_tandatangan.setText("Lama Cuti : " + konsepcuti.getCuti().getLamaHari()+" Hari");
                    t_deskripsi.setText("Telepon : " +konsepcuti.getPemohon().getTelp());

                    strPesan = konsepcuti.getCuti().getAlasan();
                    alasancuti.setText(strPesan);
                    t_tujuan.setText("Alamat cuti :"+ konsepcuti.getCuti().getAlamatCuti());

                    tanggalcuti.setText(konversiTanggal(konsepcuti.getCuti().getMulaiCuti())+"  s.d. "+ konversiTanggal(konsepcuti.getCuti().getSelesaiCuti()));
                    instansi.setText(konsepcuti.getPemohon().getUnitKerja());
                    pengirim.setText(konsepcuti.getPemohon().getNama());
                    subjek.setText(konsepcuti.getCuti().getJenis());


                    if(konsepcuti.getCuti().getLampiran()!=null){
                        Log.d("Lampiran", konsepcuti.getCuti().getLampiran());
                        strLampiran = konsepcuti.getCuti().getLampiran().toString();
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

    /**  Fungsi memparsing tanggal */
    public String konversiTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, d MMM yyyy", new Locale("id", "ID"));

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
        Intent intentLampiran = new Intent(getApplicationContext(), LampiranCutiActivity.class);
        intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );
        /**  Pindah halaman **/
        startActivity(intentLampiran);
    }

    protected void closeAllActivities(){
        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }

}
