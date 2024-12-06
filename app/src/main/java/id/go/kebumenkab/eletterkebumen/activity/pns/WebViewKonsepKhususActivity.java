package id.go.kebumenkab.eletterkebumen.activity.pns;

import static id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_DISPOSISI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_HAPUS;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUARINTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KEMBALIKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENARIKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENARIKAN_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENGEMBALIAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PERMINTAANTANDATANGAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TANDAI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TELAAH;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TEMBUSAN;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.AksiItemKhusus;
import id.go.kebumenkab.eletterkebumen.model.DataItemSuratMasuk;
import id.go.kebumenkab.eletterkebumen.model.KonsepKhususDetail;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewKonsepKhususActivity extends AppCompatActivity {
//    private String idCuti,idHistori,tokenDariActivity;
    private String urlPreview;
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
    String token,idAksiKhusus,idkonsepkhusus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger              = new Logger();
        logger.d("LIFECYCLE", "onCreate");
        prefManager         = new PrefManager(this);
        logger.d("Jabatan", prefManager.getStatusJabatan());
        token = prefManager.getSessionToken();
        notifikasiDialog    = new NotifikasiDialog(getApplicationContext(), WebViewKonsepKhususActivity.this);
        isConnected         = NetworkUtil.cekInternet(getApplicationContext());
        setContentView(R.layout.activity_webview_cuti);
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
        webview.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
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
        if(getIntent().getStringExtra("urlpreview") != null){
            urlPreview = getIntent().getStringExtra("urlpreview");
            idkonsepkhusus = getIntent().getStringExtra("idkonsepkhusus");
            URL_PREVIEW = urlPreview;
            logger.d("URL Preview", URL_PREVIEW);
        }else{
            hideDialog(3, 0, "");
        }
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
    @Override
    public void onBackPressed() {
            super.onBackPressed();
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
            logger.d("HandlerURLPreview", URL_PREVIEW);
            PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(), webview, reload);
            pdfWebViewClient.loadPdfUrl(URL_PREVIEW);
//                loadPDF(URL_PREVIEW+idSurat);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_permintaan_tandatangan, menu);
//        if (aksiItemKhususes != null && !aksiItemKhususes.isEmpty()){
//            // Menambahkan item menu sesuai dengan jumlah objek aksi
//            for (AksiItemKhusus aksi : aksiItemKhususes) {
////                menu.add(Menu.NONE, aksi.getAksiId(), Menu.NONE, aksi.getLabel())
////                        .setIcon(R.drawable.ic_concept_white)
////                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//                MenuItem menuItem = menu.add(Menu.NONE, aksi.getAksiId(), Menu.NONE, aksi.getLabel());
//                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            }
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Tindakan yang diambil ketika tombol panah kembali diklik
                finish(); // Menutup Activity saat tombol panah diklik
                return true;
            case R.id.action_tandatangani:
                // Tanda tangani
                showDialogAksi(Tag.TAG_PERMINTAANTANDATANGAN);
                break;
            case R.id.action_reload:
                if(isConnected)  {
                    prosesPDF();
                } else{
                    hideDialog(0, 1, "");
                    webview.setVisibility(View.GONE);
                    reload.setVisibility(View.VISIBLE);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
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
        final EditText edt = dialogView.findViewById(R.id.edit);
        edt.setSelection(edt.getText().length());

        // Passphrase
        final TextInputLayout textInputLayout = dialogView.findViewById(R.id.passphrase);
        final EditText editTextPassphrase = dialogView.findViewById(R.id.edit_passphrase);

        String strTombolEksekusi = "Kirim";

        if(stringStatus.equalsIgnoreCase(TAG_PERMINTAANTANDATANGAN)) {
            /**  sembunyikan inputan **/
            editTextPassphrase.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);

            strTombolEksekusi = "TANDA TANGANI";

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_setujui_kirim));
        }
        dialogBuilder.setPositiveButton(strTombolEksekusi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String pesan        = edt.getText().toString();
                String myPassphrase = editTextPassphrase.getText().toString();
                if(myPassphrase.length()< 7){
                    notifikasiDialog.showDialogError(6, "");
                }else{
                    postAksi(stringStatus, myPassphrase);
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

    private void postAksi(final String statusKhusus, final String pesan) {

        if(statusKhusus.length()==0 || statusKhusus.isEmpty()){
            // notifikasiDialog.showToast("Status surat kosong, "+ statusSurat);
        }else {
            ApiInterface apiServiceCuti = ApiClient.getDomainCuti().create(ApiInterface.class);
            Call<ResponStandar> call = null;
            logger.d("status_surat", statusKhusus+"/"+pesan+"/");
            if (statusKhusus.equalsIgnoreCase(Tag.TAG_PERMINTAANTANDATANGAN)) {
                call = apiServiceCuti.sendTandaTanganiKonsepKhusus(token, idkonsepkhusus, pesan);
            }
//            else {
//                // notifikasiDialog.showToast( "Status surat: " + statusSurat);
//                call = apiServiceCuti.sendAksiKonsepKhusus(token,idkonsepkhusus, idAksiKhusus, pesan);
//            }

            if (call != null) {
                showDialog();
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

//                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
//                                closeAllActivities();
//                                startActivity(i);
                                Intent i = new Intent();
                                i.putExtra("successMessage", data.getPesan());
                                setResult(RESULT_OK, i);
                                finish();
                                overridePendingTransition(0, 0); // Tidak ada animasi keluar
                                hideDialog(1, 100, data.getPesan());
                                // keluar
                            }else{
                                /** Tidak sukses **/
                                hideDialog(0, 100, data.getPesan());
//                                notifikasiDialog.showDialogError(13, data.getPesan());
                            }
                        }else{
                            logger.d("debug_eletter", "data kosong");
//                            notifikasiDialog.showDialogError(4, data.getPesan());
                            hideDialog(0, 100, data.getPesan());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponStandar> call, Throwable t) {
                        notifikasiDialog.showDialogError(10, t.getLocalizedMessage());
                        logger.d("debug_eletter", "failure " + t.getMessage());
                        hideDialog(0, 100, t.getMessage());
                    }
                });

            }else{
                logger.d("debug_eletter", "call is null");
                notifikasiDialog.showDialogError(10, "Null Retrofit Builder");

            }
        }
    }

    protected void closeAllActivities(){
        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }

}