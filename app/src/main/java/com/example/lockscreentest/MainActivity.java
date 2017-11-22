package com.example.lockscreentest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.lockscreentest.LockScreenService;
import com.example.lockscreentest.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("INSIDE","MainActivity.java");
        super.onCreate(savedInstanceState);

        //Set up our Lockscreen
        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        if(sharedPreferences.getBoolean("SWITCH",true)&&canCallNow.getBoolean("CALL",true)) {
            Log.v("INSIDE","MainActivity.java if statement");
            makeFullScreen();
            editor.putBoolean("CALL",false);
            editor.commit();
//            startService(new Intent(this, LockScreenService.class));

            setContentView(R.layout.activity_main);
        }

        else{
            finish();
        }
    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     *   the Actionbar and the virtual keys (if they are on the phone)
     */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        editor.putBoolean("CALL",true);
        editor.commit();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
