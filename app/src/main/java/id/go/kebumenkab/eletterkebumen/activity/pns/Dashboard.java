package id.go.kebumenkab.eletterkebumen.activity.pns;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;

//import com.android.volley.BuildConfig;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import id.go.kebumenkab.eletterkebumen.BuildConfig;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.LoginActivity;
import id.go.kebumenkab.eletterkebumen.activity.PengaturanActivity;
import id.go.kebumenkab.eletterkebumen.adapter.ViewPagerAdapter;
import id.go.kebumenkab.eletterkebumen.fragment.pns.ArsipFragment;
import id.go.kebumenkab.eletterkebumen.fragment.pns.MyBottomSheetDialog;
import id.go.kebumenkab.eletterkebumen.fragment.pns.PemberitahuanFragment;
import id.go.kebumenkab.eletterkebumen.fragment.pns.PermintaanTandatanganFragment;
import id.go.kebumenkab.eletterkebumen.fragment.pns.SuratKonsepFragment;
import id.go.kebumenkab.eletterkebumen.fragment.pns.SuratMasukFragment;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.MyStartServiceReceiver;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.network.AppController;

import static id.go.kebumenkab.eletterkebumen.helper.Config.VERSIONS_FILE_URL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_APLIKASI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PAGE;

public class Dashboard extends AppBaseActivity implements
        PemberitahuanFragment.OnFragmentInteractionListener,
        ArsipFragment.OnFragmentInteractionListener,
        SuratMasukFragment.OnFragmentInteractionListener,
        SuratKonsepFragment.OnFragmentInteractionListener,
        PermintaanTandatanganFragment.OnFragmentInteractionListener,
        MyBottomSheetDialog.BottomSheetListener{

    private static final int DAILY_REMINDER_REQUEST_CODE = 100;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private ViewPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private  int pagePosition;
    public static  boolean allowRefresh;

    private final int[] imageResPenandaTangan = {
            R.drawable.ic_concept_white,
            R.drawable.ic_surat_white,
     //       R.drawable.ic_content_paste_white,
            R.drawable.ic_announcement_24dp,
     //       R.drawable.ic_note_24dp,
            R.drawable.ic_archive_white };

    private final int[] imageResId = {
            R.drawable.ic_concept_white,
            R.drawable.ic_surat_white,
            R.drawable.ic_announcement_24dp,
            R.drawable.ic_archive_white };

    private final int[] imageResIdStaf = {
            R.drawable.ic_surat_white,
            R.drawable.ic_announcement_24dp,
            R.drawable.ic_archive_white };

    public static  TabLayout tabLayout;

    public static ViewPager viewPager;

    private int page;
    private String versiCode;
    private boolean statusVersi = false;
    private String  sedangMaintenance;

    private String jabatan;
    private PrefManager prefManager;
    private Logger logger;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final long REPEAT_TIME = 1000 * 60 * 30; // 30 menit sekali

    private SuratMasukFragment suratMasukFragment;
    private SuratKonsepFragment suratKonsepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Tangkap pesan dari Intent setelah tanda tangan
        String successMessage = getIntent().getStringExtra("successMessage");
        // Tampilkan pesan jika ada
        if (successMessage != null) {
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
//            new AlertDialog.Builder(this) // 'this' merujuk ke context dari Activity
//                    .setTitle("Berhasil")
//                    .setMessage(successMessage)
//                    .setPositiveButton("OK", null)
//                    .show();
        }

        registerBaseActivityReceiver(); //method ini bermasalah di android 14

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefManager = new PrefManager(getApplicationContext());

       /* if(prefManager.getDarkMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Night :" +prefManager.getDarkMode(), Toast.LENGTH_SHORT).show();
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Night :" +prefManager.getDarkMode(), Toast.LENGTH_SHORT).show();
        }*/
        jabatan = prefManager.getStatusJabatan();
        logger  = new Logger();
        allowRefresh = false;

        logger.d("jabatan", prefManager.getJabatan());
        logger.d("status jabatan", prefManager.getStatusJabatan());
        logger.d("url", prefManager.getUrlDesa());

        suratKonsepFragment = new SuratKonsepFragment();
        suratMasukFragment  = new SuratMasukFragment();

        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        page = getIntent().getIntExtra(TAG_PAGE, 0);
        setPage(page);

        // Jabatan STAF
        if(jabatan.equals("")){
            for (int i = 0; i < imageResIdStaf.length; i++) {

                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.setIcon(imageResIdStaf[i]);
                tab.setCustomView(R.layout.badged_tab);
                tab.getCustomView().findViewById(android.R.id.text1).setVisibility(View.GONE);

            }
        }else{

            if(cekPenandaTangan()){
                // Pejabat Penandatangan
                for (int i = 0; i < imageResPenandaTangan.length; i++) {

                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    tab.setIcon(imageResPenandaTangan[i]);
                    tab.setCustomView(R.layout.badged_tab);
                    tab.getCustomView().findViewById(android.R.id.text1).setVisibility(View.GONE);

                }
            }else{
                // Pejabat Eselon
                for (int i = 0; i < imageResId.length; i++) {

                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    tab.setIcon(imageResId[i]);
                    tab.setCustomView(R.layout.badged_tab);
                    tab.getCustomView().findViewById(android.R.id.text1).setVisibility(View.GONE);

                }
            }

        }

       setRecurringAlarm(getApplicationContext());
    }

    public static void setRefresh(boolean status){
        allowRefresh = status;
    }

    private void setRecurringAlarm(Context context) {

        logger.d("XXX", "set recurring alarm 1");

        // memastikan alarm sudah dimatikan
        if (cancelAlarm(context)) {
            logger.d("XXX", "set recurring alarm 2");
            Intent downloader = new Intent(context, MyStartServiceReceiver.class);
            downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //untuk mengatasi error force close pada android 12 ke atas
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(context, 0, downloader,
                        PendingIntent.FLAG_IMMUTABLE);
            }
            else
            {
                pendingIntent = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT| PendingIntent.FLAG_IMMUTABLE);
            }
//            pendingIntent = PendingIntent.getBroadcast(context, 0, downloader,
//                    PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+ REPEAT_TIME , REPEAT_TIME,
                    pendingIntent);
            
        }
    }

    public boolean cancelAlarm(Context context){
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager = null;
            Log.d("MyActivity", "alarm Manager canceled ");
            return true;
        }

        return true;
    }



    public static void setBadge(int position, String number){
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if(tab != null && tab.getCustomView() != null) {
            TextView b = tab.getCustomView().findViewById(R.id.badge);

            if(b != null) {
                b.setText(number);
            }

            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if(v != null && !number.equalsIgnoreCase("0")) {
                v.setVisibility(View.VISIBLE);
            }else{
                v.setVisibility(View.GONE);
            }
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());


        if(jabatan.equals("")){
            adapter.addFrag(suratMasukFragment, "MASUK");
            adapter.addFrag(new PemberitahuanFragment(), "PEMBERITAHUAN");
            adapter.addFrag(new ArsipFragment(), "ARSIP");
        }else{

            if(cekPenandaTangan()){
                adapter.addFrag(suratKonsepFragment,"KONSEP");
                adapter.addFrag(suratMasukFragment, "MASUK");
             //   adapter.addFrag(new PermintaanTandatanganFragment(), "PERMINTAAN TANDATANGAN");
                adapter.addFrag(new PemberitahuanFragment(), "PEMBERITAHUAN");
                adapter.addFrag(new ArsipFragment(), "ARSIP");
            }else{
                adapter.addFrag(suratKonsepFragment,"KONSEP");
                adapter.addFrag(suratMasukFragment, "MASUK");
                adapter.addFrag(new PemberitahuanFragment(), "PEMBERITAHUAN");
                adapter.addFrag(new ArsipFragment(), "ARSIP");
            }


        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("SCROLL", String.valueOf(positionOffset));
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Page", String.valueOf(position));
                setPage(position);
                invalidateFragmentMenus(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);
    }


    private void invalidateFragmentMenus(int position){
        for(int i = 0; i < adapter.getCount(); i++){
            adapter.getItem(i).setHasOptionsMenu(i == position);
        }
        invalidateOptionsMenu(); //or respectively its support method.
    }

    private void setPage(int position){
        pagePosition = position;
        viewPager.setCurrentItem(pagePosition);

        if(jabatan.equals("")){

            if(position == 0) {
                getSupportActionBar().setTitle(getString(R.string.tab_masuk));
            }

            if(position == 1) {
                getSupportActionBar().setTitle(getString(R.string.tab_pemberitahuan));
            }

            if(position == 2) {
                getSupportActionBar().setTitle(getString(R.string.tab_arsip));
            }
        }else{

            if(cekPenandaTangan()){
                if(position == 0) {
                    getSupportActionBar().setTitle(getString(R.string.tab_konsep));
                }

                if(position == 1) {
                    getSupportActionBar().setTitle(getString(R.string.tab_masuk));
                }

              /*  if(position == 2) {
                    getSupportActionBar().setTitle(getString(R.string.tab_permintaan_tandatangan));
                }*/

                if(position == 2) {
                    getSupportActionBar().setTitle(getString(R.string.tab_pemberitahuan));
                }
                if(position == 3) {
                    getSupportActionBar().setTitle(getString(R.string.tab_arsip));
                }
            }else{
                if(position == 0) {
                    getSupportActionBar().setTitle(getString(R.string.tab_konsep));
                }

                if(position == 1) {
                    getSupportActionBar().setTitle(getString(R.string.tab_masuk));
                }

                if(position == 2) {
                    getSupportActionBar().setTitle(getString(R.string.tab_pemberitahuan));
                }
                if(position == 3) {
                    getSupportActionBar().setTitle(getString(R.string.tab_arsip));
                }
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(Dashboard.this, PengaturanActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }




    // Untuk testing aplikasi
    public void cekVersi() {
        String url =  VERSIONS_FILE_URL + prefManager.getUsername()+"/"+  prefManager.getSessionIdJabatan();
        logger.d("cekversi", url);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        // display response
                        logger.d("Response", response.toString());

                        try {

                            JSONObject objAndroid = response.getJSONObject("android");
                            String minimumVersion = objAndroid.getString("minimum_version");
                            versiCode = objAndroid.getString("versi_code");
                            sedangMaintenance = objAndroid.getString("perbaikan");

                            String versiCodeUpdate = objAndroid.getString("versi_code_update");

                            statusVersi = true;

                            if(Integer.parseInt(sedangMaintenance) == 1) {
                                tampilCekVersi(1, "Aplikasi sedang dalam proses maintenance");
                            }else{
                                if(Integer.parseInt(versiCode) > BuildConfig.VERSION_CODE)
                                    tampilCekVersi(2, "Silakan klik Update untuk memperbarui aplikasi");
                            }

                            logger.d("Minimum Version", minimumVersion);
                            String id_unit_kerja    =   "";
                            String status_jabatan   =   "";

                            if(response.has("detail") ){

                                if(!response.getString("detail").isEmpty()){
                                    JSONObject objDetail = response.getJSONObject("detail");
                                    id_unit_kerja = objDetail.getString("id_unit_kerja");
                                    status_jabatan = objDetail.getString("status_jabatan");

                                    // Jika jabatan ganti maka logout
                                    if(!prefManager.getStatusJabatan().equalsIgnoreCase(status_jabatan)){
                                        logger.d("Ganti Jabatan", status_jabatan+" dari "+ prefManager.getStatusJabatan());
                                        logout();
                                    }

                                    // Jika id unit kerja pindah maka logout
                                    if(!prefManager.getSessionIdUnit().equalsIgnoreCase(id_unit_kerja)){
                                        logger.d("Ganti Unit Kerja", id_unit_kerja+" dari "+ prefManager.getSessionIdUnit());
                                        logout();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the reuest has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                        }
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(getRequest, TAG_APLIKASI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cekVersi();
    }

    protected  void tampilCekVersi(int kode,String pesan){
        MyBottomSheetDialog bottomSheet = new MyBottomSheetDialog(kode, pesan);
        bottomSheet.setCancelable(false);
        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests(TAG_APLIKASI);
    }

    private boolean cekPenandaTangan(){
        return jabatan.equalsIgnoreCase(Tag.JAB_KEPALA) || jabatan.equalsIgnoreCase(Tag.JAB_SEKRETARIS)
                || jabatan.equalsIgnoreCase(Tag.JAB_ASISTEN) || jabatan.equalsIgnoreCase(Tag.JAB_SEKDA)
                || jabatan.equalsIgnoreCase(Tag.JAB_WABUP) || jabatan.equalsIgnoreCase(Tag.JAB_BUPATI);
    }

    public void logout(){
        prefManager.clearSession();
        prefManager.setIsLoggedIn(false);
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            ActivityCompat.finishAffinity(this);
        }

    }


    @Override
    public void onButtonClicked(String text) {
        if(text.equalsIgnoreCase("close")){
            closeAllActivities();
            finish();
        }else if(text.equalsIgnoreCase("update")){
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }
}
