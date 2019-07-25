/*
*This class scans the QR code of the posters
* Once the QR is scanned the information such as date,time and lccation of the event is obtained
*
*/

package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.db.EventDataQueries;
import com.example.notify.model.EventModel;
import com.google.zxing.Result;

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

        String[] messageArray = message.split("::");

        String eventDateString = messageArray[1];
        String eventLocation = messageArray[2];
        String eventName = messageArray[0];

        try {
            final EventDataQueries database = new EventDataQueries(getApplicationContext());
            EventModel event = new EventModel(eventName, eventDateString, eventLocation);
            database.open();
            EventModel modelObject = database.create(event);
            database.close();

            // creating intent and passing the event informations
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("id", modelObject.getId());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("date", event.getDate().toString());
            intent.putExtra("name", event.getName());
            intent.putExtra("priority", event.getIsPrior().toString());
            Log.d(TAG, "location: " + event.getLocation());
            Log.d(TAG, "date: " + event.getDate());
            Log.d(TAG, "name: " + event.getName());
            NotificationReceiver alarm = new NotificationReceiver();
            alarm.setAlarm(this, event.getDate(), intent);

            Toast.makeText(this, "Saved successfully", Toast.LENGTH_LONG).show();
//                Snackbar.make(CoordinatorLayout, R.string.saved,
//                        Snackbar.LENGTH_SHORT)
//                        .show();

            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Some error occurred while reading the QR", Toast.LENGTH_LONG).show();
        }
    }
}