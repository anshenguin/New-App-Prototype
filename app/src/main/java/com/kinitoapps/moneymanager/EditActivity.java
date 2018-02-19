package com.kinitoapps.moneymanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_MONEY_LOADER = 0;

    private EditText mValueEditText;
    private RadioButton spent;
    private RadioButton received;
    private EditText mDescEditText;
    private MoneyDbHelper mDbHelper;
    private Button save;
    private SharedPreferences sharedPreferences;
    private double oldValue;
    private String currentDate;
    private int mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mDbHelper = new MoneyDbHelper(this);
        sharedPreferences = getSharedPreferences("LIMIT", Context.MODE_PRIVATE);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        spent = (RadioButton) findViewById(R.id.spent);
        received = (RadioButton) findViewById(R.id.received);
        mDescEditText = (EditText) findViewById(R.id.edit_desc);
        save = (Button) findViewById(R.id.saveValue);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editValue();
            }
        });
        mValueEditText = (EditText) findViewById(R.id.edit_value);
        getSupportLoaderManager().initLoader(EXISTING_MONEY_LOADER, null, this);
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
            Notification noti = new Notification.Builder(this)
                    .setContentTitle("DAILY LIMIT WARNING")
                    .setContentText("You have exceeded your daily limit").setSmallIcon(R.drawable.noti_wallet)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            notificationManager.notify(1, noti);
        }

        if(limit_month<=getMonthlySumSpent()&&limit_month>0&&(getMonthlySumSpent()+oldValue-currentVal)<limit_month){
            Notification noti = new Notification.Builder(this)
                    .setContentTitle("MONTHLY LIMIT WARNING")
                    .setContentText("You have exceeded your monthly limit").setSmallIcon(R.drawable.noti_wallet)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            notificationManager.notify(2, noti);
        }

        finish();

    }
}
