package com.example.notify;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "Activity";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ExpandNotification.class));
        } else {
            context.startService(new Intent(context, ExpandNotification.class));
        }

        Intent notificationIntent = new Intent(context, ExpandNotification.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ExpandNotification.class);
        stackBuilder.addNextIntent(notificationIntent);
        Log.d(TAG, "NotificationReceiver: ");
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
        Toast.makeText(context, "Notification", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sampleId");
        Log.d(TAG, "NotificationReceiver: 1");
        Notification notification = builder.setContentTitle("Event Notification").setContentText("You have an event").setTicker("Event Alert").setAutoCancel(false).setSmallIcon(R.drawable.ic_settings_icon).setContentIntent(pendingIntent).build();
        Log.d(TAG, "NotificationReceiver: 2");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "NotificationReceiver: 3");
        notificationManager.notify(0, notification);
        Log.d(TAG, "NotificationReceiver: 4");
    }

    public void setAlarm(Context context)
    {
        Log.d(TAG, "setAlarm: ");
        AlarmManager alarmManager =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 5000, pi); // Millisec * Second * Minute
    }
}