package com.kinitoapps.moneymanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.kinitoapps.moneymanager.tutorialviews.IntroActivity;

/**
 * Created by HP INDIA on 18-Feb-18.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int WRITE_EXT_STORAGE = 100;
    SharedPreferences firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstRun = getSharedPreferences("com.example.lockscreentest", Context.MODE_PRIVATE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXT_STORAGE);
        }

        else{
            if(firstRun.getBoolean("firstrun",true)) {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(this, home.class);
                startActivity(intent);
                finish();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXT_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(firstRun.getBoolean("firstrun",true)) {
                        Intent intent = new Intent(this, IntroActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(this, home.class);
                        startActivity(intent);
                        finish();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Intent intent = new Intent(this, PermDeniedActivity.class);
//                    startActivity(intent);
//                    finish();
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Please allow Money Manager to save its database on your device to proceed...");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SplashActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        WRITE_EXT_STORAGE);
                            }
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SplashActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        WRITE_EXT_STORAGE);
                            }
                        }
                    });
                    builder.show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}