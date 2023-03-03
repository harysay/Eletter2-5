package id.go.kebumenkab.eletterkebumen.activity.pns;

import androidx.appcompat.app.AppCompatActivity;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIPLAIN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_DISPOSISI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISTUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUAREKSTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUARINTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KERJAKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_MASUKEKSTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENGEMBALIAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PERMINTAANTANDATANGAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TELAAH;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TINDAKLANJUTI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_URL_PDF;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDFJS;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDF_KONSEP;

public class WebViewKoreksiActivity extends AppCompatActivity {
    private String alurSurat;
    private String idSurat;
    private String token;
    private String jenisSurat;
    private PrefManager prefManager;
    private NotifikasiDialog notifikasiDialog;
    private boolean isConnected;
    private Logger logger;
    private WebView webview;
    private Button reload;
    private ProgressBar progressBar;
    private TextView tvMessage;
    private Intent intent;
    private String URL_PREVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger              = new Logger();
        logger.d("LIFECYCLE", "onCreate");
        prefManager         = new PrefManager(this);

        logger.d("Jabatan", prefManager.getStatusJabatan());
        token               = prefManager.getSessionToken();
        notifikasiDialog    = new NotifikasiDialog(getApplicationContext(), WebViewKoreksiActivity.this);

        isConnected         = NetworkUtil.cekInternet(getApplicationContext());
        setContentView(R.layout.activity_web_view_koreksi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_lihat));

        initViews();
        cekIntent();
    }

    public void initViews(){
        logger.d("LIFECYCLE", "Init View");
        reload  = (Button)findViewById(R.id.reloadkoreksi);
        webview = (WebView)findViewById(R.id.webviewkoreksi);
        tvMessage = (TextView) findViewById(R.id.messageKoreksi);
        progressBar     = findViewById(R.id.progressBarKoreksi);

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
//                prosesPDF();
            }
        });

        showDialog();
    }

    private void showDialog() {

        logger.d("LIFECYCLE", "showDialog");
        progressBar.setVisibility(View.VISIBLE);

        webview.setVisibility(View.INVISIBLE);
        reload.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    private void cekIntent(){
        /*** Menerima parameter dari halaman sebelumnya untuk diproses pada pemanggilan API  **/

        logger.d("LIFECYCLE", "cek Intent" + getIntent().toString());

        intent = getIntent();

        if(getIntent().getStringExtra(Tag.TAG_ALUR_SURAT) != null){

            alurSurat       = getIntent().getStringExtra(Tag.TAG_ALUR_SURAT);

            // disposisi, telaah, ajuan, koreksi

            if(alurSurat.equalsIgnoreCase(TAG_KOREKSI)){
                URL_PREVIEW = ELETTER_KOREKSI;
            }

            logger.d("URL Preview", URL_PREVIEW);

        }else{
            alurSurat = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_ID_SURAT) != null){
            idSurat = getIntent().getStringExtra(Tag.TAG_ID_SURAT);
        }else{
            idSurat = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_JENISSURAT)!=null)
            jenisSurat = getIntent().getStringExtra(Tag.TAG_JENISSURAT);
        else jenisSurat = "";


        logger.d("PindahHalaman",
                alurSurat+"/" // disposisi, telaah, koreksi, ajuan
                        + idSurat +"/"
                        + jenisSurat); // ''

    }
    @Override
    protected void onResume() {
        super.onResume();
        logger.d("LIFECYCLE", "onResume");

        if(cekInternet()){
            prosesPDF();
        }else{
            hideDialog(0, 1, "");
            webview.setVisibility(View.GONE);
            reload.setVisibility(View.VISIBLE);
        }
    }

    private boolean cekInternet(){
        return NetworkUtil.cekInternet(getApplicationContext());
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

    public void prosesPDF(){
        logger.d("LIFECYCLE", "proses PDF");

        /** PEMBERITAHUAN **/
        if(jenisSurat.equalsIgnoreCase(TAG_PEMBERITAHUAN)){

            /** Untuk  mengubah agar status sudah dibaca **/
            jenisSurat      = TAG_PEMBERITAHUAN;

            // loadPDF(URL_PREVIEW+idSurat);
            logger.d("Handler - Alur surat dan PDF", alurSurat +"/"+URL_PREVIEW+idSurat);
            PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
                    webview, reload);
            pdfWebViewClient.loadPdfUrl(URL_PREVIEW+idSurat);
//                loadPDF(URL_PREVIEW+idSurat);

//            if(alurSurat.equalsIgnoreCase(TAG_PENGEMBALIAN)){
//                // tak perlu lakukan postDibac
//                if(getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN)!= null) {
//                    idPemberitahuan = getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN);
//                    logger.d("Alur surat dan PDF 1", alurSurat + "/" + jenisSurat + "/" + idSurat + "/" + idPemberitahuan);
//                }else{
//                    logger.d("Alur surat dan PDF 1", alurSurat + "/" + jenisSurat + "/" + idSurat + "/" + idPemberitahuan);
//                }
//            }else{
//                if(getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN)!= null){
//                    idPemberitahuan = getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN);
//                    logger.d("Alur surat dan PDF 2", alurSurat +"/"+jenisSurat+"/"+idPemberitahuan);
//                    postDibaca(idPemberitahuan, 0);
//                }else{
//                    hideDialog(0, 100, "Tidak Ada ID Pemberitahuan");
//                }
//            }


            /** KELUAR EKSTERNAL **/
        } else {
            /** **/
//            if(isConnected){
//                loadPDF(URL_PREVIEW+idSurat);
//            }else{
//                hideDialog(0,1,"");
//            }
        }

        // hideDialog();

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