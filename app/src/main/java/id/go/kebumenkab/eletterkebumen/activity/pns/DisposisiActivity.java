package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.adapter.pns.BawahanAdapter;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.DividerItemDecoration;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Bawahan;
import id.go.kebumenkab.eletterkebumen.model.ResultBawahan;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_HISTORI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;

public class DisposisiActivity extends AppBaseActivity implements BawahanAdapter.BawahanAdapterListener{

    private  RecyclerView recyclerView ;
    private Button lanjutkan;
    private PrefManager prefManager;
    private String token;

    private ProgressDialog pDialog;
    private List<Bawahan> listBawahan = new ArrayList<>();
    private List<Bawahan> listBawahanAkanDikirim =  new ArrayList<Bawahan>();
    private BawahanAdapter adapter;
    private SearchView searchView;


    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposisi);

        registerBaseActivityReceiver();

        // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_bawahan));


        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();

        notifikasiDialog = new NotifikasiDialog(getApplicationContext(), DisposisiActivity.this);
        logger = new Logger();

        pDialog     = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));

        recyclerView = (RecyclerView) findViewById(R.id.listView);
        lanjutkan =  (Button)findViewById(R.id.kirim);

        adapter = new BawahanAdapter(getApplicationContext(), listBawahan, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

       // recyclerView.setAdapter(adapter);

        String idSurat = "";
        String idHistori = "";

        if(getIntent().getStringExtra(TAG_ID_SURAT).length()>0)
            idSurat = getIntent().getStringExtra(TAG_ID_SURAT);

        if(getIntent().getStringExtra(TAG_ID_HISTORI).length()>0)
            idHistori = getIntent().getStringExtra(TAG_ID_HISTORI);

        final String finalIdSurat = idSurat;
        final String finalIdHistori = idHistori;

        logger.d("ID SURAT", finalIdSurat);
        logger.d("ID HISTORI", finalIdHistori);
        logger.d("List Bawahan size", String.valueOf(listBawahanAkanDikirim.size()));

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listBawahanAkanDikirim.size()>0){
                    Intent intent = new Intent (getApplicationContext(), DisposisiLanjutanActivity.class);
                    intent.putExtra("LIST", (Serializable) listBawahanAkanDikirim);
                    intent.putExtra(TAG_ID_SURAT, finalIdSurat);
                    intent.putExtra(TAG_ID_HISTORI, finalIdHistori);

                    startActivity(intent);
                }else{
                    notifikasiDialog.showDialogError(3, "");
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listBawahanAkanDikirim.size()>0) listBawahanAkanDikirim.clear();
        showBawahanFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cari, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }


        });

        return true;
    }

    public void showBawahanFromServer(){
        /** Mengambil daftar bawahan ke server **/
        showDialog();

        ApiInterface apiService =
                ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

        Call<ResultBawahan> call =  apiService.getBawahan(token);

        call.enqueue(new Callback<ResultBawahan>() {
            @Override
            public void onResponse(Call<ResultBawahan> call, Response<ResultBawahan> response) {

                hideDialog();

                final ResultBawahan data = response.body();

                Log.d("Jabatan", response.toString());

                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){
                    /** Hasil sukses **/
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            List<Bawahan> dataList = data.getData();
                            listBawahan = dataList;

                            //adapter.notifyDataSetChanged();

                            if(dataList.size()>0)
                                showBawahan(dataList);

                            else notifikasiDialog.showDialogError(3, "");
                        }
                    });

                }else{
                    /** Tidak sukses **/
                    notifikasiDialog.showDialogError(2, "");
                }

            }

            @Override
            public void onFailure(Call<ResultBawahan> call, Throwable t) {
                hideDialog();

                notifikasiDialog.showToast("Unable to fetch json: "
                        + t.getMessage());

            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing()) pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing()) pDialog.dismiss();
    }

    public void showBawahan(final List<Bawahan> bawahanList){

        /** Menampilkan daftar bawahan  **/
        adapter = new BawahanAdapter(getApplicationContext(), bawahanList, this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onMessageRowClicked(int position) {
        Bawahan bawahan = adapter.getItem(position);

        if(bawahan.isSelect())   bawahan.setSelected(false);
        else bawahan.setSelected(true);

        Logger logger = new Logger();
        logger.d("message row cliked", bawahan.getNama());
        if(listBawahanAkanDikirim.contains(bawahan)){
            listBawahanAkanDikirim.remove(bawahan);
        }else{
            listBawahanAkanDikirim.add(bawahan);
        }

        adapter.notifyItemChanged(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}



