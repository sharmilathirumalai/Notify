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
import android.content.SharedPreferences;
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
            context.startForegroundService(new Intent(context, SaveEvent.class));
            context.startForegroundService(new Intent(context, ExpandNotification.class));
        } else {
            context.startService(new Intent(context, SaveEvent.class));
            context.startService(new Intent(context, ExpandNotification.class));
        }

        String event_name = intent.getStringExtra("name");
        String event_location = intent.getStringExtra("location");
        String event_date = intent.getStringExtra("date");

        // create intent to be passed to next class when user clicks on the notification
        Intent notificationIntent = new Intent(context, SaveEvent.class);
        notificationIntent.putExtra("event_name", event_name);
        notificationIntent.putExtra("event_location", event_location);
        notificationIntent.putExtra("event_date", event_date);
        notificationIntent.putExtra("ActionType", "notification");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SaveEvent.class);
        stackBuilder.addNextIntent(notificationIntent);
        Log.d(TAG, "NotificationReceiver: ");
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        // intent for notification action
        Intent intentAction = new Intent(context, ExpandNotification.class);
        notificationIntent.putExtra("event_name", event_name);
        notificationIntent.putExtra("event_location", event_location);
        notificationIntent.putExtra("event_date", event_date);
        intentAction.putExtra("ActionType", "map");
        TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(context);
        stackBuilder1.addParentStack(ExpandNotification.class);
        stackBuilder1.addNextIntent(intentAction);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, intentAction, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setContentTitle(event_name);
        builder.setContentText(event_location + " " + event_date);
        builder.setTicker("Event Alert");
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.ic_location_icon, "Show nagivation", actionPendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        wakeLock.release();
    }


//    public void setAlarm(Context context, long time) {
//        Log.d(TAG, "setAlarm: ");
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, NotificationReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 2000, pendingIntent);
//    }

    public void setAlarm(Context context, long time, Intent intent) {
        long alarmTime = time;
        SharedPreferences prefs = context.getSharedPreferences("notify", context.MODE_PRIVATE);
        String notifyBefore = prefs.getString("notify_before", null);
        if (notifyBefore == null) {
            alarmTime = alarmTime - (1000 * 60 * 60);
        } else {
            if(notifyBefore == "30"){
                alarmTime = alarmTime - (1000 * 60 * 30);
            }
        }
        long newTime = 20000000;
        Log.d(TAG, "alarmTime: "+alarmTime);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, newTime, pendingIntent);
//        alarmManager.setExact(AlarmManager.RTC, alarmTime, pendingIntent);
    }
}