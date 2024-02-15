package id.go.kebumenkab.eletterkebumen.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah;
import id.go.kebumenkab.eletterkebumen.activity.operator.DashboardOperator;
import id.go.kebumenkab.eletterkebumen.model.DataItemSuratMasuk;
import id.go.kebumenkab.eletterkebumen.model.KonsepDesa;
import id.go.kebumenkab.eletterkebumen.model.Notif;
import id.go.kebumenkab.eletterkebumen.model.ResponseSuratMasukOperator;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsepDesa;
import id.go.kebumenkab.eletterkebumen.model.ResultNotif;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;
import id.go.kebumenkab.eletterkebumen.network.pns.ApiInterface;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiClientDesa;
import id.go.kebumenkab.eletterkebumen.network.desa.ApiInterfaceDesa;
import id.go.kebumenkab.eletterkebumen.service.MyNotificationManager;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyStartServiceReceiver extends BroadcastReceiver {

    Context mContext;
    PrefManager prefManager;
    int jumlahNotifikasi = 0;
    Logger logger;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.mContext = context;
        this.prefManager = new PrefManager(context);
        this.logger = new Logger();

        logger.d("XXX", "MyStartServiceReceiver1");

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour > 6 && hour < 18) {
            logger.d("XXX", "MyStartServiceReceiver2");
            if(prefManager.getIdPerangkat().length()>0){
                Intent intentDashboardDesa = new Intent(context, id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);
                cekNotifikasiDesa(intentDashboardDesa);


            }else{

                String[] arrayTipe = {"3", "4", "5", "6", "7", "8", "9"};

                if(ArrayUtils.contains(arrayTipe, prefManager.getType())){
                    // pindah ke Dashboard Operator
                    Intent intentDashboardOperator = new Intent(context, DashboardOperator.class);
                    cekNotifikasiOperator(intentDashboardOperator);
                }else{
                    Intent intentDashboardOPD = new Intent(context, Dashboard.class);
                    cekNotifikasi(intentDashboardOPD);
                }
            }

        }
    }

    private void cekNotifikasiOperator(Intent myIntent){
        logger.d("XXX", "MyStartServiceReceiver3");
        id.go.kebumenkab.eletterkebumen.network.operator.ApiInterface apiService =
                id.go.kebumenkab.eletterkebumen.network.operator.ApiClient.loginRequest(mContext).create(id.go.kebumenkab.eletterkebumen.network.operator.ApiInterface.class);

        String authorization = prefManager.getSessionToken();

        Call<ResponseSuratMasukOperator> call = apiService.getSuratMasuk(authorization);

        call.enqueue(new Callback<ResponseSuratMasukOperator>() {
            @Override
            public void onResponse(Call<ResponseSuratMasukOperator> call, Response<ResponseSuratMasukOperator> response) {
                final ResponseSuratMasukOperator result = response.body();

                logger.d("XXX", response.toString());

                if (result != null) {

                    if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                        List<DataItemSuratMasuk> todos = result.getData();
                        jumlahNotifikasi += todos.size();

                        if (jumlahNotifikasi > 0) {
                            MyNotificationManager mNotificationManager = new MyNotificationManager(mContext);

                            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            myIntent.putExtra("number", jumlahNotifikasi);
                            mNotificationManager.showSmallNotification("Eletter Kebumen",
                                     jumlahNotifikasi + " surat masuk belum dibuka",
                                    myIntent,  false);

                            ShortcutBadger.applyCount(mContext, jumlahNotifikasi); //for 1.1.4+

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseSuratMasukOperator> call, Throwable t) {
                logger.d("XXX", t.getLocalizedMessage());
            }
        });
    }

    private void cekNotifikasi(Intent myIntent) {

        logger.d("XXX", "MyStartServiceReceiver3");
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
                            MyNotificationManager mNotificationManager = new MyNotificationManager(mContext);

                            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            myIntent.putExtra("number", jumlahNotifikasi);
                            mNotificationManager.showSmallNotification("Eletter Kebumen",
                                    data.getKonsep() + " konsep , " + data.getMasuk() + " surat masuk belum dibuka",
                                    myIntent,  false);

                            ShortcutBadger.applyCount(mContext, jumlahNotifikasi); //for 1.1.4+

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



    private void cekNotifikasiDesa(final Intent myIntent) {
        // desa dan kelurahan

        logger.d("XXX", "MyStartServiceReceiver4");

        ApiInterfaceDesa apiService =
                ApiClientDesa.getClient(mContext).create(ApiInterfaceDesa.class);

        String authorization = Config.AUTHORIZATION;
        String idPerangkat = prefManager.getIdPerangkat();

        logger.d("Token Eletter Konsep", authorization);

        if(!authorization.isEmpty() && !idPerangkat.isEmpty()){
            String useragent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";
            Call<ResultKonsepDesa> call = apiService.getKonsep(authorization,useragent, idPerangkat);

            call.enqueue(new Callback<ResultKonsepDesa>() {
                @Override
                public void onResponse(Call<ResultKonsepDesa> call, Response<ResultKonsepDesa> response) {
                    final ResultKonsepDesa result = response.body();
                    if(result !=null) {

                        logger.w10("hasil", response);

                        if (result.getStatus().equals(Tag.TAG_STATUS_SUKSES)) {

                            List<KonsepDesa> todos = result.getData();
                            if (todos.size() == 0) {


                            } else {
                                jumlahNotifikasi = todos.size();
                                if (jumlahNotifikasi > 0) {
                                    MyNotificationManager mNotificationManager = new MyNotificationManager(mContext);

                                    if(prefManager.getJabatan().contains("Lurah")){
                                        Intent mySecondIntent = new Intent(mContext, DashboardLurah.class);
                                        mySecondIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mySecondIntent.putExtra("number", jumlahNotifikasi);
                                        mNotificationManager.showSmallNotification("Eletter Kebumen",
                                                jumlahNotifikasi + " konsep ajuan belum dibuka",
                                                mySecondIntent,  false);
                                    }else{
                                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        myIntent.putExtra("number", jumlahNotifikasi);
                                        mNotificationManager.showSmallNotification("Eletter Kebumen",
                                                jumlahNotifikasi + " konsep ajuan belum dibuka",
                                                myIntent,  false);
                                    }


                                    ShortcutBadger.applyCount(mContext, jumlahNotifikasi); //for 1.1.4+


                                    if(prefManager.getJabatan().contains("Lurah")){

                                        Intent intentLurah = new Intent(mContext, DashboardLurah.class);

                                        cekNotifikasi(intentLurah);
                                    }

                                }
                            }

                        } else {

                        }



                    }else{
                        if(prefManager.getJabatan().toLowerCase().contains("lurah")){
                            //Dashboard.setBadge(0, String.valueOf(0));
                        }else{

                            id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.setBadge(0, String.valueOf(0));
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResultKonsepDesa> call, Throwable t) {


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
                }
            });
        }


    }
}
