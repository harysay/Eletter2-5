package id.go.kebumenkab.eletterkebumen.arsip;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
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
import id.go.kebumenkab.eletterkebumen.adapter.pns.ArsipDiajukanAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ArsipDiajukanData;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDiajukan;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISARSIP;

public class ArsipDiajukanActivity extends AppCompatActivity  implements
        SwipeRefreshLayout.OnRefreshListener, ArsipDiajukanAdapter.MessageAdapterListener{

    private List<ArsipDiajukanData> arsips = new ArrayList<>();
    private RecyclerView recyclerView;
    public  ArsipDiajukanAdapter mAdapter;

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

    boolean isLoading = false;

    private int currentPage;
    private int totalPage;
    private int nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arsip);

        logger = new Logger();
        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), ArsipDiajukanActivity.this);

        /** Session yang tersimpan **/
        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ArsipDiajukanAdapter(getApplicationContext(), arsips,
                ArsipDiajukanActivity.this);

        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) findViewById(R.id.error);
        errorText           = (TextView)findViewById(R.id.message_text);
        errorGambar         = (ImageView)findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        isConnected = NetworkUtil.cekInternet(getApplicationContext());

        initScrollListener();

        if(getIntent().getStringExtra(TAG_JENISARSIP) !=  null){
            jenisArsip  = getIntent().getStringExtra(Tag.TAG_JENISARSIP);
            getSupportActionBar().setTitle(getString(R.string.title_activity_arsip)+" "+jenisArsip);

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

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(totalPage>currentPage) {
                    if (!isLoading) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == arsips.size() - 1) {
                            //bottom of list!
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            }
        });


    }

    private void loadMore() {
        nextPage = currentPage +1;
        // arsips.add(null);
        //mAdapter.notifyItemInserted(arsips.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                /*arsips.remove(arsips.size() - 1);
                int scrollPosition = arsips.size();
                mAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    arsips.add(arsips.get(currentSize));
                    currentSize++;
                }

                mAdapter.notifyDataSetChanged();
                isLoading = false;*/

                getDataNext(nextPage);

                currentPage = nextPage;

            }
        }, 2000);


    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);

        tampilError(false, 0, "");
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        currentPage = 1;

        Call<ResultArsipDiajukan> call = apiService.getArsipDiajukan(token,String.valueOf(currentPage));

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_KOREKSI)){
            call = apiService.getArsipDiajukan(token, String.valueOf(currentPage));
        }
        if(call != null) {
            call.enqueue(new Callback<ResultArsipDiajukan>() {
                @Override
                public void onResponse(Call<ResultArsipDiajukan> call, Response<ResultArsipDiajukan> response) {
                    final ResultArsipDiajukan result = response.body();


                    logger.w8("diajukan", response);
                    if(result  != null) {


                        arsips.clear();

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result != null) {


                                    List<ArsipDiajukanData> todos = result.getData();

                                    swipeRefreshLayout.setRefreshing(false);

                                    if (todos.size() == 0) {
                                        tampilError(true, 5 , "");
                                    } else {

                                        totalPage = result.getTotalPage();

                                        for (ArsipDiajukanData data : todos) {
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
                        tampilError(true, 3, "");
                    }

                }

                @Override
                public void onFailure(Call<ResultArsipDiajukan> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);

                    tampilError(true, 1, t.getLocalizedMessage());
                }
            });
        }else{
            logger.d("Arsip", "call is null");
        }
    }

    private void getDataNext(int page) {
        swipeRefreshLayout.setRefreshing(true);

        tampilError(false, 0, "");

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ResultArsipDiajukan> call = apiService.getArsipDiajukanLengkap(token, String.valueOf(page));

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_AJUAN)){
            call = apiService.getArsipDiajukanLengkap(token, String.valueOf(currentPage));
        }

        if(call != null) {
            call.enqueue(new Callback<ResultArsipDiajukan>() {
                @Override
                public void onResponse(Call<ResultArsipDiajukan> call, Response<ResultArsipDiajukan> response) {
                    final ResultArsipDiajukan result = response.body();

                    logger.w8("diajukan", response);
                    if(result  != null) {


                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result != null) {

                                    List<ArsipDiajukanData> todos = result.getData();

                                    swipeRefreshLayout.setRefreshing(false);

                                    if (todos.size() == 0) {
                                        tampilError(true, 5 , "");
                                    } else {

                                        for (ArsipDiajukanData data : todos) {
                                            arsips.add(data);


                                        }
                                        logger.d("Jumlah data", String.valueOf(arsips.size()) );
                                        mAdapter.notifyDataSetChanged();

                                        isLoading = false;
                                    }
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    tampilError(true, 3, getString(R.string.error_api));
                                }
                            }
                        });
                    }else{

                        swipeRefreshLayout.setRefreshing(false);
                        tampilError(true, 3, "");
                    }

                }

                @Override
                public void onFailure(Call<ResultArsipDiajukan> call, Throwable t) {
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

        if (mAdapter.getSelectedItemCount() > 0) {

        } else {

            ArsipDiajukanData arsip = arsips.get(position);

            arsips.set(position, arsip);
            mAdapter.notifyDataSetChanged();

            Intent intentDetail = new Intent(getApplicationContext(), ArsipDiajukanDetail.class);
            intentDetail.putExtra("object", arsip);
            intentDetail.putExtra("position", position);
            intentDetail.putExtra(TAG_ARSIP, TAG_ARSIP);
            intentDetail.putExtra(TAG_JENISARSIP, jenisArsip);

            this.startActivityForResult(intentDetail, 1);
        }

    }

    @Override
    public void onRowLongClicked(int position) {

    }
}
