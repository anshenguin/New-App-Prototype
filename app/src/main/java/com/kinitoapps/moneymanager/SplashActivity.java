package com.kinitoapps.moneymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HP INDIA on 18-Feb-18.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int WRITE_EXT_STORAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXT_STORAGE);
        }

        else{
            Intent intent = new Intent(this, home.class);
            startActivity(intent);
            finish();
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
                    Intent intent = new Intent(this, home.class);
                    startActivity(intent);
                    finish();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent intent = new Intent(this, PermDeniedActivity.class);
                    startActivity(intent);
                    finish();



                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}