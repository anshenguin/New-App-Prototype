package com.example.lockscreentest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by HP INDIA on 17-Nov-17.
 */

public class LockScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("INSIDE","onReceive.java");
        String action = intent.getAction();

        //If the screen was just turned on or it just booted up, start your Lock Activity
//        if(action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED))
//        {
            Log.v("INSIDE","onReceive.java IF STATEMENT");
            Intent i = new Intent(context, com.example.lockscreentest.MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
//        }
    }
}
