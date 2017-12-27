package com.kinitoapps.moneymanager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EnterValueActivity extends AppCompatActivity {

    private EditText mDescEditText;
    private Uri mCurrentPetUri;

    /** EditText field to enter the pet's breed */
    /** EditText field to enter the pet's weight */
    private EditText mValueEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mStatusSpinner;
    private Button mSaveButton;
    private MoneyDbHelper mDbHelper;
    private int mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_value);
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
                finish();
            }
        });
        setupSpinner();

    }
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStatusSpinner.setAdapter(genderSpinnerAdapter);

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
        String desc = mDescEditText.getText().toString().trim();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DateFormat df = new SimpleDateFormat("h:mm a",Locale.getDefault());
        String time = df.format(Calendar.getInstance().getTime());
        int value = Integer.parseInt(mValueEditText.getText().toString().trim());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE, value);
        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC, desc);
        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_DATE, date);
        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_TIME, time);
        values.put(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS, mStatus);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(MoneyContract.MoneyEntry.TABLE_NAME, null, values);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences canCallNow = getSharedPreferences("CALL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = canCallNow.edit();
        editor.putBoolean("CALL",true);
        editor.commit();
    }
}
