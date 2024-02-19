package id.go.kebumenkab.eletterkebumen.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AppBaseActivity extends AppCompatActivity {
    public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "id.go.kebumenkab.eletterkebumen.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
    private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
    public static final IntentFilter INTENT_FILTER = createIntentFilter();

    private static IntentFilter createIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
        return filter;
    }

    protected void registerBaseActivityReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(baseActivityReceiver, new IntentFilter(INTENT_FILTER), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(baseActivityReceiver, INTENT_FILTER);
//            requireActivity().registerReceiver(refreshUIReceiver, new IntentFilter("com.gt-broadcast-refresh"));
        }
    }

    protected void unRegisterBaseActivityReceiver() {
        unregisterReceiver(baseActivityReceiver);
    }

    public class BaseActivityReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)){
                finish();
            }
        }
    }

    protected void closeAllActivities(){
        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }
}