package adsfree.fullchargeindicator.rkr.com.fullchargeindicator_adsfree;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class MainActivity extends AppCompatActivity{

    public static final String PREFERENCE_KEY_ENABLED = "enabled";
    public static final String PREFERENCE_KEY_VIBRATE = "vibrate";
    public static final String PREFERENCE_KEY_SOUND = "sound";
    public static final String PREFERENCE_BATTERY_LEVEL = "battery_level";
    public static final String PREFERENCE_BATTERY_HEALTH = "battery_health";
    public static final String PREFERENCE_BATTERY_VOLTAGE = "battery_voltage";
    public static final String PREFERENCE_BATTERY_CAPACITY = "battery_capacity";
    public static final String PREFERENCE_BATTERY_TEMPERATURE = "battery_temperature";
    public static final String PREFERENCE_BATTERY_ACTION = "battery_action";
    public static final String PREFERENCE_BATTERY_TECH = "battery_tech";


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int action = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            MyApp.getInstance().getmPreferences().edit().putInt(PREFERENCE_BATTERY_LEVEL,level).apply();
            MyApp.getInstance().getmPreferences().edit().putInt(PREFERENCE_BATTERY_VOLTAGE,voltage).apply();
            MyApp.getInstance().getmPreferences().edit().putInt(PREFERENCE_BATTERY_HEALTH,health).apply();
            MyApp.getInstance().getmPreferences().edit().putInt(PREFERENCE_BATTERY_TEMPERATURE,temperature).apply();
            MyApp.getInstance().getmPreferences().edit().putInt(PREFERENCE_BATTERY_ACTION,action).apply();
            MyApp.getInstance().getmPreferences().edit().putString(PREFERENCE_BATTERY_TECH,intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)).apply();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(R.id.linear_layout_fragment, new MyPreferenceFragment()).commit();
    }

    @Override
    protected void onResume() {
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBatInfoReceiver);
        super.onPause();
    }

    public static class MyPreferenceFragment extends PreferenceFragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            CheckBoxPreference enabling_button = (CheckBoxPreference) findPreference(getString(R.string.pref_enabled));
            CheckBoxPreference vibration_button = (CheckBoxPreference) findPreference(getString(R.string.pref_vibrate));
            CheckBoxPreference sound_preference = (CheckBoxPreference) findPreference(getString(R.string.pref_sound));

            //change listeners

            if(enabling_button.isChecked()){
                MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_ENABLED,true).apply();
                MainService.startIfEnabled(getActivity());
            }
            if(vibration_button.isChecked()){
                MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_VIBRATE,true).apply();
            }
            if(sound_preference.isChecked()){
                MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_SOUND,true).apply();
            }

            enabling_button.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(((boolean)o)){
                        MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_ENABLED,true).apply();
                    }else{
                        MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_ENABLED,false).apply();
                    }
                    MainService.startIfEnabled(getActivity());
                    return true;
                }
            });


            vibration_button.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(((boolean)o)){
                        MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_VIBRATE,true).apply();
                    }else{
                        MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_VIBRATE,false).apply();
                    }
                    return true;
                }
            });

            sound_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(((boolean)o)){
                        MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_SOUND,true).apply();
                    }else{
                        MyApp.getInstance().getmPreferences().edit().putBoolean(PREFERENCE_KEY_SOUND,false).apply();
                    }
                    return true;
                }
            });

            Preference select_tone = findPreference(getString(R.string.pref_choose_tone));
            String summarytext = MyApp.getInstance().getmPreferences().getString("ringtone_name","None");
            select_tone.setSummary(summarytext);
            select_tone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    chooseToneDialog();
                    return true;
                }
            });

            Preference notify_me = findPreference(getString(R.string.pref_notify_me));
            int text_sul = MyApp.getInstance().getmPreferences().getInt("level_indicator",100);
            notify_me.setSummary("charged "+text_sul+"%");
            notify_me.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    chooseBatteryLevelDialog();
                    return true;
                }
            });

            Preference battery_info = findPreference(getString(R.string.pref_battery_info));
            battery_info.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showBatteryInfoDialog();
                    return true;
                }
            });

            Preference more_apps = findPreference(getString(R.string.pref_more_apps));
            more_apps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openMoreApps();
                    return false;
                }
            });

            Preference goto_feedback = findPreference(getString(R.string.pref_give_feedback));
            goto_feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    giveFeedbackDialog();
                    return true;
                }
            });
        }

        private void giveFeedbackDialog(){
            String myDeviceModel = Build.MODEL;
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",getString(R.string.email), null));
            String[] address = new String[]{getString(R.string.email)};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for "+myDeviceModel);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Developer, \n\n");
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
        }

        private void openMoreApps(){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=rapidmusicplayer.rkr.com.rapidmusicplayer"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        private void showBatteryInfoDialog(){
            int level = MyApp.getInstance().getmPreferences().getInt(PREFERENCE_BATTERY_LEVEL,0);
            int health = MyApp.getInstance().getmPreferences().getInt(PREFERENCE_BATTERY_HEALTH,0);
            int temp = MyApp.getInstance().getmPreferences().getInt(PREFERENCE_BATTERY_TEMPERATURE,0);
            int voltage = MyApp.getInstance().getmPreferences().getInt(PREFERENCE_BATTERY_VOLTAGE,0);
            int action = MyApp.getInstance().getmPreferences().getInt(PREFERENCE_BATTERY_ACTION,0);
            String technology = MyApp.getInstance().getmPreferences().getString(PREFERENCE_BATTERY_TECH,"Unknown");
            String health_bat ="Unknown";
            if(health==3){
                health_bat = "Over heat";
            } else if(health==5){
              health_bat = "Over voltage";
            }
            else if(health==2){
                health_bat = "Good";
            }
            String action_bat = "Unknown";

            if(action==2){
                action_bat = "Charging";
            }else if(action==4){
                action_bat = "Not charging";
            }else if(action==3){
                action_bat = "Discharging";
            }

            StringBuilder info_battery = new StringBuilder();
            info_battery.append("   Status: ")
                        .append(action_bat)
                        .append("\n")
                        .append("   Level: ")
                        .append(level + " %")
                        .append("\n")
                        .append("   Health: ")
                        .append(health_bat)
                        .append("\n")
                        .append("   Voltage: ")
                        .append(voltage+" mV")
                        .append("\n")
                        .append("   Temperature: ")
                        .append(temp/10 +"."+temp%10+" °C")
                        .append("\n")
                        .append("   Battery technology: ")
                        .append(technology);
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Battery Info");
            LinearLayout linear = new LinearLayout(getActivity());
            linear.setOrientation(LinearLayout.VERTICAL);
            final TextView text = new TextView(getActivity());
            text.setText(info_battery);

            text.setPadding(20, 20,20,10);
            text.setTextSize(15);
            //text.setGravity(Gravity.CENTER);

            linear.addView(text);
            alert.setView(linear);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            alert.show();
        }

        private void chooseToneDialog(){
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            this.startActivityForResult(intent, 5);
        }

        @Override
        public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {

            if(resultCode == Activity.RESULT_OK && requestCode ==5){
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    MyApp.getInstance().getmPreferences().edit().putString("ringtone",uri.toString()).apply();
                    Ringtone ringtone = RingtoneManager.getRingtone(getActivity(),uri);
                    Log.d("mynameis",ringtone.getTitle(getActivity()));
                    MyApp.getInstance().getmPreferences().edit().putString("ringtone_name",ringtone.getTitle(getActivity())).apply();
                    findPreference(getString(R.string.pref_choose_tone)).setSummary(ringtone.getTitle(getActivity()));
                }

            }
        }

        private void chooseBatteryLevelDialog(){

            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

            LinearLayout linear = new LinearLayout(getActivity());
            linear.setOrientation(LinearLayout.VERTICAL);
            final TextView text = new TextView(getActivity());

            text.setPadding(0, 10,0,0);
            text.setGravity(Gravity.CENTER);
            int dura_text = MyApp.getInstance().getmPreferences().getInt("level_indicator",100);
            text.setText(dura_text + " %");

            final SeekBar seek = new SeekBar(getActivity());
            seek.setPadding(40,10,40,10);
            seek.setMax(100);
            seek.setProgress(dura_text);

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text.setText(progress +" %");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();

                }
            });

            linear.addView(seek);
            linear.addView(text);

            builder
                    .title(getActivity().getString(R.string.set_title))
                    .positiveText("Ok")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(seek.getProgress()!=0) {
                                MyApp.getInstance().getmPreferences().edit().putInt("level_indicator",seek.getProgress()).apply();
                                findPreference(getString(R.string.pref_notify_me)).setSummary("charged "+seek.getProgress()+"%");
                            }
                            else{
                                MyApp.getInstance().getmPreferences().edit().putInt("level_indicator",100).apply();
                                findPreference(getString(R.string.pref_notify_me)).setSummary("charged 100%");
                            }
                        }
                    })
                    .customView(linear,true)
                    .show();
        }

    }

}
