package com.mobdeve.s13.group2.financify.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import com.mobdeve.s13.group2.financify.MainActivity;

import com.mobdeve.s13.group2.financify.R;

/**
 * Notification service class for creating application's notification to be shown in the device
 * according to specified date and time.
 */
public class NotifyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // notification manager
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // create application's notification to be displayed in the device
        Notification notification = new Notification.Builder(this)
                .setContentTitle("You have a financial reminder today!")
                .setContentText("Please check your reminder list.")
                .setSmallIcon(R.drawable.financify_logo)
                .setContentIntent(pIntent)
                .addAction(0, "Open Application", pIntent)
                .build();

        // set notification
        notificationManager.notify(0, notification);
    }
}
