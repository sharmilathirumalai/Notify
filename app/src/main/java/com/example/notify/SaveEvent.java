package com.example.notify;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;

public class SaveEvent extends AppCompatActivity {

    public static final String actionType = "ActionType";
    public static final String message = "message";
    public static final String TAG = "SaveEvent";
    public static final String poster = "Poster";
    private String action;

    private EditText eventName,eventLocation,eventDate;
    private ImageView eventPoster;

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
        eventPoster = findViewById(R.id.event_poster);

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
        try {
            FileInputStream file = new FileInputStream(new File(intent.getStringExtra(SaveEvent.poster)));
            eventPoster.setImageBitmap(BitmapFactory.decodeStream(file));
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "message: "+message);
    }
}
