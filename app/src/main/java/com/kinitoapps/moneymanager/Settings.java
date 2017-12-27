package com.kinitoapps.moneymanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        setContentView(R.layout.activity_settings);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        final Switch switch_unlock = (Switch) findViewById(R.id
//                .switch_unlock);
        final Switch switch_notification = (Switch) findViewById(R.id.switch_notification);
        LinearLayout createShortcut = (LinearLayout) findViewById(R.id.create_shortcut);
        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH_NOTIFICATION", Context.MODE_PRIVATE);
        SharedPreferences firstRun = getSharedPreferences("com.example.lockscreentest",Context.MODE_PRIVATE);

//        switch_unlock.setChecked(sharedPreferences.getBoolean("SWITCH",true));
        if(firstRun.getBoolean("firstrun",true)){
//            switch_unlock.setChecked(true);
            switch_notification.setChecked(true);
            firstRun.edit().putBoolean("firstrun",false).commit();
        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        createShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEnterValueActivityShortcut();
            }
        });
        switch_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_notification.isChecked()){
                    editor.putBoolean("switch notification",true);
                    Intent intent = new Intent(getApplicationContext(), EnterValueActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

                    // Build notification
                    // Actions are just fake
                    Notification noti = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Money Manager notification is on")
                            .setContentText("Click to add an entry").setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pIntent)
                            .setOngoing(true)
                            .build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;

                    notificationManager.notify(0, noti);
                }

                else{
                    editor.putBoolean("switch notification",false);
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(0);
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

    private void createEnterValueActivityShortcut() {

        Intent shortcutIntent = new Intent(getApplicationContext(),
                EnterValueActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Enter Value");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
        getApplicationContext().sendBroadcast(addIntent);
    }
}
