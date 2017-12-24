package adsfree.fullchargeindicator.rkr.com.fullchargeindicator_adsfree;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ray on 22/12/17.
 */

public class MyApp extends Application {
    private static SharedPreferences mPreferences;
    public static final String PREFERENCES_NAME = "adsfree.fullchargeindicator.rkr.com";
    private Context context;
    private static MyApp instance;

    @Override
    public void onCreate() {
        instance = this;
        context = getApplicationContext();
        mPreferences = getSharedPreferences(PREFERENCES_NAME,0);
        super.onCreate();
    }

    public static Context getContext(){
        return instance;
    }

    public static MyApp getInstance(){
        return instance;
    }

    public SharedPreferences getmPreferences(){
        return mPreferences;
    }
}
