package com.kinitoapps.moneymanager;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;
import com.kinitoapps.moneymanager.tutorialviews.IntroActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EnterValueActivity extends AppCompatActivity {

    private EditText mDescEditText;
    private String currentDate;

    /** EditText field to enter the pet's breed */
    /** EditText field to enter the pet's weight */
    private EditText mValueEditText;
    private SharedPreferences sharedPreferences;
    /** EditText field to enter the pet's gender */
    private Button mSaveButton;
    private MoneyDbHelper mDbHelper;
    private RadioButton spent;
    private RadioButton received;
    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout  adView;
    private static final int WRITE_EXT_STORAGE = 100;

    private int mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;

    private void showNativeAd() {
        nativeAd = new NativeAd(EnterValueActivity.this, "174139459886109_174366406530081");
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {

                if (nativeAd != null) {
                    nativeAd.unregisterView();
                }

                // Add the Ad view into the ad container.
                nativeAdContainer = (LinearLayout) findViewById(R.id.native_ad_container);
                LayoutInflater inflater = LayoutInflater.from(EnterValueActivity.this);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(EnterValueActivity.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(nativeAdContainer,clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_enter_value);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        spent =  findViewById(R.id.spent);
        received = findViewById(R.id.received);
        if(spent.isChecked()) {
            spent.setTextColor(Color.parseColor("#FFFFFF"));
            received.setTextColor(Color.parseColor("#9E9E9E"));
        }
        else{
            received.setTextColor(Color.parseColor("#FFFFFF"));
            spent.setTextColor(Color.parseColor("#9E9E9E"));
        }
        sharedPreferences = getSharedPreferences("LIMIT", Context.MODE_PRIVATE);
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        editor.putBoolean("CALL",false);
        editor.commit();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXT_STORAGE);
        }
        else {
            mDbHelper = new MoneyDbHelper(this);
        }
        mDescEditText = findViewById(R.id.edit_desc);
        mValueEditText = findViewById(R.id.edit_value);
        mSaveButton = findViewById(R.id.saveValue);
        mSaveButton.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        mSaveButton.setBackgroundResource(R.drawable.gradient_save_two);
                        mSaveButton.setTextColor(Color.parseColor("#FAFAFA"));

                        return true;
                    case MotionEvent.ACTION_UP:
                        if(rect.contains(v.getLeft() + (int) motionEvent.getX(), v.getTop() + (int) motionEvent.getY())){
                            mSaveButton.performClick();
                            mSaveButton.setBackgroundResource(R.drawable.gradient_save);
                            mSaveButton.setTextColor(Color.parseColor("#53aade"));
                            insertValue();
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if(!rect.contains(v.getLeft() + (int) motionEvent.getX(), v.getTop() + (int) motionEvent.getY())){
                            // User moved outside bounds
                            mSaveButton.setBackgroundResource(R.drawable.gradient_save);
                            mSaveButton.setTextColor(Color.parseColor("#53aade"));
                        }
                }
                return false;
            }
        });

        spent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(spent.isChecked()) {
                    spent.setTextColor(Color.parseColor("#FFFFFF"));
                    received.setTextColor(Color.parseColor("#9E9E9E"));
                }
                else{
                    received.setTextColor(Color.parseColor("#FFFFFF"));
                    spent.setTextColor(Color.parseColor("#9E9E9E"));
                }
            }
        });

        showNativeAd();


    }

    private void insertValue(){
        double value=0;
        String desc = mDescEditText.getText().toString().trim();
        if(TextUtils.isEmpty(mDescEditText.getText().toString().trim())){
            desc="No Description Given";
        }
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DateFormat df = new SimpleDateFormat("h:mm a",Locale.getDefault());
        String time = df.format(Calendar.getInstance().getTime());

            if (!TextUtils.isEmpty(mValueEditText.getText().toString().trim())) {
                if(Double.parseDouble(mValueEditText.getText().toString().trim())>9999999){
                    Toast.makeText(EnterValueActivity.this,"Value cannot be greater than 9,999,999",Toast.LENGTH_LONG).show();
                }
                else {
                    if (desc.length()>140)
                        Toast.makeText(EnterValueActivity.this, "Description cannot have more than 140 characters", Toast.LENGTH_LONG).show();
                    else {
                        value = Double.parseDouble(mValueEditText.getText().toString().trim());

                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        // Create a ContentValues object where column names are the keys,
                        // and Toto's pet attributes are the values.
                        ContentValues values = new ContentValues();
                        if (spent.isChecked())
                            mStatus = MoneyContract.MoneyEntry.STATUS_SPENT;
                        else
                            mStatus = MoneyContract.MoneyEntry.STATUS_RECEIVED;
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE, value);
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC, desc);
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DATE, date);
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_TIME, time);
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS, mStatus);
                        db.insert(MoneyContract.MoneyEntry.TABLE_NAME, null, values);
                        if (mStatus == MoneyContract.MoneyEntry.STATUS_SPENT)
                            checkForLimit(value);
                        else
                            finish();
                    }
                }
            } else {
                Toast.makeText(this, "Value field cannot be blank", Toast.LENGTH_LONG).show();
            }
        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
    }

    private void checkForLimit(double currentVal){
        float limit_today = sharedPreferences.getFloat("limit_today",0);
        float limit_month = sharedPreferences.getFloat("limit_month",0);
        if(limit_today<= getDailySumSpent()&&limit_today>0&&(getDailySumSpent()-currentVal)<limit_today){
            // Build notification
            // Actions are just fake
            Intent intent = new Intent(this, home.class);
            intent.putExtra("dailyormonthly","daily");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EnterValueActivity.this,"Daily and Monthly Limit")
                    .setSmallIcon(R.drawable.noti_wallet)
                    .setContentTitle("DAILY LIMIT WARNING")
                    .setContentText("You have exceeded your daily limit")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[] { 1000, 1000})
                    .setPriority(NotificationManagerCompat.IMPORTANCE_MAX);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(EnterValueActivity.this);
            notificationManager.notify(101, mBuilder.build());

        }

        if(limit_month<=getMonthlySumSpent()&&limit_month>0&&(getMonthlySumSpent()-currentVal)<limit_month){
            Intent intent = new Intent(this, home.class);
            intent.putExtra("dailyormonthly","monthly");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EnterValueActivity.this,"Daily and Monthly Limit")
                    .setSmallIcon(R.drawable.noti_wallet)
                    .setContentTitle("MONTHLY LIMIT WARNING")
                    .setContentText("You have exceeded your monthly limit")
                    .setAutoCancel(true)
                    .setVibrate(new long[] { 1000, 1000})
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManagerCompat.IMPORTANCE_MAX);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(EnterValueActivity.this);
            notificationManager.notify(102, mBuilder.build());
        }

        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        editor.putBoolean("CALL",true);
        editor.commit();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXT_STORAGE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EnterValueActivity.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Please allow Money Manager to save its database on your device to proceed...");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            if (ContextCompat.checkSelfPermission(EnterValueActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(EnterValueActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        WRITE_EXT_STORAGE);
                            }
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if (ContextCompat.checkSelfPermission(EnterValueActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(EnterValueActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        WRITE_EXT_STORAGE);
                            }
                        }
                    });
                    builder.show();

                }

                else{
                    mDbHelper = new MoneyDbHelper(this);
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
