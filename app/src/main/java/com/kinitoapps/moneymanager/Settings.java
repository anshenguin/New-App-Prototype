package com.kinitoapps.moneymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        setContentView(R.layout.activity_settings);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final Switch switch_unlock = (Switch) findViewById(R.id.switch_unlock);
        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);
        SharedPreferences firstRun = getSharedPreferences("com.example.lockscreentest",Context.MODE_PRIVATE);

        switch_unlock.setChecked(sharedPreferences.getBoolean("SWITCH",true));
        if(firstRun.getBoolean("firstrun",true)){

            Log.v("INSIDE","if statement of switch using firstrun");

            switch_unlock.setChecked(true);
            firstRun.edit().putBoolean("firstrun",false).commit();

        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final Context mContext = this;
        switch_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_unlock.isChecked()) {
                    Log.v("switch","on");
                    editor.putBoolean("SWITCH", true);
                    editor.commit();
                    startService(new Intent(mContext, LockScreenService.class));

                }
                else {
                    Log.v("switch","off");
                    editor.putBoolean("SWITCH", false);
                    editor.commit();
                    stopService(new Intent(mContext, LockScreenService.class));
                }

            }
        });
//
//        setSupportActionBar(toolbar);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Settings");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
