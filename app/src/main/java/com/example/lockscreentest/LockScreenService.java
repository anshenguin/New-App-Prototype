package com.example.lockscreentest;

import android.app.KeyguardManager;
import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by HP INDIA on 17-Nov-17.
 */

public class LockScreenService  extends Service {

    BroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate() {

        Log.v("INSIDE","Service.java");
//        KeyguardManager.KeyguardLock key;
//        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        editor.putBoolean("CALL",true);
        editor.commit();

        if(sharedPreferences.getBoolean("SWITCH",true)) {
            Log.v("INSIDE","Service.java IF STATEMENT");
            //This is deprecated, but it is a simple way to disable the lockscreen in code
//            key = km.newKeyguardLock("IN");
//
//            key.disableKeyguard();

            //Start listening for the Screen On, Screen Off, and Boot completed actions
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_BOOT_COMPLETED);

            //Set up a receiver to listen for the Intents in this Service
            receiver = new LockScreenReceiver();
            registerReceiver(receiver, filter);
        }


        super.onCreate();
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("SWITCH",true))
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
