package id.go.kebumenkab.eletterkebumen.arsip;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.HistoriActivity;
import id.go.kebumenkab.eletterkebumen.activity.LampiranActivity;
import id.go.kebumenkab.eletterkebumen.activity.pns.WebViewActivity;
import id.go.kebumenkab.eletterkebumen.adapter.pns.PenerimaAdapter;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ArsipDiajukanData;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;

import static android.view.View.GONE;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISPENERIMA;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISTUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KELUARINTERNAL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PESAN;

public class ArsipDitelaahDetail extends AppBaseActivity implements View.OnClickListener, PenerimaAdapter.AdapterListener{

    /** Variabel komponen layout dan variabel lainnya **/
    private LinearLayout btn_telaah;
    private LinearLayout btn_riwayat;
    private LinearLayout btn_arsip;
    private LinearLayout btn_lampiran;


    private TextView tanggal, subjek, pesan;
    private TextView t_nomor, t_pesan, t_deskripsi, t_penerima;
    private ImageView image_tick;

    private String  idSurat;
    private String  idHistori;
    private String  alurSurat;
    private String  pejabatTandaTangan;
    private String  jenisSurat;
    private String  strLampiran;
    private String  jenisArsip = "";
    private String  jenisPenerima;
    private String  jenisTujuan;
    private String  nomor;

    private PrefManager prefManager;
    private String token;
    private ProgressDialog pDialog;
    private RelativeLayout layoutPengirim;

    private Logger logger;
    private NotifikasiDialog notifikasiDialog;
    private String penerima;
    ArsipDiajukanData data;

    private String strArsip="";

    private RecyclerView recyclerView ;
    private PenerimaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_arsipdiajukan);
        registerBaseActivityReceiver();

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        logger      = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), ArsipDitelaahDetail.this);

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
        data            = (ArsipDiajukanData) intent.getSerializableExtra("object");
        idSurat         = data.getIdSurat();
        idHistori       = data.getIdHistory();
        alurSurat       = data.getStatus(); // telaah,  disposisi
        jenisSurat      = data.getJenisTujuan(); // tujuan , tembusan
        //penerima        = data.getPenerima().getNama();
        nomor           = data.getNomor();
        strLampiran     = data.getLampiran();

        logger.d("JENIS SURAT", jenisSurat);
        logger.d("ALUR SURAT", alurSurat);

        if(getIntent().getStringExtra(TAG_JENISARSIP)!= null){
            jenisArsip = getIntent().getStringExtra(TAG_JENISARSIP); // untuk deteksi arsip ajuan, koreksi, setuju
        }

        if(getIntent().getStringExtra(TAG_JENISTUJUAN) != null){
            jenisTujuan = getIntent().getStringExtra(TAG_JENISTUJUAN);
        }

        strArsip        = intent.getStringExtra(TAG_ARSIP);

        /**  Inisiasi komponen tampilan **/

        setupView();





        /** Mengecek internet **/
        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        /** Bila memiliki koneksi internet maka panggil api detail **/
        if(isConnected){
            getDetail();
        }else{
            notifikasiDialog.showDialogError(1, "");
        }

    }

    /**  Fungsi menghubungkan komponen di layout dengan activity**/
    public void setupView(){
        tanggal             = (TextView)findViewById(R.id.txt_tanggal);
        subjek              = (TextView)findViewById(R.id.subject);
        pesan               = (TextView)findViewById(R.id.message);
        t_nomor             = (TextView)findViewById(R.id.text_nomor);
        t_deskripsi         = (TextView)findViewById(R.id.text_deskripsi);
        t_penerima         = (TextView)findViewById(R.id.nama_penerima);

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

        btn_riwayat.setVisibility(View.VISIBLE);



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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lampiran:
                lihatLampiran();
                break;

            case R.id.btn_telaah:
                /**  **/

                jenisSurat =  TAG_KELUARINTERNAL ;
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra(Tag.TAG_ALUR_SURAT, alurSurat ); // telaah, disposisi
                intent.putExtra(Tag.TAG_ID_SURAT, idSurat );
                intent.putExtra(Tag.TAG_ID_HISTORI, idHistori );
                intent.putExtra(Tag.TAG_PEJABAT_TANDATANGAN, pejabatTandaTangan);
                intent.putExtra(Tag.TAG_JENISSURAT, jenisSurat); // tujuan, tembusan, setuju
                intent.putExtra(TAG_JENISPENERIMA, jenisPenerima); // instansi, sekda, bupati, wakilbupati
                intent.putExtra(TAG_PESAN,  pesan.getText().toString());
                intent.putExtra(TAG_JENISTUJUAN, jenisTujuan);


                intent.putExtra(Tag.TAG_PESAN, "");
                intent.putExtra(TAG_ARSIP, strArsip);
                intent.putExtra(TAG_JENISPENERIMA, ""); // instansi, sekda, bupati, wakilbupati
                intent.putExtra(Tag.TAG_TANDAI, "0");
                intent.putExtra(TAG_JENISTUJUAN, "");
                /**  Pindah halaman **/;
                logger.d("Jenis Surat", jenisSurat);

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

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
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
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                            String strPesan =  "";
                            String strTindakan = "";
                            // String penerima=data.getPenerima().getNama();
                            strPesan = data.getMessage();

                            t_penerima.setText(penerima);
                            pesan.setText(strPesan);
                            /**  Menampilkan data dari activity sebelumnya **/
                            tanggal.setText(konversiTanggal(data.getTimestamp()));

                            subjek.setText(data.getSubject());
                            t_nomor.setText(data.getNomor());


                            /** Selesai **/

                            //t_deskripsi.setText(strTindakan);

                            if(data.getLampiran().length()>0){
                                strLampiran = data.getLampiran();
                                btn_lampiran.setVisibility(View.VISIBLE);

                               // showLampiran();
                            }

                    }
                });


    }

    /**  Fungsi memparsing tanggal */
    public String konversiTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

        return timeFormat.format(date);
    }

    public void showLampiran() {

        /**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_message, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit);

        /** Konfirmasi arsipkan tidak perlu kolom pesan **/
            /**  untuk tombol ajuan **/
            edt.setVisibility(GONE);
            dialogBuilder.setMessage(getString(R.string.message_lihatlampiran));


        dialogBuilder.setPositiveButton(getString(R.string.action_lihat), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                lihatLampiran();
            }
        });

        dialogBuilder.setNeutralButton(getString(R.string.action_tutup), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /** Tutup dialog **/
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void lihatLampiran(){
        /**  Dikirimkan ke Lampiran  Activity **/
        Intent intentLampiran = new Intent(getApplicationContext(), LampiranActivity.class);
        intentLampiran.putExtra(Tag.TAG_LAMPIRAN, strLampiran );

        /**  Pindah halaman **/
        startActivity(intentLampiran);
    }

    @Override
    public void onMessageRowClicked(int position) {

    }
}
