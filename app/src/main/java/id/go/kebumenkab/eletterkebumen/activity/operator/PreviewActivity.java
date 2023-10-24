package id.go.kebumenkab.eletterkebumen.activity.operator;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.operator.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.operator.ApiInterface;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_APLIKASI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PATH;
import static id.go.kebumenkab.eletterkebumen.network.operator.ApiClient.ELETTER_PDF;
import static id.go.kebumenkab.eletterkebumen.network.operator.ApiClient.ELETTER_PDFJS;

public class PreviewActivity extends AppBaseActivity
        implements EasyPermissions.PermissionCallbacks  {


    private String idSurat;
    private String token;

    private String URL_PREVIEW;

    private WebView webview;
    private PrefManager prefManager;

    private Menu myMenu;
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private Button reload;
    private ProgressBar progressBar;
    private TextView tvMessage;

    private Intent intent;

    private boolean isConnected;
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private String urlPathIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger              = new Logger();
        logger.d("LIFECYCLE", "onCreate");

        prefManager         = new PrefManager(this);

        logger.d("Jabatan", prefManager.getStatusJabatan());
        token               = prefManager.getSessionToken();
        notifikasiDialog    = new NotifikasiDialog(getApplicationContext(), PreviewActivity.this);

        isConnected         = NetworkUtil.cekInternet(getApplicationContext());

        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_lihat));

        initViews();

        registerBaseActivityReceiver();

    }

    private boolean hasExternalStoragePermission(){
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }



    private boolean cekInternet(){
        return NetworkUtil.cekInternet(getApplicationContext());
    }


    public void initViews(){
        logger.d("LIFECYCLE", "Init View");
        reload  = (Button)findViewById(R.id.reload);
        webview = (WebView)findViewById(R.id.webview);
        tvMessage = (TextView) findViewById(R.id.message);
        progressBar     = findViewById(R.id.progressBar);

        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //This will zoom out the WebView
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setInitialScale(1);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prosesPDF();
            }
        });

        showDialog();
    }


    private void cekIntent(){
        /*** Menerima parameter dari halaman sebelumnya untuk diproses pada pemanggilan API  **/

        logger.d("LIFECYCLE", "cek Intent" + getIntent().toString());
        logger.d("DataItemSuratMasuk", getIntent().getStringExtra(TAG_PATH));

        intent = getIntent();

        URL_PREVIEW = ELETTER_PDFJS;
        idSurat = intent.getStringExtra(Tag.TAG_ID_SURAT);
        urlPathIntent = intent.getStringExtra(TAG_PATH);

        prosesPDF();


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


    public void prosesPDF(){
        logger.d("LIFECYCLE", "proses PDF:"+ URL_PREVIEW+idSurat);

        PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
                webview, reload);
        pdfWebViewClient.loadPdfUrl(URL_PREVIEW+idSurat);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        logger.d("LIFECYCLE", "onPrepareOptionMenu");

        invalidateOptionsMenu();


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logger.d("LIFECYCLE", "onCreateOptions");
        getMenuInflater().inflate(R.menu.menu_action_telaah_operator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        // Telaah
        if (item.getItemId() == R.id.action_telaah) {
                showDialogAksi();
        }


        // Reload
        if (item.getItemId() == R.id.action_reload) {
            /**  **/
            if(isConnected)  {
                prosesPDF();
            } else{
                hideDialog(0, 1, "");
                webview.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
            }
        }

        // Download
        if (item.getItemId() == R.id.action_download) {

            if(hasExternalStoragePermission()){
                String downloadUrl =  ELETTER_PDF+idSurat;

                try {

                    Download(downloadUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }else{

                EasyPermissions.requestPermissions(this,
                        "Membutuhkan akses unduh ke storage",
                        EXTERNAL_STORAGE_PERMISSION_CODE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                // Ketika passphrase masih kurang dari 7 karakter
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

    private void postAksi(final String urlPath) {

            ApiInterface apiService =
                    ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);


        Call<ResponStandar> call = apiService.telaahKonsep (token,urlPath);

            if (call != null) {
                showDialog();

                call.enqueue(new Callback<ResponStandar>() {
                    @Override
                    public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                            ResponStandar data = response.body();
                            logger.w4("Respon gson ", response);
                            if(data!=null){
                                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                                    hideDialog(1, 0,"");
                                    /** Hasil sukses **/
                                    logger.d("debug_eletter", "proses Berhasil");
                                    notifikasiDialog.showDialogSukses(9, "", DashboardOperator.class);

                                }else{
                                    /** Tidak sukses **/
                                    if(data.getPesan().contains("telah")){
                                        hideDialog(0, 100, data.getPesan());
                                    }else {
                                        hideDialog(0, 100, data.getPesan());
                                    }
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


    public void showDialogAksi() {

        /**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setMessage(getString(R.string.message_dialog_telaah_operator));

        dialogBuilder.setPositiveButton("Telaah", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                postAksi(ApiClient.URL_TELAAH+urlPathIntent);
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

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }



    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        try {
            Download(ELETTER_PDF+idSurat);
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, PreviewActivity.this);
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

                new CountDownTimer(3000, 1000) {

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
}





