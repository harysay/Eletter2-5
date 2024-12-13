package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.adapter.pns.KonsepAdapter;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsep;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;

public class DitandaiActivity extends AppBaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, KonsepAdapter.MessageAdapterListener{

    private List<Konsep> dtList = new ArrayList<>();
    private RecyclerView recyclerView;
    public  KonsepAdapter mAdapter;

    private PrefManager prefManager;
    private String token;
    private SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayout errorMessage;
    private TextView errorText;
    private ImageView errorGambar;

    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ditandai);

        logger = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DitandaiActivity.this);

        pDialog     = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_ditandai));

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new KonsepAdapter(getApplicationContext(), dtList,
                DitandaiActivity.this);

        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) findViewById(R.id.error);
        errorText           = (TextView)findViewById(R.id.message_text);
        errorGambar         = (ImageView)findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());
        if(isConnected){
            swipeRefreshLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            getInbox();

                        }

                    }
            );
        }else{
            notifikasiDialog.showDialogError(1, "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         Menu myMenu = menu;

        getMenuInflater().inflate(R.menu.menu_action_kepala_kirim, menu);

        MenuItem menuTandai = myMenu.findItem(R.id.action_tandai);
        MenuItem menuKoreksi = myMenu.findItem(R.id.action_koreksi);

        menuTandai.setVisible(false);
        menuKoreksi.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.action_kirim){
            // di sini looping array list konsep surat
           if(dtList.size()>0){
               showDialogAksi();
           }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        tampilError(false, 0);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ResultKonsep> call = apiService.getKonsep(token);
        if(call != null) {

            call.enqueue(new Callback<ResultKonsep>() {
                @Override
                public void onResponse(Call<ResultKonsep> call, Response<ResultKonsep> response) {
                    final ResultKonsep result = response.body();
                    if(result  != null) {

                        logger.w("hasil", response);


                        dtList.clear();

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result != null) {

                                    List<Konsep> todos = result.getData();

                                    swipeRefreshLayout.setRefreshing(false);

                                    if (todos.size() == 0) {
                                        tampilError(true, 5);
                                    } else {
                                        for (Konsep data : todos) {

                                            logger.d("Respon", data.getFrom());
                                            if(data.getTandai().equals("1")){
                                                dtList.add(data);
                                            }

                                        }

                                        mAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    tampilError(true, 3);
                                }
                            }
                        });
                    }else{

                        swipeRefreshLayout.setRefreshing(false);
                        tampilError(true, 3);
                    }

                }

                @Override
                public void onFailure(Call<ResultKonsep> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);

                    tampilError(true, 1);
                }
            });
        }else{
            logger.d("Arsip", "call is null");
        }
    }

    public void tampilError(boolean param, int kode){

        if(param == true){
            recyclerView.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);

            if(kode == 1)
                errorText.setText(getString(R.string.error_api));
            if(kode == 2)
                errorText.setText(getString(R.string.error_api));
            if(kode == 3)
                errorText.setText(getString(R.string.error_api));
            if(kode == 4)
                errorText.setText(getString(R.string.error_api));
            if(kode == 5){
                errorText.setText(getString(R.string.error_message_nodata));
                if(Build.MANUFACTURER.contains("Xiaomi")){
                    errorGambar.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_error_outline_dark_24dp, null));

                }else{
                    errorGambar.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_outline_dark_24dp));

                }
            }


        }else{
            recyclerView.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                       getInbox();
                    }
                }
        );
    }


//    @Override
//    public void onIconClicked(int position) {
//
//    }
//
//    @Override
//    public void onIconImportantClicked(int position) {
//
//    }

    @Override
    public void onMessageRowClicked(int position) {

        /** Ketika  salah satu item Diklik lemparkan ke detail surat masuk **/

        if (mAdapter.getSelectedItemCount() > 0) {

        } else {

            Konsep arsip = dtList.get(position);

//            dtList.set(position, arsip);
//            mAdapter.notifyDataSetChanged();

            Intent intentDetail = new Intent(getApplicationContext(), DetailKonsep.class);
            intentDetail.putExtra("object", arsip);
            intentDetail.putExtra("position", position);
            intentDetail.putExtra(TAG_ARSIP, TAG_ARSIP);

            logger.d("Ditandai", "Cek jumlah surat "+ String.valueOf(dtList.size()) +" -  posisi "+String.valueOf(position));

            this.startActivityForResult(intentDetail, 1);
        }

    }

    private int getAdapterItemPosition(long id)
    {
        for (int position=0; position<mAdapter.getSize(); position++)
            if (mAdapter.getSelectedItems().get(position) == id)
                return position;
        return 0;
    }

//    @Override
//    public void onRowLongClicked(int position) {
//
//    }

    @Override
    public void onItemClicked(Konsep konsep,int position) {

    }

    public void showDialogAksi() {

        /**  Membuat alert dialog untuk konfirmasi aksi sebelum dilanjutkan **/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_message, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(Tag.TAG_SETUJU);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit);
        String strTombolEksekusi = "Kirim";

        edt.setVisibility(View.VISIBLE);
        edt.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edt.setLines(1);
        edt.setSelection(edt.getText().length());

        dialogBuilder.setMessage(getString(R.string.message_dialog_setujui_kirim));

        dialogBuilder.setPositiveButton(strTombolEksekusi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String passphrase = edt.getText().toString();

                    if(passphrase.length()<7){
                        notifikasiDialog.showDialogError(6,"");
                    }else{

                        // Diulang sebanyak konsep surat
                        for(int i = 0; i < dtList.size();i++){
                            Konsep konsep = dtList.get(i);
                            String idSurat = konsep.getIdSurat();
                            String idHistori = konsep.getIdHistory();

                            if(i == dtList.size()-1){
                                if(konsep.getTandai().toLowerCase().contains("1")){
                                    postAksi(Tag.TAG_SETUJU, idSurat, idHistori, passphrase, true);
                                }
                            }else{
                                if(konsep.getTandai().toLowerCase().contains("1")) {
                                    postAksi(Tag.TAG_SETUJU, idSurat, idHistori, passphrase, false);
                                }
                            }


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

    // Proses ke server
    private void postAksi(final String statusSurat, final String idSurat,
                          final String idHistori, String passphrase, final boolean isTerakhir) {

            ApiInterface apiService =
                    ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);
        Call<ResponStandar> call = null;
            if (statusSurat.equalsIgnoreCase(Tag.TAG_SETUJU)) {
                 call = apiService.sendSetuju(token, idSurat, idHistori, passphrase);
            }else{
                call = apiService.sendKirim(token, idSurat,idHistori);
            }

            if (call != null) {
                showDialog();
                logger.d("debug_eletter_call", idSurat+"/"+idHistori);

                call.enqueue(new Callback<ResponStandar>() {
                    @Override
                    public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {

                        ResponStandar data = response.body();

                        logger.w4("hasil", response);

                        hideDialog();
                        if(data!=null){
                            String pesan = data.getPesan();
                            if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){
                                /** Hasil sukses **/
                                logger.d("debug_eletter", "proses "+ statusSurat+" Berhasil");

                                if(statusSurat.equalsIgnoreCase(Tag.TAG_SETUJU)){

                                    /**  Jika statusnya adalah baru selesai disetujui
                                     dan surat konsep keluar internal
                                     maka setelah setujui kemudian dikirimkan **/

                                    logger.d("debug", "proses Kirim "+" berlangsung");

                                    postAksi(Tag.TAG_KIRIM, idSurat, idHistori, "", isTerakhir);

                                }else{
                                    // Tutup halaman dan buka halaman utama
                                    if(isTerakhir){
                                        logger.d("debug WebView", " "+ statusSurat+" Selesai");
                                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                        startActivity(intent);
                                        closeAllActivities();

                                        finish();
                                    }


                                }
                            }else{
                                /** Tidak sukses **/
                                if(data.getPesan().contains("telah")){
                                    notifikasiDialog.showDialogError(400, pesan);
                                }else
                                    notifikasiDialog.showDialogError(200, pesan);
                            }

                        }else{
                            logger.d("debug_eletter", "data kosong");
                            notifikasiDialog.showDialogError(2, "");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponStandar> call, Throwable t) {
                        hideDialog();
                        Log.d("debug_eletter", "failure");
                        notifikasiDialog.showDialogError(100, t.getLocalizedMessage());
                    }
                });

            }else{
                Log.d("debug_eletter", "call is null");
                notifikasiDialog.showDialogError(100, "Call is Null");
            }
        }

    private void showDialog() {
        if (!pDialog.isShowing()) pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing()) pDialog.dismiss();
    }

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/

    }

