package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

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


        if(getIntent().getStringExtra("FromActivityTAG") != null) {
            Intent myIntent = new Intent(getApplicationContext(), SaveEvent.class);
            myIntent.putExtra(SaveEvent.actionType, actionType);
            myIntent.putExtra(SaveEvent.message, result.getText());
            startActivity(myIntent);
        } else {
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
                NotificationReceiver alarm = new NotificationReceiver();
                // todo change the time parameter.
                // todo. store the details in local storage. with that id, create an intent and push it to alarmManager
                alarm.setAlarm(this, 2000);

                Toast.makeText(this, "Saved successfully", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Some error occurred while reading the QR", Toast.LENGTH_LONG).show();
            }
        }



//        eventName.setText(messageArray[0]);
//        eventLocation.setText(messageArray[2]);
//        eventDate.setText(messageArray[1]);

    }
}