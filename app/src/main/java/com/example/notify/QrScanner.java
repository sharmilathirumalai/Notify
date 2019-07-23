package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.db.EventDataQueries;
import com.example.notify.model.EventModel;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private static final String TAG = "scanner";
    public static final String actionType = "QR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    // callback method through which we can get access to the result object
    @Override
    public void handleResult(Result result) {
        Log.d(TAG, "handleResult: " + result.getText());
        String message = result.getText();


//        if(getIntent().getStringExtra("FromActivityTAG") != null) {
//            Intent myIntent = new Intent(getApplicationContext(), SaveEvent.class);
//            myIntent.putExtra(SaveEvent.actionType, actionType);
//            myIntent.putExtra(SaveEvent.message, result.getText());
//            startActivity(myIntent);
//        } else {
            String[] messageArray = message.split("::");
            // these data has to be stored in local
            // todo
            String eventDateString = messageArray[1];
            String eventLocation = messageArray[2];
            String eventName = messageArray[0];
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date eventDate = format.parse(eventDateString);
                Date currentTime = Calendar.getInstance().getTime();
                long difference = eventDate.getTime() - currentTime.getTime();
                Log.d(TAG, "eventDate: " + eventDate);
                Log.d(TAG, "currentTime: " + currentTime);
                Log.d(TAG, "difference: " + difference);
                // todo change the time parameter.
                // todo. store the details in local storage. with that id, create an intent and push it to alarmManager

                final EventDataQueries database = new EventDataQueries(getApplicationContext());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String eventDateStr = dateFormat.format(eventDate);
                Log.d(TAG, "eventDateStr: "+eventDateStr);
                EventModel event = new EventModel(eventName, eventDateStr, eventLocation);
                database.open();
                EventModel modelObject = database.create(event);
                database.close();

                // creating intent and passing the event informations
                Intent intent = new Intent(this,NotificationReceiver.class);
                intent.putExtra("id",modelObject.getId());
                intent.putExtra("location",modelObject.getLocation());
                intent.putExtra("date",modelObject.getDate());
                intent.putExtra("name",modelObject.getName());
                Log.d(TAG, "location: "+modelObject.getLocation());
                Log.d(TAG, "date: "+modelObject.getDate());
                Log.d(TAG, "name: "+modelObject.getName());
                NotificationReceiver alarm = new NotificationReceiver();
                alarm.setAlarm(this, 2000, intent);

                Toast.makeText(this, "Saved successfully", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Some error occurred while reading the QR", Toast.LENGTH_LONG).show();
            }
//        }

    }
}