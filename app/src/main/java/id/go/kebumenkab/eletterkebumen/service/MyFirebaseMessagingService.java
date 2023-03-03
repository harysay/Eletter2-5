package id.go.kebumenkab.eletterkebumen.service;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.activity.pns.DetailKonsep;
import id.go.kebumenkab.eletterkebumen.activity.pns.DetailSuratMasuk;
import id.go.kebumenkab.eletterkebumen.activity.NewsActivity;
import id.go.kebumenkab.eletterkebumen.adapter.ViewPagerAdapter;
import id.go.kebumenkab.eletterkebumen.fragment.pns.PemberitahuanFragment;
import id.go.kebumenkab.eletterkebumen.fragment.pns.SuratKonsepFragment;
import id.go.kebumenkab.eletterkebumen.fragment.pns.SuratMasukFragment;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;
import id.go.kebumenkab.eletterkebumen.model.Konsep;
import id.go.kebumenkab.eletterkebumen.model.Notif;
import id.go.kebumenkab.eletterkebumen.model.ResultNotif;
import id.go.kebumenkab.eletterkebumen.model.SuratMasuk;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_AJUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ARSIP;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_DISPOSISI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_HISTORI;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_ID_SURAT;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_NEWS;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PAGE;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_PEMBERITAHUAN;
import static id.go.kebumenkab.eletterkebumen.helper.Tag.TAG_TELAAH;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    private PrefManager prefManager;
    private int jumlahNotifikasi=0;
    private Logger logger;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        prefManager = new PrefManager(getApplicationContext());
        boolean isLogin = prefManager.isLoggedIn();
        logger = new Logger();

        // Log.d("Firebase eletter", String.valueOf(isLogin));

        if (remoteMessage.getData().size() > 0) {
           // Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                /** Jika user telah login baru bisa mendapat notifikasi **/
                if(isLogin)    sendPushNotification(json);
            } catch (Exception e) {
               // Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("token", s);
        storeToken(s);
    }

    private void storeToken(String token) {
        PrefManager.getInstance(getApplicationContext()).storeTokenFirebase(token);
    }

    private void cekNotifikasi() {

        logger.d("XXX", "set recurring alarm 5");
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        String authorization = prefManager.getSessionToken();

        Call<ResultNotif> call = apiService.getNotifikasi(authorization, prefManager.getUsername().trim());

        call.enqueue(new Callback<ResultNotif>() {
            @Override
            public void onResponse(Call<ResultNotif> call, Response<ResultNotif> response) {
                final ResultNotif result = response.body();

                logger.d("XXX", response.toString());

                if (result != null) {

                    if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                        Notif data = result.getData();

                        jumlahNotifikasi += data.getKonsep();
                        jumlahNotifikasi += data.getMasuk();

                        if (jumlahNotifikasi > 0) {
                            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                            intent.putExtra("number", jumlahNotifikasi);

                            if(prefManager.getIdPerangkat().length()>0){
                                intent = new Intent(getApplicationContext(), id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);
                                if(prefManager.getJabatan().contains("Lurah")){
                                    intent = new Intent(getApplicationContext(), id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah.class);

                                }
                            }

                            mNotificationManager.showSmallNotification("Eletter Kebumen",
                                    data.getKonsep() + " konsep , " + data.getMasuk() + " surat masuk belum dibuka", intent, false);

                            ShortcutBadger.applyCount(getApplicationContext(), jumlahNotifikasi); //for 1.1.4+

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResultNotif> call, Throwable t) {
                logger.d("XXX", t.getLocalizedMessage());
            }
        });
    }


    private void sendPushNotification(JSONObject json) {
        logger.d(TAG, "Notification JSON " + json.toString());

        String strJabatan = prefManager.getStatusJabatan();
        logger.d(TAG, "Jabatan " + strJabatan);

        try {


            /** Untuk parsing data dari Firebase  **/
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String[] arrayMessage = message.split("#");

            String kategori = title.toLowerCase();
            String strIdSurat = arrayMessage[0];
            String strIdHistory = arrayMessage[1];
            String strPesan = arrayMessage[2];
            String jenisTujuan = arrayMessage[3];



            /** Untuk pembaruan data di masing-masing Fragment **/
            ViewPagerAdapter fa = (ViewPagerAdapter) Dashboard.viewPager.getAdapter();

            if(strJabatan.equals("")){
                SuratMasukFragment suratMasukFragment= (SuratMasukFragment) fa.getItem(0);
                suratMasukFragment.reloadData();

                PemberitahuanFragment pemberitahuanFragment =(PemberitahuanFragment)fa.getItem(1);
                pemberitahuanFragment.reloadData();
            }else{
                SuratKonsepFragment suratKonsepFragment = (SuratKonsepFragment) fa.getItem(0);
                suratKonsepFragment.reloadData();

                SuratMasukFragment suratMasukFragment= (SuratMasukFragment) fa.getItem(1);
                suratMasukFragment.reloadData();
            }

            logger.d(TAG, "Kategori " + kategori);

            /** Untuk memunculkan notifikasi di status bar **/

            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);

            if(kategori.equalsIgnoreCase(TAG_AJUAN)){
                intent = new Intent(getApplicationContext(), DetailKonsep.class);
                Konsep konsep = new Konsep();
                konsep.setIdSurat(strIdSurat);
                konsep.setIdHistory(strIdHistory);
                konsep.setStatus(kategori);
                intent.putExtra("object", konsep);

            }else if(kategori.equalsIgnoreCase(TAG_DISPOSISI)){
                intent = new Intent(getApplicationContext(), DetailSuratMasuk.class);
                SuratMasuk suratMasuk = new SuratMasuk();
                suratMasuk.setIdHistory(strIdHistory);
                suratMasuk.setIdSurat(strIdSurat);
                suratMasuk.setStatus(kategori);
                suratMasuk.setJenisTujuan(jenisTujuan);
                intent.putExtra("object", suratMasuk);
                intent.putExtra(TAG_ARSIP, "");

            }else if(kategori.equalsIgnoreCase(TAG_TELAAH)){
                intent = new Intent(getApplicationContext(), DetailSuratMasuk.class);
                SuratMasuk suratMasuk = new SuratMasuk();
                suratMasuk.setIdHistory(strIdHistory);
                suratMasuk.setIdSurat(strIdSurat);
                suratMasuk.setStatus(kategori);
                suratMasuk.setJenisTujuan(jenisTujuan);
                intent.putExtra("object", suratMasuk);
                intent.putExtra(TAG_ARSIP, "");

            }else if(kategori.equalsIgnoreCase(TAG_PEMBERITAHUAN)){
                intent = new Intent(getApplicationContext(), Dashboard.class);
                intent.putExtra(TAG_PAGE, 2);

            }else if(kategori.equalsIgnoreCase(TAG_NEWS)){
                intent = new Intent(getApplicationContext(), NewsActivity.class);
                intent.putExtra("message", strPesan);

            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            intent.putExtra(TAG_ID_SURAT, strIdSurat);
            intent.putExtra(TAG_ID_HISTORI, strIdHistory);

            mNotificationManager.showSmallNotification(title, strPesan, intent, true);

            /** selesai memunculkan notifikasi **/


            cekNotifikasi();



        } catch (JSONException e) {
            logger.d(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            logger.d(TAG, "Exception: " + e.getMessage());
        }
    }


}
