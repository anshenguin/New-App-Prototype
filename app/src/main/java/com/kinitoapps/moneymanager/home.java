package com.kinitoapps.moneymanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TodayFragment.OnFragmentInteractionListener,YesterdayFragment.OnFragmentInteractionListener,ThisYearFragment.OnFragmentInteractionListener,ThisMonthFragment.OnFragmentInteractionListener, SelectedDateFragment.OnFragmentInteractionListener,OverviewFragment.OnFragmentInteractionListener, DatePickerDialog.OnDateSetListener{
    boolean mDrawerItemClicked = false;
    short clicked = 0;
    short selected;
    boolean cancelledCalendar = false;
    boolean changedDate;
    int LAST_SELECTED ;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    private static final int WRITE_EXT_STORAGE = 100;

    String currentDate;
    String CHANNEL_ID = "Quick Entry";
    boolean isDateSelected = false;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_entry);
            String description = getString(R.string.channel_entry_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
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
        String extStore = System.getenv("EXTERNAL_STORAGE");
        File file = new File(extStore+File.separator+"Money Manager"+File.separator+
                "Database");
        if(!file.exists()||!file.isDirectory())
            file.mkdirs();
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH_NOTIFICATION", Context.MODE_PRIVATE);
        SharedPreferences firstRun = getSharedPreferences("com.example.lockscreentest",Context.MODE_PRIVATE);
        SharedPreferences sh = getSharedPreferences("LIMIT",Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_today).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        if(firstRun.getBoolean("firstrun",true)) {
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
            firstRun.edit().putBoolean("firstrun",false).commit();
            sh.edit().putFloat("limit_today",0).commit();
            sh.edit().putFloat("limit_month",0).commit();
            sharedPreferences.edit().putBoolean("SWITCH_NOTIFICATION",false).commit();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null){
            if(b.getString("dailyormonthly").equals("daily")){
                replaceFragmentToToday();
                navigationView.getMenu().findItem(R.id.nav_overview).setChecked(false);
                navigationView.getMenu().findItem(R.id.nav_today).setChecked(true);
            }
            else{
                replaceFragmentToThisMonth();
                navigationView.getMenu().findItem(R.id.nav_overview).setChecked(false);
                navigationView.getMenu().findItem(R.id.nav_this_month).setChecked(true);
            }
        }
        else{
            LAST_SELECTED = R.id.nav_overview;
            selected = 6;
            navigationView.getMenu().findItem(R.id.nav_overview).setChecked(true);
        }

        if(sharedPreferences.getBoolean("SWITCH_NOTIFICATION",true))
            createNotification();
        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                cancelledCalendar = false;
            }

            @Override
            public void onDrawerClosed(final View drawerView) {

                if(mDrawerItemClicked) {
//                    Toast.makeText(home.this,"this is being called",Toast.LENGTH_LONG).show();
                    androidx.fragment.app.Fragment
                            fragment = null;
                    Class fragmentClass = null;
                    if(clicked!=5 && clicked !=8 && clicked !=7 && clicked !=9)
                        isDateSelected = false;
                    if(clicked == 9)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://kinitoapps.com/moneymanager/privacy.html")));

                    if (clicked ==7){
                        //TODO: SHARE ACTION
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String shareBodyText = "Hey there! I use Money Manager for Android to manage my expenses efficiently. I recommend you to use it as well. Download link: https://bit.ly/2GEypBS";
//                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject/Title");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                        startActivity(Intent.createChooser(intent, "Share app with others"));
                    }
                    if (clicked == 8)
                        startActivity(new Intent(home.this, Settings.class));
                    else if (clicked == 6) {
                        selected = 6;
                        fragmentClass = OverviewFragment.class;
                        try {
                            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
                    }
                    else if (clicked == 1) {
                        selected = 1;
                        fragmentClass = TodayFragment.class;
                        try {
                            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
                    }

                    else if (clicked == 2){
                        selected = 2;
                        fragmentClass = YesterdayFragment.class;
                        try {
                            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
                    }

                    else if(clicked == 3){
                        selected = 3;
                        fragmentClass = ThisMonthFragment.class;
                        try {
                            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();

                    }
                    else if(clicked == 4){
                        selected = 4;
                        fragmentClass = ThisYearFragment.class;
                        try {
                            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();

                    }
                    else if(clicked==5){
                        selected = 5;
                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                            builder.setTitle("Select a Date");

                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    if (!isDateSelected) {
                                        cancelledCalendar = true;
                                        navigationView.getMenu().findItem(R.id.nav_cal).setChecked(false);
                                        navigationView.getMenu().findItem(LAST_SELECTED).setChecked(true);
                                        onNavigationItemSelected(navigationView.getMenu().findItem(LAST_SELECTED));
                                    }
                                }
                            });
                            final LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.calendar_dialog, null);
                            final CalendarView calendarView = v.findViewById(R.id.calendar);

                            changedDate = false;
                            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                @Override
                                public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                                    changedDate = true;
                                    String dayS = String.valueOf(day), monthS = String.valueOf(month + 1);
                                    if ((day / 10) < 1)
                                        dayS = "0" + dayS;
                                    if (((month + 1) / 10) < 1)
                                        monthS = "0" + monthS;
                                    currentDate = dayS + "-" + monthS + "-" + year;
                                }
                            });
                            builder.setView(v);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    navigationView.getMenu().findItem(R.id.nav_cal).setChecked(true);
                                    navigationView.getMenu().findItem(R.id.nav_today).setChecked(false);
                                    isDateSelected = true;
                                    if (!changedDate) {
                                        long milliseconds = calendarView.getDate();
                                        Date date = new Date(milliseconds);
                                        //dd/mm/yyyy
                                        String currentDateWithSlashes = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
                                        currentDate = currentDateWithSlashes.substring(0, 2) + "-"
                                                + currentDateWithSlashes.substring(3, 5) + "-"
                                                + currentDateWithSlashes.substring(6);
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Date", currentDate);
                                    Class fragmentClass = null;
                                    androidx.fragment.app.Fragment fragment = null;
                                    fragmentClass = SelectedDateFragment.class;
                                    try {
                                        fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
                                        fragment.setArguments(bundle);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();

                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        if (!isDateSelected) {
                                            cancelledCalendar = true;
                                            navigationView.getMenu().findItem(R.id.nav_cal).setChecked(false);
                                            navigationView.getMenu().findItem(LAST_SELECTED).setChecked(true);
                                            onNavigationItemSelected(navigationView.getMenu().findItem(LAST_SELECTED));
                                        }
                                }
                            });
                            builder.show();
                        }
                        else{
                            if (!isDateSelected) {
                                navigationView.getMenu().findItem(R.id.nav_cal).setChecked(false);
                                navigationView.getMenu().findItem(LAST_SELECTED).setChecked(true);
//                            Toast.makeText(home.this,"This Feature Requires Android 6.0+",Toast.LENGTH_LONG).show();

                            }
                            DialogFragment newFragment = new DatePickerFragment();
                            newFragment.show(getFragmentManager(), "datePicker");
                        }



                    }


                    clicked = 0;
                    mDrawerItemClicked = false;
                }


            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(LAST_SELECTED == R.id.nav_overview)
                super.onBackPressed();
            else
            {
                androidx.fragment.app.Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = OverviewFragment.class;
                try {
                    fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
                clicked = 6;
                selected = 6;
                navigationView.getMenu().findItem(LAST_SELECTED).setChecked(false);
                navigationView.getMenu().findItem(R.id.nav_overview).setChecked(true);
                LAST_SELECTED = R.id.nav_overview;


            }
        }

    }
//workaround because opening settings does some weird stuff to today tab
//    @Override
//    protected void onResume() {
//        super.onResume();
//        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
//        boolean isNoneChecked = true;
//        int size = mNavigationView.getMenu().size();
//        for (int i = 0; i < size; i++) {
//            if(mNavigationView.getMenu().getItem(i).isChecked()){
//                isNoneChecked = false;
//                break;
//            }
//
//        }
//
//        if(isNoneChecked)
//            mNavigationView.getMenu().findItem(R.id.nav_camera).setChecked(true);
//
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //workaround for bug with selecting item 1 at default in navigationview
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        if(navigationView.getMenu().findItem(R.id.nav_camera).isChecked() && clicked!=1){
//            navigationView.getMenu().findItem(R.id.nav_camera).setChecked(false);
//
//        }
//        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(id!=R.id.nav_cal&&id!=R.id.nav_settings&&id!=R.id.nav_share)
        LAST_SELECTED = id;
        if (id == R.id.nav_today) {
            clicked = 1;
            selected = 1;
            //part of the work around
//            navigationView.getMenu().findItem(R.id.nav_camera).setChecked(true);
        } else if (id == R.id.nav_this_month) {
            clicked = 3;
            selected = 3;

        } else if (id == R.id.nav_year) {
            clicked = 4;
            selected = 4;
//        } else if (id == R.id.nav_about) {
//            clicked = 6;
//
        } else if (id == R.id.nav_settings) {
            clicked = 8;
        }
        else if(id == R.id.nav_cal){
            clicked = 5;
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            selected = 5;
        }
        else if(id == R.id.nav_overview){
            clicked = 6;
            selected = 6;
        }
        else if(id==R.id.nav_share){
            clicked = 7;
        }
        else if(id==R.id.nav_priv)
            clicked = 9;
        if(!cancelledCalendar) {
            mDrawerItemClicked = true;
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            mDrawerItemClicked = false;
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected

//        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.O) {
//            Intent intent = new Intent(this, EnterValueActivity.class);
//            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//            // Build notification
//            // Actions are just fake
//            Notification noti = new Notification.Builder(this)
//                    .setContentTitle("Money Manager")
//                    .setContentText("Click to add an entry").setSmallIcon(R.drawable.noti_wallet)
//                    .setContentIntent(pIntent)
//                    .setOngoing(true)
//                    .build();
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            // hide the notification after its selected
//        noti.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
//            notificationManager.notify(0, noti);
//        }
//        else{
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            Intent resultIntent = new Intent(this, EnterValueActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addParentStack(EnterValueActivity.class);
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent =
//                    stackBuilder.getPendingIntent(
//                            0,
//                            PendingIntent.FLAG_UPDATE_CURRENT
//                    );
//
//            String id = "enter_channel";
//            NotificationChannel mChannel = new NotificationChannel(id, "Money Manager", NotificationManager.IMPORTANCE_HIGH);
//            mChannel.setDescription("Click here to add an entry");
//            android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(this,id)
//                    .setContentTitle("Money Manager")
//                    .setContentText("Click to add an entry").setSmallIcon(R.drawable.noti_wallet)
//                    .setOngoing(true);
//            mBuilder.setContentIntent(resultPendingIntent);
//            mNotificationManager.notify(0, mBuilder.build());
//
//
//
//
//        }

        Intent intent = new Intent(this, EnterValueActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"Quick Entry")
                .setSmallIcon(R.drawable.noti_wallet)
                .setContentTitle("Quick Entry Notification")
                .setContentText("Click to add an entry")
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(100, mBuilder.build());
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Toast.makeText(this,"onResume",Toast.LENGTH_SHORT).show();
//    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
//        Toast.makeText(this,"onPostResume",Toast.LENGTH_SHORT).show();

        androidx.fragment.app.Fragment fragment = null;
        Class fragmentClass = null;



        if(selected == 5 && isDateSelected) {

                    Bundle bundle = new Bundle();
                    bundle.putString("Date", currentDate);
                    fragment = null;
                    fragmentClass = SelectedDateFragment.class;
                    try {
                        fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment, "date_fragment").commit();

        }
        else if (selected == 6) {
            fragmentClass = OverviewFragment.class;
            try {
                fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        }

    }

    public void disableDrawer(){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
    }

    public void enableDrawer(){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }



    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (home) getActivity(), year, month, day);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        isDateSelected=true;
        navigationView.getMenu().findItem(LAST_SELECTED).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_cal).setChecked(true);
        String dayS = String.valueOf(day), monthS = String.valueOf(month + 1);
        if ((day / 10) < 1)
            dayS = "0" + dayS;
        if (((month + 1) / 10) < 1)
            monthS = "0" + monthS;
        currentDate = dayS + "-" + monthS + "-" + year;
        androidx.fragment.app.Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putString("Date", currentDate);
        Class fragmentClass = null;
        fragment = null;
        fragmentClass = SelectedDateFragment.class;
        try {
            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();
    }

    public void replaceFragmentToToday(){
        androidx.fragment.app.Fragment fragment;
        navigationView.getMenu().findItem(R.id.nav_overview).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_today).setChecked(true);
        LAST_SELECTED = R.id.nav_today;
        Class fragmentClass = null;
        fragment = null;
        selected = 1;
        fragmentClass = TodayFragment.class;
        try {
            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();
    }
    public void replaceFragmentToThisMonth(){
        selected = 3;
        androidx.fragment.app.Fragment fragment;
        navigationView.getMenu().findItem(R.id.nav_overview).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_this_month).setChecked(true);
        LAST_SELECTED = R.id.nav_this_month;
        Class fragmentClass = null;
        fragment = null;
        fragmentClass = ThisMonthFragment.class;
        try {
            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();
    }
    public void replaceFragmentToThisYear(){
        selected = 4;
        androidx.fragment.app.Fragment fragment;
        navigationView.getMenu().findItem(R.id.nav_overview).setChecked(false);
        navigationView.getMenu().findItem(R.id.nav_year).setChecked(true);
        LAST_SELECTED = R.id.nav_year;
        Class fragmentClass = null;
        fragment = null;
        fragmentClass = ThisYearFragment.class;
        try {
            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXT_STORAGE);

            Toast.makeText(this, "not allowed", Toast.LENGTH_SHORT).show();
        }

    }
}

