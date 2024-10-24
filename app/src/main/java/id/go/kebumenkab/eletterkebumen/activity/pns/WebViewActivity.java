package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
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
import android.webkit.WebViewClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.desa.WebViewDesaLurahActivity;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.CheckForSDCard;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.AppController;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.JAB_ASISTEN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.JAB_BUPATI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.JAB_KASUBBAGTU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.JAB_SEKDA;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.JAB_WABUP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUDAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ALUR_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_APLIKASI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIPLAIN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_DISPOSISI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_HAPUS;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_HISTORI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISPENERIMA;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISTUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUAREKSTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUARINTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KEMBALIKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KERJAKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSILANGSUNG;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_MASUKEKSTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENARIKAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENARIKAN_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENGEMBALIAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PERMINTAANTANDATANGAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_STATUS_SUKSES;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TANDAI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TELAAH;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TEMBUSAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TINDAKLANJUTI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_URL_PDF;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDF;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDFJS;
import static id.go.kebumenkab.eletterkebumen.network.pns.ApiClient.ELETTER_PDF_KONSEP;

public class WebViewActivity extends AppBaseActivity implements EasyPermissions.PermissionCallbacks  {


    private String alurSurat;
    private String idSurat;
    private String idHistori;
    private String token;
    private boolean isArsip = false;
    private String pejabatTandaTangan;
    private String jenisSurat;
    private String koreksiLangsung;
    private String idPemberitahuan;
    private String jenisPenerima;
    private String jenisTujuan="";
    private String ditandai="";

    private String strAsisten;
    private String strPesan; /** untuk membantu user asisten yang hanya  bisa koreksi saat ada pesan **/
    private String strArsip ="";

    final String[] namaAsisten = {
            "Asisten 1", "Asisten 2", "Asisten 3"
    };

    final String[] valueAsisten = {
            "asisten1", "asisten2", "asisten3"
    };

    private String URL_PREVIEW;

    private String cekStatusDelegasi;

    private WebView webview;
    private PrefManager prefManager;

    private Menu myMenu;
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private static final int WRITE_REQUEST_CODE = 300;
    private static final int READ_REQUEST_CODE = 200;

    private Button reload;
    private ProgressBar progressBar;
    private TextView tvMessage;

    private Intent intent;

    private boolean isConnected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger              = new Logger();
        logger.d("LIFECYCLE", "onCreate");

        prefManager         = new PrefManager(this);

        logger.d("Jabatan", prefManager.getStatusJabatan());
        token               = prefManager.getSessionToken();
        notifikasiDialog    = new NotifikasiDialog(getApplicationContext(), WebViewActivity.this);

        isConnected         = NetworkUtil.cekInternet(getApplicationContext());

        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_lihat));

        initViews();

        cekIntent();

        registerBaseActivityReceiver();

    }

    private boolean cekInternet(){
        return NetworkUtil.cekInternet(getApplicationContext());
    }


    public void initViews(){
        logger.d("LIFECYCLE", "Init View");
        reload  = findViewById(R.id.reload);
        webview = findViewById(R.id.webview);
        tvMessage = findViewById(R.id.message);
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

        intent = getIntent();

        if(getIntent().getStringExtra(TAG_JENISTUJUAN) != null){
            jenisTujuan = getIntent().getStringExtra(TAG_JENISTUJUAN);
        }else{
            jenisTujuan = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN) != null){
            pejabatTandaTangan = getIntent().getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN);
        }else{
            pejabatTandaTangan = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_PESAN) != null){
            strPesan        = getIntent().getStringExtra(Tag.TAG_PESAN);
        }else{
            strPesan = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_ARSIP)!=null){
            strArsip        = getIntent().getStringExtra(Tag.TAG_ARSIP);

            if(strArsip.equals(TAG_ARSIP)){
                isArsip = true;
            }
        }else{
            strArsip = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_ALUR_SURAT) != null){

            alurSurat       = getIntent().getStringExtra(Tag.TAG_ALUR_SURAT);

            // disposisi, telaah, ajuan, koreksi

            if(alurSurat.equalsIgnoreCase(TAG_DISPOSISI)){
                URL_PREVIEW = ELETTER_PDFJS;
            }

            if(alurSurat.equalsIgnoreCase(TAG_PENGEMBALIAN)){
                URL_PREVIEW = ELETTER_PDFJS;
            }

            if(alurSurat.equalsIgnoreCase(TAG_TELAAH)){
                URL_PREVIEW = ELETTER_PDFJS;
            }

            if(alurSurat.equalsIgnoreCase(TAG_AJUAN)){
//                URL_PREVIEW = ELETTER_PDF_KONSEP;
                if(getIntent().getStringExtra(Tag.TAG_KOREKSILANGSUNG)!=null)
                    koreksiLangsung = getIntent().getStringExtra(Tag.TAG_KOREKSILANGSUNG);
                else koreksiLangsung = "";
                if(koreksiLangsung.equalsIgnoreCase(TAG_KOREKSILANGSUNG)){
                    URL_PREVIEW = ELETTER_KOREKSI;
                }else{
                    URL_PREVIEW = ELETTER_PDF_KONSEP;
                }
            }

            if(alurSurat.equalsIgnoreCase(TAG_KOREKSI)){
                 URL_PREVIEW = ELETTER_PDF_KONSEP;
//                URL_PREVIEW = ELETTER_PDF;
            }

            if(alurSurat.equalsIgnoreCase(TAG_SETUJU)){
                URL_PREVIEW = ELETTER_PDFJS;
            }

            if(alurSurat.equalsIgnoreCase(TAG_TINDAKLANJUTI)){
                URL_PREVIEW = ELETTER_PDFJS;
            }

            if(alurSurat.equalsIgnoreCase(TAG_KERJAKAN)){
                URL_PREVIEW = ELETTER_PDFJS;
            }

            if(alurSurat.equalsIgnoreCase(TAG_ARSIPLAIN)){
                URL_PREVIEW = ELETTER_PDFJS;
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

        if(getIntent().getStringExtra(Tag.TAG_ID_HISTORI) != null){
            idHistori = getIntent().getStringExtra(Tag.TAG_ID_HISTORI);
        }else{
            idHistori = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN) != null){
            pejabatTandaTangan = getIntent().getStringExtra(Tag.TAG_PEJABAT_TANDATANGAN);
        }else{
            pejabatTandaTangan = "";
        }

        if(getIntent().getStringExtra(Tag.TAG_TANDAI)!=null)
            ditandai = getIntent().getStringExtra(Tag.TAG_TANDAI);
        else ditandai = "";

        if(getIntent().getStringExtra(Tag.TAG_JENISSURAT)!=null)
            jenisSurat = getIntent().getStringExtra(Tag.TAG_JENISSURAT);
        else jenisSurat = "";




        logger.d("PindahHalaman",
                strPesan +"/"
                + strArsip +"/" // arsip, ''
                + alurSurat+"/" // disposisi, telaah, koreksi, ajuan
                + idSurat +"/"
                + idHistori+"/"
                + pejabatTandaTangan +"/"
                + jenisSurat + "/" // null, keluarinternal
                + ditandai); // ''

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

                if(alurSurat.equalsIgnoreCase(TAG_PENGEMBALIAN)){
                    // tak perlu lakukan postDibac
                    if(getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN)!= null) {
                        idPemberitahuan = getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN);
                        logger.d("Alur surat dan PDF 1", alurSurat + "/" + jenisSurat + "/" + idSurat + "/" + idPemberitahuan);
                    }else{
                        logger.d("Alur surat dan PDF 1", alurSurat + "/" + jenisSurat + "/" + idSurat + "/" + idPemberitahuan);
                    }
                }else{
                    if(getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN)!= null){
                        idPemberitahuan = getIntent().getStringExtra(TAG_ID_PEMBERITAHUAN);
                        logger.d("Alur surat dan PDF 2", alurSurat +"/"+jenisSurat+"/"+idPemberitahuan);
                        postDibaca(idPemberitahuan, 0);
                    }else{
                        hideDialog(0, 100, "Tidak Ada ID Pemberitahuan");
                    }
                }


            /** KELUAR EKSTERNAL **/
        } else if(jenisSurat.equalsIgnoreCase(TAG_KELUAREKSTERNAL)) {//tujuan di luar e-letter masuk konsep

            jenisSurat      = TAG_KELUAREKSTERNAL;

                logger.d("Handler - Alur surat dan PDF", alurSurat +"/"+URL_PREVIEW+idSurat);
                PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
                        webview, reload);
                pdfWebViewClient.loadPdfUrl(URL_PREVIEW+idSurat);
//                loadPDF(URL_PREVIEW+idSurat);


            /** KELUAR INTERNAL **/
        } else if(jenisSurat.equalsIgnoreCase(TAG_KELUARINTERNAL)) { //sudah ditandatangani
            jenisSurat      = TAG_KELUARINTERNAL;
            if(alurSurat.equalsIgnoreCase("ajuan")) { //jika termasuk konsep surat
                if (koreksiLangsung.equalsIgnoreCase(TAG_KOREKSILANGSUNG)) {
                    logger.d("Handler - Alur surat dan PDF", alurSurat + "/" + URL_PREVIEW + idSurat);
                    PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
                            webview, reload);
                    pdfWebViewClient.loadPdfUrl(URL_PREVIEW + idSurat + "?koreksi&token=" + token);
                } else {
                    logger.d("Handler - Alur surat dan PDF", alurSurat + "/" + URL_PREVIEW + idSurat);
                    PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
                            webview, reload);
                    pdfWebViewClient.loadPdfUrl(URL_PREVIEW + idSurat);
                    //                loadPDF(URL_PREVIEW+idSurat);
                }
            } else { //'ajuan','koreksian','telaah','disposisi','tindaklanjuti','setujui','kirim','arsipkan','arsiplain','tarik'
                logger.d("Handler - Alur surat dan PDF", alurSurat + "/" + URL_PREVIEW + idSurat);
                PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
                        webview, reload);
                pdfWebViewClient.loadPdfUrl(URL_PREVIEW + idSurat);
            }
            //ini asline yang dari mas yusuf
//            else if(alurSurat.equalsIgnoreCase("telaah")||alurSurat.equalsIgnoreCase("disposisi")){ //'ajuan','koreksian','telaah','disposisi','tindaklanjuti','setujui','kirim','arsipkan','arsiplain','tarik'
//                logger.d("Handler - Alur surat dan PDF", alurSurat + "/" + URL_PREVIEW + idSurat);
//                PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(getApplicationContext(),
//                        webview, reload);
//                pdfWebViewClient.loadPdfUrl(URL_PREVIEW + idSurat);
//            }
            /** MASUK EKSTERNAL **/
        } else if(jenisSurat.equalsIgnoreCase(TAG_MASUKEKSTERNAL)) {
            jenisSurat      = TAG_MASUKEKSTERNAL;

//                String url = "https://docs.google.com/gview?embedded=true&url=http://programmerkebumen.000webhostapp.com/eletter/file/Surat%20Undangan.pdf";
//                PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(this, webview);
//                pdfWebViewClient.loadPdfUrl(URL_PREVIEW+idSurat);
                // pdfWebViewClient.loadPdfUrl(url);


                logger.d("Handler - Alur surat dan PDF", alurSurat +"/"+URL_PREVIEW+idSurat);
 //                 loadPDF(URL_PREVIEW+idSurat);
                PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(this, webview, reload);
                pdfWebViewClient.loadPdfUrl(URL_PREVIEW+idSurat);


        } else if(jenisSurat.equalsIgnoreCase(TAG_PERMINTAANTANDATANGAN)){
            jenisSurat = TAG_KELUARINTERNAL;

                String url = intent.getStringExtra(TAG_URL_PDF);
                Logger logger = new Logger();
                loadPDFAplikasiLain(url);

        } else {
            /** **/
            if(isConnected){
                loadPDF(URL_PREVIEW+idSurat);
            }else{
                hideDialog(0,1,"");
            }
        }

        // hideDialog();

    }

    /**
     * Mengambil file PDF
     */
    public void loadPDF(final String url){

        logger.d("LIFECYCLE", "loadPDF");

        logger.d("PDF", URL_PREVIEW+idSurat+"/ is Arsip: "+ isArsip);

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

        if (isArsip){
            // webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + URL_PREVIEW+idSurat);
            webview.loadUrl(URL_PREVIEW+idSurat);
        }else{
            webview.loadUrl(URL_PREVIEW+idSurat);
        }

    }

    /**
     * Mengambil file PDF
     */
    public void loadPDFAplikasiLain(final String url){

        logger.d("LIFECYCLE", "loadPDFAplikasilain");
        logger.d("PDF", ELETTER_PDF+idSurat+"/ is Arsip: "+ isArsip);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideDialog(1, 0, "");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideDialog(0, 100, "");
            }
        });

        webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
    }


    /**
     * Mengolah menu yang muncul di sebelah kanan atas
     * akan mengecek status jabatannya apa (Kepala, Sekretaris, lainnya)
     * akan mengecek status surat yang akan dieksekusi
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        logger.d("LIFECYCLE", "onPrepareOptionMenu");

        invalidateOptionsMenu();

        myMenu = menu;

        if(intent.hasExtra(TAG_ALUR_SURAT)){ // cek apakah ada intent
            alurSurat = intent.getStringExtra(TAG_ALUR_SURAT); // telaah, disposisi, ajuan, pengembalian

            /** Syarat menampilkan tombol aksi adalah jenis surat bukan pemberitahuan **/
            /** Syarat menampilkan tombol aksi adalah surat bukan arsip (belum diarsipkan) **/

            if (!jenisSurat.equalsIgnoreCase(TAG_PEMBERITAHUAN)) {

                if(!isArsip){
                    /** ketika JABATAN = Pejabat Penanda tangan
                     *  dan alur surat berstatus AJUAN maka tampilkan tombol setuju **/

                    if (alurSurat.equalsIgnoreCase(Tag.TAG_AJUAN)) {
                        if(koreksiLangsung.equalsIgnoreCase(TAG_KOREKSILANGSUNG)){
                            getMenuInflater().inflate(R.menu.menu_kosongan_koreksilive, menu);
                        } else if (prefManager.getStatusJabatan().equalsIgnoreCase(pejabatTandaTangan)) {

                            /** Bila terdapat pesan maka dipastikan ajuan memiliki koreksi dari operator
                             *  (misal operator Asisten dan Sekda)
                             *  maka pejabat tanda tangan hanya memiliki opsi menekan tombol KOREKSI **/

                            if (jenisSurat.equalsIgnoreCase(TAG_KELUAREKSTERNAL)) { // tidak ada
                                getMenuInflater().inflate(R.menu.menu_action_kepala_setuju, menu);
                            } else if (jenisSurat.equalsIgnoreCase(TAG_KELUARINTERNAL)) {
                                getMenuInflater().inflate(R.menu.menu_action_kepala_kirim, menu);

                                if(getIntent().getStringExtra(Tag.TAG_TANDAI)!=null){
                                    String tandai = getIntent().getStringExtra(Tag.TAG_TANDAI);
                                    if(tandai.equals("1")){
                                        MenuItem menuTandai = myMenu.findItem(R.id.action_tandai);
                                        MenuItem menuKoreksi = myMenu.findItem(R.id.action_koreksi);
                                        menuTandai.setTitle("Buang Tanda");
                                        menuKoreksi.setVisible(false);
                                    }
                                }
                            }
                        } else if (prefManager.getStatusJabatan().equalsIgnoreCase(TAG_AJUDAN)) {
                            logger.d("Ajudan", "Tidak ada menu");
                        } else {

                            /** Bila ada pesan dari operator, maka asisten hanya bisa mengkoreksi **/
                            getMenuInflater().inflate(R.menu.menu_action_keluar, menu);

                        }

                    } else if (alurSurat.equalsIgnoreCase(Tag.TAG_KOREKSI)) {
                        /** Belum dipakai **/

                        if (prefManager.getStatusJabatan().equalsIgnoreCase(TAG_AJUDAN)) {
                            logger.d("Ajudan", "Tidak ada menu");
                        }else{
                            getMenuInflater().inflate(R.menu.menu_action_koreksi, menu);
                        }

                    } else if (alurSurat.equalsIgnoreCase(Tag.TAG_TELAAH)) {

                        String jenisPenerima = getIntent().getStringExtra(TAG_JENISPENERIMA);

                        if (prefManager.getStatusJabatan().contains(Tag.JAB_SEKRETARIS)) {

                            /** untuk Sekdin dari operator **/
                            String logCekStatusDelegasi = (cekStatusDelegasi == null) ? "Cek Status Delegasi Null" : cekStatusDelegasi;
                            logger.d("1alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);

                            getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);

                            MenuItem menuArsip = myMenu.findItem(R.id.action_arsip);
                            MenuItem menuDispo = myMenu.findItem(R.id.action_disposisi);
                            MenuItem menuTindakLanjuti = myMenu.findItem(R.id.action_tindaklanjuti);
                            MenuItem menuRefresh = myMenu.findItem(R.id.action_reload);

                            menuArsip.setVisible(false);
                            //sekdin disdik minta ada menu dispo khusus untuk sekdin disdik muncul
                            if(prefManager.getSessionUnit().equalsIgnoreCase("Dinas Pendidikan, Kepemudaan dan Olahraga") && prefManager.getStatusJabatan().equalsIgnoreCase("sekretaris")){
                                menuDispo.setVisible(true);
                            }else{
                                menuDispo.setVisible(false);
                            }
                            menuTindakLanjuti.setVisible(false);


                            cekDelegasi();

                        } else if (prefManager.getStatusJabatan().contains(JAB_KASUBBAGTU)) {

                            logger.d("2alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);

                            if (jenisPenerima.contains(JAB_SEKDA) || jenisPenerima.contains(JAB_BUPATI) || jenisPenerima.contains(JAB_WABUP)) {
                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);

                                MenuItem menuArsip = myMenu.findItem(R.id.action_arsip);
                                MenuItem menuDispo = myMenu.findItem(R.id.action_disposisi);
                                MenuItem menuTindakLanjuti = myMenu.findItem(R.id.action_tindaklanjuti);

                                menuArsip.setVisible(false);
                                menuDispo.setVisible(false);
                                menuTindakLanjuti.setVisible(false);
                            } else {
                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
                            }
                        } else if (prefManager.getStatusJabatan().contains(JAB_ASISTEN)) {
                            logger.d("3alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);

                            if (jenisPenerima.contains(JAB_SEKDA) || jenisPenerima.contains(JAB_BUPATI) || jenisPenerima.contains(JAB_WABUP)) {
                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);

                                MenuItem menuArsip = myMenu.findItem(R.id.action_arsip);
                                MenuItem menuDispo = myMenu.findItem(R.id.action_disposisi);
                                MenuItem menuTindakLanjuti = myMenu.findItem(R.id.action_tindaklanjuti);

                                menuArsip.setVisible(true);
                                menuDispo.setVisible(true);
                                menuTindakLanjuti.setVisible(true);
                            } else {
                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
                                MenuItem menuTelaah = myMenu.findItem(R.id.action_telaah);
                                menuTelaah.setVisible(false);
                                //kata lukman meskipun tembusan agar bisa ada menu tindakan

//                                if(jenisTujuan.equals(TAG_TEMBUSAN)){
//                                    logger.d("3balurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);
//
//                                    getMenuInflater().inflate(R.menu.menu_action_arsiptembusan, menu);
//                                }else{
//                                    getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
//                                    MenuItem menuTelaah = myMenu.findItem(R.id.action_telaah);
//                                    menuTelaah.setVisible(false);
//                                }
                            }
                        } else if (prefManager.getStatusJabatan().contains(JAB_SEKDA)) {
                            logger.d("4alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);

                            if (jenisPenerima.contains(JAB_BUPATI) || jenisPenerima.contains(JAB_WABUP)) {
                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
                            } else {
                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
                                MenuItem menuTelaah = myMenu.findItem(R.id.action_telaah);
                                menuTelaah.setVisible(false);
                            }

                        }   else  if (prefManager.getStatusJabatan().equalsIgnoreCase(TAG_AJUDAN)) {
                            logger.d("Ajudan", "Tidak ada menu");
                        }  else {
                            logger.d("5alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);
                            //kata lukman agar meskipun tembusan tetap ada menu tindakan
                            getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
                            MenuItem menuTelaah = myMenu.findItem(R.id.action_telaah);
                            menuTelaah.setVisible(false);
//                            if(jenisTujuan.equals(TAG_TEMBUSAN)){
//                                logger.d("6alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);
//                                getMenuInflater().inflate(R.menu.menu_action_arsiptembusan, menu);
//                            }else{
//                                logger.d("7alurSurat-jenisPenerima-jenisTujuan-jenisSurat", alurSurat+"/"+jenisPenerima+"/"+jenisTujuan+"/"+jenisSurat);
//
//                                getMenuInflater().inflate(R.menu.menu_action_telaah, myMenu);
//                                MenuItem menuTelaah = myMenu.findItem(R.id.action_telaah);
//                                menuTelaah.setVisible(false);
//                            }
                        }
                    } else if (alurSurat.equalsIgnoreCase(TAG_PERMINTAANTANDATANGAN)) {
                        getMenuInflater().inflate(R.menu.menu_action_permintaan_tandatangan, menu);

                    } else {

                        if (prefManager.getStatusJabatan().length() == 0) {
                            /** Munculkan tombol  tindak lanjuti, telaahkan, dan arsipkan **/
                            getMenuInflater().inflate(R.menu.menu_action_masuk_staf, menu);

                        } else {
                            /** Munculkan tombol  disposisi, tindak lanjuti, telaahkan, dan arsipkan **/
                            getMenuInflater().inflate(R.menu.menu_action_masuk_nonkepala, menu);
                        }
                    }
                }else{
                    if (prefManager.getStatusJabatan().equalsIgnoreCase(TAG_AJUDAN)) {
                        logger.d("Ajudan", "Tidak ada menu");
                    }else {

                        if (alurSurat.equalsIgnoreCase(Tag.TAG_DISPOSISI)) {
                            /** Belum dipakai **/
                            getMenuInflater().inflate(R.menu.menu_action_tarik, menu);

                        } else if (alurSurat.equals(TAG_AJUAN) || alurSurat.equals(TAG_KOREKSI) || alurSurat.equals(TAG_SETUJU) ) {
                            /** Menu Tarik muncul bagi penanda tangan pada surat yang dia kirim **/
                            // getMenuInflater().inflate(R.menu.menu_action_tarik, myMenu);

                            getMenuInflater().inflate(R.menu.menu_action_arsip, menu);
                        } else{
                            // jika surat masuk dan bukan arsip konsep surat
                            getMenuInflater().inflate(R.menu.menu_action_arsip, menu);
                        }
                    }
                }
            }


            else if (jenisSurat.equalsIgnoreCase(TAG_PEMBERITAHUAN)) {
                logger.d("JENIS SURAT", "Pemberitahuan");

                if (getIntent().getStringExtra(TAG_ALUR_SURAT).equalsIgnoreCase(TAG_PENGEMBALIAN)) {
                    getMenuInflater().inflate(R.menu.menu_action_tarik_hapus, myMenu);
                }
            } else{
                logger.d("JENIS SURAT", "TIDAK ADA");
            }
        }

//        return true;


        return super.onPrepareOptionsMenu(menu);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logger.d("LIFECYCLE", "onCreateOptions");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        // Koreksi
        if (item.getItemId() == R.id.action_koreksi) {
            showDialogAksi(Tag.TAG_KOREKSI);
        }

        // Ajukan
        if (item.getItemId() == R.id.action_ajukan) {
            showDialogAksi(Tag.TAG_AJUAN);
        }

        // Setuju
        if (item.getItemId() == R.id.action_setuju) {
            showDialogAksi(Tag.TAG_SETUJU);
        }

        // Tandai
        if (item.getItemId() == R.id.action_tandai) {
            showDialogAksi(Tag.TAG_TANDAI);
        }

        // Kirim
        if (item.getItemId() == R.id.action_kirim) {
            showDialogAksi(Tag.TAG_SETUJU); // setujui dulu baru dikirim
        }

        // Tanda tangani
        if (item.getItemId() == R.id.action_tandatangani) {
            showDialogAksi(Tag.TAG_PERMINTAANTANDATANGAN); // setujui dulu baru dikirim
        }

        // Telaah
        if (item.getItemId() == R.id.action_telaah) {

            if(prefManager.getStatusJabatan().equalsIgnoreCase(JAB_KASUBBAGTU)){
                    showDialogNamaAsisten();
            }else{
                showDialogAksi(Tag.TAG_TELAAH);
            }
        }

        // Tarik
        if (item.getItemId() == R.id.action_tarik) {
            showDialogAksi(Tag.TAG_PENARIKAN);
        }

        // Tindak Lanjuti
        if (item.getItemId() == R.id.action_tindaklanjuti) {
            showDialogAksi(Tag.TAG_TINDAKLANJUTI);
        }

        // Hapus
        if (item.getItemId() == R.id.action_delete) {
            showDialogAksi(Tag.TAG_HAPUS);
        }

        // Arsipkan
        if (item.getItemId() == R.id.action_arsip) {
            showDialogAksi(Tag.TAG_ARSIP);
        }

        // Kembalikan
        if (item.getItemId() == R.id.action_kembalikan) {
            showDialogAksi(Tag.TAG_KEMBALIKAN);
        }

        // Disposisi
        if (item.getItemId() == R.id.action_disposisi) {
            /** Cari bawahan terlebih dahulu **/
            Intent localIntent = new Intent(getApplicationContext(), DisposisiActivity.class);
            localIntent.putExtra(TAG_ID_SURAT, idSurat);
            localIntent.putExtra(TAG_ID_HISTORI, idHistori);
            startActivity(localIntent);

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

        // Disposisi
        if (item.getItemId() == R.id.action_download) {

            String downloadUrl =  ELETTER_PDF+idSurat;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Kode untuk Android 10 (atau versi di atasnya)
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WebViewActivity.this);
                builder.setMessage("Pilih Cara Download");
                builder.setPositiveButton("Gunakan Default Browser", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)));
                    }
                });
                builder.setNegativeButton("Gunakan Download Manager", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        downloadPdf(downloadUrl,"surat_"+idSurat);
                    }
                });
                builder.show();
            }
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
//                downloadPdfWithRedirect(WebViewActivity.this,downloadUrl,"surat_"+idSurat);
//            }
            else {
                if (CheckForSDCard.isSDCardPresent()) {

                    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(WebViewActivity.this, perms)) {
                        try {
                            Download(downloadUrl);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    } else {

                        EasyPermissions.requestPermissions(WebViewActivity.this, getString(R.string.message_writefile), WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        EasyPermissions.requestPermissions(WebViewActivity.this, getString(R.string.message_writefile), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }

                } else {
                    notifikasiDialog.showToast("SD Card not found");
                }
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


    private void postDibaca(String idPemberitahuan, final int tag) {
        showDialog();

        ApiInterface apiService =
                ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

        Call<ResponStandar> call = null;
        call = apiService.sendDibaca(token, idPemberitahuan);

        call.enqueue(new Callback<ResponStandar>() {
            @Override
            public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                ResponStandar result = response.body();

                logger.w4("Respon", response);
                if (response.body().getStatus().equalsIgnoreCase(TAG_STATUS_SUKSES)){
                    logger.d("debug", "onResponse: BERHASIL");

                    hideDialog(1, 0, "");

                    // ketika pemberitahuan telaahkan di hapus
                    if(tag == 1){
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        closeAllActivities();
                    }
                } else {
                    hideDialog(0, 100, "Belum berhasil, Silakan ulangi");
                    logger.d("debug", "onResponse: GA BERHASIL");
                }

            }

            @Override
            public void onFailure(Call<ResponStandar> call, Throwable t) {
                hideDialog(0, 100, t.getLocalizedMessage());

            }
        });

    }

    private void postAksi(final String statusSurat, final String pesan, final String usernameBawahan) {

        if(statusSurat.length()==0 || statusSurat.isEmpty()){
            // notifikasiDialog.showToast("Status surat kosong, "+ statusSurat);
        }else {
            ApiInterface apiService =
                    ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

            Call<ResponStandar> call = null;
            logger.d("status_surat", statusSurat+"/"+pesan+"/"+alurSurat);

            /** Membedakan API  berdasarakan inputkan status surat / konsep **/

            if (statusSurat.equalsIgnoreCase(Tag.TAG_AJUAN)) {
                call = apiService.sendAjukan(token, idSurat, idHistori, pesan);
            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_KOREKSI)) {
                call = apiService.sendKoreksi(token, pesan, idSurat, idHistori);
            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_SETUJU)) {

                    call = apiService.sendSetuju(token, idSurat, idHistori, pesan);

            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_KIRIM)) {

                    call = apiService.sendKirim(token, idSurat, idHistori);

            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_TELAAH)) {

                if(prefManager.getStatusJabatan().contains(Tag.JAB_KASUBBAGTU))
                    call = apiService.sendTelaahKasubbagtu(token, idSurat, idHistori, strAsisten, pesan);
                else if(prefManager.getStatusJabatan().contains(Tag.JAB_ASISTEN))
                    call = apiService.sendTelaahAsisten(token, idSurat, idHistori, pesan);
                else if(prefManager.getStatusJabatan().contains(Tag.JAB_SEKDA))
                    call = apiService.sendTelaahSekda(token, idSurat, idHistori, pesan);
                else
                    call = apiService.sendTelaah(token, idSurat, idHistori, pesan);
                    //call = apiService.sendTelaahOld(token, idSurat, idHistori);


            }else if (statusSurat.equalsIgnoreCase(Tag.TAG_DISPOSISI)) {
                call = apiService.sendDispo(token, idSurat, idHistori, usernameBawahan, pesan);
            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_KERJAKAN)) {
                call = apiService.sendKerjakan(token, idSurat,idHistori);
            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_TINDAKLANJUTI)) {
                call = apiService.sendTindakLanjuti(token, idSurat,idHistori);
            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_ARSIP)) {

                if(jenisTujuan.equals(TAG_TEMBUSAN)){
                    call = apiService.sendArsipTembusan(token, idSurat,idHistori);
                }else{
                    if(jenisSurat.equals(Tag.TAG_TEMBUSAN)){
                        call = apiService.sendArsipTembusan(token, idSurat,idHistori);
                    }else{
                        call = apiService.sendArsipkan(token, idSurat,idHistori);
                    }
                }

            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_PENARIKAN)) {

                if(alurSurat.equals(TAG_DISPOSISI))
                    call = apiService.tarikDispo(token, idSurat, idHistori, pesan);
                // pada pemberitahuan tidak ada idHistori
                else if(alurSurat.equals(TAG_SETUJU))
                    call = apiService.tarikSurat(token, idSurat, pesan);
                else if(alurSurat.equals(TAG_PENGEMBALIAN))
                    call = apiService.tarikSuratDispoPemberitahuan(token, idSurat, idPemberitahuan, pesan);

            }  else if (statusSurat.equalsIgnoreCase(Tag.TAG_KEMBALIKAN)) {
                call = apiService.sendKembalikan(token, idSurat,idHistori, pesan);

            } else if (statusSurat.equalsIgnoreCase(Tag.TAG_TANDAI)) {

                if(ditandai.equals("1")){
                    call = apiService.sendTandai(token, idSurat, "0");
                }else{
                    call = apiService.sendTandai(token, idSurat, "1");
                }
                logger.d("debug_eletter", "Tandai");
            } else  if(statusSurat.equalsIgnoreCase(TAG_PERMINTAANTANDATANGAN)){
                logger.d("XXX", "Permintaan Tanda tangan");
                call = apiService.sendTandatangan(token, idSurat, pesan);
            }else {
                // notifikasiDialog.showToast( "Status surat: " + statusSurat);
                return;
            }

            if (call != null) {
                showDialog();
                logger.d("debug_eletter_call", idSurat+"/"+idHistori);

                call.enqueue(new Callback<ResponStandar>() {
                    @Override
                    public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                            ResponStandar data = response.body();
                            logger.w4("Respon gson ", response);
                            if(data!=null){
                                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){

                                    hideDialog(1, 0,"");
                                    /** Hasil sukses **/
                                    logger.d("debug_eletter", "proses "+ statusSurat+" Berhasil");

                                    if(statusSurat.equalsIgnoreCase(Tag.TAG_SETUJU) &&
                                            jenisSurat.equalsIgnoreCase(TAG_KELUARINTERNAL)){

                                            /**  Jika statusnya adalah baru selesai disetujui
                                              dan surat konsep keluar internal
                                              maka setelah setujui kemudian dikirimkan **/

                                        logger.d("debug", "proses Kirim "+" berlangsung");

//                                        postAksi(Tag.TAG_KIRIM, "", "");

                                    }else{
                                        // Tutup halaman dan buka halaman utama
                                        logger.d("debug WebView", " "+ statusSurat+" Selesai");

                                        hideDialog(0, 9, "");

                                        // Tutup halaman webview



                                        Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                        closeAllActivities();
                                        startActivity(i);

                                    }

                                    // keluar


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
    }

    public void showDialogNamaAsisten(){
        final String[] valueAsisten = getResources().getStringArray(R.array.value_asisten);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Asisten");
        builder.setItems(R.array.nama_asisten, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
               strAsisten = valueAsisten[item];
               logger.d("nama asisten", strAsisten);

               showDialogAksi(Tag.TAG_TELAAH);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini));
            dialogBuilder.setMessage(getString(R.string.message_dialog_ajukan));
            strTombolEksekusi = "Ajukan";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_SETUJU)) {
            /**  sembunyikan inputan **/
            editTextPassphrase.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);

            strTombolEksekusi = "TANDA TANGANI";

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            if(jenisSurat.equals(TAG_KELUARINTERNAL))
                dialogBuilder.setMessage(getString(R.string.message_dialog_setujui_kirim));
            else
                dialogBuilder.setMessage(getString(R.string.message_dialog_setujui));

        }else if(stringStatus.equalsIgnoreCase(TAG_PERMINTAANTANDATANGAN)) {
            /**  sembunyikan inputan **/
            editTextPassphrase.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);

            strTombolEksekusi = "TANDA TANGANI";

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_setujui_kirim));

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_TELAAH)) {
            /**  sembunyikan inputan **/
            edt.setText("");
            edt.setVisibility(View.VISIBLE);

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_telaah));
            strTombolEksekusi = "Telaah";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_KERJAKAN)) {

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_kerjakan));
            strTombolEksekusi = "Kerjakan";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_TINDAKLANJUTI)) {

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_kerjakan));
            strTombolEksekusi = "Tindak Lanjuti";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_ARSIP)) {

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_arsipkan));
            strTombolEksekusi = "Arsipkan";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_KOREKSI)) {
            /**  sembunyikan inputan **/
            edt.setText("");
            edt.setSelection(edt.getText().length());
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            edt.setVisibility(View.VISIBLE);

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_koreksi));

            strTombolEksekusi = "Koreksi";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_KEMBALIKAN)) {
            /**  sembunyikan inputan **/
            edt.setText("");
            edt.setSelection(edt.getText().length());
            edt.setHint(getString(R.string.message_silakan_tulisa_pesan_disini_2));
            edt.setVisibility(View.VISIBLE);

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            dialogBuilder.setMessage(getString(R.string.message_dialog_kembalikan));

            strTombolEksekusi = "Telaahkan";

        }else if(stringStatus.equalsIgnoreCase(Tag.TAG_PENARIKAN)) {

            strTombolEksekusi = "Tarik";

            /** Pesan di dalam kotak dialog dibedakan dengan jenis surat **/
            if(alurSurat.equals(TAG_DISPOSISI))
                dialogBuilder.setMessage(getString(R.string.message_dialog_penarikan));
            else if(alurSurat.equals(TAG_SETUJU))
                dialogBuilder.setMessage(getString(R.string.message_dialog_penarikan_yangdikirim));
            else if(alurSurat.equals(TAG_PENGEMBALIAN))
                dialogBuilder.setMessage(getString(R.string.message_dialog_penarikan_pengembalian));
        }else if(stringStatus.equals(Tag.TAG_TANDAI)){
            logger.d("Ditandai", ditandai);

            if(ditandai.equals("1")){
                dialogBuilder.setMessage(getString(R.string.message_dialog_bersihkantanda));
                strTombolEksekusi = "Buang Tanda";
            }else{
                dialogBuilder.setMessage(getString(R.string.message_dialog_tandai));
                strTombolEksekusi = "Tandai";
            }
        }else if(stringStatus.equals(Tag.TAG_HAPUS)){
                dialogBuilder.setMessage(getString(R.string.message_dialog_hapus_pemberitahuan));
                strTombolEksekusi = TAG_HAPUS;

        }else{
            // edt.setText(strPesan);
            // edt.setSelection(edt.getText().length());

            dialogBuilder.setMessage(getString(R.string.message_dialog_send_with_comment));
        }

            if(stringStatus.equals(Tag.TAG_TANDAI)){
                edt.setVisibility(View.GONE);
            }

            dialogBuilder.setPositiveButton(strTombolEksekusi, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    String pesan        = edt.getText().toString();
                    String myPassphrase = editTextPassphrase.getText().toString();

                        if(stringStatus.equals(TAG_PENARIKAN)){
                            if(pesan.length()< 5){
                                notifikasiDialog.showDialogError(5, "");
                            }else{
                                postAksi(stringStatus, pesan, "");
                            }
                        }else if(stringStatus.equals(TAG_KOREKSI)){
                            if(pesan.length()< 5){
                                notifikasiDialog.showDialogError(5, "");
                            }else{
                                postAksi(stringStatus, pesan, "");
                            }
                        }else if(stringStatus.equals(TAG_PENARIKAN_SURAT)){
                            if(pesan.length()< 5){
                                notifikasiDialog.showDialogError(5, "");
                            }else{
                                postAksi(stringStatus, pesan, "");
                            }
                        }else if(stringStatus.equals(TAG_KEMBALIKAN)){
                            if(pesan.length() < 5){
                                notifikasiDialog.showDialogError(5, "");
                            }else{
                                postAksi(stringStatus, pesan, "");
                            }
                        }else if(stringStatus.equals(TAG_TANDAI)){
                            postAksi(stringStatus, "", "");
                        }else if(stringStatus.equals(TAG_TELAAH)){
                            if(pesan.length()< 5){
                                notifikasiDialog.showDialogError(5, "");
                            }else{
                                postAksi(stringStatus, pesan, "");
                            }
                        }else if(stringStatus.equals(TAG_SETUJU)){

                            if(myPassphrase.length()< 7){
                                notifikasiDialog.showDialogError(6, "");
                            }else{
                                postAksi(stringStatus, myPassphrase, "");
                            }
                        }else if(stringStatus.equals(TAG_HAPUS)){
                            //  Menghapus pemberitahuan koreksi
                            postDibaca(idPemberitahuan, 1);
                        }else{
                            postAksi(stringStatus, pesan, "");
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

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    public void cekDelegasi() {

        showDialog();

        if (token == null) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            cekStatusDelegasi = "false";
        }else{
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_CEK_DELEGASI,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            logger.d("Cek Delegasi Response", response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String pesan = jsonObject.getString("pesan");

                                if(status.equals("success") && pesan.equals("true")) {

                                    hideDialog(1, 0,  "");

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
                                hideDialog(0, 100, e.getLocalizedMessage());
                            }


                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String err = (error.getMessage()==null)?"Volley Error":error.getMessage();
                            logger.d("volley", err);
                            hideDialog(0,100, err);

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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        new DownloadFile().execute(ELETTER_PDF+idSurat);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        logger.d(TAG_APLIKASI, "Permission has been denied");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, WebViewActivity.this);
    }

    public void viewPDF(String fileName)
    {
        final ProgressDialog progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage("Sedang membuka file unduhan");
        progressDialog.show();

        File pdfFile = new File(fileName);
        // Uri path = Uri.fromFile(pdfFile);

        Uri pathURI = FileProvider.getUriForFile(WebViewActivity.this,
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
            notifikasiDialog.showToast("Tidak ada aplikasi untuk membuka PDF");
        }
    }

    /**
     * Async Task to download file from URL
     */
    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private String downloadedFile;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(WebViewActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1)+".pdf";

                //Append timestamp to file name
                // fileName = timestamp + "_" + fileName;

                File externalCacheFile = new File(getApplicationContext().getExternalCacheDir(), fileName);
                downloadedFile = externalCacheFile.toString();
                OutputStream output = new FileOutputStream(externalCacheFile);

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    logger.d(Tag.TAG_APLIKASI, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();

                input.close();
                return "File disimpan sementara di cache";

            } catch (Exception e) {
                logger.d("Error: ", e.getMessage());
            }
            return "Maaf, aplikasi tidak memiliki hak akses simpan file.";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            notifikasiDialog.showToast(message);

            viewPDF(downloadedFile);
        }
    }


    private void downloadPdf(String url,String namaFile) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("PDF "+namaFile+" Download");
        request.setDescription("Downloading PDF file");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, namaFile+".pdf");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    public void downloadPdfWithRedirect(Context context, String originalUrl, String fileName) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(originalUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(fileName);
        request.setDescription("Please wait...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName + ".pdf";
        request.setDestinationUri(Uri.fromFile(new File(filePath)));
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
        // Show a toast message when download is started
        Toast.makeText(context, "Downloading PDF...", Toast.LENGTH_SHORT).show();
    }
    private void Download(String... downloadUrl) throws MalformedURLException {

        String url ="";
        url = downloadUrl[0];
        //String fileName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())+".pdf";
        //Extract file name from URL
        try  {
            URL myUrl = new URL(downloadUrl[0]);

            String   fileName = downloadUrl[0].substring(downloadUrl[0].lastIndexOf('/') + 1)+".pdf";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url + ""));
            request.setTitle(fileName);
            request.setMimeType("application/pdf");
            request.allowScanningByMediaScanner();
            request.setAllowedOverMetered(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "eletter/" + fileName);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

//            File externalCacheFile = new File(getApplicationContext().getExternalCacheDir(), fileName);
//            String downloadedFile = externalCacheFile.toString();
//            viewPDF(downloadedFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PdfWebViewClient extends WebViewClient
    {
        private static final String TAG = "PdfWebViewClient";
        private static final String PDF_EXTENSION = ".pdf";
        private static final String PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url=";

        private final Context mContext;
        private final WebView mWebView;
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
            handleError(errorCode, description, failingUrl);
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





