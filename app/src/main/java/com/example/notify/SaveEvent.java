package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SaveEvent extends AppCompatActivity {

    public static final String actionType = "ActionType";
    public static final String message = "message";
    public static final String TAG = "SaveEvent";
    private String action;

    private EditText eventName,eventLocation,eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "SaveEvent: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_event);
        Intent intent = getIntent();
        action = intent.getStringExtra(actionType);
        setContentView(R.layout.save_event);

        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.event_location);
        eventDate = findViewById(R.id.event_date);

        if (action.equals("QR")) {
            handleQR(intent);
        } else {
            handleOCR(intent);
        }

    }

    // this method reads the decoded QR text and loads it into the ui input fields
    private void handleQR(Intent intent) {
        String message = intent.getStringExtra(SaveEvent.message);
        String[] messageArray = message.split("::");
        eventName.setText(messageArray[0]);
        eventLocation.setText(messageArray[2]);
        eventDate.setText(messageArray[1]);
        Log.d(TAG, "messageArray: "+messageArray);
    }

    // this method reads the decoded OCR text.
    // extracts date, location and loads it into input fields
    private void handleOCR(Intent intent) {
        String message = intent.getStringExtra(SaveEvent.message);
        Log.d(TAG, "message: "+message);
    }
}
