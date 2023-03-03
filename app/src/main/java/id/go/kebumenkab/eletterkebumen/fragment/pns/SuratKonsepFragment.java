package id.go.kebumenkab.eletterkebumen.fragment.pns;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

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

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.activity.pns.DetailKonsep;
import id.go.kebumenkab.eletterkebumen.activity.pns.DitandaiActivity;
import id.go.kebumenkab.eletterkebumen.adapter.pns.KonsepAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
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


public class SuratKonsepFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, KonsepAdapter.MessageAdapterListener{

    private static final String STATUS = "status";
    private static final String DIBACA = "isRead";

    private PrefManager prefManager;

    public static List<Konsep> konseps = new ArrayList<>();
    private RecyclerView recyclerView;
    public static KonsepAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    private SearchView searchView;
    private LinearLayout errorMessage;
    private TextView errorText;
    private ImageView errorGambar;

    private Logger logger;
    public boolean allowUpdateUI = true;

    private Button btnLihatTandai;

    private String deviceName; // untuk mengecek device tertentu


    public SuratKonsepFragment() {

    }


    public static SuratKonsepFragment newInstance(String status, String isReadParam) {
        SuratKonsepFragment fragment = new SuratKonsepFragment();

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
        logger.d("onCreate","SuratKonsepFragment  | allowUpdate UI "+  allowUpdateUI);

        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        allowUpdateUI = true;
        logger.d("onCreateView","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));


        prefManager = new PrefManager(getActivity());
        deviceName = prefManager.getSessionDevice();

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView        = (RecyclerView)view.findViewById(R.id.recycler_view);
        swipeRefreshLayout  = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) view.findViewById(R.id.error);
        errorText           = (TextView)view.findViewById(R.id.message_text);
        errorGambar         = (ImageView) view.findViewById(R.id.gambar);
        btnLihatTandai      = (Button)view.findViewById(R.id.btn_lihattandai);

        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new KonsepAdapter(getActivity().getApplicationContext(), konseps, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

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
                            getInbox();
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
                    public void run() {
                            getInbox();
                    }
                }
        );
        }else{
            swipeRefreshLayout.setRefreshing(false);
            showDialogError(1);
        }
    }

    @Override
    public void onIconClicked(int position) {
       //  toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
    }

    @Override
    public void onMessageRowClicked(int position) {

            Konsep konsep = konseps.get(position);
//            konsep.setIsRead("1");
//
//            konseps.set(position, konsep);
//            mAdapter.notifyDataSetChanged();

            Intent intentDetail = new Intent(SuratKonsepFragment.this.getContext(), DetailKonsep.class);
            intentDetail.putExtra("object", konsep);
            intentDetail.putExtra("position", position);
            startActivity(intentDetail);

            Dashboard.setRefresh(true);
    }

    @Override
    public void onRowLongClicked(int position) {

    }


    public void tampilError(boolean param, int kode, String pesan){

        if(param == true){
            recyclerView.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);

            if(allowUpdateUI){
                if (kode == 1)
                {}
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
            recyclerView.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.GONE);
        }
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);
        tampilError(false, 0,"");
        recyclerView.setVisibility(View.GONE);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String authorization = prefManager.getSessionToken();

        logger.d("Token Eletter Konsep", authorization);

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


                                    if (todos.size() == 0) {
                                        tampilError(true, 5, "");
                                        Dashboard.setBadge(0, String.valueOf(0));
                                    } else {

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
                                        else btnLihatTandai.setVisibility(View.GONE);

                                        mAdapter.ambilKonsepBelumDitandai();
                                        mAdapter.notifyDataSetChanged();

                                        Dashboard.setBadge(0, String.valueOf(todos.size()));

                                    }
                                    recyclerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);

                                } else {
                                    swipeRefreshLayout.setRefreshing(false);

                                    if(prefManager.getJabatan().toLowerCase().contains("lurah")){
                                        //Dashboard.setBadge(0, String.valueOf(0));
                                    }else{
                                        Dashboard.setBadge(0, String.valueOf(0));

                                    }

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
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
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

            case R.id.action_date:

                return true;
        }

        return false;
    }






    @Override
    public void onResume() {
        super.onResume();

        allowUpdateUI = true;

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                getInbox();
                // Toast.makeText(getActivity(), "Sedang mengambil data", Toast.LENGTH_SHORT).show();
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
        mAdapter.notifyDataSetChanged();
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
                        getInbox();

                        logger.d("OnResume","SuratKonsepFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));
                    }
                });
            }

            }
    }
}

