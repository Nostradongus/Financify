package com.mobdeve.s13.group2.financify.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mobdeve.s13.group2.financify.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // create application's notification to be displayed in the device
        NotificationCompat.Builder builder = new NotificationCompat.Builder (context, "financify_notify")
                .setSmallIcon (R.drawable.financify_logo)
                .setContentTitle ("Financify")
                .setContentText("Please check your reminder list!")
                .setPriority (NotificationCompat.PRIORITY_DEFAULT);

        // notification manager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from (context);

        // set notification
        notificationManager.notify (200, builder.build ());
    }
}
