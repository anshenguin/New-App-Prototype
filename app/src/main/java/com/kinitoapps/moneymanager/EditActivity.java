package com.kinitoapps.moneymanager;

import android.content.ContentValues;
import android.content.Intent;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_MONEY_LOADER = 0;

    private EditText mValueEditText;
    private Spinner mStatusSpinner;
    private EditText mDescEditText;
    private MoneyDbHelper mDbHelper;
    private Button save;

    private int mStatus = MoneyContract.MoneyEntry.STATUS_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mDbHelper = new MoneyDbHelper(this);
        mDescEditText = (EditText) findViewById(R.id.edit_desc);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_status);
        save = (Button) findViewById(R.id.saveValue);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editValue();
                finish();
            }
        });
        mValueEditText = (EditText) findViewById(R.id.edit_value);
        setupSpinner();
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

            mValueEditText.setText(str);
            mDescEditText.setText(desc);
            mStatusSpinner.setSelection(status-1);
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
        mStatusSpinner.setSelection(0);
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
    private void editValue(){
        Intent intent = getIntent();
        Uri currentEntryUri = intent.getData();
        String desc = mDescEditText.getText().toString().trim();
        double value = Double.parseDouble(mValueEditText.getText().toString().trim());
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
                        }
    }
}
