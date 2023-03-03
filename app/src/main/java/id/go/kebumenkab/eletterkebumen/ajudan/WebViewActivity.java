package id.go.kebumenkab.eletterkebumen.ajudan;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.network.AppController;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ALUR_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISSURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUAREKSTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUARINTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_MASUKEKSTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDF;

public class WebViewActivity extends AppBaseActivity {

    private WebView webview;
    private PrefManager prefManager;

    private String alurSurat;
    private String idSurat;
    private String idHistori;
    private String token;
    private boolean isArsip = false;
    private String pejabatTandaTangan;
    private String jenisSurat;
    private String idPemberitahuan;
    private String jenisPenerima;
    private String ditandai="";

    final String[] namaAsisten = {
            "Asisten 1", "Asisten 2", "Asisten 3"
    };

    final String[] valueAsisten = {
            "asisten1", "asisten2", "asisten3"
    };

    private String strAsisten;
    private String strPesan; /** untuk membantu user asisten yang hanya  bisa koreksi saat ada pesan **/
    private String strArsip ="";
    private ProgressDialog pDialog;
    private String cekStatusDelegasi;
    private Menu myMenu;
    public Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

        logger = new Logger();

        logger.d("Jabatan", prefManager.getStatusJabatan());

        pDialog     = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));

        logger      = new Logger();

        setContentView(R.layout.activity_web_view);

        registerBaseActivityReceiver();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_lihat));

        webview = (WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //This will zoom out the WebView
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setInitialScale(1);

        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /*** Menerima parameter dari halaman sebelumnya untuk diproses pada pemanggilan API  **/
        Intent intent   = getIntent();
        if(intent.getStringExtra(Tag.TAG_TANDAI)!=null) ditandai = intent.getStringExtra(Tag.TAG_TANDAI);
        logger.d("Tanda SURAT", ditandai);

        logger.d("JENIS SURAT", intent.getStringExtra(TAG_JENISSURAT));

        /** PEMBERITAHUAN **/
        if(intent.getStringExtra(TAG_JENISSURAT).equalsIgnoreCase(TAG_PEMBERITAHUAN)){

            /** Untuk  mengubah agar status sudah dibaca **/
            idPemberitahuan = intent.getStringExtra(TAG_ID_PEMBERITAHUAN);
            idSurat         = intent.getStringExtra(Tag.TAG_ID_SURAT);
            jenisSurat      = TAG_PEMBERITAHUAN;
            alurSurat       = intent.getStringExtra(TAG_ALUR_SURAT);

            if(isConnected){
                // loadPDF(ELETTER_PDF+idSurat);

                PdfWebViewClientDua pdfWebViewClient = new PdfWebViewClientDua(this, webview);
                pdfWebViewClient.loadPdfUrl(ELETTER_PDF+idSurat);

                Log.d("PDF", ELETTER_PDF+idSurat);


            }else{
                showDialogError(1);
            }

            /** KELUAR EKSTERNAL **/
        }else if(intent.getStringExtra(TAG_JENISSURAT).equalsIgnoreCase(TAG_KELUAREKSTERNAL)) {

            strPesan        = intent.getStringExtra(Tag.TAG_PESAN);
            strArsip        = intent.getStringExtra(Tag.TAG_ARSIP);
            alurSurat       = intent.getStringExtra(Tag.TAG_ALUR_SURAT);
            idSurat         = intent.getStringExtra(Tag.TAG_ID_SURAT);
            idHistori       = intent.getStringExtra(Tag.TAG_ID_HISTORI);
            pejabatTandaTangan = intent.getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN);

            if(strArsip.equals(TAG_ARSIP)){
                isArsip = true;
            }

            jenisSurat      = TAG_KELUAREKSTERNAL;

            if(isConnected){
                PdfWebViewClientDua pdfWebViewClient = new PdfWebViewClientDua(this, webview);
                pdfWebViewClient.loadPdfUrl(ELETTER_PDF+idSurat);

            }else{
                showDialogError(1);
            }

            /** KELUAR INTERNAL **/
        }else if(intent.getStringExtra(TAG_JENISSURAT).equalsIgnoreCase(TAG_KELUARINTERNAL)) {

            strPesan        = intent.getStringExtra(Tag.TAG_PESAN);
            strArsip        = intent.getStringExtra(Tag.TAG_ARSIP);
            alurSurat       = intent.getStringExtra(Tag.TAG_ALUR_SURAT);
            idSurat         = intent.getStringExtra(Tag.TAG_ID_SURAT);
            idHistori       = intent.getStringExtra(Tag.TAG_ID_HISTORI);
            pejabatTandaTangan = intent.getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN);

            if(strArsip.equals(TAG_ARSIP)){
                isArsip = true;
            }

            jenisSurat      = TAG_KELUARINTERNAL;

            if(isConnected){
                // loadPDF(ELETTER_PDF+idSurat);

                // untuk melihat PDF  jalan
                PdfWebViewClientDua pdfWebViewClient = new PdfWebViewClientDua(this, webview);
                pdfWebViewClient.loadPdfUrl(ELETTER_PDF+idSurat);

            }else{
                showDialogError(1);
            }

            /** MASUK EKSTERNAL **/
        }else if(intent.getStringExtra(TAG_JENISSURAT).equalsIgnoreCase(TAG_MASUKEKSTERNAL)) {

            strPesan        = intent.getStringExtra(Tag.TAG_PESAN);
            strArsip        = intent.getStringExtra(Tag.TAG_ARSIP);
            alurSurat       = intent.getStringExtra(Tag.TAG_ALUR_SURAT);
            idSurat         = intent.getStringExtra(Tag.TAG_ID_SURAT);
            idHistori       = intent.getStringExtra(Tag.TAG_ID_HISTORI);
            pejabatTandaTangan = intent.getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN);

            if(strArsip.equals(TAG_ARSIP)){
                isArsip = true;
            }

            jenisSurat      = TAG_MASUKEKSTERNAL;

            if(isConnected){
//                String url = "https://docs.google.com/gview?embedded=true&url=http://programmerkebumen.000webhostapp.com/eletter/file/Surat%20Undangan.pdf";
//                PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(this, webview);
//                pdfWebViewClient.loadPdfUrl(ELETTER_PDF+idSurat);
                // pdfWebViewClient.loadPdfUrl(url);

                // loadPDF(ELETTER_PDF+idSurat);

                PdfWebViewClientDua pdfWebViewClient = new PdfWebViewClientDua(this, webview);
                pdfWebViewClient.loadPdfUrl(ELETTER_PDF+idSurat);
            }else{
                showDialogError(1);
            }

        }else {
            /** **/
            alurSurat       = intent.getStringExtra(Tag.TAG_ALUR_SURAT);
            idSurat         = intent.getStringExtra(Tag.TAG_ID_SURAT);
            idHistori       = intent.getStringExtra(Tag.TAG_ID_HISTORI);
            pejabatTandaTangan = intent.getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN);
            jenisSurat      =  intent.getStringExtra(Tag.TAG_JENISSURAT);
            strPesan        = intent.getStringExtra(Tag.TAG_PESAN);


            if(isConnected){
                loadPDF(ELETTER_PDF+idSurat);
            }else{
                showDialogError(1);
            }
        }
    }

    /**
     * Mengambil file PDF
     */
    public void loadPDF(final String url){
        Log.d("PDF", ELETTER_PDF+idSurat+"/ is Arsip: "+ String.valueOf(isArsip));

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideDialog();
            }

        });

        if (isArsip){
            webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + ELETTER_PDF+idSurat);
        }else{
            webview.loadUrl(ELETTER_PDF+idSurat);
        }

        // PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(this, webview);
        // pdfWebViewClient.loadPdfUrl(ELETTER_PDF+idSurat);

        // String url = "https://docs.google.com/gview?embedded=true&url=http://programmerkebumen.000webhostapp.com/eletter/file/Surat%20Undangan.pdf";
        // pdfWebViewClient.loadPdfUrl(url);

    }


    /**
     * Mengolah menu yang muncul di sebelah kanan atas
     * akan mengecek status jabatannya apa (Kepala, Sekretaris, lainnya)
     * akan mengecek status surat yang akan dieksekusi
     */




    private void showDialog() {
        if (!pDialog.isShowing()) pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(pDialog.isShowing()) pDialog.cancel();
    }

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/

    public void showDialogError(int i){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = null;
        if(i==1){
            dialogView = inflater.inflate(R.layout.dialog_warning, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }else if(i==2){
            dialogView = inflater.inflate(R.layout.dialog_warning_server, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }else if(i==3){
            dialogView = inflater.inflate(R.layout.dialog_warning_empty, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }else if(i==4){
            dialogView = inflater.inflate(R.layout.dialog_warning_empty, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    // closeAllActivities();
                }
            });
        }else if(i==5){
            dialogView = inflater.inflate(R.layout.dialog_warning_empty_message, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    public void cekDelegasi() {

        final ProgressDialog progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage("Cek Delegasi");
        progressDialog.show();

        if (token == null) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            cekStatusDelegasi = "false";
        }else{
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_CEK_DELEGASI,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Log.d("Cek Delegasi Response", response.toString());

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String pesan = jsonObject.getString("pesan");

                                if(status.equals("success") && pesan.equals("true")) {
                                    cekStatusDelegasi = pesan;

                                    MenuItem itemTelaah = myMenu.findItem(R.id.action_telaah);
                                    MenuItem itemArsip = myMenu.findItem(R.id.action_arsip);
                                    MenuItem itemDisposisi =  myMenu.findItem(R.id.action_disposisi);
                                    MenuItem itemTindakLanjuti =  myMenu.findItem(R.id.action_tindaklanjuti);
                                    itemTelaah.setVisible(false);
                                    itemArsip.setVisible(true);
                                    itemDisposisi.setVisible(true);
                                    itemTindakLanjuti.setVisible(true);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String err = (error.getMessage()==null)?"Volley Error":error.getMessage();
                            Log.e("Login", err);

                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", token);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", token);
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
    }
}

class PdfWebViewClientDua extends WebViewClient
{
    private static final String TAG = "PdfWebViewClient";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url=";

    private Context mContext;
    private WebView mWebView;
    private ProgressDialog mProgressDialog;
    private boolean isLoadingPdfUrl;
    private Logger logger;

    public PdfWebViewClientDua(Context context, WebView webView)
    {
        mContext = context;
        mWebView = webView;
        mWebView.setWebViewClient(this);
        logger = new Logger();
    }

    public void loadPdfUrl(String url)
    {
        mWebView.stopLoading();

        if (!TextUtils.isEmpty(url))
        {
            isLoadingPdfUrl = isPdfUrl(url);
            if (isLoadingPdfUrl)
            {
                mWebView.clearHistory();
            }

            showProgressDialog();
        }

        mWebView.loadUrl(url);
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
        dismissProgressDialog();
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
        dismissProgressDialog();
        handleError(error.getErrorCode(), error.getDescription().toString(), uri.toString());
    }

    @Override
    public void onPageFinished(final WebView view, final String url)
    {
        Log.i(TAG, "Finished loading. URL : " + url);
        dismissProgressDialog();
    }

    private boolean shouldOverrideUrlLoading(final String url)
    {
        Log.i(TAG, "shouldOverrideUrlLoading() URL : " + url);

        if (!isLoadingPdfUrl && isPdfUrl(url))
        {
            mWebView.stopLoading();

            final String pdfUrl = PDF_VIEWER_URL + url;

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    loadPdfUrl(pdfUrl);
                }
            }, 300);

            return true;
        }

        return false; // Load url in the webView itself
    }

    private void handleError(final int errorCode, final String description, final String failingUrl)
    {
        logger.e(TAG, "Error : " + errorCode + ", " + description + " URL : " + failingUrl);
    }

    private void showProgressDialog()
    {
        dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(mContext, "", "Loading...");
    }

    private void dismissProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
