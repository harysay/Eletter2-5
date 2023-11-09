package id.go.kebumenkab.eletterkebumen.activity.pns;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TELAAH;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.LampiranCutiActivity;
import id.go.kebumenkab.eletterkebumen.adapter.pns.DetailItemKhususAdapter;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
//import id.go.kebumenkab.eletterkebumen.model.DataItemCuti;
import id.go.kebumenkab.eletterkebumen.model.AksiItemKhusus;
import id.go.kebumenkab.eletterkebumen.model.DataItemKonsepKhusus;
import id.go.kebumenkab.eletterkebumen.model.DetailItemKhusus;
import id.go.kebumenkab.eletterkebumen.model.KonsepKhususDetail;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKonsepKhusus extends AppBaseActivity implements View.OnClickListener{
    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_lihatcuti,btn_perubahan, btn_tandatangani,btn_tangguhkan,btn_lampiran,btn_tolakcuti;
    private TextView title2, title1, nameApp, alasancuti, pesan_atas;
    private TextView t_inisiator, t_nomor, t_tandatangan, t_tujuan, t_deskripsi, t_pesan;
    private String  idkonsepkhusus,idhistoricuti,namapemohoncuti,jabatanpemohon,unitkerjapemohon, masakerjapemohon,nohppemohon, jeniscuti,mulaicuit,selesaicuti, alamatcuti,lamaharicuti ;
    private String  jenisSurat;
    private String  strPesan;
    private String  strUrlFile="";
    private String  strLampiran;

    private String  strArsip="";
    private PrefManager prefManager;
    private String token;
    private ProgressDialog pDialog;

    private String ditandai ="";
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;
    /** Mendeklarasikan RecyclerView dan Adapter **/
    RecyclerView detailRecyclerView;
    DetailItemKhususAdapter detailAdapter;
    ArrayList<DetailItemKhusus> detailItems = new ArrayList<>();
    List<AksiItemKhusus> aksiItemKhususes;
    KonsepKhususDetail konsepsKhusus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_konsepkhusus);
        registerBaseActivityReceiver();

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DetailKonsepKhusus.this);

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
        DataItemKonsepKhusus konsepcuti = (DataItemKonsepKhusus)intent.getSerializableExtra("object");
        idkonsepkhusus = konsepcuti.getId();
        namapemohoncuti = konsepcuti.getTitle();
        logger.d("PindahHalamanKonsep", idkonsepkhusus+"/"+"/"+namapemohoncuti);


        /** Mengecek internet **/
        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /** Bila memiliki koneksi internet maka panggil api detail **/
        if(isConnected){
            getDetailItemKhusus();
        }else{
            notifikasiDialog.showDialogError(1, "");
        }

    }

    /**  Fungsi menghubungkan komponen di layout dengan activity**/
    public void setupView(){
        // Inisialisasi RecyclerView
        detailRecyclerView = findViewById(R.id.detailRecyclerViewKonsepKhusus);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailAdapter = new DetailItemKhususAdapter(detailItems);
        detailRecyclerView.setAdapter(detailAdapter);

        nameApp              = (TextView)findViewById(R.id.name_app);
        title1            = (TextView)findViewById(R.id.txt_title1);
        title2            = (TextView)findViewById(R.id.txt_title2);

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


        btn_lihatcuti.setOnClickListener(this);
        btn_tandatangani.setOnClickListener(this);
        btn_lampiran.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (aksiItemKhususes != null && !aksiItemKhususes.isEmpty()) {
            getMenuInflater().inflate(R.menu.aksi_menu_konsepkhusus, menu);
            SubMenu subMenu = menu.findItem(R.id.menu_pilih_tindakan).getSubMenu();
            for (AksiItemKhusus aksi : aksiItemKhususes) {
                subMenu.add(0, aksi.getAksiId(), Menu.NONE, aksi.getLabel());
            }
        }
        return true;

//        getMenuInflater().inflate(R.menu.aksi_menu_konsepkhusus, menu);
//        if (aksiItemKhususes != null && !aksiItemKhususes.isEmpty()){
//            // Menambahkan item menu sesuai dengan jumlah objek aksi
//            for (AksiItemKhusus aksi : aksiItemKhususes) {
//                MenuItem menuItem = menu.add(Menu.NONE, aksi.getAksiId(), Menu.NONE, aksi.getLabel());
//                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            }
//        }
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            /** Ketika ditekan tombol panah di pojok kiri atas maka tutup activity **/
            finish();
        }
        // Mendapatkan ID item menu yang diklik
        int id = item.getItemId();

        for (AksiItemKhusus aksi : aksiItemKhususes) {
            if (aksi.getAksiId() == id) {
                // Lakukan tindakan yang sesuai dengan ID aksi
//                Toast.makeText(this, "Aksi " + aksi.getAksiId() + " dipilih", Toast.LENGTH_SHORT).show();
                showDialogAksi(aksi.getAksiId(),aksi.getLabel());
                break;
            }
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
                Intent intent = new Intent(getApplicationContext(), WebViewKonsepKhususActivity.class);
                intent.putExtra("urlpreview", strUrlFile );
                intent.putExtra("idkonsepkhusus", idkonsepkhusus );
                /**  Pindah halaman **/;
                startActivity(intent);
                break;
            case R.id.btn_tandatangani:
                showDialogAksi(0,Tag.TAG_SETUJU);
                break;
            case R.id.btn_lampiran:
                lihatLampiran();
                break;
            default:
                break;
        }
    }

    public void showDialogAksi(final int idAksi, String stringStatus) {
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
        else {
            /**  untuk tombol perubahan **/
            edt.setVisibility(View.VISIBLE);
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            dialogBuilder.setMessage("Klik "+stringStatus+" untuk mengembalikan konsep");
            strTombolEksekusi = stringStatus;
        }
        dialogBuilder.setPositiveButton(strTombolEksekusi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pesan        = edt.getText().toString();
                String myPassphrase = editTextPassphrase.getText().toString();
                if(stringStatus.equals(TAG_SETUJU)){
                    if(myPassphrase.length()< 7){
                        notifikasiDialog.showDialogError(6, "");
                    }else{
                        postAksi(Integer.toString(idAksi),stringStatus, myPassphrase);
                    }
                }else if(stringStatus.equals(TAG_TELAAH)){
                    if(pesan.length()< 5){
                        notifikasiDialog.showDialogError(5, "");
                    }else{
                        postAksi(Integer.toString(idAksi),stringStatus, pesan);
                    }
                }
                else {
                    if(pesan.length()< 1){
                        notifikasiDialog.showDialogError(5, "");
                    }else{
                        postAksi(Integer.toString(idAksi),stringStatus, pesan);
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

    private void postAksi(final String idAksiKhusus,String statusKhusus, final String pesan) {
        if(statusKhusus.length()==0 || statusKhusus.isEmpty()){
            // notifikasiDialog.showToast("Status surat kosong, "+ statusSurat);
        }else {
            ApiInterface apiServiceCuti = ApiClient.getDomainCuti().create(ApiInterface.class);
            Call<ResponStandar> call = null;
            logger.d("status_surat", statusKhusus+"/"+pesan+"/");

            /** Membedakan API  berdasarakan inputkan status surat / konsep **/

            if (statusKhusus.equalsIgnoreCase(Tag.TAG_SETUJU)) {
                call = apiServiceCuti.sendTandaTanganiKonsepKhusus(token, idkonsepkhusus, pesan);
            }
            else {
                // notifikasiDialog.showToast( "Status surat: " + statusSurat);
                call = apiServiceCuti.sendAksiKonsepKhusus(token,idkonsepkhusus, idAksiKhusus, pesan);
            }

            if (call != null) {
                showDialog();
                logger.d("debug_eletter_call", idkonsepkhusus+"/"+idhistoricuti);
                call.enqueue(new Callback<ResponStandar>() {
                    @Override
                    public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {
                        ResponStandar data = response.body();
                        logger.w4("ResTandatanganiCuti ", response);
                        if(data!=null){
                            if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){
                                /** Hasil sukses **/
                                logger.d("debug_eletter", "proses "+ statusKhusus+" Berhasil");
                                // Tutup halaman dan buka halaman utama
                                logger.d("debug WebView", " "+ statusKhusus+" Selesai");
                                // Tutup halaman webview

                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                closeAllActivities();
                                startActivity(i);
                                notifikasiDialog.showDialogError(9, "");
                                // keluar
                            }else{
                                /** Tidak sukses **/
                                hideDialog();
                                notifikasiDialog.showDialogError(13, data.getPesan());
                            }
                        }else{
                            logger.d("debug_eletter", "data kosong");
                            notifikasiDialog.showDialogError(4, data.getPesan());
                            hideDialog();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponStandar> call, Throwable t) {
                        notifikasiDialog.showDialogError(10, t.getLocalizedMessage());
                        logger.d("debug_eletter", "failure " + t.getMessage());
                        hideDialog();
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

    private void getDetailItemKhusus(){
        ApiInterface apiServiceKonsepKhusus = ApiClient.getDomainCuti().create(ApiInterface.class);
        Call<KonsepKhususDetail> callkonsepkhusus = apiServiceKonsepKhusus.getKonsepKhususDetail(token,idkonsepkhusus);
        callkonsepkhusus.enqueue(new Callback<KonsepKhususDetail>() {
            @Override
            public void onResponse(Call<KonsepKhususDetail> call, Response<KonsepKhususDetail> response) {
                final KonsepKhususDetail result = response.body();
                logger.d("hasil", response.toString());
                if (result != null) {
                    konsepsKhusus = result;
                    List<DetailItemKhusus> todos = result.getDatadetailkhusus().getDetail();
                    nameApp.setText(result.getDatadetailkhusus().getAppName());
                    title1.setText(result.getDatadetailkhusus().getTitle());
                    title2.setText(result.getDatadetailkhusus().getTitle2());
                    if(result.getDatadetailkhusus().getPreviewLampiran()!=null){
                        Log.d("Lampiran", result.getDatadetailkhusus().getPreviewLampiran());
                        strLampiran = result.getDatadetailkhusus().getPreviewLampiran().toString();
                        showLampiran();
                        btn_lampiran.setVisibility(View.VISIBLE);
                    }
                    strUrlFile = result.getDatadetailkhusus().getPreviewUrl();
                    for (DetailItemKhusus konsep : todos) {
                        logger.d("hasil2", konsep.getLabel() + "/" + konsep.getValue());
                        detailItems.add(konsep);
                    }
                    detailAdapter.notifyDataSetChanged();
                    aksiItemKhususes = result.getDatadetailkhusus().getAksi();
                    invalidateOptionsMenu();

                }else {
                    notifikasiDialog.showDialogError(2, "");
                }
            }

            @Override
            public void onFailure(Call<KonsepKhususDetail> call, Throwable t) {

            }
        });
    }

//    private String getJenis(String lengkap){
//        // Mencari teks di dalam kurung [ ]
//        Pattern pattern1 = Pattern.compile("\\[(.*?)\\]");
//        Matcher matcher1 = pattern1.matcher(lengkap);
//        String textInSquareBrackets="";
//        while (matcher1.find()) {
//            textInSquareBrackets = matcher1.group(1);
//        }
//        return textInSquareBrackets;
//    }
//    private String getNama(String lengkap){
//        Pattern pattern = Pattern.compile("\\[(.*?)\\]\\s*(.*?)\\s*\\((.*?)\\)");
//        Matcher matcher = pattern.matcher(lengkap);
//        String textInSquareBrackets="",textInParentheses="",dateRange ="";
//        if (matcher.find()) {
////            textInSquareBrackets = matcher.group(1); // Akan menghasilkan "Kalimat 1"
//            textInParentheses = matcher.group(2);   // Akan menghasilkan "Kalimat 2"
////            dateRange = matcher.group(3);           // Akan menghasilkan "Kalimat 3"
//        }
//        return textInParentheses;
//    }
//    private String getDate(String lengkap){
//        // Mencari teks di dalam kurung ( )
//        Pattern pattern2 = Pattern.compile("\\((.*?)\\)");
//        Matcher matcher2 = pattern2.matcher(lengkap);
//        String textInRoundBrackets="";
//        while (matcher2.find()) {
//            textInRoundBrackets = matcher2.group(1);
//        }
//        return textInRoundBrackets;
//    }


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
