package com.mobdeve.s13.group2.financify.reminders;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mobdeve.s13.group2.financify.MainActivity;
import com.mobdeve.s13.group2.financify.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // get reminder's id and title
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        String reminderTitle = intent.getStringExtra("NOTIFICATION_TITLE");

        // setup activity to show when notification is pressed by user
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // setup notification sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // create application's notification to be displayed in the device
        NotificationCompat.Builder builder = new NotificationCompat.Builder (context, "financify_notify")
                .setSmallIcon(R.drawable.financify_logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setTicker("You have a reminder for today!").setAutoCancel(true)
                .setSound(notificationSound)
                .setContentTitle ("financify: You have a reminder today!")
                .setContentText("Please check your reminder list.")
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // notification manager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from (context);

        // set notification
        notificationManager.notify (notificationId, builder.build ());
    }
}
