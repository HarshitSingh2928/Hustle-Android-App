package com.example.hustle;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class Notification extends BroadcastReceiver {

    String task;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onReceive(Context context, Intent intent) {

        // Create an explicit intent for an Activity in your app
        Intent i = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"myid")
                .setSmallIcon(R.drawable.hustle_icon)
                .setContentTitle("Remainder For you upcoming daily task")
                .setContentText("Did you complete your tasks? Click here to check!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(123, builder.build());
    }
}
