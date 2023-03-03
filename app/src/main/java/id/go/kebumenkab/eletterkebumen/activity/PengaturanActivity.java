package id.go.kebumenkab.eletterkebumen.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.intro.WelcomeActivity;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;


public class PengaturanActivity extends AppCompatPreferenceActivity {
    private static final String TAG;
    private static int versionCode;
    private static String versionName;


    static {
        TAG = PengaturanActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Info");

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            versionName = pInfo.versionName;//Version Name
            versionCode = pInfo.versionCode;//Version Code


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        private PrefManager prefManager;
        private String deviceName;


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            prefManager = new PrefManager(getActivity());
            deviceName = prefManager.getSessionDevice();


           /* Preference darkmodePref = findPreference(getString(R.string.key_daynight));
            darkmodePref.setDefaultValue(prefManager.getDarkMode());

            darkmodePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    if(prefManager.getDarkMode()){
                        prefManager.setDayNight(false);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }else{
                        prefManager.setDayNight(true);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }

                    return true;
                }
            });*/
            // feedback preference click listener
            Preference versionPref = findPreference(getString(R.string.key_version));
            versionPref.setSummary(versionName
                    +"\n\nInformasi Device: \n- "+ Build.BRAND
                    +"\n- Model: " + Build.MODEL
                    + "\n- Manufacturer: " + Build.MANUFACTURER
                    + "\n- OS Android "+ Build.VERSION.RELEASE
            );

            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            // feedback preference click listener
            Preference myPrefCustomerService = findPreference(getString(R.string.key_send_whatsapp));
            myPrefCustomerService.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    // openWhatsapp(getActivity());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Dalam pengembangan menunggu akun WA instansi.");

                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                    return true;
                }
            });


            // open Profil preference click listener
            Preference prefProfil = findPreference(getString(R.string.title_profil));
            prefProfil.setTitle(prefManager.getSessionNama());
            prefProfil.setSummary(prefManager.getSessionJabatan() +" - " +  prefManager.getSessionUnit());

            prefProfil.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    openProfil(getActivity());
                    return true;
                }
            });



            // open Profil preference click listener
            Preference prefLogout = findPreference(getString(R.string.title_logout));
            prefLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Anda akan logout?");
                    builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            prefManager.clearSession();
                            prefManager.setIsLoggedIn(false);
                            prefManager.setFirstTimeLaunch(false);
                            Intent intent = new Intent(getActivity(), LoginActivity.class);

                            startActivity(intent);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                getActivity().finishAffinity();
                            }else{
                                ActivityCompat.finishAffinity(getActivity());
                            }
                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();

                    return true;
                }
            });


            // open Profil preference click listener
            Preference prefIntro = findPreference(getString(R.string.title_intro));
            prefIntro.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    openIntro(getActivity());
                    return true;
                }
            });

            // open Riwayat Dev preference click listener
            Preference prefRiwayatDev = findPreference(getString(R.string.key_riwayat_dev));
            prefRiwayatDev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    openRiwayatDev(getActivity());
                    return true;
                }
            });

            // open Riwayat Dev preference click listener
            Preference prefFAQ = findPreference(getString(R.string.key_faq));
            prefFAQ.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    openFAQ(getActivity());
                    return true;
                }
            });

            // open Domain preference click listener
            Preference prefDomain = findPreference(getString(R.string.title_domain));
            prefDomain.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    return true;
                }
            });

            PreferenceCategory mCategori = (PreferenceCategory)findPreference(getString(R.string.pref_header_about));
            mCategori.removePreference(prefDomain);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        PrefManager prefManager = new PrefManager(context);
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body =  "\n\n\n\n"+prefManager.getSessionNama()+"("+
                    prefManager.getUsername()+
                    ")\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }

//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//
//        intent.setData(Uri.parse("mailto:"));
//        intent.setType("message/rfc822");
//        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kominfo.kebumenkab@gmail.com"});
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Eletter");
//        intent.putExtra(Intent.EXTRA_TEXT, body);
//        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));


        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Eletter");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kominfo.kebumenkab@gmail.com"});
        intent.resolveActivity(context.getPackageManager());
       // intent.putExtra(Intent.EXTRA_STREAM, getSnapshotUri(snapshot, context, event));

        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfos.size() == 0) {
            new AlertDialog.Builder(context)
                    .setMessage("Aplikasi tidak mendukung")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            String packageName = resolveInfos.get(0).activityInfo.packageName;
            String name = resolveInfos.get(0).activityInfo.name;

            intent.setAction(Intent.ACTION_SEND);
            intent.setComponent(new ComponentName(packageName, name));

            context.startActivity(intent);
        }



        }

    public static void openProfil(Context context){
        Intent intent = new Intent(context, ProfilActivity.class);
        context.startActivity(intent);
    }

    public static void openIntro(Context context){

        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }

    public static void openRiwayatDev(Context context){

        Intent intent = new Intent(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void openFAQ(Context context){

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("faq", "1");
        context.startActivity(intent);
    }

    public static void openWhatsapp(Context context){

        PrefManager prefManager = new PrefManager(context);

        String number   = "6289637319180";

        String pesan    =  prefManager.getSessionNama()+"\n"+  prefManager.getSessionJabatan() +
                "\n\n Device OS: Android \n Device OS version: " +
                Build.VERSION.RELEASE + "\n App Version: " + versionName + "\n Device Brand: " + Build.BRAND +
                "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER+
                "\n\nJangan hapus informasi di atas.\n-----------------------------\n" +
                "\nSilakan tuliskan keluhan Anda di bawah ini\n\n";

        String url      = "https://wa.me/"+number+"?text="+ pesan;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static void openTelegram(Context context){

        PrefManager prefManager = new PrefManager(context);

        String number   = "yusufmufti";

        String pesan    =  prefManager.getSessionNama()+"\n"+  prefManager.getSessionJabatan() +
                "\n\n Device OS: Android \n Device OS version: " +
                Build.VERSION.RELEASE + "\n App Version: " + versionName + "\n Device Brand: " + Build.BRAND +
                "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER+
                "\n\nJangan hapus informasi di atas.\n-----------------------------\n" +
                "\nSilakan tuliskan keluhan Anda di bawah ini\n\n";

        String url      = "https://t.me/"+number+"?text="+ pesan;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    // https://eletter.kebumenkab.go.id/panduan/SK-Aplikasi.pdf

}
