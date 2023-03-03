package id.go.kebumenkab.eletterkebumen.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;
import id.go.kebumenkab.eletterkebumen.helper.Tag;


public class MyNotificationManager {

    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void showSmallNotification(String title, String message, Intent intent, boolean random) {

        Uri path = Uri.parse("android.resource://id.go.kebumenkab.eletterkebumen/" + R.raw.notif);

        NotificationManager notificationManager =(NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID ="my_channel_id_01";

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mCtx);
        stackBuilder.addParentStack(Dashboard.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notifications",  NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(Tag.TAG_APLIKASI);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500,1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),att);


            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }


            int jumlahNotifikasi = 0;
            if(intent.hasExtra("number")) jumlahNotifikasi = intent.getIntExtra("number", 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, NOTIFICATION_CHANNEL_ID);
            Notification notification;
            notification = mBuilder
                    .setTicker(title)
                    .setWhen(0)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.logo))
                    .setContentText(message)
                    .setNumber(jumlahNotifikasi)
                    .setLights(0xff00ff00, 300, 100)
                    .setVibrate(new long[]{0, 1000, 500,1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            if (notificationManager != null) {
                // Jika pesan dari Firebase
                if(random){
                    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                    notificationManager.notify(m, notification);

                }else{
                    // Jika pesan dari AlarmManager
                    notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
                }

            }
    }
}
