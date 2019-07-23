package com.example.notify;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.db.EventDataQueries;
import com.example.notify.model.EventModel;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SaveEvent extends AppCompatActivity {

    public static final String actionType = "ActionType";
    public static final String message = "message";
    public static final String TAG = "SaveEvent";
    public static final String posterThumbnail = "poster";
    private static String imagepath = "";
    private String action;

    private EditText eventName,eventLocation,eventDate;
    private ImageView eventPoster;
    private Button savebtn;

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
        savebtn = findViewById(R.id.save_btn);


        if (action.equals("QR")) {
            handleQR(intent);
        } else {
            handleOCR(intent);
        }


        savebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EventDataQueries database = new EventDataQueries(getApplicationContext());
                String name = String.valueOf(eventName.getText());
                String location = String.valueOf(eventDate.getText());
                String date = String.valueOf(eventLocation.getText());

                EventModel event = new EventModel(name, date, location);
                database.open();
                EventModel a = database.create(event);
                database.close();
                Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_LONG).show();
            }
            });

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
        extractDate(message);

        try {
            imagepath = intent.getStringExtra(SaveEvent.posterThumbnail);
            FileInputStream file = new FileInputStream(new File(imagepath));
            eventPoster.setImageBitmap(BitmapFactory.decodeStream(file));
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "message: "+message);
    }

    private void extractDate(String message) {
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(message);
        String date = "";
        Date formatteddate = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (DateGroup group : groups) {
            date = group.getDates().toString().substring(1, group.getDates().toString().length() - 1);
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");

            try {
                 formatteddate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "dates: " + date);
            Log.d(TAG, "dates: " + formatteddate);
        }
        if(formatteddate != null) {
            eventDate.setText(targetFormat.format(formatteddate));
        }
    }

    private void setExtractedText(String message) {
        String[] messageArray = message.split("::");
        eventDate.setText(messageArray[0]);
        eventLocation.setText(messageArray[1]);
        Log.d(TAG, "messageArray: "+messageArray);
    }
}
