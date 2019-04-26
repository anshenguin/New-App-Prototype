package com.kinitoapps.moneymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import androidx.loader.app.LoaderManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_MONEY_LOADER = 0;

    private EditText mValueEditText;
    private RadioButton spent;
    private RadioButton received;
    private short selection;
    public static String dateSelected;
    private EditText mDescEditText;
    private MoneyDbHelper mDbHelper;
    private Button save;
    private SharedPreferences sharedPreferences;
    private double oldValue;
    private String currentDate;
    private int mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;
    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout  adView;

    private void showNativeAd() {
        nativeAd = new NativeAd(EditActivity.this, "174139459886109_174366406530081");
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
                LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
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
                AdChoicesView adChoicesView = new AdChoicesView(EditActivity.this, nativeAd, true);
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
        setContentView(R.layout.activity_edit);
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
            mChannel.setVibrationPattern(new long[] { 1000, 1000});

            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
        mDbHelper = new MoneyDbHelper(this);
        sharedPreferences = getSharedPreferences("LIMIT", Context.MODE_PRIVATE);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        spent = findViewById(R.id.spent);
        received = findViewById(R.id.received);
        if(spent.isChecked()) {
            spent.setTextColor(Color.parseColor("#FFFFFF"));
            received.setTextColor(Color.parseColor("#424242"));
        }
        else{
            received.setTextColor(Color.parseColor("#FFFFFF"));
            spent.setTextColor(Color.parseColor("#424242"));
        }
        mDescEditText = findViewById(R.id.edit_desc);
        save = findViewById(R.id.saveValue);
        save.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        save.setBackgroundResource(R.drawable.gradient_save_two);
                        save.setTextColor(Color.parseColor("#FAFAFA"));

                        return true;
                    case MotionEvent.ACTION_UP:
                        if(rect.contains(v.getLeft() + (int) motionEvent.getX(), v.getTop() + (int) motionEvent.getY())){
                            save.performClick();
                            save.setBackgroundResource(R.drawable.gradient_save);
                            save.setTextColor(Color.parseColor("#53aade"));
                            editValue();
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if(!rect.contains(v.getLeft() + (int) motionEvent.getX(), v.getTop() + (int) motionEvent.getY())){
                            // User moved outside bounds
                            save.setBackgroundResource(R.drawable.gradient_save);
                            save.setTextColor(Color.parseColor("#53aade"));
                        }
                }
                return false;
            }
        });
        mValueEditText = findViewById(R.id.edit_value);
        spent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(spent.isChecked()) {
                    spent.setTextColor(Color.parseColor("#FFFFFF"));
                    received.setTextColor(Color.parseColor("#424242"));
                }
                else{
                    received.setTextColor(Color.parseColor("#FFFFFF"));
                    spent.setTextColor(Color.parseColor("#424242"));
                }
            }
        });
        getSupportLoaderManager().initLoader(EXISTING_MONEY_LOADER, null, this);
        showNativeAd();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getIntent();
        Uri currentEntryUri = intent.getData();
        Log.v("tag",String.valueOf(currentEntryUri));
        String[] projection = {
                MoneyContract.MoneyEntry._ID,
                MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE,
                MoneyContract.MoneyEntry.COLUMN_MONEY_DESC,
                MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS,
                MoneyContract.MoneyEntry.COLUMN_MONEY_TIME,
                MoneyContract.MoneyEntry.COLUMN_MONEY_DATE
        };

        return new CursorLoader(this,
                currentEntryUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;}

        if (cursor.moveToFirst()) {
            int valueColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE);
            int descColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC);
            int statusColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS);
            double value = cursor.getDouble(valueColumnIndex);
            String desc = cursor.getString(descColumnIndex);
            int status = cursor.getInt(statusColumnIndex);
            String str = String.valueOf(value);
            oldValue = value;
            mValueEditText.setText(str);
            mDescEditText.setText(desc);
            if(status == MoneyContract.MoneyEntry.STATUS_SPENT)
                spent.setChecked(true);
            else
                received.setChecked(true);
//            if(status==MoneyContract)
//            switch (status){
//                case MoneyContract.MoneyEntry.STATUS_SPENT:
//                    mStatusSpinner.setSelection(2);
//                    break;
//                default:
//                    mStatusSpinner.setSelection(1);
//                    break;
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mValueEditText.setText("");
        mDescEditText.setText("");
    }
    private void editValue(){
        Intent intent = getIntent();
        Uri currentEntryUri = intent.getData();

        String desc = mDescEditText.getText().toString().trim();
        if(TextUtils.isEmpty(mDescEditText.getText().toString().trim())){
            desc="No Description Given";
        }
        double value = 0;

        if(spent.isChecked())
            mStatus = MoneyContract.MoneyEntry.STATUS_SPENT;
        else
            mStatus = MoneyContract.MoneyEntry.STATUS_RECEIVED;

            if (!TextUtils.isEmpty(mValueEditText.getText().toString().trim())) {
                if (Double.parseDouble(mValueEditText.getText().toString().trim()) > 9999999) {
                    Toast.makeText(EditActivity.this, "Value cannot be greater than 9,999,999", Toast.LENGTH_LONG).show();
                } else {
                    if(desc.length()>140)
                        Toast.makeText(EditActivity.this, "Description cannot have more than 140 characters", Toast.LENGTH_LONG).show();
                    else {
                        value = Double.parseDouble(mValueEditText.getText().toString().trim());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE, value);
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC, desc);
                        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS, mStatus);

                        // Insert a new row for Toto in the database, returning the ID of that new row.
                        // The first argument for db.insert() is the pets table name.
                        // The second argument provides the name of a column in which the framework
                        // can insert NULL in the event that the ContentValues is empty (if
                        // this is set to "null", then the framework will not insert a row when
                        // there are no values).
                        // The third argument is the ContentValues object containing the info for Toto.
                        int rowsAffected = getContentResolver().update(currentEntryUri, values, null, null);
                        db.insert(MoneyContract.MoneyEntry.TABLE_NAME, null, values);
                        if (rowsAffected == 0) {
                            // If no rows were affected, then there was an error with the update.
                            Toast.makeText(this, "Error: Entry Not Updated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Otherwise, the update was successful and we can display a toast.
                            Toast.makeText(this, "Entry Updated Successfully",
                                    Toast.LENGTH_SHORT).show();
                            if (mStatus == MoneyContract.MoneyEntry.STATUS_SPENT)
                                checkForLimit(value, oldValue);
                            else
                                finish();

                        }
                    }
                }
            }
            else{
                Toast.makeText(this, "Value field cannot be blank", Toast.LENGTH_LONG).show();
            }
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

    private void checkForLimit(double currentVal, double oldValue){
        float limit_today = sharedPreferences.getFloat("limit_today",0);
        float limit_month = sharedPreferences.getFloat("limit_month",0);
        if(limit_today<= getDailySumSpent()&&limit_today>0&&(getDailySumSpent()+oldValue-currentVal)<limit_today){
            // Build notification
            // Actions are just fake
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EditActivity.this,"Daily and Monthly Limit")
                    .setSmallIcon(R.drawable.noti_wallet)
                    .setContentTitle("DAILY LIMIT WARNING")
                    .setContentText("You have exceeded your daily limit")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(EditActivity.this);
            notificationManager.notify(101, mBuilder.build());
        }

        if(limit_month<=getMonthlySumSpent()&&limit_month>0&&(getMonthlySumSpent()+oldValue-currentVal)<limit_month){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EditActivity.this,"Daily and Monthly Limit")
                    .setSmallIcon(R.drawable.noti_wallet)
                    .setContentTitle("MONTHLY LIMIT WARNING")
                    .setContentText("You have exceeded your monthly limit")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(EditActivity.this);
            notificationManager.notify(102, mBuilder.build());
        }

        finish();

    }

    public void showDatePickerDialog(View v) {

    }

}
