package id.go.kebumenkab.eletterkebumen.ajudan;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.DividerItemDecoration;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.ResultSuratMasuk;
import id.go.kebumenkab.eletterkebumen.model.SuratMasuk;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SuratMasukFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SuratMasukFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuratMasukFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, SuratMasukAdapter.MessageAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS = "status";
    private static final String DIBACA = "isRead";

    // TODO: Rename and change types of parameters

    private PrefManager prefManager;


    public static List<SuratMasuk> messages = new ArrayList<>();
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

    public void  showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        allowUpdateUI = true;

        logger.d("onCreateView","SuratMasukFragment | allowUpdateUI "+ String.valueOf(allowUpdateUI));

        prefManager = new PrefManager(getActivity());

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        recyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(recyclerView);

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
        }else {
            showDialogError(1);
        }
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
        // Star icon is clicked,
        // mark the message as important
        SuratMasuk message = messages.get(position);
        message.setIsImportant(!message.isIsImportant());
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {

        } else {
            // read the message which removes bold from the row
            SuratMasuk message = messages.get(position);
            message.setIsread("1");

            messages.set(position, message);
            mAdapter.notifyDataSetChanged();

            Intent intentDetail = new Intent(SuratMasukFragment.this.getContext(), DetailSuratMasuk.class);
            intentDetail.putExtra("object", message);
            intentDetail.putExtra("position", position);
            intentDetail.putExtra(TAG_ARSIP, "");
            //startActivity(intentDetail);

            SuratMasukFragment.this.startActivityForResult(intentDetail, 1);
        }
    }

    @Override
    public void onRowLongClicked(int position) {

    }


    public void tampilError(boolean param, int kode){

        if(param == true){
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
                    errorText.setText(getActivity().getString(R.string.error_message_nodata));
                    if(Build.MANUFACTURER.contains("Xiaomi")){
                        errorGambar.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_error_outline_dark_24dp, null));

                    }else{
                        errorGambar.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_outline_dark_24dp));

                    }
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

        tampilError(false, 0);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);


        String authorization = prefManager.getSessionToken();

        logger.d("Token Eletter", authorization);


        Call<ResultSuratMasuk> call = apiService.getMasuk(authorization);

        call.enqueue(new Callback<ResultSuratMasuk>() {
            @Override
            public void onResponse(Call<ResultSuratMasuk> call, Response<ResultSuratMasuk> response) {
                final ResultSuratMasuk result = response.body();

                if(result  != null){
                    messages.clear();

                    Log.w("Respon gson ", new Gson().toJson(response));
                    logger.d("Respon GSON",new Gson().toJson(response));

                    if(allowUpdateUI) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                                    List<SuratMasuk> todos = result.getData();

                                    if (todos.size() == 0) {
                                        tampilError(true, 5);
                                        Dashboard.setBadge(1, String.valueOf(0));
                                    } else {
                                        for (SuratMasuk message : todos) {
                                            Log.d("Respon", message.getFrom().toString());

                                            messages.add(message);
                                        }

                                        mAdapter.notifyDataSetChanged();

                                        Dashboard.setBadge(1, String.valueOf(todos.size()));


                                    }

                                    swipeRefreshLayout.setRefreshing(false);

                                } else {
                                    swipeRefreshLayout.setRefreshing(false);

                                    Dashboard.setBadge(1, String.valueOf(0));

                                    if (allowUpdateUI) tampilError(true, 3);
                                }
                            }
                        });
                    }

                }else{
                    swipeRefreshLayout.setRefreshing(false);

                    if(allowUpdateUI) tampilError(true, 3);
                }


            }

            @Override
            public void onFailure(Call<ResultSuratMasuk> call, Throwable t) {

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

                if(allowUpdateUI) tampilError(true,1);
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
                 return true;
        }

        return false;
    }




    @Override
    public void onResume() {
        super.onResume();

        allowUpdateUI = true;

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
}


