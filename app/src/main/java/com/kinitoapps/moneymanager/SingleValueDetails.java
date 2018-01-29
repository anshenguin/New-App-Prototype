package com.kinitoapps.moneymanager;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kinitoapps.moneymanager.data.MoneyContract;

public class SingleValueDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView timedatetextView;
    private static final int EXISTING_MONEY_LOADER = 0;

    private TextView valueTextView;
    private TextView descTextView;
    private TextView statusTextView;
    private Button delete;
    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_value_details);
        timedatetextView = (TextView) findViewById(R.id.timedatetext);
        descTextView = (TextView) findViewById(R.id.desc);
        valueTextView = (TextView) findViewById(R.id.value);
        statusTextView = (TextView) findViewById(R.id.status);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP)
            actionBar.setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));
        else
            actionBar.setTitle("");
        actionBar.setElevation(0);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F5F5F5")));
        delete = (Button) findViewById(R.id.delete_button);
        edit = (Button) findViewById(R.id.edit_button);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleValueDetails.this, EditActivity.class);
                Intent i = getIntent();
                Uri currentEntryUri = i.getData();
                intent.setData(currentEntryUri);
                startActivity(intent);
            }
        });
        edit.setWidth(delete.getWidth());
        getSupportLoaderManager().initLoader(EXISTING_MONEY_LOADER, null, this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor("#E0E0E0"));
//        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getIntent();
        Uri currentEntryUri = intent.getData();
        Log.v("tag", String.valueOf(currentEntryUri));
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
            return;
        }

        if (cursor.moveToFirst()) {
            int valueColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE);
            int descColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC);
            int statusColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS);
            int timeColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_TIME);
            int dateColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DATE);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            double value = cursor.getDouble(valueColumnIndex);
            String desc = cursor.getString(descColumnIndex);
            int status = cursor.getInt(statusColumnIndex);
            String str = String.valueOf(value);
            String datentime = date + ", " + time;
            valueTextView.setText(str);
            descTextView.setText(desc);
            timedatetextView.setText(datentime);
            if (status == MoneyContract.MoneyEntry.STATUS_SPENT) {
                statusTextView.setText("You Spent");
                statusTextView.setTextColor(Color.parseColor("#D32F2F"));
                valueTextView.setTextColor(Color.parseColor("#D32F2F"));
                descTextView.setTextColor(Color.parseColor("#D32F2F"));
            } else {
                statusTextView.setText("You Received");
                statusTextView.setTextColor(Color.parseColor("#388E3C"));
                valueTextView.setTextColor(Color.parseColor("#388E3C"));
                descTextView.setTextColor(Color.parseColor("#388E3C"));
            }
//            mStatusSpinner.setSelection(status-1);
////            switch (status){
////                case MoneyContract.MoneyEntry.STATUS_SPENT:
////                    mStatusSpinner.setSelection(2);
////                    break;
////                default:
////                    mStatusSpinner.setSelection(1);
////                    break;
////            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        statusTextView.setText("");
        valueTextView.setText("");
        descTextView.setText("");
        timedatetextView.setText("");
    }

    private void deletePet() {
        int rowsDeleted = 0;
        Intent intent = getIntent();
        Uri currentEntryUri = intent.getData();
        rowsDeleted = getContentResolver().delete(currentEntryUri, null, null);
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Deletion Failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "Deleted Successfully",
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to delete this entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
