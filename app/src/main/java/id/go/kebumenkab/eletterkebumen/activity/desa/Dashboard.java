package id.go.kebumenkab.eletterkebumen.activity.desa;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.LoginActivity;
import id.go.kebumenkab.eletterkebumen.adapter.ViewPagerAdapter;
import id.go.kebumenkab.eletterkebumen.fragment.desa.ArsipFragment;
import id.go.kebumenkab.eletterkebumen.fragment.desa.MyBottomSheetDialog;
import id.go.kebumenkab.eletterkebumen.fragment.desa.SuratKonsepFragment;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.MyStartServiceReceiver;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.network.AppController;

import static id.go.kebumenkab.eletterkebumen.helper.Config.VERSIONS_FILE_URL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_APLIKASI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PAGE;

public class Dashboard extends AppBaseActivity implements
        ArsipFragment.OnFragmentInteractionListener,
        SuratKonsepFragment.OnFragmentInteractionListener,
        MyBottomSheetDialog.BottomSheetListener{

    private static final int DAILY_REMINDER_REQUEST_CODE = 100;

    private ViewPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private  int pagePosition;
    public static  boolean allowRefresh;

    private int[] imageResId = {
            R.drawable.ic_concept_white,
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

    private id.go.kebumenkab.eletterkebumen.fragment.desa.SuratKonsepFragment suratKonsepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        registerBaseActivityReceiver();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefManager = new PrefManager(getApplicationContext());

        logger  = new Logger();
        allowRefresh = false;

        logger.d("jabatan", prefManager.getJabatan());
        logger.d("status jabatan", prefManager.getStatusJabatan());
        logger.d("url", prefManager.getUrlDesa());

        suratKonsepFragment = new SuratKonsepFragment();

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        page = getIntent().getIntExtra(TAG_PAGE, 0);
        setPage(page);

        for (int i = 0; i < imageResId.length; i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setIcon(imageResId[i]);
            tab.setCustomView(R.layout.badged_tab);
            tab.getCustomView().findViewById(android.R.id.text1).setVisibility(View.GONE);

        }

        logger.d("Halaman", "Dashboard Kades");
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

            pendingIntent = PendingIntent.getBroadcast(context, 0, downloader,
                    PendingIntent.FLAG_CANCEL_CURRENT);
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
            TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);

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
        adapter.addFrag(suratKonsepFragment,"KONSEP");
        adapter.addFrag(new ArsipFragment(), "ARSIP");

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

        if(position == 0) {
            getSupportActionBar().setTitle(getString(R.string.tab_konsep));
        }

        if(position == 1) {
            getSupportActionBar().setTitle(getString(R.string.tab_arsip));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_dashboard_desa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(Dashboard.this, InfoDesaActivity.class));
            return true;
        }else if(id == R.id.action_browser){
            Intent intent = new Intent(getApplicationContext(), WebViewDesaLurahActivity.class);
            intent.putExtra("WEB", "WEB");
            intent.putExtra("id_surat", "0");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        /** Tidak dipakai karena akan menggunakan fitur trigger notifikasi Firebase **/

        /*if(isMyServiceRunning(ServiceCheckBadge.class)){

            stopService(new Intent(this, ServiceCheckBadge.class));
        }*/

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


                            PackageInfo pInfo = null;
                            try {
                                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                                String version = pInfo.versionName;//Version Name
                                int verCode = pInfo.versionCode;//Version Code
                                logger.d("Info", version +"/"+ String.valueOf(verCode));
                                if(Integer.parseInt(sedangMaintenance) == 1) {
                                    tampilCekVersi(1, "Aplikasi sedang dalam proses maintenance");
                                }else{
                                    if(Integer.parseInt(versiCode) > verCode)
                                        tampilCekVersi(2, "Silakan klik Update untuk memperbarui aplikasi");
                                }

                                logger.d("Minimum Version", minimumVersion);

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
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

        logger.d("URLKU", prefManager.getUrlDesa());
        if(allowRefresh){
            getSupportFragmentManager().beginTransaction().detach(suratKonsepFragment).attach(suratKonsepFragment).commit();

        }
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

    private void cekPreferencaManager(){
        logger.d("cekPreference", prefManager.getNIK());
        logger.d("cekPreference", prefManager.getNIP());
        logger.d("mintaforo", prefManager.getUsername());
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
