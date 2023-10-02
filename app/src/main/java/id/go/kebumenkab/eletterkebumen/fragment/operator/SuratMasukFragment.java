package id.go.kebumenkab.eletterkebumen.fragment.operator;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import id.go.kebumenkab.eletterkebumen.activity.operator.DashboardOperator;
import id.go.kebumenkab.eletterkebumen.activity.operator.DetailSuratMasuk;
import id.go.kebumenkab.eletterkebumen.activity.operator.PreviewActivity;
import id.go.kebumenkab.eletterkebumen.adapter.operator.SuratMasukAdapter;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.DataItem;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.ResponseSuratMasukOperator;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import id.go.kebumenkab.eletterkebumen.network.operator.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.operator.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PATH;


public class SuratMasukFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, SuratMasukAdapter.MessageAdapterListener{

    private static final String STATUS = "status";
    private static final String DIBACA = "isRead";

    private PrefManager prefManager;
    private String strJabatan;

    private static List<DataItem> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    public static SuratMasukAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    private SearchView searchView;
    private LinearLayout errorMessage;
    private TextView errorText;
    private ImageView errorGambar;

    private Logger logger;

    public boolean allowUpdateUI = true;


    public SuratMasukFragment() {

    }

    public static SuratMasukFragment newInstance(String status, String isReadParam) {
        SuratMasukFragment fragment = new SuratMasukFragment();

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
        logger.d("onCreate","SuratMasukFragment  | allowUpdate UI "+  allowUpdateUI);


        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        allowUpdateUI = true;

        logger.d("onCreateView","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

        prefManager = new PrefManager(getActivity());
        strJabatan  = prefManager.getStatusJabatan();

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suratmasuk, container, false);

        recyclerView        = (RecyclerView)view.findViewById(R.id.recycler_view);
        swipeRefreshLayout  = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        errorMessage        = (LinearLayout) view.findViewById(R.id.error);
        errorText           = (TextView)view.findViewById(R.id.message_text);
        errorGambar         = (ImageView) view.findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new SuratMasukAdapter(getActivity().getApplicationContext(), messages, this);

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

    // TODO: Rename method, update argument and hook method into UI event
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

        strJabatan  = prefManager.getStatusJabatan();

        Log.d("jabatan", strJabatan);

    }

    @Override
    public void onRefresh() {
        boolean isConnected = NetworkUtil.cekInternet(getActivity());
        if(isConnected){
            // show loader and fetch messages
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
        DataItem message = messages.get(position);
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {

        DataItem message = messages.get(position);

        messages.set(position, message);
        mAdapter.notifyDataSetChanged();

        Intent intentDetail = new Intent(SuratMasukFragment.this.getContext(), PreviewActivity.class);
        intentDetail.putExtra("object", message);
        intentDetail.putExtra("position", position);
        intentDetail.putExtra(Tag.TAG_ID_SURAT, message.getIdSuratInternal());
        intentDetail.putExtra(Tag.TAG_PESAN, message.getIdSuratInternal());
        intentDetail.putExtra(TAG_PATH, message.getTelaah());
        startActivity(intentDetail);

        DashboardOperator.setRefresh(true);

        // SuratMasukFragment.this.startActivityForResult(intentDetail, 1);
    }

    @Override
    public void onRowLongClicked(int position) {

    }


    public void tampilError(boolean param, int kode, String pesan){

        if(param){
            recyclerView.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);

            if(allowUpdateUI){
                if(kode == 1)
                    errorText.setText(getActivity().getString(R.string.error_api));
                if(kode == 2)
                    errorText.setText(getActivity().getString(R.string.error_api));
                if(kode == 3)
                    errorText.setText(getActivity().getString(R.string.error_api));
                if(kode == 4)
                    errorText.setText(getActivity().getString(R.string.error_api));
                if(kode == 5){
                    errorText.setText(getActivity().getString(R.string.error_suratmasuk_empty));

                }else{
                    errorText.setText(pesan);
                }

                // ini solusi untuk Xiaomi redmi 2 hape mas Didik
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
        recyclerView.setVisibility(View.INVISIBLE);

        ApiInterface apiService =
                ApiClient.loginRequest(getContext()).create(ApiInterface.class);

        String authorization = prefManager.getSessionToken();
        logger.d("Token Eletter", authorization);
        Call<ResponseSuratMasukOperator> call = apiService.getSuratMasuk(authorization);

        call.enqueue(new Callback<ResponseSuratMasukOperator>() {
            @Override
            public void onResponse(Call<ResponseSuratMasukOperator> call, Response<ResponseSuratMasukOperator> response) {
                final ResponseSuratMasukOperator result = response.body();

                if(result  != null){
                    messages.clear();

                    logger.w2("hasil", result.getData());

                    if(allowUpdateUI) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                                    List<DataItem> todos = result.getData();

                                    if (todos.size() == 0) {
                                        tampilError(true, 5,"");

                                    } else {
                                        for (DataItem message : todos) {
                                            logger.d("DataItem", message.getNamaSurat().toString()+"/"+message.getTelaah());

                                            messages.add(message);

                                        }

                                        mAdapter.notifyDataSetChanged();


                                    }
                                    swipeRefreshLayout.setRefreshing(false);
                                    recyclerView.setVisibility(View.VISIBLE);

                                } else {
                                    swipeRefreshLayout.setRefreshing(false);

                                    tampilError(true, 3,"");
                                }
                            }
                        });
                    }
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                    if(allowUpdateUI) tampilError(true, 3,"");
                }
            }

            @Override
            public void onFailure(Call<ResponseSuratMasukOperator> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);
                call.cancel();
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

                Logger logger = new Logger();
                logger.d(errorType, errorDesc);

                tampilError(true,100, errorType);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.menu_main, menu);

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
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                getInbox();
            }
        });

        logger.d("OnResume","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));
    }

    @Override
    public void onPause() {
        super.onPause();
        allowUpdateUI = false;
        logger.d("onPause","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));
    }

    @Override
    public void onStop() {
        super.onStop();
        allowUpdateUI=  false;
        //getActivity().unregisterReceiver(myReceiver);
        logger.d("onStop","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allowUpdateUI = false;
        logger.d("onDestroyView","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allowUpdateUI =false;
        logger.d("onDestroy","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

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

    public static void updateDataList(List<DataItem> arrayList){
        messages = arrayList;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        //setReceiver();
        super.onStart();
    }


    public void reloadData(){
        Logger logger = new Logger();
        logger.d("Pembaruan dari firebase", "Pembaruan dari firebase");

        new Thread(new Task()).start();
    }

    class Task implements Runnable {
        @Override
        public void run() {
            if(allowUpdateUI) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getInbox();
                    }
                });

                logger.d("OnResume","PemberitahuanFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

            }
        }
    }





}




