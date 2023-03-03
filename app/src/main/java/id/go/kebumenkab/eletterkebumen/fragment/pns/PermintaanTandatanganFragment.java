package id.go.kebumenkab.eletterkebumen.fragment.pns;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.activity.pns.DetailPermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.adapter.pns.PermintaanTandatanganAdapter;
import id.go.kebumenkab.eletterkebumen.helper.DividerItemDecoration;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.PermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.model.ResultPermintaanTandatangan;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PermintaanTandatanganFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PermintaanTandatanganFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PermintaanTandatanganFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, PermintaanTandatanganAdapter.MessageAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS = "status";
    private static final String DIBACA = "isRead";

    // TODO: Rename and change types of parameters

    private PrefManager prefManager;


    public static List<PermintaanTandatangan> konseps = new ArrayList<>();
    private RecyclerView recyclerView;
    public static PermintaanTandatanganAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    private SearchView searchView;
    private LinearLayout errorMessage;
    private TextView errorText;
    private ImageView errorGambar;

    private Logger logger;
    public boolean allowUpdateUI = true;

    private String deviceName; // untuk mengecek device tertentu


    public PermintaanTandatanganFragment() {

    }


    public static PermintaanTandatanganFragment newInstance(String status, String isReadParam) {
        PermintaanTandatanganFragment fragment = new PermintaanTandatanganFragment();

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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        allowUpdateUI = true;
        logger.d("onCreateView","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI);


        prefManager = new PrefManager(getActivity());
        deviceName = prefManager.getSessionDevice();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView        = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout  = view.findViewById(R.id.swipe_refresh_layout);
        errorMessage        = view.findViewById(R.id.error);
        errorText           = view.findViewById(R.id.message_text);
        errorGambar         = view.findViewById(R.id.gambar);

        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new PermintaanTandatanganAdapter(getActivity().getApplicationContext(), konseps, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
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
                    () -> getInbox()
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
                () -> getInbox()
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
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        // read the konsep which removes bold from the row
        PermintaanTandatangan konsep = konseps.get(position);

        konseps.set(position, konsep);
        mAdapter.notifyDataSetChanged();

        //Toast.makeText(getActivity(), "Read: " + konsep.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intentDetail = new Intent(PermintaanTandatanganFragment.this.getContext(), DetailPermintaanTandatangan.class);
        intentDetail.putExtra("object", konsep);
        intentDetail.putExtra("position", position);
        //startActivity(intentDetail);

        PermintaanTandatanganFragment.this.startActivityForResult(intentDetail, 1);

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

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String authorization = prefManager.getSessionToken();

        logger.d("Token Eletter Konsep", authorization);

        Call<ResultPermintaanTandatangan> call = apiService.getPermintaanTandatangan(authorization);

        call.enqueue(new Callback<ResultPermintaanTandatangan>() {
            @Override
            public void onResponse(Call<ResultPermintaanTandatangan> call, Response<ResultPermintaanTandatangan> response) {
                final ResultPermintaanTandatangan result = response.body();
                if(result !=null) {
                    konseps.clear();



                    if(allowUpdateUI) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                                    List<PermintaanTandatangan> todos = result.getData();
                                    if (todos.size() == 0) {
                                        tampilError(true, 5, "");
                                        Dashboard.setBadge(2, String.valueOf(0));
                                    } else {

                                        int jumlahDitandai = 0;

                                        for (PermintaanTandatangan konsep : todos) {

                                                konseps.add(konsep);


                                        }

                                        mAdapter.notifyDataSetChanged();

                                        Dashboard.setBadge(0, String.valueOf(todos.size()));


                                    }

                                    swipeRefreshLayout.setRefreshing(false);

                                } else {
                                    swipeRefreshLayout.setRefreshing(false);

                                    Dashboard.setBadge(0, String.valueOf(0));

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
            public void onFailure(Call<ResultPermintaanTandatangan> call, Throwable t) {

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
        if(allowUpdateUI)

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI
                    getInbox();
                }
            });



        deviceName =prefManager.getSessionDevice();

        logger.d("OnResume","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI + " - " +  deviceName);
    }

    @Override
    public void onPause() {
        super.onPause();

        allowUpdateUI = false;

        logger.d("onPause","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI);

    }

    @Override
    public void onStop() {
        super.onStop();

        allowUpdateUI=  false;

        logger.d("onStop","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allowUpdateUI = false;
        logger.d("onDestroyView","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allowUpdateUI =false;
        logger.d("onDestroy","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI);

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
        builder.setPositiveButton("Tutup", (dialogInterface, i1) -> dialogInterface.cancel());
        builder.show();
    }

    public static void updateDataList(List<PermintaanTandatangan> arrayList){
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
                getActivity().runOnUiThread(() -> {

                    // Stuff that updates the UI
                    getInbox();

                    logger.d("OnResume","SuratKonsepFragment | allowUpdateUI "+ allowUpdateUI);
                });
            }
            }
    }
}