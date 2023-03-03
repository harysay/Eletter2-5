package id.go.kebumenkab.eletterkebumen.activity.desa;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.CheckForSDCard;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.KonsepDesa;
import id.go.kebumenkab.eletterkebumen.model.ResponData;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResponUrl;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_APLIKASI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_BERSIHKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TANDAI;

public class WebViewDesaLurahActivity extends AppBaseActivity implements EasyPermissions.PermissionCallbacks  {

    private String idSurat;
    private String token;
    private boolean isArsip = false;
    private String alurSurat;

    private String URL_PREVIEW;

    private WebView webview;
    private PrefManager prefManager;

    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private static final int WRITE_REQUEST_CODE = 300;
    private static final int READ_REQUEST_CODE = 200;

    private Button reload;
    private ProgressBar progressBar;
    private TextView tvMessage;

    private Intent intent;

    private String urlFile;

    private boolean isConnected;
    private KonsepDesa konsepDesa;
    private ArrayList<KonsepDesa> listKonsepDesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger              = new Logger();
        logger.d("LIFECYCLE", "onCreateWebViewDesa");

        prefManager         = new PrefManager(this);

        token               = Config.AUTHORIZATION;
        notifikasiDialog    = new NotifikasiDialog(getApplicationContext(), WebViewDesaLurahActivity.this);

        setContentView(R.layout.activity_web_view_desalurah);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_lihat));

        registerBaseActivityReceiver();

        initViews();

        if(cekInternet()){
            cekIntent();
        }else{
            hideDialog(0, 1, "");
            webview.setVisibility(View.GONE);
            reload.setVisibility(View.VISIBLE);
        }
    }

    private boolean cekInternet(){
        return NetworkUtil.cekInternet(getApplicationContext());
    }

    public void initViews(){
        logger.d("LIFECYCLE", "WebViewDesaLurah");
        reload          = (Button)findViewById(R.id.reload);
        webview         = (WebView)findViewById(R.id.webview);
        tvMessage       = (TextView) findViewById(R.id.message);
        progressBar     = findViewById(R.id.progressBar);

        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.getSettings().setUseWideViewPort(true);
        webview.setInitialScale(1);
//        webview.getSettings().setLoadWithOverviewMode(true);


        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cekInternet()){
                    cekIntent();
                }else{
                    hideDialog(0, 1, "");
                    webview.setVisibility(View.GONE);
                    reload.setVisibility(View.VISIBLE);
                }
            }
        });
    }

   private void cekIntent(){
        /*** Menerima parameter dari halaman sebelumnya untuk diproses pada pemanggilan API  **/

        logger.d("LIFECYCLE", "cek Intent");

           intent = getIntent();
           if(getIntent().hasExtra(TAG_ARSIP)){
               //  Mengambil PDF yang sudah ditandatangani
               if (getIntent().getStringExtra(TAG_ARSIP) == null) return;
               isArsip = true;
               idSurat = getIntent().getStringExtra(TAG_ID_SURAT);

               alurSurat = TAG_ARSIP;
               postAksi(alurSurat, idSurat, prefManager.getIdPerangkat(),"", "");

           }else{

               if(getIntent().hasExtra("WEB")){
                   // Menampilkan halaman website desa
                   loadWeb(prefManager.getUrlDesa());
                   getSupportActionBar().setTitle(getString(R.string.action_browser));

               }else{
                   // Mengambil data Preview konsep
                   idSurat = getIntent().getStringExtra(TAG_ID_SURAT);
                   alurSurat = TAG_AJUAN;

                   postAksi(alurSurat, idSurat, prefManager.getIdPerangkat(),"", "");

               }

           }

    }


    @Override
    protected void onResume() {
        super.onResume();
        logger.d("LIFECYCLE", "onResume");
        if(cekInternet()){
            cekIntent();
        }else{
            hideDialog(0, 1, "");
            webview.setVisibility(View.GONE);
            reload.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Mengambil file PDF
     */
    public void loadPDF(String url){

        logger.d("LIFECYCLE", "loadPDF");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideDialog(1, 0, "");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideDialog(0, 100, error.toString());
            }
        });

        // url = "https://development.kebumenkab.go.id/sitektonik/assets/public/30122019_1613359891_423.pdf";

        webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);

    }

    public void loadWeb(String url){
        showDialog();

        logger.d("LIFECYCLE", "load Web");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideDialog(1, 0, "");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideDialog(0, 2, error.toString());
            }
        });

        webview.loadUrl("https://"+ prefManager.getUrlDesa());

    }



    public void loadHTML(String htmlString){
        webview.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);
    }

    /**
     * Mengolah menu yang muncul di sebelah kanan atas
     * akan mengecek status surat yang akan dieksekusi
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        logger.d("LIFECYCLE", "onPrepareOptionMenu");

        invalidateOptionsMenu();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logger.d("LIFECYCLE", "onCreateOptionsMenu");
        // Menu di pojok kanan atas
        // Cek true/false dalam array list konsep ditandai
        if(getIntentKonsepDesa()){
            getMenuInflater().inflate(R.menu.menu_action_kepala_desa_setuju_dua, menu);
        }else{
            // jika false, cek terlebih dahulu apakah ada intent arsip
            if(getIntent().hasExtra(TAG_ARSIP)) {
                //  jika arsip
                getMenuInflater().inflate(R.menu.menu_action_download, menu);
            }else{
                // jika bukan arsip dan belum ditandai
                getMenuInflater().inflate(R.menu.menu_action_kepala_desa_setuju, menu);
            }

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pilihan menu atas
        // Kembali
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        // Tandai
        if (item.getItemId() == R.id.action_tandai) {
            showDialogAksi(Tag.TAG_TANDAI);
        }
        // Bersihkan Tanda
        if (item.getItemId() == R.id.action_bersihkan) {
            showDialogAksi(Tag.TAG_BERSIHKAN);
        }
        // Setuju
        if (item.getItemId() == R.id.action_setuju) {
            showDialogAksi(Tag.TAG_SETUJU);
        }
        // Koreksi
        if (item.getItemId() == R.id.action_koreksi) {
            showDialogAksi(Tag.TAG_KOREKSI);
        }
        // Reload
        if (item.getItemId() == R.id.action_reload) {
            /**  **/
            if(isConnected)  {
                cekIntent();
            } else{
                hideDialog(0, 1, "");
                webview.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
            }
        }

        // Unduh
        if (item.getItemId() == R.id.action_download) {

            if (CheckForSDCard.isSDCardPresent()) {

                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};

                //check if app has permission to write to the external storage.
                if (EasyPermissions.hasPermissions(WebViewDesaLurahActivity.this, perms)) {
                     try {
                        Download(urlFile);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else {

                    EasyPermissions.requestPermissions(WebViewDesaLurahActivity.this, getString(R.string.message_writefile), WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    EasyPermissions.requestPermissions(WebViewDesaLurahActivity.this, getString(R.string.message_writefile), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            } else {
                notifikasiDialog.showToast("SD Card not found");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {

        logger.d("LIFECYCLE", "showDialog");
        progressBar.setVisibility(View.VISIBLE);
        webview.setVisibility(View.INVISIBLE);
        reload.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    private void hideDialog(int pilihan, int pesan, String keterangan) {

        logger.d("LIFECYCLE", "hideDialog");
        progressBar.setVisibility(View.GONE);

        if(pilihan == 1){
            // Sukses
            webview.setVisibility(View.VISIBLE);
            reload.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
        }else{
            // tidak sukses
            webview.setVisibility(View.GONE);

            reload.setVisibility(View.VISIBLE);
            tvMessage.setVisibility(View.VISIBLE);

            String msg = "";
            if(pesan == 1){
                // periksa internet
                 msg = getString(R.string.error_network);
            }else if(pesan==2){
                // periksa server
                 msg = getString(R.string.error_api);
            }else if(pesan ==3 ){
                // data tidak ditemukan
                 msg = getString(R.string.error_empty);
            }else if(pesan ==4){
                // data tidak ditemukan
                msg = getString(R.string.error_empty);
            }else if(pesan ==5){
                msg = "Pesan kosong";
            }else if(pesan ==6){
                // Ketika passphrase masih kurang dari 8 karakter
                msg = getString(R.string.message_minimal_8_karakter);
            }else if(pesan==7){
                // dialog lupa password
                msg = getString(R.string.message_silakan_hub_bagian_organisasi);
            }else if(pesan ==8){
                msg = getString(R.string.error_empty_message_tindakan);
            }else if(pesan==9){
                msg = getString(R.string.message_success);
            }else if(pesan==10){
                msg = getString(R.string.message_failed);
            }else if(pesan==100){
                msg = keterangan;
            }

            tvMessage.setText(msg);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private void postAksi(final String request, final String idSurat, final String idPerangkat, final String passphrase, final String pesan) {
            ApiInterfaceDesa apiService =
                    ApiClientDesa.getClient(getApplicationContext()).create(ApiInterfaceDesa.class);

            if(request.equalsIgnoreCase(TAG_AJUAN)){
                // mendapatkan html preview konsep surat
                Call<ResponData> call = apiService.getPreviewKonsep(Config.AUTHORIZATION, idSurat);
                if (call != null) {

                    showDialog();

                    call.enqueue(new Callback<ResponData>() {
                        @Override
                        public void onResponse(Call<ResponData> call, Response<ResponData> response) {

                            ResponData data = response.body();
                            logger.d("debug_eletter", response.toString()   );
                            if(data!=null){
                                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                                    hideDialog(1, 0,"");
                                    /** Hasil sukses **/
                                    // Tutup halaman webview
                                    String htmlString =  data.getData();
                                    loadHTML(htmlString);


                                }else{
                                    hideDialog(0, 100, data.getData());
                                }

                            }else{
                                logger.d("debug_eletter", "data kosong");
                                hideDialog(0, 10, "");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponData> call, Throwable t) {
                            hideDialog(0, 100,  t.getLocalizedMessage());
                            logger.d("debug_eletter", "failure " + t.getMessage());
                        }
                    });

                }else{
                    logger.d("debug_eletter", "call is null");
                    hideDialog(0, 100, "Null Retrofit Builder");

                }
            }else if(request.equalsIgnoreCase(TAG_SETUJU)){
                showDialog();
                // mengirimkan permintaan tanda tangan
                Call<ResponStandar> call = apiService.sendSetuju(Config.AUTHORIZATION, idSurat, prefManager.getIdPerangkat(), prefManager.getNIK(), passphrase);

                logger.d("tanda_tangan", prefManager.getNIK()+"/"+idSurat);
                if (call != null) {

                    call.enqueue(new Callback<ResponStandar>() {
                        @Override
                        public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                            ResponStandar data = response.body();
                            if(data!=null){
                                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                                    hideDialog(1, 0,"");

                                    notifikasiDialog.showDialogError(11, "");

                                }else{
                                    hideDialog(0, 100, data.getPesan());
                                }

                            }else{
                                logger.d("debug_eletter", "data kosong");
                                hideDialog(0, 10, "");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponStandar> call, Throwable t) {
                            hideDialog(0, 100,  t.getLocalizedMessage());
                            logger.d("debug_eletter", "failure " + t.getMessage());
                        }
                    });

                }else{
                    logger.d("debug_eletter", "call is null");
                    hideDialog(0, 100, "Null Retrofit Builder");

                }
            }else if(request.equalsIgnoreCase(TAG_ARSIP)){
                Call<ResponUrl> call = apiService.getPreviewDitandatangani(Config.AUTHORIZATION, idSurat);
                if (call != null) {
                    showDialog();

                    call.enqueue(new Callback<ResponUrl>() {
                        @Override
                        public void onResponse(Call<ResponUrl> call, Response<ResponUrl> response) {

                            ResponUrl data = response.body();

                            if(data!=null){
                                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                                    hideDialog(1, 0,"");

                                    urlFile = data.getUrl();
                                    loadPDF(urlFile);


                                }else{
                                    hideDialog(0, 100, data.getUrl());
                                }

                            }else{
                                logger.d("debug_eletter", "data kosong");
                                hideDialog(0, 10, "");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponUrl> call, Throwable t) {
                            hideDialog(0, 100,  t.getLocalizedMessage());
                            logger.d("debug_eletter", "failure " + t.getMessage());
                        }
                    });

                }else{
                    logger.d("debug_eletter", "call is null");
                    hideDialog(0, 100, "Null Retrofit Builder");

                }
            }else if(request.equalsIgnoreCase(TAG_KOREKSI)){
                Call<ResponStandar> call = apiService.sendKoreksi(Config.AUTHORIZATION, idSurat, pesan);
                if (call != null) {
                    showDialog();

                    call.enqueue(new Callback<ResponStandar>() {
                        @Override
                        public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                            ResponStandar data = response.body();

                            if(data!=null){
                                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                                    notifikasiDialog.showDialogError(11, "");

                                }else{
                                    hideDialog(0, 100, data.getPesan());
                                }

                            }else{
                                logger.d("debug_eletter", "data kosong");
                                hideDialog(0, 10, "");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponStandar> call, Throwable t) {
                            hideDialog(0, 100,  t.getLocalizedMessage());
                            logger.d("debug_eletter", "failure " + t.getMessage());
                        }
                    });

                }else{
                    logger.d("debug_eletter", "call is null");
                    hideDialog(0, 100, "Null Retrofit Builder");

                }
            }



    }

    /** Persiapan Menu Tandai **/
    private boolean getIntentKonsepDesa() {
        if (getIntent().hasExtra(Tag.TAG_ID_SURAT)) {
            String id = getIntent().getStringExtra(Tag.TAG_ID_SURAT);
            PrefManager prefManager = new PrefManager(getApplicationContext());
            ArrayList<String> infoDitandai = prefManager.ambilDataKonsepList();
            logger.d("ditandai", infoDitandai.toString() + "/" + id);

            if (infoDitandai.contains(id)) {
                return true;
            } else {
                return false;
            }

        }
        return false;
    }



    public void showDialogAksi(final String stringStatus) {

        /**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_message, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(stringStatus.toUpperCase());

        // Koreksi
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit);
        edt.setInputType(InputType.TYPE_CLASS_TEXT);
        edt.setLines(3);
        edt.setSelection(edt.getText().length());

        // Passphrase
        final TextInputLayout textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.passphrase);
        final EditText editTextPassphrase = (EditText)dialogView.findViewById(R.id.edit_passphrase);

        String strTombolEksekusi = "Kirim";

        /** Konfirmasi ajuan tidak perlu kolom pesan **/
       if(stringStatus.equalsIgnoreCase(Tag.TAG_SETUJU)) {
            /**  sembunyikan inputan **/

            // Tampilkan
            editTextPassphrase.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);

            strTombolEksekusi = "TANDA TANGANI";

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_setujui_kirim));

        } else if(stringStatus.equalsIgnoreCase(TAG_KOREKSI)) {
            /**  sembunyikan inputan **/

            // tampilkan
            edt.setVisibility(View.VISIBLE);

            strTombolEksekusi = "LANJUTKAN";

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_koreksi));

        } else if (stringStatus.equalsIgnoreCase(TAG_TANDAI)){
           // tampilkan
           edt.setVisibility(View.GONE);
           strTombolEksekusi = "LANJUTKAN";

           /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
           dialogBuilder.setMessage(getString(R.string.message_dialog_tandai));

       }else if (stringStatus.equalsIgnoreCase(TAG_BERSIHKAN)){
           // bersihkan tanda
           edt.setVisibility(View.GONE);
           strTombolEksekusi = "LANJUTKAN";

           /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
           dialogBuilder.setMessage(getString(R.string.message_dialog_bersihkantanda));

       }

       dialogBuilder.setPositiveButton(strTombolEksekusi, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    String pesan = "";
                    // String untuk mengambil dari EditText atau Passphrase

                        if(stringStatus.equals(TAG_SETUJU)){

                            String passphrase = editTextPassphrase.getText().toString();
                            postAksi(TAG_SETUJU, idSurat, prefManager.getIdPerangkat(), passphrase, "");

                        }else if(stringStatus.equalsIgnoreCase(TAG_KOREKSI)){
                            pesan = " "+ edt.getText().toString();
                            postAksi(TAG_KOREKSI, idSurat, prefManager.getIdPerangkat(), "", pesan);

                        }else if(stringStatus.equalsIgnoreCase(TAG_TANDAI)){
                            prefManager.tambahkanDataKonsepList(idSurat);
                            finish();
                        }else if(stringStatus.equalsIgnoreCase(TAG_BERSIHKAN)){
                            prefManager.hilangkanDataKonsepList(idSurat);
                            finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted

        try {
            Download(urlFile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        logger.d(TAG_APLIKASI, "Permission has been denied");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, WebViewDesaLurahActivity.this);
    }

    private class PdfWebViewClient extends WebViewClient
    {
        private static final String TAG = "PdfWebViewClient";
        private static final String PDF_EXTENSION = ".pdf";
        private static final String PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url=";

        private Context mContext;
        private WebView mWebView;
        private boolean isLoadingPdfUrl;

        public PdfWebViewClient(Context context, WebView webView, Button reload)
        {
            mContext = context;
            mWebView = webView;
            mWebView.setWebViewClient(this);
        }

        public void loadPdfUrl(final String url)
        {
            showDialog();
            if (!TextUtils.isEmpty(url))
            {
                isLoadingPdfUrl = isPdfUrl(url);
                if (isLoadingPdfUrl)
                {
                    mWebView.clearHistory();

                }

                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // do something after 1s
                        showDialog();
                    }

                    @Override
                    public void onFinish() {
                        mWebView.loadUrl(url);
                    }

                }.start();

            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showDialog();
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url)
        {
            return shouldOverrideUrlLoading(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl)
        {
            handleError(errorCode, description.toString(), failingUrl);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request)
        {
            final Uri uri = request.getUrl();
            return shouldOverrideUrlLoading(webView, uri.toString());
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onReceivedError(final WebView webView, final WebResourceRequest request, final WebResourceError error)
        {
            final Uri uri = request.getUrl();
            handleError(error.getErrorCode(), error.getDescription().toString(), uri.toString());
        }

        @Override
        public void onPageFinished(final WebView view, final String url)
        {
            hideDialog(1, 0, "");
            Log.i(TAG, "Finished loading. URL : " + url);
        }

        private boolean shouldOverrideUrlLoading(final String url)
        {
            Log.i(TAG, "shouldOverrideUrlLoading() URL : " + url);

            if (isPdfUrl(url))
            {
                mWebView.stopLoading();

                final String pdfUrl = PDF_VIEWER_URL + url;
                loadPdfUrl(pdfUrl);

                return true;
            }

            return false; // Load url in the webView itself
        }

        private void handleError(final int errorCode, final String description, final String failingUrl)
        {
            hideDialog(0, 100, description);
            Log.e(TAG, "Error : " + errorCode + ", " + description + " URL : " + failingUrl);

        }

        private boolean isPdfUrl(String url)
        {
            if (!TextUtils.isEmpty(url))
            {
                url = url.trim();
                int lastIndex = url.toLowerCase().lastIndexOf(PDF_EXTENSION);
                if (lastIndex != -1)
                {
                    return url.substring(lastIndex).equalsIgnoreCase(PDF_EXTENSION);
                }
            }
            return false;
        }
    }

    private void Download(String... downloadUrl) throws MalformedURLException {

        String url ="";
        url = new String(downloadUrl[0]);
        //String fileName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())+".pdf";
        //Extract file name from URL
        try  {
             URL myUrl = new URL(downloadUrl[0]);

            String   fileName = downloadUrl[0].substring(downloadUrl[0].lastIndexOf('/') + 1, downloadUrl[0].length())+".pdf";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url + ""));
            request.setTitle(fileName);
            request.setMimeType("application/pdf");
            request.allowScanningByMediaScanner();
            request.setAllowedOverMetered(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "eletter/" + fileName);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





