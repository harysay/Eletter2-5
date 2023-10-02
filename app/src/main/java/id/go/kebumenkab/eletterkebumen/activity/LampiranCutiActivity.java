package id.go.kebumenkab.eletterkebumen.activity;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_LAMPIRAN;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDFJS_LAMPIRAN;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import pub.devrel.easypermissions.EasyPermissions;

public class LampiranCutiActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private WebView webview;
    private Button reload;
    private ProgressBar progressBar;
    private TextView tvMessage;

    private String urlFileSurat;
    private static final int WRITE_REQUEST_CODE = 300;

    private static final int READ_REQUEST_CODE = 200;
    private  boolean isConnected;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger              = new Logger();
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_lampiran));

        reload  = (Button)findViewById(R.id.reload);
        webview = (WebView)findViewById(R.id.webview);
        tvMessage = (TextView) findViewById(R.id.message);
        progressBar     = findViewById(R.id.progressBar);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prosesPDF();
            }
        });

        webview = (WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //This will zoom out the WebView
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setInitialScale(1);

        /*** Menerima parameter dari halaman sebelumnya untuk diproses pada pemanggilan API  **/
        Intent intent   = getIntent();
        /** Untuk  nama file lampiran **/
        urlFileSurat  = intent.getStringExtra(TAG_LAMPIRAN);
        Log.d("Lampiran", urlFileSurat);

    }

    protected void prosesPDF(){
        isConnected = NetworkUtil.cekInternet(getApplicationContext());

        if(isConnected){
             String url = "https://docs.google.com/gview?embedded=true&url="+ urlFileSurat;
//            String url = "https://eletter.kebumenkab.go.id/index.php/viewer/lampiran/"+namaFileSurat;
//            String url = urlFileSurat;

            PdfWebViewClientLampiran pdfWebViewClient = new PdfWebViewClientLampiran(getApplicationContext(), webview);
            pdfWebViewClient.loadPdfUrl(url);

        }else{
            showDialogError(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prosesPDF();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        // Reload
        if (item.getItemId() == R.id.action_reload) {
            /**  **/
            prosesPDF();
        }


        if (item.getItemId() == R.id.action_download) {
//             String downloadUrl =  ApiClient.DOMAIN +"/lampiran/"+namaFileSurat;

            try {
                Download(urlFileSurat);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            /*if (CheckForSDCard.isSDCardPresent()) {

                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


                //check if app has permission to write to the external storage.
                if (EasyPermissions.hasPermissions(LampiranActivity.this, perms)) {
                    //Get the URL entered
                    new DownloadFile().execute(ApiClient.DOMAIN +"/lampiran/"+namaFileSurat);
)
                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(LampiranActivity.this, getString(R.string.message_writefile), WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    EasyPermissions.requestPermissions(LampiranActivity.this, getString(R.string.message_writefile), WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "SD Card not found", Toast.LENGTH_LONG).show();
            }*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_arsip, menu);
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void showDialog() {
        progressBar.setVisibility(View.VISIBLE);

        webview.setVisibility(View.INVISIBLE);
        reload.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    private void hideDialog(int pilihan, int pesan, String keterangan) {
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



    public void viewPDF(String fileName)
    {

        final ProgressDialog progressDialog = new ProgressDialog(LampiranCutiActivity.this);
        progressDialog.setMessage("Sedang membuka file unduhan");
        progressDialog.show();

        File pdfFile = new File(fileName);
        // Uri path = Uri.fromFile(pdfFile);

        Uri pathURI = FileProvider.getUriForFile(LampiranCutiActivity.this,
                getApplicationContext()
                        .getPackageName()+".provider",
                pdfFile);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(pathURI, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try{
            startActivity(pdfIntent);
            progressDialog.dismiss();
        }catch(ActivityNotFoundException e){
            Toast.makeText(LampiranCutiActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
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
        }else if(i==3){
            dialogView = inflater.inflate(R.layout.dialog_warning_empty, null);
        }
        builder.setView(dialogView);
        builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    class PdfWebViewClientLampiran extends WebViewClient
    {
        private static final String TAG = "PdfWebViewClient";
        private static final String PDF_EXTENSION = ".pdf";
        private static final String PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url=";

        private Context mContext;
        private WebView mWebView;
        private boolean isLoadingPdfUrl;

        public PdfWebViewClientLampiran(Context context, WebView webView)
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



