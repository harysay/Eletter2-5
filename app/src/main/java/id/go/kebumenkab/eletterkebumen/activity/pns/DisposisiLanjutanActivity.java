package id.go.kebumenkab.eletterkebumen.activity.pns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.adapter.pns.TindakanAdapter;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.DividerItemDecoration;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Bawahan;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResultTindakan;
import id.go.kebumenkab.eletterkebumen.model.Tindakan;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_HISTORI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;

public class DisposisiLanjutanActivity extends AppBaseActivity implements TindakanAdapter.TindakanAdapterListener {

    private RecyclerView recyclerView ;
    private ProgressDialog pDialog;

    private PrefManager prefManager;
    private String token;

    private Button kirim;
    private EditText editPesan,passPhraseInput;
    private LinearLayout inputKhusuBupati;
    private List<Bawahan> listBawahan  = new ArrayList<>();
    private List<Tindakan> listTindakan = new ArrayList<>();
    private List<Tindakan> listTindakanAkanDikirim = new ArrayList<>();

    private String idHistori;
    private String idSurat;
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposisi_2);

        registerBaseActivityReceiver();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_tindakan));



        prefManager = new PrefManager(this);
        token       = prefManager.getSessionToken();
        logger      = new Logger();
        notifikasiDialog  = new NotifikasiDialog(getApplicationContext(), DisposisiLanjutanActivity.this);

        pDialog     = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));

        editPesan = (EditText)findViewById(R.id.pesan);

        recyclerView = (RecyclerView) findViewById(R.id.listview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        kirim = (Button)findViewById(R.id.kirim);
        inputKhusuBupati = (LinearLayout)findViewById(R.id.inputankhususbupati);
        passPhraseInput = (EditText)findViewById(R.id.passphrasebupati);

        Intent i = getIntent();
        listBawahan = (List<Bawahan>) i.getSerializableExtra("LIST");
        Logger logger = new Logger();
        logger.d("list bawahan size", String.valueOf(listBawahan.size()));

        for(int x=0; x < listBawahan.size();x++){
            logger.d("list bawahan", listBawahan.get(x).getNipBaru());
        }

        if(getIntent().getStringExtra(TAG_ID_SURAT).length()>0)
            idSurat = getIntent().getStringExtra(TAG_ID_SURAT);

        if(getIntent().getStringExtra(TAG_ID_HISTORI).length()>0)
            idHistori = getIntent().getStringExtra(TAG_ID_HISTORI);

        if(prefManager.getStatusJabatan().equalsIgnoreCase("bupati")||prefManager.getStatusJabatan().equalsIgnoreCase("wakilbupati")){
            inputKhusuBupati.setVisibility(View.VISIBLE);
        }
        logger.d("DP Lanjutan ID SURAT", idSurat);
        logger.d("DP Lanjutan ID HISTORI", idHistori);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showTindakanFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    public static void setListViewHeightBasedOnChildren
            (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount()-1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void showDialog() {
        if (!pDialog.isShowing()) pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing()) pDialog.dismiss();
    }


    public void showTindakanFromServer(){
        /** Mengambil daftar bawahan ke server **/
        showDialog();

        ApiInterface apiService =
                ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

        Call<ResultTindakan> call =  apiService.getTindakan(token);

        call.enqueue(new Callback<ResultTindakan>() {
            @Override
            public void onResponse(Call<ResultTindakan> call, Response<ResultTindakan> response) {

                hideDialog();
                logger.w6("Respon gson ", response);

                final ResultTindakan data = response.body();
                if(data.getStatus().equals(Tag.TAG_STATUS_SUKSES)){
                    /** Hasil sukses **/
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            List<Tindakan> dataList = data.getData();

                            if(dataList.size()>0)
                                showTindakan(dataList);

                            else notifikasiDialog.showDialogError(3,"");
                        }
                    });

                }else{
                    /** Tidak sukses **/
                    notifikasiDialog.showDialogError(2, "");
                }

            }

            @Override
            public void onFailure(Call<ResultTindakan> call, Throwable t) {
                hideDialog();

                notifikasiDialog.showToast("Error: "
                        + t.getMessage());

            }
        });
    }

    public void showTindakan(final List<Tindakan> tindakanList){

        /** Menampilkan daftar tindakan  **/
        listTindakan =  tindakanList;
        TindakanAdapter adapter = new TindakanAdapter(getApplicationContext(), listTindakan, this);

        recyclerView.setAdapter(adapter);



        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pesan = editPesan.getText().toString();
                if(prefManager.getStatusJabatan().equalsIgnoreCase("bupati")||prefManager.getStatusJabatan().equalsIgnoreCase("wakilbupati")){
                    passPhraseInput.getText().toString();
                    logger.d("Disposisi", String.valueOf(listTindakanAkanDikirim.size()) + "/" + String.valueOf(pesan.length()));

                    if (listTindakanAkanDikirim.size() == 0)
                        notifikasiDialog.showDialogError(8, "");
                    else if (pesan.length() == 0)
                        notifikasiDialog.showDialogError(8, "");
                    else
                        postAksi(Tag.TAG_DISPOSISI, editPesan.getText().toString());
//                        postAksiKhususBupati(Tag.TAG_DISPOSISI, editPesan.getText().toString());
                }else {
                    logger.d("Disposisi", String.valueOf(listTindakanAkanDikirim.size()) + "/" + String.valueOf(pesan.length()));

                    if (listTindakanAkanDikirim.size() == 0)
                        notifikasiDialog.showDialogError(8, "");
                    else if (pesan.length() == 0)
                        notifikasiDialog.showDialogError(8, "");
                    else
                        postAksi(Tag.TAG_DISPOSISI, editPesan.getText().toString());
                }
            }
        });
    }

    private void postAksi(final String statusSurat, final String pesan) {

        boolean isConnected = NetworkUtil.cekInternet(getApplicationContext());

        if(isConnected){
            if(statusSurat.length()==0 || statusSurat.isEmpty()){
                notifikasiDialog.showToast("Status surat: "+ statusSurat);
            }else {
                ApiInterface apiService =
                        ApiClient.loginRequest(getApplicationContext()).create(ApiInterface.class);

                logger.d("status surat", statusSurat+"/"+pesan);

                ArrayList<String> bawahan = new ArrayList<>();
                for(int i= 0; i<listBawahan.size();i++){
                    bawahan.add(listBawahan.get(i).getNipBaru());
                    logger.d("bawahan", listBawahan.get(i).getNipBaru());
                }

                ArrayList<String> tindakan = new ArrayList<>();
                for(int i= 0; i<listTindakanAkanDikirim.size();i++){
                    tindakan.add(listTindakanAkanDikirim.get(i).getIdSuratDisposisiTindakan());
                    logger.d("id tindakan", listTindakanAkanDikirim.get(i).getTindakan());
                }


                Call<ResponStandar>  call = apiService.sendDisposisi(token, pesan, bawahan, tindakan, idSurat, idHistori);

                if (call != null) {
                    showDialog();

                    call.enqueue(new Callback<ResponStandar>() {
                        @Override
                        public void onResponse(Call<ResponStandar> call, Response<ResponStandar> response) {
                            hideDialog();

                            ResponStandar result = response.body();
//                            logger.d("result disposisi", response.toString());
//                            logger.d("status disposisi", result.getStatus());
//                            logger.d("pesan disposisi", result.getPesan());


                            // Tutup halaman dan buka halaman utama
                            notifikasiDialog.showDialogError(9, "");
                        }

                        @Override
                        public void onFailure(Call<ResponStandar> call, Throwable t) {
                            hideDialog();
                            logger.d("Error Failure", t.getMessage());

                            if(!isFinishing()){
                                notifikasiDialog.showDialogError(100, t.getLocalizedMessage());
                            }



                        }
                    });
                }
            }
        }else{
            notifikasiDialog.showDialogError(1, "");
        }
    }

    @Override
    public void onMessageRowClicked(int position) {

        Tindakan tindakan = listTindakan.get(position);

        Logger logger = new Logger();
        logger.d("message row cliked", tindakan.getTindakan());

        if(listTindakanAkanDikirim.contains(tindakan))
            listTindakanAkanDikirim.remove(tindakan);
        else listTindakanAkanDikirim.add(tindakan);

    }

    @Override
    public void onRowLongClicked(int position) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}


