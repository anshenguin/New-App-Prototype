package com.kinitoapps.moneymanager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EnterValueActivity extends AppCompatActivity {

    private EditText mDescEditText;
    private String currentDate;

    /** EditText field to enter the pet's breed */
    /** EditText field to enter the pet's weight */
    private EditText mValueEditText;
    private SharedPreferences sharedPreferences;
    /** EditText field to enter the pet's gender */
    private Spinner mStatusSpinner;
    private Button mSaveButton;
    private MoneyDbHelper mDbHelper;
    private int mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_value);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("LIMIT", Context.MODE_PRIVATE);
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        editor.putBoolean("CALL",false);
        editor.commit();
        mDbHelper = new MoneyDbHelper(this);
        mDescEditText = (EditText) findViewById(R.id.edit_desc);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_status);
        mValueEditText = (EditText) findViewById(R.id.edit_value);
        mSaveButton = (Button) findViewById(R.id.saveValue);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValue();
            }
        });
        setupSpinner();

    }
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStatusSpinner.setAdapter(spinnerAdapter);

        // Set the integer mSelected to the constant values
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(("Spent"))) {
                        mStatus = MoneyContract.MoneyEntry.STATUS_SPENT;
                    } else if (selection.equals(("Received"))) {
                        mStatus = MoneyContract.MoneyEntry.STATUS_RECEIVED;
                    } else {
                        mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;
            }
        });
    }

    private void insertValue(){
        double value=0;
        String desc = mDescEditText.getText().toString().trim();
        if(TextUtils.isEmpty(mDescEditText.getText().toString().trim())){
            desc="No description given";
        }
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DateFormat df = new SimpleDateFormat("h:mm a",Locale.getDefault());
        String time = df.format(Calendar.getInstance().getTime());
        if(!TextUtils.isEmpty(mValueEditText.getText().toString().trim())) {
            value = Double.parseDouble(mValueEditText.getText().toString().trim());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            ContentValues values = new ContentValues();
            values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE, value);
            values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC, desc);
            values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DATE, date);
            values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_TIME, time);
            values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS, mStatus);
            db.insert(MoneyContract.MoneyEntry.TABLE_NAME, null, values);
            if(mStatus == MoneyContract.MoneyEntry.STATUS_SPENT)
            checkForLimit(value);
        }
        else{
            Toast.makeText(this,"Value field can't be blank",Toast.LENGTH_LONG).show();
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
        //TODO: DONT CHECK FOR LIMIT IF ALREADY CHECKED TODAY
        float limit_today = sharedPreferences.getFloat("limit_today",0);
        float limit_month = sharedPreferences.getFloat("limit_month",0);
        if(limit_today<= getDailySumSpent()&&limit_today>0&&(getDailySumSpent()-currentVal)<limit_today){
            // Build notification
            // Actions are just fake
            Notification noti = new Notification.Builder(this)
                    .setContentTitle("DAILY LIMIT WARNING")
                    .setContentText("You have exceeded your daily limit").setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            notificationManager.notify(1, noti);
        }

        if(limit_month<=getMonthlySumSpent()&&limit_month>0&&(getMonthlySumSpent()-currentVal)<limit_month){
            Notification noti = new Notification.Builder(this)
                    .setContentTitle("MONTHLY LIMIT WARNING")
                    .setContentText("You have exceeded your monthly limit").setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            notificationManager.notify(2, noti);
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
}
