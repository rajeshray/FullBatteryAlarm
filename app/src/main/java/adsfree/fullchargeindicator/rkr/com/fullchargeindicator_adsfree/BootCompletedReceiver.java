package adsfree.fullchargeindicator.rkr.com.fullchargeindicator_adsfree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ray on 22/12/17.
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    public BootCompletedReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())){
            MainService.startIfEnabled(context);
        }
    }
}
