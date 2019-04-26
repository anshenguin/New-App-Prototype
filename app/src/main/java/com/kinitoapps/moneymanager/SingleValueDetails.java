package com.kinitoapps.moneymanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.kinitoapps.moneymanager.data.MoneyContract;

import java.util.ArrayList;
import java.util.List;

public class SingleValueDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView timedatetextView;
    private static final int EXISTING_MONEY_LOADER = 0;

    private TextView valueTextView;
    private TextView descTextView;
    private TextView statusTextView;
    private Button delete;
    private Button edit;
    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout  adView;

    private void showNativeAd() {
        nativeAd = new NativeAd(SingleValueDetails.this, "174139459886109_174366406530081");
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
                LayoutInflater inflater = LayoutInflater.from(SingleValueDetails.this);
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
                AdChoicesView adChoicesView = new AdChoicesView(SingleValueDetails.this, nativeAd, true);
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
        setContentView(R.layout.activity_single_value_details);
        timedatetextView = (TextView) findViewById(R.id.timedatetext);
        descTextView = (TextView) findViewById(R.id.desc);
        valueTextView = (TextView) findViewById(R.id.value);
        statusTextView = (TextView) findViewById(R.id.status);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
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
        showNativeAd();
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
