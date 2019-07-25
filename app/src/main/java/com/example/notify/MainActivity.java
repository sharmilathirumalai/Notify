/*
* The activity_main.xml has the UI for this activity
**/

package com.example.notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        if(getIntent().getIntExtra("selected_navigation", 0) == R.id.navigation_events) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_events);
        } else  {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
        createNotificationChannel();
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}




