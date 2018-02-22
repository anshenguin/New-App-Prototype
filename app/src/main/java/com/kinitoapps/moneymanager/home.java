package com.kinitoapps.moneymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TodayFragment.OnFragmentInteractionListener,YesterdayFragment.OnFragmentInteractionListener,ThisYearFragment.OnFragmentInteractionListener,ThisMonthFragment.OnFragmentInteractionListener, SelectedDateFragment.OnFragmentInteractionListener, DatePickerDialog.OnDateSetListener {
    boolean mDrawerItemClicked = false;
    short clicked = 0;
    short selected = 1;
    boolean cancelledCalendar = false;
    boolean changedDate;
    int LAST_SELECTED = R.id.nav_today;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    String currentDate;
    boolean isDateSelected = false;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Enter Value");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.mipmap.ic_shortcut));
            addIntent
                    .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
            getApplicationContext().sendBroadcast(addIntent);
            firstRun.edit().putBoolean("firstrun",false).commit();
            sh.edit().putFloat("limit_today",0).commit();
            sh.edit().putFloat("limit_month",0).commit();
            sharedPreferences.edit().putBoolean("SWITCH_NOTIFICATION",true).commit();
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
                    android.support.v4.app.Fragment fragment = null;
                    Class fragmentClass = null;
                    if(clicked!=5 && clicked !=8)
                        isDateSelected = false;
                    if (clicked == 8)
                        startActivity(new Intent(home.this, Settings.class));
//                    else if (clicked == 6)
//                        startActivity(new Intent(home.this,AboutActivity.class));
                    else if (clicked == 1) {
                        selected = 1;
                        fragmentClass = TodayFragment.class;
                        try {
                            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
                    }

                    else if (clicked == 2){
                        selected = 2;
                        fragmentClass = YesterdayFragment.class;
                        try {
                            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
                    }

                    else if(clicked == 3){
                        selected = 3;
                        fragmentClass = ThisMonthFragment.class;
                        try {
                            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();

                    }
                    else if(clicked == 4){
                        selected = 4;
                        fragmentClass = ThisYearFragment.class;
                        try {
                            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();

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
                                    android.support.v4.app.Fragment fragment = null;
                                    fragmentClass = SelectedDateFragment.class;
                                    try {
                                        fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                                        fragment.setArguments(bundle);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();

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
                            //TODO: DATE PICKER FOR PRE MARSHMALLOW
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
        navigationView.getMenu().findItem(R.id.nav_today).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        if(id!=R.id.nav_cal&&id!=R.id.nav_settings)
        LAST_SELECTED = id;
        if (id == R.id.nav_today) {
            clicked = 1;
            selected = 1;
            //part of the work around
//            navigationView.getMenu().findItem(R.id.nav_camera).setChecked(true);
        } else if (id == R.id.nav_yesterday) {
            clicked = 2;
            selected = 2;


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

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, EnterValueActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            // Build notification
            // Actions are just fake
            Notification noti = new Notification.Builder(this)
                    .setContentTitle("Money Manager")
                    .setContentText("Click to add an entry").setSmallIcon(R.drawable.noti_wallet)
                    .setContentIntent(pIntent)
                    .setOngoing(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
        noti.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(0, noti);
        }
        else{
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(this, EnterValueActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(EnterValueActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            String id = "enter_channel";
            NotificationChannel mChannel = new NotificationChannel(id, "Money Manager", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("Click here to add an entry");
            android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(this,id)
                    .setContentTitle("Money Manager")
                    .setContentText("Click to add an entry").setSmallIcon(R.drawable.noti_wallet)
                    .setOngoing(true);
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(0, mBuilder.build());




        }

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

        android.support.v4.app.Fragment fragment = null;
        Class fragmentClass = null;
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
//        Toast.makeText(home.this,"isDateSelected="+isDateSelected,Toast.LENGTH_LONG).show();

        if(selected == 1){
            fragmentClass = TodayFragment.class;
            try {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        }
        else if(selected == 2){
            fragmentClass = YesterdayFragment.class;
            try {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        }

        else if(selected == 3){
            fragmentClass = ThisMonthFragment.class;
            try {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        }
        else if(selected == 4){
            fragmentClass = ThisYearFragment.class;
            try {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        }
//        else if(selected == 5){
//            navigationView.getMenu().findItem(R.id.nav_cal).setChecked(false);
//            navigationView.getMenu().findItem(R.id.nav_today).setChecked(true);
//            fragmentClass = TodayFragment.class;
//            try {
//                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
//        }
        else if(selected == 5 && isDateSelected) {

                    Bundle bundle = new Bundle();
                    bundle.putString("Date", currentDate);
                    fragment = null;
                    fragmentClass = SelectedDateFragment.class;
                    try {
                        fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment, "date_fragment").commit();

        }
    }

    public void disableDrawer(){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
        android.support.v4.app.Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putString("Date", currentDate);
        Class fragmentClass = null;
        fragment = null;
        fragmentClass = SelectedDateFragment.class;
        try {
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment,"date_fragment").commit();

    }

}

