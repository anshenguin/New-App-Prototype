package com.kinitoapps.moneymanager;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

/**
 * Created by anshul on 10/2/18.
 */

@TargetApi(Build.VERSION_CODES.O)
public class NotificationHelper extends ContextWrapper {
    public static final String CHANNEL_ONE_ID = "com.kinitoapps.moneymanager.enterchannel";
    public static final String CHANNEL_ONE_NAME = "Enter Channel";

    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
            CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);



    public NotificationHelper(Context base) {
        super(base);
    }
}
