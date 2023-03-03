package id.go.kebumenkab.eletterkebumen.fragment.lurah;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import id.go.kebumenkab.eletterkebumen.activity.pns.WebViewActivity;
import id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah;
import id.go.kebumenkab.eletterkebumen.adapter.pns.PemberitahuanAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Pemberitahuan;
import id.go.kebumenkab.eletterkebumen.model.ResultPemberitahuan;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ALUR_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_JENISSURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PENGEMBALIAN;

public class PemberitahuanFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, PemberitahuanAdapter.MessageAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS = "status";
    private static final String DIBACA = "isRead";

    // TODO: Rename and change types of parameters

    private PrefManager prefManager;

    private String strJabatan;

    public static List<Pemberitahuan> pemberitahuans = new ArrayList<>();
    private RecyclerView recyclerView;
    public static PemberitahuanAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    private SearchView searchView;
    private LinearLayout errorMessage;
    private TextView errorText;
    private ImageView errorGambar;
    private boolean isArsip;

    private Logger logger;
    private NotifikasiDialog notifikasiDialog;
    public boolean allowUpdateUI = true;
    private String deviceName;
    private String jabatan;

    public PemberitahuanFragment() {

    }


    public static PemberitahuanFragment newInstance(String status, String isReadParam) {
        PemberitahuanFragment fragment = new PemberitahuanFragment();

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
        logger.d("onCreate","PemberitahuanFragment  | allowUpdate UI "+  allowUpdateUI);
        notifikasiDialog = new NotifikasiDialog(getContext(), getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        allowUpdateUI = true;
        logger.d("onCreateView","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));


        prefManager = new PrefManager(getActivity());
        deviceName = prefManager.getSessionDevice();
        strJabatan  = prefManager.getStatusJabatan();
        jabatan = prefManager.getStatusJabatan();

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pemberitahuan, container, false);

        recyclerView        = (RecyclerView)view.findViewById(R.id.recycler_view);
        swipeRefreshLayout  = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) view.findViewById(R.id.error);
        errorText           = (TextView)view.findViewById(R.id.message_text);
        errorGambar         = (ImageView) view.findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new PemberitahuanAdapter(getActivity().getApplicationContext(), pemberitahuans,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

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
        }else{
            notifikasiDialog.showDialogError(1,"");
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
            notifikasiDialog.showDialogError(1,"");
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
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {

        } else {
            // read the konsep which removes bold from the row
            final Pemberitahuan pemberitahuan = pemberitahuans.get(position);

            pemberitahuans.set(position, pemberitahuan);
            mAdapter.notifyDataSetChanged();

            Intent intentDetail = new Intent(PemberitahuanFragment.this.getContext(), WebViewActivity.class);

            final String jenis = pemberitahuan.getJenis();

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialog);
            if(jenis.equalsIgnoreCase("pengembalian")){

                alertBuilder.setTitle("Pengembalian Disposisi");
                alertBuilder.setMessage(pemberitahuan.getPesan());
                alertBuilder.setPositiveButton("Lihat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentDetail = new Intent(PemberitahuanFragment.this.getContext(), WebViewActivity.class);

                        intentDetail.putExtra(TAG_ID_SURAT, pemberitahuan.getIdSurat());
                        intentDetail.putExtra(TAG_ID_PEMBERITAHUAN, pemberitahuan.getIdPemberitahuan());
                        intentDetail.putExtra(TAG_JENISSURAT, TAG_PEMBERITAHUAN);
                        intentDetail.putExtra(TAG_ALUR_SURAT, TAG_PENGEMBALIAN);

                        getActivity().startActivity(intentDetail);
                    }
                });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();



            }else if(jenis.equalsIgnoreCase("penarikan")){

                alertBuilder.setTitle("Penarikan Disposisi");
                alertBuilder.setMessage(pemberitahuan.getPesan());
                alertBuilder.setPositiveButton("Lihat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentDetail = new Intent(PemberitahuanFragment.this.getContext(), WebViewActivity.class);

                        intentDetail.putExtra(TAG_ID_SURAT, pemberitahuan.getIdSurat());
                        intentDetail.putExtra(TAG_ID_PEMBERITAHUAN, pemberitahuan.getIdPemberitahuan());
                        intentDetail.putExtra(TAG_JENISSURAT, TAG_PEMBERITAHUAN);
                        intentDetail.putExtra(TAG_ALUR_SURAT, jenis);
                        intentDetail.putExtra("arsip", isArsip);

                        getActivity().startActivity(intentDetail);
                    }
                });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();



            }else if(jenis.equalsIgnoreCase("koreksi")){

                alertBuilder.setTitle("Koreksi Konsep");
                alertBuilder.setMessage(pemberitahuan.getPesan());
                alertBuilder.setPositiveButton("Lihat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentDetail = new Intent(PemberitahuanFragment.this.getContext(), WebViewActivity.class);

                        intentDetail.putExtra(TAG_ID_SURAT, pemberitahuan.getIdSurat());
                        intentDetail.putExtra(TAG_ID_PEMBERITAHUAN, pemberitahuan.getIdPemberitahuan());
                        intentDetail.putExtra(TAG_JENISSURAT, TAG_PEMBERITAHUAN);

                        intentDetail.putExtra(TAG_ALUR_SURAT, Tag.TAG_KOREKSI);
                        intentDetail.putExtra("arsip", isArsip);

                        getActivity().startActivity(intentDetail);
                    }
                });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();



            }else{
                intentDetail.putExtra(TAG_ID_SURAT, pemberitahuan.getIdSurat());
                intentDetail.putExtra(TAG_ID_PEMBERITAHUAN, pemberitahuan.getIdPemberitahuan());
                intentDetail.putExtra(TAG_JENISSURAT, TAG_PEMBERITAHUAN);
                intentDetail.putExtra("arsip", isArsip);

                intentDetail.putExtra(TAG_ALUR_SURAT, jenis);

                PemberitahuanFragment.this.startActivityForResult(intentDetail, 1);
            }



        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
            logger.d("Activity result", "OKE + " + getActivity().getIntent().getStringExtra("result"));
    }

    @Override
    public void onRowLongClicked(int position) {

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        tampilError(false, 0,"");

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String authorization = prefManager.getSessionToken();

        logger.d("Token Eletter", authorization);

        Call<ResultPemberitahuan> call = apiService.getPemberitahuan(authorization);

        call.enqueue(new Callback<ResultPemberitahuan>() {
            @Override
            public void onResponse(Call<ResultPemberitahuan> call, Response<ResultPemberitahuan> response) {
                final ResultPemberitahuan result = response.body();
                if(result != null) {
                    pemberitahuans.clear();


                    logger.w3("hasil", response);

                    if(allowUpdateUI) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getStatus().equalsIgnoreCase(Tag.TAG_STATUS_SUKSES)) {

                                    swipeRefreshLayout.setRefreshing(false);

                                    List<Pemberitahuan> todos = result.getData();

                                    if (todos.size() == 0) {
                                        tampilError(true, 5, "");


                                        DashboardLurah.setBadge(2, String.valueOf(0));



                                    } else {

                                        for (Pemberitahuan pemberitahuan : todos) {

                                            logger.d("Respon", pemberitahuan.getNamaPelaksana().toString());
                                            pemberitahuans.add(pemberitahuan);
                                        }


                                        mAdapter.notifyDataSetChanged();

                                        DashboardLurah.setBadge(2, " ");

                                    }


                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    tampilError(true, 3, "");
                                }
                            }
                        });
                    }
                }else{

                    swipeRefreshLayout.setRefreshing(false);

                    //tampilError(true, 3);
                }

            }

            @Override
            public void onFailure(Call<ResultPemberitahuan> call, Throwable t) {
               swipeRefreshLayout.setRefreshing(false);
//                tampilError(true,1);
//                Log.e("Error eletter", t.getLocalizedMessage());
                call.cancel();
                tampilError(true, 100, t.getLocalizedMessage());

            }
        });
    }
    private void getInboxBackground() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String authorization = prefManager.getSessionToken();

        logger.d("Token Eletter", authorization);

        Call<ResultPemberitahuan> call = apiService.getPemberitahuan(authorization);

        call.enqueue(new Callback<ResultPemberitahuan>() {
            @Override
            public void onResponse(Call<ResultPemberitahuan> call, Response<ResultPemberitahuan> response) {
                final ResultPemberitahuan result = response.body();
                if(result != null) {
                    pemberitahuans.clear();

                    logger.w3("hasil", response);

                    if(allowUpdateUI) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getStatus().equalsIgnoreCase(Tag.TAG_STATUS_SUKSES)) {

                                    List<Pemberitahuan> todos = result.getData();

                                    if (todos.size() == 0) {
                                        tampilError(true, 5, "");
                                        if(strJabatan.equals("")){
                                            Dashboard.setBadge(1, String.valueOf(" "));
                                        }else {
                                            if(cekPenandaTangan()){

                                                Dashboard.setBadge(2, String.valueOf(" "));
                                            }else{

                                                Dashboard.setBadge(2, String.valueOf(" "));
                                            }
                                        }
                                    } else {

                                        for (Pemberitahuan pemberitahuan : todos) {

                                            logger.d("Respon", pemberitahuan.getNamaPelaksana().toString());
                                            pemberitahuans.add(pemberitahuan);
                                        }


                                        mAdapter.notifyDataSetChanged();

                                        if(strJabatan.equals("")){
                                            Dashboard.setBadge(1, String.valueOf(" "));
                                        }else {
                                            if(cekPenandaTangan()){
                                                Dashboard.setBadge(2, String.valueOf(" "));
                                            }else{
                                                Dashboard.setBadge(2, String.valueOf(" "));
                                            }

                                        }
                                    }


                                } else {

                                }
                            }
                        });
                    }
                }else{


                }

            }

            @Override
            public void onFailure(Call<ResultPemberitahuan> call, Throwable t) {
                call.cancel();
                tampilError(true, 100, t.getLocalizedMessage());
            }
        });
    }

    /**  **/
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
                return true;

        }

        return false;
    }



    @Override
    public void onResume() {
        super.onResume();

        allowUpdateUI = true;
        deviceName = prefManager.getSessionDevice();

        if(allowUpdateUI)  getInbox();

        logger.d("OnResume","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onPause() {
        super.onPause();

        allowUpdateUI = false;

        logger.d("onPause","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onStop() {
        super.onStop();

        allowUpdateUI=  false;

        logger.d("onStop","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allowUpdateUI = false;
        logger.d("onDestroyView","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allowUpdateUI =false;
        logger.d("onDestroy","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    public static void updateDataList(List<Pemberitahuan> arrayList){
        pemberitahuans = arrayList;
        if(mAdapter!=null) mAdapter.notifyDataSetChanged();
    }

    public void reloadData(){
        Logger logger = new Logger();
        logger.d("Pembaruan dari firebase", "Pembaruan dari firebase");

        new Thread(new Task()).start();
    }

    private boolean cekPenandaTangan(){
        if(jabatan.equalsIgnoreCase(Tag.JAB_KEPALA) || jabatan.equalsIgnoreCase(Tag.JAB_SEKRETARIS)
                || jabatan.equalsIgnoreCase(Tag.JAB_ASISTEN) || jabatan.equalsIgnoreCase(Tag.JAB_SEKDA)
                || jabatan.equalsIgnoreCase(Tag.JAB_WABUP) || jabatan.equalsIgnoreCase(Tag.JAB_BUPATI)){
            return true;
        }else{
            return false;
        }
    }

    class Task implements Runnable {
        @Override
        public void run() {
            allowUpdateUI = true;

            if(allowUpdateUI)  getInboxBackground();
            logger.d("OnResume","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));
        }
    }

}


