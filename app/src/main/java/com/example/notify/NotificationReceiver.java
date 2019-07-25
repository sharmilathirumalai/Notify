/*
* This class is used to receive the notification triggered from the alarm manager.
* This alarm is received by the receiver in the manifest file and is passed to this class.
* */

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

        String event_name = intent.getStringExtra("name");
        String event_location = intent.getStringExtra("location");
        String event_date = intent.getStringExtra("date");

        // create intent to be passed to next class when user clicks on the notification
        Intent notificationIntent = new Intent(context, ExpandNotification.class);
        notificationIntent.putExtra("location",event_location);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ExpandNotification.class);
        stackBuilder.addNextIntent(notificationIntent);
        Log.d(TAG, "NotificationReceiver: ");
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);



        // intent for notification action
//        Intent intentAction = new Intent(context, ProvideNavigation.class);
//        intentAction.putExtra("action", "actionName");
//
//        TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(context);
//        stackBuilder1.addParentStack(ProvideNavigation.class);
//        stackBuilder1.addNextIntent(intentAction);
//        Log.d(TAG, "NotificationReceiver: ");
//        PendingIntent actionPendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent actionPendingIntent = PendingIntent.getActivity(context,0,intentAction,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setContentTitle(event_name);
        builder.setContentText(event_location + " " + event_date);
        builder.setTicker("Event Alert");
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentIntent(pendingIntent);
//        builder.addAction(R.drawable.ic_location_icon, "Show .navigation", actionPendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        wakeLock.release();
    }


    public void setAlarm(Context context, long time) {
        Log.d(TAG, "setAlarm: ");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 2000, pendingIntent);
    }

    public void setAlarm(Context context, long time, Intent intent) {
        Log.d(TAG, "setAlarm: ");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 2000, pendingIntent);
    }
}