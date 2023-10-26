package id.go.kebumenkab.eletterkebumen.activity.operator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import id.go.kebumenkab.eletterkebumen.BuildConfig;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.LoginActivity;
import id.go.kebumenkab.eletterkebumen.activity.PengaturanActivity;
import id.go.kebumenkab.eletterkebumen.adapter.ViewPagerAdapter;
import id.go.kebumenkab.eletterkebumen.fragment.pns.MyBottomSheetDialog;
import id.go.kebumenkab.eletterkebumen.fragment.operator.SuratMasukFragment;
import id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.MyStartServiceReceiver;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.network.AppController;

import static id.go.kebumenkab.eletterkebumen.helper.Config.VERSIONS_FILE_URL;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_APLIKASI;

import androidx.fragment.app.FragmentTransaction;

public class DashboardOperator extends AppBaseActivity implements

        SuratMasukFragment.OnFragmentInteractionListener,
        MyBottomSheetDialog.BottomSheetListener{

    private static final int DAILY_REMINDER_REQUEST_CODE = 100;

    public static  boolean allowRefresh;

    private String versiCode;
    private boolean statusVersi = false;
    private String  sedangMaintenance;

    private PrefManager prefManager;
    private Logger logger;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final long REPEAT_TIME = 1000 * 60 * 20; // 20 menit sekali

    private SuratMasukFragment suratMasukFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_operator);

        registerBaseActivityReceiver();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.tab_masuk));

        prefManager = new PrefManager(getApplicationContext());

        logger  = new Logger();
        allowRefresh = false;

        suratMasukFragment  = new SuratMasukFragment();

        loadFragment(suratMasukFragment);

//        viewPager = findViewById(R.id.container);
//        setupViewPager(viewPager);
//
//        tabLayout = findViewById(R.id.tab);
//        tabLayout.setupWithViewPager(viewPager);
//
//        page = getIntent().getIntExtra(TAG_PAGE, 0);
//        setPage(page);

        setRecurringAlarm(getApplicationContext());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
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
                pendingIntent = PendingIntent.getBroadcast(context, 0, downloader,
                        PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(DashboardOperator.this, PengaturanActivity.class));
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

                            statusVersi = true;

                            if(Integer.parseInt(sedangMaintenance) == 1) {
                                tampilCekVersi(1, "Aplikasi sedang dalam proses maintenance");
                            }else{
                                if(Integer.parseInt(versiCode) > BuildConfig.VERSION_CODE)
                                    tampilCekVersi(2, "Silakan klik Update untuk memperbarui aplikasi");
                            }

                            logger.d("Minimum Version", minimumVersion);
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
