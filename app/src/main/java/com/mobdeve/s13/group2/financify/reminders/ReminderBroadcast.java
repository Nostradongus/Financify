package com.mobdeve.s13.group2.financify.reminders;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mobdeve.s13.group2.financify.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // create application's notification to be displayed in the device
        NotificationCompat.Builder builder = new NotificationCompat.Builder (context, "financify_notify")
                .setSmallIcon (R.drawable.financify_logo)
                .setTicker("You have a reminder for today!").setAutoCancel(true)
                .setContentTitle ("Financify: You have a reminder set for today")
                .setContentText("Please check your reminder list!")
                .setPriority (NotificationCompat.PRIORITY_DEFAULT);

        // notification manager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from (context);

        // set notification
        notificationManager.notify (200, builder.build ());
    }
}
