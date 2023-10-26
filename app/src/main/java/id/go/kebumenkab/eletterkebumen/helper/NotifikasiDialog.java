package id.go.kebumenkab.eletterkebumen.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.pns.Dashboard;

import static id.go.kebumenkab.eletterkebumen.helper.AppBaseActivity.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION;

public class NotifikasiDialog {

    private Context mContext;
    private Activity mActivity;

    public NotifikasiDialog(Context mContext, Activity mActivity){
        this.mActivity = mActivity;
        this.mContext   = mContext;
    }

    public NotifikasiDialog(Context mContext){
        this.mContext   = mContext;
    }

    public void showToast(String pesan){
        Toast.makeText(mContext, pesan, Toast.LENGTH_LONG).show();
    }

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/
    public void showDialogError(final int i, String pesan){

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning_empty_message, null);
        TextView tvMessage = (TextView)dialogView.findViewById(R.id.message_text);
        ImageView errorGambar = (ImageView)dialogView.findViewById(R.id.gambar);
        PrefManager prefManager = new PrefManager(mContext);

        if(i==1){
            // periksa internet
            dialogView = inflater.inflate(R.layout.dialog_warning, null);
            pesan = mActivity.getString(R.string.error_network);
        }else if(i==2){
            // periksa server
            pesan = mActivity.getString(R.string.error_api);
        }else if(i==3){
            // data tidak ditemukan
            pesan = mActivity.getString(R.string.error_empty);
        }else if(i==4){
            // data tidak ditemukan
            pesan = mActivity.getString(R.string.error_empty);
        }else if(i==5){
            dialogView = inflater.inflate(R.layout.dialog_warning_empty_message, null);
            builder.setView(dialogView );
        }else if(i==6){
            // Ketika passphrase masih kurang dari 7 karakter
            pesan = mActivity.getString(R.string.message_minimal_8_karakter);
        }else if(i==7){
            // dialog lupa password
            pesan = mActivity.getString(R.string.message_silakan_hub_bagian_organisasi);
        }else if(i==8){
            pesan = mActivity.getString(R.string.error_empty_message_tindakan);
        }else if(i==9){
            errorGambar.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_setuju_blue_24dp, null));
            pesan = mActivity.getString(R.string.message_success);
            builder.setCancelable(false);
        }else if(i==10){
            pesan = mActivity.getString(R.string.message_failed);
        }else if(i==11){
            errorGambar.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_setuju_blue_24dp, null));
            pesan = mActivity.getString(R.string.message_success);
            builder.setCancelable(false);

        }else if(i==12){
            pesan = "Ganti password lain waktu";
        }
        else if(i==13){
            pesan = "Proses Gagal: "+pesan;
        }

        tvMessage.setText(pesan);

        builder.setView(dialogView);
        builder.setPositiveButton(mContext.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                Intent intent = new Intent(mContext, Dashboard.class);
                if(i==9){

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    closeAllActivities();
                    mActivity.startActivity(intent);
                }else if(i==11){
                    intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);

                    // jika statusnya adalah lurah maka ganti intent

                    if(prefManager.getJabatan().contains("Lurah")){
                        intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah.class);

                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    closeAllActivities();
                    mActivity.startActivity(intent);

                }else if(i==12){
                    intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);

                    if(prefManager.getJabatan().contains("Lurah")){
                        intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah.class);

                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    closeAllActivities();
                    mActivity.startActivity(intent);
                }else{
                    dialogInterface.cancel();
                }
            }
        });

        try {
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** Pop up ketika tidak terkoneksi internet atau error lainnya**/
    public void showDialogSukses(final int i, String pesan, Class mClass){

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning_empty_message, null);
        TextView tvMessage = (TextView)dialogView.findViewById(R.id.message_text);
        ImageView errorGambar = (ImageView)dialogView.findViewById(R.id.gambar);
        PrefManager prefManager = new PrefManager(mContext);

        if(i==1){
            // periksa internet
            dialogView = inflater.inflate(R.layout.dialog_warning, null);
            pesan = mActivity.getString(R.string.error_network);
        }else if(i==2){
            // periksa server
            pesan = mActivity.getString(R.string.error_api);
        }else if(i==3){
            // data tidak ditemukan
            pesan = mActivity.getString(R.string.error_empty);
        }else if(i==4){
            // data tidak ditemukan
            pesan = mActivity.getString(R.string.error_empty);
        }else if(i==5){
            dialogView = inflater.inflate(R.layout.dialog_warning_empty_message, null);
            builder.setView(dialogView );
        }else if(i==6){
            // Ketika passphrase masih kurang dari 7 karakter
            pesan = mActivity.getString(R.string.message_minimal_8_karakter);
        }else if(i==7){
            // dialog lupa password
            pesan = mActivity.getString(R.string.message_silakan_hub_bagian_organisasi);
        }else if(i==8){
            pesan = mActivity.getString(R.string.error_empty_message_tindakan);
        }else if(i==9){
            errorGambar.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_setuju_blue_24dp, null));
            pesan = mActivity.getString(R.string.message_success);
            builder.setCancelable(false);
        }else if(i==10){
            pesan = mActivity.getString(R.string.message_failed);
        }else if(i==11){
            errorGambar.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_setuju_blue_24dp, null));
            pesan = mActivity.getString(R.string.message_success);
            builder.setCancelable(false);

        }else if(i==12){
            pesan = "Ganti password lain waktu";
        }

        tvMessage.setText(pesan);

        builder.setView(dialogView);
        builder.setPositiveButton(mContext.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                Intent intent = new Intent(mContext, mClass);
                if(i==9){

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    closeAllActivities();
                    mActivity.startActivity(intent);
                }else if(i==11){
                    intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);

                    // jika statusnya adalah lurah maka ganti intent

                    if(prefManager.getJabatan().contains("Lurah")){
                        intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah.class);

                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    closeAllActivities();
                    mActivity.startActivity(intent);

                }else if(i==12){
                    intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.desa.Dashboard.class);

                    if(prefManager.getJabatan().contains("Lurah")){
                        intent = new Intent(mContext, id.go.kebumenkab.eletterkebumen.activity.lurah.DashboardLurah.class);

                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    closeAllActivities();
                    mActivity.startActivity(intent);
                }else{
                    dialogInterface.cancel();
                }
            }
        });

        try {
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void showDialogSuccessError(final int status){

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warning_empty_message, null);
        ImageView errorGambar = (ImageView)dialogView.findViewById(R.id.gambar);

        TextView tvPesan =  (TextView)dialogView.findViewById(R.id.message_text);
        if(status == 1){
           errorGambar.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_setuju_blue_24dp, null));
            tvPesan.setText(mActivity.getString(R.string.message_success));

        }else if(status == 2){
            errorGambar.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_error_outline_dark_24dp, null));
            tvPesan.setText(mActivity.getString(R.string.message_failed));
        }
        builder.show();
    }

    public void closeAllActivities(){
        mActivity.sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }
}
