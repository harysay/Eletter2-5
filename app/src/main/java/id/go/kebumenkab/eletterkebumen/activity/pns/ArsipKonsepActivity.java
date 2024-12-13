package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
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
import id.go.kebumenkab.eletterkebumen.adapter.pns.KonsepAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsep;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_KOREKSI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_SETUJU;

public class ArsipKonsepActivity extends AppCompatActivity  implements
        SwipeRefreshLayout.OnRefreshListener, KonsepAdapter.MessageAdapterListener{

    private List<Konsep> arsips = new ArrayList<>();
    private RecyclerView recyclerView;
    public KonsepAdapter mAdapter;

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
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), ArsipKonsepActivity.this);

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new KonsepAdapter(getApplicationContext(), arsips,
                ArsipKonsepActivity.this);

        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) findViewById(R.id.error);
        errorText           = (TextView)findViewById(R.id.message_text);
        errorGambar         = (ImageView)findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        isConnected = NetworkUtil.cekInternet(getApplicationContext());

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

        tampilError(false, 0,"");

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ResultKonsep> call = null;

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_AJUAN)){
            call = apiService.getArsipDiajukan(token);
        }else if(jenisArsip.equalsIgnoreCase(Tag.TAG_KOREKSI)){
            call = apiService.getArsipDikoreksi(token);
        }else if(jenisArsip.equalsIgnoreCase(Tag.TAG_SETUJU)){
            call = apiService.getArsipDisetujui(token);
        }

        logger.d("jenisArsip", jenisArsip);


        if(call != null) {

            call.enqueue(new Callback<ResultKonsep>() {
                @Override
                public void onResponse(Call<ResultKonsep> call, Response<ResultKonsep> response) {
                    final ResultKonsep result = response.body();
                    logger.d("Hasil", response.body().toString());
                    if(result  != null) {

                        logger.w("Hasil", response);

                        arsips.clear();

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result != null) {

                                    List<Konsep> todos = result.getData();

                                    swipeRefreshLayout.setRefreshing(false);

                                    if (todos.size() == 0) {
                                        tampilError(true, 5,"");
                                    } else {
                                        for (Konsep data : todos) {

                                            arsips.add(data);
                                        }

                                        mAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    tampilError(true, 3, getString(R.string.error_api));
                                }
                            }
                        });
                    }else{

                        swipeRefreshLayout.setRefreshing(false);
                        tampilError(true, 3, getString(R.string.error_api));
                    }

                }

                @Override
                public void onFailure(Call<ResultKonsep> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);

                    tampilError(true, 1, t.getLocalizedMessage());
                }
            });
        }else{
            logger.d("Arsip", "call is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    // Menampilkan error
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
        }
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

            Konsep arsip = arsips.get(position);

            arsips.set(position, arsip);
            mAdapter.notifyDataSetChanged();

            Intent intentDetail = new Intent(getApplicationContext(), DetailKonsep.class);

            if(jenisArsip.equalsIgnoreCase(Tag.TAG_AJUAN)){
                intentDetail.putExtra(TAG_JENISARSIP, TAG_AJUAN);
            }else if(jenisArsip.equalsIgnoreCase(Tag.TAG_KOREKSI)){
                intentDetail.putExtra(TAG_JENISARSIP, TAG_KOREKSI);
            }else if(jenisArsip.equalsIgnoreCase(Tag.TAG_SETUJU)){
                intentDetail.putExtra(TAG_JENISARSIP, TAG_SETUJU);
            }

            intentDetail.putExtra("object", arsip);
            intentDetail.putExtra("position", position);
            intentDetail.putExtra(TAG_ARSIP, TAG_ARSIP);

            this.startActivityForResult(intentDetail, 1);
        }

    }

//    @Override
//    public void onRowLongClicked(int position) {
//
//    }

    @Override
    public void onItemClicked(Konsep konsep,int position) {

    }
}
