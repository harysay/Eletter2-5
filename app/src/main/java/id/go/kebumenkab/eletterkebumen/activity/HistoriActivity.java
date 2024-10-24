package id.go.kebumenkab.eletterkebumen.activity;

import android.os.Build;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.adapter.HistoriAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Histori;
import id.go.kebumenkab.eletterkebumen.model.ResultHistory;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT_TUJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISSURAT;

public class HistoriActivity extends AppCompatActivity  implements
        SwipeRefreshLayout.OnRefreshListener {

    private List<Histori> historis = new ArrayList<>();
    private RecyclerView recyclerView;
    public  HistoriAdapter mAdapter;

    private PrefManager prefManager;
    private String token;
    private String idSurat;
    private String idSuratInternalTujuan;
    private String idUnitKerja;
    private String jenisSurat;
    private SwipeRefreshLayout swipeRefreshLayout;


    private LinearLayout errorMessage;
    private TextView errorText;
    private ImageView errorGambar;
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_riwayat));

        logger = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), HistoriActivity.this);


        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        idUnitKerja = prefManager.getSessionIdUnit();

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) findViewById(R.id.error);
        errorText           = (TextView)findViewById(R.id.message_text);
        errorGambar         = (ImageView)findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        isConnected = NetworkUtil.cekInternet(getApplicationContext());

        if(getIntent().getStringExtra(TAG_JENISSURAT)!=null)
            jenisSurat  = getIntent().getStringExtra(TAG_JENISSURAT);

        if(getIntent().getStringExtra(TAG_ID_SURAT)!=null)
            idSurat     = getIntent().getStringExtra(Tag.TAG_ID_SURAT);

        if(getIntent().getStringExtra(TAG_ID_SURAT_TUJUAN)!=null)
            idSuratInternalTujuan = getIntent().getStringExtra(TAG_ID_SURAT_TUJUAN);

        if(getIntent().getStringExtra(TAG_ID_SURAT)!=null && getIntent().getStringExtra(TAG_JENISSURAT)!=null){
            if(isConnected){
                swipeRefreshLayout.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                getData();
                            }
                        }
                );
            }else{
                notifikasiDialog.showDialogError(1, "");
            }
        }else{
            notifikasiDialog.showDialogError(100, getString(R.string.message_kurang_parameter));
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);

        logger.d("Histori", "/idSurat-"+idSurat+"/idUnitkerja-"+idUnitKerja+"/jenisSurat-"+jenisSurat);

        tampilError(false, 0,"");

        ApiInterface apiService =
                ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

        Call<ResultHistory> call = null;

        if(jenisSurat.equalsIgnoreCase(Tag.TAG_KONSEP))
            call = apiService.getHistori(token, idSurat);
        else if(jenisSurat.equalsIgnoreCase(Tag.TAG_KOREKSI))
            call = apiService.getHistori(token, idSurat);
        else if(jenisSurat.equalsIgnoreCase(Tag.TAG_SETUJU))
            call = apiService.getHistori(token, idSurat);
        else if(jenisSurat.equalsIgnoreCase(Tag.TAG_SURATMASUK))
            call = apiService.getHistoriSuratMasuk(token,idSurat,idUnitKerja,idSuratInternalTujuan);
        else if(jenisSurat.equalsIgnoreCase(Tag.TAG_TELAAH))
            call = apiService.getHistoriSuratMasuk(token,idSurat,idUnitKerja,idSuratInternalTujuan);
        else if(jenisSurat.equalsIgnoreCase(Tag.TAG_DISPOSISI))
            call = apiService.getHistoriSuratMasuk(token,idSurat,idUnitKerja,idSuratInternalTujuan);

        if(call != null) {

            call.enqueue(new Callback<ResultHistory>() {
                @Override
                public void onResponse(Call<ResultHistory> call, Response<ResultHistory> response) {
                    final ResultHistory result = response.body();

                    logger.d("data histori", response.toString());

                    historis.clear();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            if (result != null) {

                                List<Histori> todos = result.getData();

                                swipeRefreshLayout.setRefreshing(false);

                                if (todos.size() == 0) {
                                    tampilError(true, 5, "Belum ada riwayat");
                                } else {
                                    for (Histori data : todos) {
                                        historis.add(data);
                                    }

                                    mAdapter = new HistoriAdapter(getApplicationContext(), historis);
                                    recyclerView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();

                                }
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                tampilError(true, 3, getString(R.string.error_api));
                            }
                        }
                    });

                }

                @Override
                public void onFailure(Call<ResultHistory> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);

                    tampilError(true, 1, t.getLocalizedMessage());
                }
            });
        }else{
            logger.d("Histori", "call is null");
        }
    }

    public void tampilError(boolean param, int kode, String pesan){

        if(param == true){
            recyclerView.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);

            if(kode == 1)

            if(kode == 2)

            if(kode == 3)

            if(kode == 4)

            if(kode == 5){
                pesan = getString(R.string.error_message_nodata);
                if(Build.MANUFACTURER.contains("Xiaomi")){
                    errorGambar.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_error_outline_dark_24dp, null));
                }else{
                    errorGambar.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_outline_dark_24dp));
                }
            }
            errorText.setText(pesan);

        }else{
            recyclerView.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        if(getIntent().getStringExtra(TAG_ID_SURAT)!=null && getIntent().getStringExtra(TAG_JENISSURAT)!=null){
            if(isConnected){
                swipeRefreshLayout.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                getData();
                            }
                        }
                );
            }else{
                notifikasiDialog.showDialogError(1, "");
            }
        }else{
            notifikasiDialog.showDialogError(100, getString(R.string.message_kurang_parameter));
        }
    }
}
