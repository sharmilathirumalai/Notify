package com.example.notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Activity";

    MainPage mainPage = new MainPage();
    EventsPage eventsPage = new EventsPage();
    Settings settings = new Settings();

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);


        // notification set
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND,5);
//
//        Intent alarmIntent = new Intent("notify.NOTIFICATION");
//        Log.d(TAG, "setting alarmManager: ");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,100, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.d(TAG, "onNavigationItemSelected: start");
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                Log.d(TAG, "navigation_home: ");
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.id_frame, mainPage).commit();
                return true;
            case R.id.navigation_events:
                Log.d(TAG, "navigation_events: ");
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.id_frame, eventsPage).commit();
                return true;
            case R.id.navigation_settings:
                Log.d(TAG, "navigation_settings: ");
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.id_frame, settings).commit();
                return true;
        }
        Log.d(TAG, "onNavigationItemSelected:  else");
        return false;
    }
}




