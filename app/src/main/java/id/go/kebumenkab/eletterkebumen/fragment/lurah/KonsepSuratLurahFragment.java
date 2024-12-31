package id.go.kebumenkab.eletterkebumen.fragment.lurah;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.activity.pns.DetailKonsep;
import id.go.kebumenkab.eletterkebumen.activity.pns.DitandaiActivity;
import id.go.kebumenkab.eletterkebumen.activity.desa.WebViewDesaLurahActivity;
import id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah;
import id.go.kebumenkab.eletterkebumen.adapter.pns.KonsepAdapter;
import id.go.kebumenkab.eletterkebumen.adapter.lurah.KonsepLurahAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Config;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.KonsepDesa;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsep;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsepDesa;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;


public class KonsepSuratLurahFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        KonsepAdapter.MessageAdapterListener,
        KonsepLurahAdapter.MessageAdapterListener{

    private static final String STATUS = "status";
    private static final String DIBACA = "isRead";

    private PrefManager prefManager;

    public static List<Konsep> konseps = new ArrayList<>();
    public static List<KonsepDesa> konsepsLurah = new ArrayList<>();
    private RecyclerView recyclerViewKonsepOPD;
    private RecyclerView recyclerViewKonsepLurah;
    public static KonsepAdapter mAdapterOPD;
    public static KonsepLurahAdapter mAdapterLurah;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    private SearchView searchView;
    private LinearLayout errorMessage;
    private TextView errorText;
    private TextView header1, header2;
    private ImageView errorGambar;

    private Logger logger;
    public boolean allowUpdateUI = true;

    private Button btnLihatTandai;

    private String isiSuratOpd;
    private String isiSuratLurah;

    private String deviceName; // untuk mengecek device tertentu

    private int jumlahKonsepBelumDibuka = 0;
    public KonsepSuratLurahFragment() {

    }


    public static KonsepSuratLurahFragment newInstance(String status, String isReadParam) {
        KonsepSuratLurahFragment fragment = new KonsepSuratLurahFragment();

        Bundle args = new Bundle();
        args.putString(STATUS, status);
        args.putString(DIBACA, isReadParam);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new Logger();
        logger.d("onCreate","SuratKonsepFragment Lurah | allowUpdate UI "+  allowUpdateUI);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        allowUpdateUI = true;
        logger.d("onCreateView","SuratKonsepFragment Lurah | allowUpdateUI "+ String.valueOf(allowUpdateUI));


        prefManager = new PrefManager(getActivity());
        deviceName = prefManager.getSessionDevice();

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_konsep_lurah, container, false);

        recyclerViewKonsepOPD        = (RecyclerView)view.findViewById(R.id.recycler_view_konsep);
        recyclerViewKonsepLurah        = (RecyclerView)view.findViewById(R.id.recycler_view_konsep_lurah);

        swipeRefreshLayout  = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) view.findViewById(R.id.error);
        errorText           = (TextView)view.findViewById(R.id.message_text);
        errorGambar         = (ImageView) view.findViewById(R.id.gambar);
        btnLihatTandai      = (Button)view.findViewById(R.id.btn_lihattandai);

        header1             = (TextView)view.findViewById(R.id.header_surat_1); // OPD
        header2             = (TextView)view.findViewById(R.id.header_surat_2); // Layanan Mandiri

        header1.setVisibility(GONE);
        header2.setVisibility(GONE);

        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapterOPD = new KonsepAdapter(getActivity().getApplicationContext(), konseps, this);
        mAdapterLurah =  new KonsepLurahAdapter(getActivity().getApplicationContext(), konsepsLurah, this);



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewKonsepOPD.setLayoutManager(mLayoutManager);
        recyclerViewKonsepOPD.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewKonsepOPD.setAdapter(mAdapterOPD);

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());

        recyclerViewKonsepLurah.setLayoutManager(mLayoutManager2);
        recyclerViewKonsepLurah.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewKonsepLurah.setAdapter(mAdapterLurah);

        btnLihatTandai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DitandaiActivity.class);
                startActivity(intent);
            }
        });

        // mengaktifkan menu
        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean isConnected = NetworkUtil.cekInternet(getActivity());

        if(isConnected){
            // show loader and fetch konseps
            swipeRefreshLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            jumlahKonsepBelumDibuka = 0;
                            getInboxOPD();
                        }
                    }
            );
        }else {
            showDialogError(1);
        }
    }

    @Override
    public void onRefresh() {
        boolean isConnected = NetworkUtil.cekInternet(getActivity());
        if(isConnected){
        // show loader and fetch konseps
        swipeRefreshLayout.post(
            new Runnable() {
                @Override
                public void run()
                {
                    jumlahKonsepBelumDibuka = 0;
                    getInboxOPD();
                }
            }
        );
        }else{
            swipeRefreshLayout.setRefreshing(false);
            showDialogError(1);
        }
    }

//    @Override
//    public void onIconClicked(int position) {
//       //  toggleSelection(position);
//    }
//
//    @Override
//    public void onIconImportantClicked(int position) {
//    }

    @Override
    public void onMessageRowClicked(int position) {

            Konsep konsep = konseps.get(position);

            Intent intentDetail = new Intent(KonsepSuratLurahFragment.this.getContext(), DetailKonsep.class);
            intentDetail.putExtra("object", konsep);
            intentDetail.putExtra("position", position);
            startActivity(intentDetail);

            Dashboard.setRefresh(true);
    }

//    @Override
//    public void onRowLongClicked(int position) {
//
//    }

    @Override
    public void onItemClicked(Konsep konsep,int position) {

        Intent intentDetail = new Intent(KonsepSuratLurahFragment.this.getContext(), DetailKonsep.class);
        intentDetail.putExtra("object", konsep);
        intentDetail.putExtra("position", position);
        startActivity(intentDetail);

        Dashboard.setRefresh(true);

    }


    public void tampilError(boolean param, int kode, String pesan){

        if(param == true){
            if(pesan=="opdkosong"){
                header1.setVisibility(GONE);
                recyclerViewKonsepOPD.setVisibility(GONE);
            } else if (pesan=="lurahkosong") {
                header2.setVisibility(GONE);
                recyclerViewKonsepLurah.setVisibility(GONE);
            }
//            recyclerViewKonsepOPD.setVisibility(GONE);
//            recyclerViewKonsepLurah.setVisibility(GONE);
            errorMessage.setVisibility(View.VISIBLE);

            if(allowUpdateUI){
                if (kode == 1)
                {
                    errorMessage.setVisibility(GONE);
                }
                else if (kode == 2)
                {}
                else if (kode == 3)
                {}
                else if (kode == 4)
                {}
                else if (kode == 5) {
                    errorText.setText(getActivity().getString(R.string.error_konsep_empty));

                    // ini solusi untuk Xiaomi redmi 2 hape mas Didik
                    if(deviceName.contains("Xiaomi"))
                        errorGambar.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_error_outline_dark_24dp, null));
                    else
                        errorGambar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_error_outline_dark_24dp));

                }else{
                    errorText.setText(pesan);
                }
            }
        }else{
            recyclerViewKonsepOPD.setVisibility(View.VISIBLE);
            recyclerViewKonsepLurah.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(GONE);
        }
    }

    @Override
    public void onKonsepLurahClicked(int position) {

        KonsepDesa konsep = konsepsLurah.get(position);

        Intent intentDetail = new Intent(KonsepSuratLurahFragment.this.getContext(), WebViewDesaLurahActivity.class);
        intentDetail.putExtra(Tag.TAG_ID_SURAT, konsep.getId());
        intentDetail.putExtra(Tag.TAG_ALUR_SURAT, Tag.TAG_AJUAN);
        startActivity(intentDetail);


        DashboardLurah.setRefresh(true);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void getInboxOPD() {
        swipeRefreshLayout.setRefreshing(true);
        tampilError(false, 0,"");
        recyclerViewKonsepOPD.setVisibility(GONE);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String authorization = prefManager.getSessionToken();

        logger.d("GET INBOX OPD Token Eletter Konsep", authorization);

        Call<ResultKonsep> call = apiService.getKonsep(authorization);

        call.enqueue(new Callback<ResultKonsep>() {
            @Override
            public void onResponse(Call<ResultKonsep> call, Response<ResultKonsep> response) {
                final ResultKonsep result = response.body();
                logger.d("hasil", response.toString());
                if(result !=null) {
                    konseps.clear();

                    if(allowUpdateUI) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                                    List<Konsep> todos = result.getData();
//                                    List<Konsep> todos = new ArrayList<>();
                                    if (todos.size() == 0) {
//                                        isiSuratOpd = "opdkosong";
                                        tampilError(true, 5, "opdkosong");
                                        DashboardLurah.setBadge(0, String.valueOf(0));
//                                        header1.setVisibility(GONE);
                                    } else {
                                        isiSuratOpd = "opdisi";
                                        header1.setVisibility(View.VISIBLE);
                                        jumlahKonsepBelumDibuka += todos.size();

                                        int jumlahDitandai = 0;

                                        for (Konsep konsep : todos) {

                                            logger.d("hasil2", konsep.getIdSurat()+"/"+konsep.getSubject());
                                            konsep.setColor(R.color.colorAccent);
                                            logger.d("Respon", konsep.getFrom().toString());

                                            if(konsep.getTandai().equals("1")){
                                                jumlahDitandai++;
                                            }

                                            if(konsep.getTandai().equals("0")){
                                                konseps.add(konsep);
                                            }

                                        }


                                        if(jumlahDitandai>0) btnLihatTandai.setVisibility(View.VISIBLE);
                                        else btnLihatTandai.setVisibility(GONE);

                                        mAdapterOPD.ambilKonsepBelumDitandai();
                                        mAdapterOPD.notifyDataSetChanged();

                                        DashboardLurah.setBadge(0, String.valueOf(jumlahKonsepBelumDibuka));


                                    }
                                    recyclerViewKonsepOPD.setVisibility(View.VISIBLE);
                                    recyclerViewKonsepLurah.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);


                                    // ambil data dari desa online
                                    getInboxLurah();

                                } else {
                                    swipeRefreshLayout.setRefreshing(false);

                                    DashboardLurah.setBadge(1, String.valueOf(0));

                                    tampilError(true, 3, "");
                                }
                            }
                        });
                    }
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                    tampilError(true, 3, "");
                }
            }

            @Override
            public void onFailure(Call<ResultKonsep> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);

                String errorType;
                String errorDesc;

                if (t instanceof IOException) {
                    errorType = "Timeout";
                    errorDesc = String.valueOf(t.getCause());
                }
                else if (t instanceof IllegalStateException) {
                    errorType = "ConversionError";
                    errorDesc = String.valueOf(t.getCause());
                } else {
                    errorType = "Other Error";
                    errorDesc = String.valueOf(t.getLocalizedMessage());
                }


                logger.d(errorType, errorDesc);

                tampilError(true, 100, errorType);


            }
        });
    }

    private void getInboxLurah() {
        swipeRefreshLayout.setRefreshing(true);
        tampilError(false, 0,"");
        recyclerViewKonsepLurah.setVisibility(GONE);

        ApiInterfaceDesa apiService =
                ApiClientDesa.getClient(getContext()).create(ApiInterfaceDesa.class);

        String authorization = Config.AUTHORIZATION;
        String idPerangkat = prefManager.getIdPerangkat();

        logger.d("Token Eletter Konsep", authorization);

        if(!authorization.isEmpty() && !idPerangkat.isEmpty()){
            String useragent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";
            Call<ResultKonsepDesa> call = apiService.getKonsep(authorization,useragent, idPerangkat);

            call.enqueue(new Callback<ResultKonsepDesa>() {
                @Override
                public void onResponse(Call<ResultKonsepDesa> call, Response<ResultKonsepDesa> response) {
                    final ResultKonsepDesa result = response.body();
                    if(result !=null) {
                        konsepsLurah.clear();

                        logger.w10("hasil", response);

                        if(allowUpdateUI) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                                        List<KonsepDesa> todos = result.getData();
//                                        List<KonsepDesa> todos = new ArrayList<>();
                                        if (todos.size() == 0) {
//                                            isiSuratLurah = "kosong";
                                            if(isiSuratOpd=="opdisi") {
                                                tampilError(true, 1, "lurahkosong");
                                            }else {
                                                tampilError(true, 5, "lurahkosong");
                                            }

//                                            header2.setVisibility(GONE);
//                                            recyclerViewKonsepLurah.setVisibility(GONE);

                                            DashboardLurah.setBadge(0, String.valueOf(jumlahKonsepBelumDibuka));

                                        } else {
                                            header2.setVisibility(View.VISIBLE);
                                            recyclerViewKonsepLurah.setVisibility(View.VISIBLE);
                                            jumlahKonsepBelumDibuka += todos.size();


                                            for (KonsepDesa konsep : todos) {
                                                konsepsLurah.add(konsep);
                                            }


                                            DashboardLurah.setBadge(0, String.valueOf(jumlahKonsepBelumDibuka));


                                        }
//                                        recyclerViewKonsepLurah.setVisibility(View.VISIBLE);
                                        swipeRefreshLayout.setRefreshing(false);

                                    } else {
                                        swipeRefreshLayout.setRefreshing(false);
                                        String pesan = result.getPesan();

                                        DashboardLurah.setBadge(0, String.valueOf(0));
                                        tampilError(true, 100, pesan);
                                    }
                                }
                            });
                        }else{
                            logger.w10("hasil", response);
                        }
                    }else{
                        swipeRefreshLayout.setRefreshing(false);
                        DashboardLurah.setBadge(0, String.valueOf(0));


                        tampilError(true, 3, "");
                    }
                }

                @Override
                public void onFailure(Call<ResultKonsepDesa> call, Throwable t) {

                    swipeRefreshLayout.setRefreshing(false);

                    String errorType;
                    String errorDesc;

                    if (t instanceof IOException) {
                        errorType = "Timeout";
                        errorDesc = String.valueOf(t.getCause());
                    }
                    else if (t instanceof IllegalStateException) {
                        errorType = "ConversionError";
                        errorDesc = String.valueOf(t.getCause());
                    } else {
                        errorType = "Other Error";
                        errorDesc = String.valueOf(t.getLocalizedMessage());
                    }


                    logger.d(errorType, errorDesc);

                    //tampilError(true, 100, errorType);


                }
            });
        }


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       // inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapterOPD.getFilter().filter(query);
                mAdapterLurah.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapterOPD.getFilter().filter(query);
                mAdapterLurah.getFilter().filter(query);
                return false;
            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                // do stuff
                return true;


        }

        return false;
    }






    @Override
    public void onResume() {
        super.onResume();

        allowUpdateUI = true;
        if(allowUpdateUI)

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI
                    getInboxOPD();
                }
            });



        deviceName =prefManager.getSessionDevice();

        logger.d("OnResume","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI)+ " - " +  deviceName);
    }

    @Override
    public void onPause() {
        super.onPause();

        allowUpdateUI = false;

        logger.d("onPause","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onStop() {
        super.onStop();

        allowUpdateUI=  false;

        logger.d("onStop","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allowUpdateUI = false;
        logger.d("onDestroyView","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allowUpdateUI =false;
        logger.d("onDestroy","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/

    public void showDialogError(int i){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = null;
        if(i==1){
            dialogView = inflater.inflate(R.layout.dialog_warning, null);
        }else if(i==2){
            dialogView = inflater.inflate(R.layout.dialog_warning_server, null);
        }
        builder.setView(dialogView);
        builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public static void updateDataList(List<Konsep> arrayList){
        konseps = arrayList;
        mAdapterLurah.notifyDataSetChanged();
    }

    public void reloadData(){
        Logger logger = new Logger();
        logger.d("Pembaruan dari firebase", "Pembaruan dari firebase");

        new Thread(new Task()).start();
    }

    class Task implements Runnable {
        @Override
        public void run() {
            if(allowUpdateUI)  {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        getInboxOPD();

                        logger.d("OnResume","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));
                    }
                });
            }

            }
    }
}


