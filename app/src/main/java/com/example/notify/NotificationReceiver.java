package com.example.notify;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "Activity";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "" +
                "");
        wakeLock.acquire();


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


        // load the data saved in local.
        String notification_title = "You have an event notification";
        String notification_body = "Halifax Coding Challenge";
        // add icon if necessary

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        Notification notification = builder.setContentTitle(notification_title).setContentText(notification_body).setTicker("Event Alert").setAutoCancel(true).setSmallIcon(R.drawable.ic_settings_icon).setContentIntent(pendingIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);




//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("Hello")
//                .setContentText("world")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, builder.build());
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show();

        wakeLock.release();
    }


    public void setAlarm(Context context, long time){
        Log.d(TAG, "setAlarm: ");
        AlarmManager alarmManager =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}