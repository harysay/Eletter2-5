package id.go.kebumenkab.eletterkebumen.arsip.desa;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.desa.WebViewDesaLurahActivity;
import id.go.kebumenkab.eletterkebumen.adapter.desa.ArsipDisetujuiAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ArsipSuratDitandatanganiDesa;
import id.go.kebumenkab.eletterkebumen.model.ResultSuratDitandatangani;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISARSIP;

public class ArsipDisetujuiActivity extends AppCompatActivity  implements
        SwipeRefreshLayout.OnRefreshListener, ArsipDisetujuiAdapter.MessageAdapterListener {

    private List<ArsipSuratDitandatanganiDesa> arsips = new ArrayList<>();
    private RecyclerView recyclerView;
    public  ArsipDisetujuiAdapter mAdapter;
    private PrefManager prefManager;
    private String token;
    private String jenisArsip;
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
        setContentView(R.layout.activity_arsip);

        logger = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), ArsipDisetujuiActivity.this);

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_arsip_layanan_mandiri_disetujui));

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ArsipDisetujuiAdapter(getApplicationContext(), arsips,
                ArsipDisetujuiActivity.this);

        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) findViewById(R.id.error);
        errorText           = (TextView)findViewById(R.id.message_text);
        errorGambar         = (ImageView)findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        isConnected = NetworkUtil.cekInternet(getApplicationContext());

        if(getIntent().getStringExtra(TAG_JENISARSIP) !=  null){
            jenisArsip  = getIntent().getStringExtra(Tag.TAG_JENISARSIP);

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
                notifikasiDialog.showDialogError(1,"");
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

        tampilError(false, 0, "");
        ApiInterfaceDesa apiService =
                ApiClientDesa.getClient(getApplicationContext()).create(ApiInterfaceDesa.class);

        Call<ResultSuratDitandatangani> call = apiService.getSurat(Config.AUTHORIZATION,prefManager.getIdPerangkat());

        if(call != null) {
            call.enqueue(new Callback<ResultSuratDitandatangani>() {
                @Override
                public void onResponse(Call<ResultSuratDitandatangani> call, Response<ResultSuratDitandatangani> response) {
                    final ResultSuratDitandatangani result = response.body();
                    logger.w11("resultditandatangani", response);

                    if(result  != null) {
                        arsips.clear();

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getData() != null) {

                                    List<ArsipSuratDitandatanganiDesa> datas = result.getData();

                                    swipeRefreshLayout.setRefreshing(false);

                                    if (datas.size() == 0) {
                                        String pesan = result.getPesan();
                                        logger.d("Pesan", pesan);
                                         tampilError(true, 1 , pesan);
                                    } else {

                                        for (ArsipSuratDitandatanganiDesa data : datas) {
                                            arsips.add(data);
                                        }

                                        mAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    tampilError(true, 1, result.getPesan());
                                }
                            }
                        });
                    }else{
                        swipeRefreshLayout.setRefreshing(false);
                        tampilError(true, 1, "");
                    }

                }

                @Override
                public void onFailure(Call<ResultSuratDitandatangani> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);

                    tampilError(true, 1, t.getLocalizedMessage());
                }
            });
        }else{
            logger.d("Arsip", "call is null");
        }
    }

    public void tampilError(boolean param, int kode, String pesan){

        if(param == true){
            recyclerView.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);

            if(kode == 1){
                errorText.setText(pesan);
            }
            if(kode == 2){
                errorText.setText(pesan);
            }

            if(kode == 3){
                errorText.setText(pesan);
            }

            if(kode == 4){
                errorText.setText(pesan);
            }

            if(kode == 5){
                pesan = getString(R.string.error_message_nodata);
                errorText.setText(pesan);
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
        if(getIntent().getStringExtra(TAG_JENISARSIP) !=  null){
            jenisArsip  = getIntent().getStringExtra(Tag.TAG_JENISARSIP);
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
                notifikasiDialog.showDialogError(1,"");
            }

        }else{
            notifikasiDialog.showDialogError(100, getString(R.string.message_kurang_parameter));
            jenisArsip ="";
        }
    }



    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {

        /** Ketika  salah satu item Diklik lemparkan ke detail surat masuk **/


            ArsipSuratDitandatanganiDesa arsip = arsips.get(position);

            Intent intentDetail = new Intent(getApplicationContext(), WebViewDesaLurahActivity.class);
            intentDetail.putExtra(TAG_ID_SURAT, arsip.getId());
            intentDetail.putExtra(TAG_ARSIP, TAG_ARSIP);

            this.startActivityForResult(intentDetail, 1);


    }

    @Override
    public void onRowLongClicked(int position) {

    }
}
