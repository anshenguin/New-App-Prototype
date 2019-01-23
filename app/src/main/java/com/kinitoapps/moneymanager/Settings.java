package com.kinitoapps.moneymanager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;
import com.kinitoapps.moneymanager.tutorialviews.IntroActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class Settings extends AppCompatActivity {
    private float lim = 0;
    private String currentDate;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_entry);
            String description = getString(R.string.channel_entry_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel("Quick Entry", name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);

            name = getString(R.string.channel_daily_monthly);
            description = getString(R.string.channel_daily_monthly_description);
            importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel("Daily and Monthly Limit", name, importance);
            mChannel.setDescription(description);
            mChannel.setVibrationPattern(new long[] { 1000, 1000});

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
        setContentView(R.layout.activity_settings);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        final Switch switch_unlock = (Switch) findViewById(R.id
//                .switch_unlock);
        final SwitchCompat switch_notification = findViewById(R.id.switch_notification);
        LinearLayout createShortcut = findViewById(R.id.create_shortcut);
        LinearLayout setDailyLimit = findViewById(R.id.set_daily_limit);
        LinearLayout setMonthlyLimit = findViewById(R.id.set_monthly_limit);
        LinearLayout viewTutorial = findViewById(R.id.view_intro);
        viewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.this, IntroActivity.class);
                i.putExtra("openedFromSettings","yes");
                startActivity(i);
            }
        });
        sharedPreferences = getSharedPreferences("LIMIT", Context.MODE_PRIVATE);


        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH_NOTIFICATION", Context.MODE_PRIVATE);
        final SharedPreferences sh = getSharedPreferences("LIMIT",Context.MODE_PRIVATE);

//        switch_unlock.setChecked(sharedPreferences.getBoolean("SWITCH",true));
//        if(firstRun.getBoolean("firstrun",true)){
////            switch_unlock.setChecked(true);
//            switch_notification.setChecked(true);
//            firstRun.edit().putBoolean("firstrun",false).commit();
//
//        }

        switch_notification.setChecked(sharedPreferences.getBoolean("SWITCH_NOTIFICATION",true));

//        else{
//            if(sharedPreferences.getBoolean("switch notification",true) || sharedPreferences.)
//                switch_notification.setChecked(true);
//            else
//                switch_notification.setChecked(false);
//        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        createShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEnterValueActivityShortcut();
            }
        });

        setDailyLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Set Daily Limit");
                final LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.daily_limit_dialog,null);
                builder.setView(v);
                final EditText input = v.findViewById(R.id.daily_limit_value);
                input.setText(String.valueOf(sh.getFloat("limit_today",0)));
                input.selectAll();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!isNumeric(input.getText().toString())){
                            if(TextUtils.isEmpty(input.getText().toString()))
                                sh.edit().putFloat("limit_today", 0).commit();
                            else{
                                Toast.makeText(Settings.this,"Please enter a numeric value as the limit",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            lim = Float.parseFloat(input.getText().toString());
                            sh.edit().putFloat("limit_today", lim).commit();
                            checkForDailyLimit();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_HIDDEN,0);
                    }
                });
                builder.show();

            }
        });

        setMonthlyLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Set Monthly Limit");
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.monthly_limit_dialog,null);
                builder.setView(v);
                final EditText input = v.findViewById(R.id.monthly_limit_value);
                input.setText(String.valueOf(sh.getFloat("limit_month",0)));
                input.selectAll();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!isNumeric(input.getText().toString())){
                            if(TextUtils.isEmpty(input.getText().toString()))
                                sh.edit().putFloat("limit_month", 0).commit();

                            else
                                Toast.makeText(Settings.this,"Please enter a numeric value as the limit",Toast.LENGTH_LONG).show();
                        }
                        else {
                            lim = Float.parseFloat(input.getText().toString());
                            sh.edit().putFloat("limit_month", lim).commit();
                            checkForMonthlyLimit();

                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_HIDDEN,0);
                    }
                });
                builder.show();
            }
        });
        switch_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_notification.isChecked()){
                    editor.putBoolean("SWITCH_NOTIFICATION",true);
                    editor.commit();
//                    if(Build.VERSION.SDK_INT< Build.VERSION_CODES.O) {
//                        Intent intent = new Intent(Settings.this, EnterValueActivity.class);
//                        PendingIntent pIntent = PendingIntent.getActivity(Settings.this, (int) System.currentTimeMillis(), intent, 0);
//
//                        // Build notification
//                        // Actions are just fake
//                        Notification noti = new Notification.Builder(Settings.this)
//                                .setContentTitle("Money Manager")
//                                .setContentText("Click to add an entry").setSmallIcon(R.drawable.noti_wallet)
//                                .setContentIntent(pIntent)
//                                .setOngoing(true)
//                                .build();
//                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        // hide the notification after its selected
//        noti.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
//
//                        notificationManager.notify(0, noti);
//                    }
//                    else{
//                        NotificationManager mNotificationManager =
//                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        Intent resultIntent = new Intent(Settings.this, EnterValueActivity.class);
//                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(Settings.this);
//                        stackBuilder.addParentStack(EnterValueActivity.class);
//                        stackBuilder.addNextIntent(resultIntent);
//                        PendingIntent resultPendingIntent =
//                                stackBuilder.getPendingIntent(
//                                        0,
//                                        PendingIntent.FLAG_UPDATE_CURRENT
//                                );
//
//                        String id = "enter_channel";
//                        NotificationChannel mChannel = new NotificationChannel(id, "Money Manager", NotificationManager.IMPORTANCE_HIGH);
//                        mChannel.setDescription("Click here to add an entry");
//                        android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(Settings.this,id)
//                                .setContentTitle("Money Manager")
//                                .setContentText("Click to add an entry").setSmallIcon(R.drawable.noti_wallet)
//                                .setOngoing(true);
//                        mBuilder.setContentIntent(resultPendingIntent);
//                        mNotificationManager.notify(0, mBuilder.build());

                    Intent intent = new Intent(Settings.this, EnterValueActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(Settings.this, 0, intent, 0);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Settings.this,"Quick Entry")
                            .setSmallIcon(R.drawable.noti_wallet)
                            .setContentTitle("Quick Entry Notification")
                            .setContentText("Click to add an entry")
                            .setOngoing(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Settings.this);
                    notificationManager.notify(100, mBuilder.build());


                    }
                else{
                    editor.putBoolean("SWITCH_NOTIFICATION",false);
                    editor.commit();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Settings.this);
                    notificationManager.cancel(100);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager mShortcutManager =
                    getApplicationContext().getSystemService(ShortcutManager.class);
            if (mShortcutManager.isRequestPinShortcutSupported()) {

                ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(getApplicationContext(), "entry-shortcut")
                        .setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_shortcut_2))

                        .setShortLabel("Create New Entry")
                        .setIntent(new Intent(this, EnterValueActivity.class).setAction(Intent.ACTION_MAIN))
                        .build();
                Intent pinnedShortcutCallbackIntent =
                        mShortcutManager.createShortcutResultIntent(pinShortcutInfo);
                PendingIntent successCallback = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        pinnedShortcutCallbackIntent, 0);
                mShortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
            }
        }
        else {
            Intent shortcutIntent = new Intent(getApplicationContext(),
                    EnterValueActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent
                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Create New Entry");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.mipmap.ic_shortcut_2));

            addIntent
                    .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
            getApplicationContext().sendBroadcast(addIntent);
        }



        }



    /**
     * Created by HP INDIA on 04-Jan-18.
     */

    public boolean isNumeric(String s) {
        return s.matches("[+]?\\d*\\.?\\d+");
    }

    public double getDailySumSpent(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";

        String[] ARGS = {currentDate,"1"};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        double sumSpent = 0;
        String[] PROJECTION = {
                "SUM(value)"
        };
        Cursor cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                ARGS,
                null,null,null);
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 1 AND date = "+currentDate, null);
        if(cur.moveToFirst())
        {
            sumSpent = cur.getDouble(0);
            cur.close();
        }
        return sumSpent;
    }

    public double getMonthlySumSpent(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String monthAndYear = currentDate.substring(2);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+monthAndYear,"1"};
        double sumspent = 0;
        MoneyDbHelper mDbHelper = new MoneyDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] PROJECTION = {
                "SUM(value)"
        };
        Cursor cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                ARGS,
                null,null,null);
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 1 AND date LIKE %"+monthAndYear, null);
        if(cur.moveToFirst())
        {
            sumspent = cur.getDouble(0);
            cur.close();
        }
        return sumspent;
    }

    private void checkForDailyLimit(){
        boolean crossesDailyLimit = false;
        //TODO: DONT CHECK FOR LIMIT IF ALREADY CHECKED TODAY
        float limit_today = sharedPreferences.getFloat("limit_today",0);
        if(limit_today<= getDailySumSpent()&&limit_today>0){
            // Build notification
            // Actions are just fake
            crossesDailyLimit = true;
        }



        if(crossesDailyLimit)
            Toast.makeText(Settings.this,"This limit value has already been exceeded today",Toast.LENGTH_LONG).show();

    }

    private void checkForMonthlyLimit(){
        boolean crossesMonthlyLimit = false;
        //TODO: DONT CHECK FOR LIMIT IF ALREADY CHECKED TODAY
        float limit_month = sharedPreferences.getFloat("limit_month",0);
        if(limit_month<=getMonthlySumSpent()&&limit_month>0){
            crossesMonthlyLimit = true;
        }

        if(crossesMonthlyLimit)
            Toast.makeText(Settings.this,"This limit value has already been exceeded this month",Toast.LENGTH_LONG).show();

    }

}
