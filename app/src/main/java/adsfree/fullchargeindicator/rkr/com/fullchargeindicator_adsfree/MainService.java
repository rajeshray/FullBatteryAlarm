package adsfree.fullchargeindicator.rkr.com.fullchargeindicator_adsfree;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ray on 22/12/17.
 */

public class MainService extends Service {

    public static final String PREFERENCE_KEY_TONE = "ringtone";
    private static final String TAG = MainService.class.getName();
    private int mNotificationId = 0;
    private boolean mAlreadyNotified = false;
    private NotificationManager mNotificationManager;
//    private int mDebugNotificationId = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "creating service");
        mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void onDestroy() {
        Log.d(TAG, "destroying service");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public static void startIfEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MyApp.PREFERENCES_NAME, 0);
        boolean isEnabled = preferences.getBoolean(MainActivity.PREFERENCE_KEY_ENABLED, false);
        Intent intent = new Intent(context, MainService.class);
        if (isEnabled) {
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    int status = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, Integer.MIN_VALUE);
                    int level = MyApp.getInstance().getmPreferences().getInt("level_indicator",100);
                    if (level==status) {
                        if (!mAlreadyNotified) {
                            mAlreadyNotified = true;
                            SharedPreferences preferences = context.getSharedPreferences(MyApp.PREFERENCES_NAME, 0);
                            String strRingtonePreference = preferences.getString(PREFERENCE_KEY_TONE, "None");
                            Uri defaultSoundUri = Uri.parse(strRingtonePreference);
                            Log.d("iamhere",defaultSoundUri.toString());
                            int defaults = Notification.DEFAULT_LIGHTS;
                            if (preferences.getBoolean(MainActivity.PREFERENCE_KEY_VIBRATE, false)) {
                                defaults |= Notification.DEFAULT_VIBRATE;
                            }
                            if (!preferences.getBoolean(MainActivity.PREFERENCE_KEY_SOUND, false)) {
                                defaultSoundUri=null;
                            }

                            Notification notification = new NotificationCompat.Builder(context)
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setSmallIcon(R.drawable.ic_action_battery)
                                    .setContentTitle(getString(R.string.full))
                                    .setContentText(getString(R.string.unplug))
                                    .setSound(defaultSoundUri)
                                    .setOnlyAlertOnce(true)
                                    .setDefaults(defaults)
                                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context,MainActivity.class), 0))
                                    .build();
                            mNotificationManager.notify(mNotificationId, notification);
                        }
                    }
                    else {
                        mAlreadyNotified = false;
                        mNotificationManager.cancel(mNotificationId);
                    }

                    Log.d(TAG, String.format("battery status: %d", status));
//                    Notification notification = new NotificationCompat.Builder(context)
//                            .setSmallIcon(R.drawable.ic_launcher)
//                            .setContentTitle(getString(R.string.app_name))
//                            .setContentText(String.format("battery status: %d", status))
//                            .build();
//                    mNotificationManager.notify(mDebugNotificationId++, notification);
                }
            }
        }
    };

}
