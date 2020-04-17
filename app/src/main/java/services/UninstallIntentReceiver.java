package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UninstallIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // fetching package names from extras
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if(packageNames!=null){
            for(String packageName: packageNames){
                if(packageName!=null && packageName.equals("YOUR_APPLICATION_PACKAGE_NAME")){
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    new ListenForUninstall(context).start();

                }
            }
        }
    }
}
