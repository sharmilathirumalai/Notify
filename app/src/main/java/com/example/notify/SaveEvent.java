/*
* This fragment is intiated once the camera takes a picture of the poster or when it scans the QR code
* This allows the user to edit the information fetched and save the events
* The save_event.xml containg the UI for this fragment
*/

package com.example.notify;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.db.EventDataQueries;
import com.example.notify.model.EventModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    public static final String EventId = "event_id";
    public static final String posterThumbnail = "poster";
    public static final String EventName = "event_name";
    public static final String EventLocation = "event_location";
    public static final String EventDate = "event_date";
    public static final String EventPriority = "event_priority";

    private String imagepath = "";
    private long eventID = -1;
    private String action;

    private EditText eventName, eventLocation, eventDate;
    private ImageView eventPoster;
    private Button savebtn;
    private Button view_map_button;
    private FloatingActionButton sharebtn;
    private Switch eventPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "SaveEvent: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_event);

        Intent intent = getIntent();
        Bundle data = getIntent().getBundleExtra("bundle");
        action = intent.getStringExtra(actionType);
        String id = null;
        imagepath = "";

        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.event_location);
        eventDate = findViewById(R.id.event_date);
        eventPoster = findViewById(R.id.event_poster);
        eventPriority = findViewById(R.id.event_priority);
        savebtn = findViewById(R.id.save_btn);
        sharebtn = findViewById(R.id.share_btn);

        view_map_button = findViewById(R.id.show_map);
        view_map_button.setVisibility(View.GONE);

        final String event_location_string = intent.getStringExtra(SaveEvent.EventLocation);
        if (data != null) {
            id = data.getString(SaveEvent.EventId);
        }

        if (id != null) {
            eventID = Long.parseLong(id);
            eventName.setText(data.getString(SaveEvent.EventName));
            eventLocation.setText(data.getString(SaveEvent.EventLocation));
            eventDate.setText(data.getString(SaveEvent.EventDate));
            eventPriority.setChecked(data.getBoolean(SaveEvent.EventPriority));

            try {
                imagepath = data.getString(SaveEvent.posterThumbnail);
                if (imagepath != null && !imagepath.trim().isEmpty()) {
                    FileInputStream file = new FileInputStream(new File(imagepath));
                    eventPoster.setImageBitmap(BitmapFactory.decodeStream(file));
                    file.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (action.equals("QR")) {
                handleQR(intent);
            } else if (action.equals("notification")) {
                savebtn.setVisibility(View.GONE);
                view_map_button.setVisibility(View.VISIBLE);

                eventName.setText(intent.getStringExtra(SaveEvent.EventName));
                eventLocation.setText(intent.getStringExtra(SaveEvent.EventLocation));
                eventDate.setText(intent.getStringExtra(SaveEvent.EventDate));

                eventName.setEnabled(false);
                eventDate.setEnabled(false);
                eventLocation.setEnabled(false);
                eventPriority.setClickable(false);
            } else {
                handleOCR(intent);
            }
        }


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDataQueries database = new EventDataQueries(getApplicationContext());
                String name = String.valueOf(eventName.getText());
                String date = String.valueOf(eventDate.getText());
                String location = String.valueOf(eventLocation.getText());
                Boolean isPrior = eventPriority.isChecked();
                Boolean isError = false;

                if(name == null || name.trim().isEmpty()) {
                    eventName.setError(getText(R.string.event_name_error));
                    isError = true;
                }

                if(date == null || date.trim().isEmpty()) {
                    eventDate.setError(getText(R.string.event_location_error));
                    isError = true;
                }

                if(location == null || location.trim().isEmpty()) {
                    eventLocation.setError(getText(R.string.event_date_error));
                    isError = true;
                }

                if(isError) {
                    return;
                }

                EventModel updatedevent;

                Log.d(TAG, "-- date: "+date);
                Log.d(TAG, "-- location: "+location);
                Log.d(TAG, "-- name: "+name);

                database.open();
                EventModel event;
                if (eventID != -1) {
                    event = new EventModel(eventID, name, date, location, imagepath, isPrior);
                    updatedevent = database.update(event);
                } else {
                    event = new EventModel(name, date, location, imagepath, isPrior);
                    updatedevent = database.create(event);
                }

                database.close();

                // creating intent and passing the event information
                Intent intent = new Intent(getApplicationContext(),NotificationReceiver.class);
                intent.putExtra("id", updatedevent.getId());
                intent.putExtra("location",event.getLocation());
                intent.putExtra("date",event.getDate().toString());
                intent.putExtra("name",event.getName());
                intent.putExtra("priority", event.getIsPrior().toString());

                NotificationReceiver alarm = new NotificationReceiver();

                alarm.setAlarm(getApplicationContext(), event.getDate(), intent);


                Toast.makeText(getApplicationContext(), getString(R.string.save_sucess), Toast.LENGTH_LONG).show();
                Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("selected_navigation", R.id.navigation_events);
                startActivity(intentMainActivity);
            }
        });

        view_map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + event_location_string);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEvent();
            }
        });

    }

    private void shareEvent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String message = eventName.getText() + "\n" + "Date: " + eventDate.getText() + "\n" + "Location: " + eventLocation.getText();
        intent.putExtra(Intent.EXTRA_SUBJECT, eventName.getText());
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");

        if (imagepath != null && !imagepath.trim().isEmpty()) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagepath));
            intent.setType("image/jpeg");
        }
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_using)));
    }

    // this method reads the decoded QR text and loads it into the ui input fields
    private void handleQR(Intent intent) {
        String message = intent.getStringExtra(SaveEvent.message);
        String[] messageArray = message.split("::");
        eventName.setText(messageArray[0]);
        eventLocation.setText(messageArray[2]);
        eventDate.setText(messageArray[1]);
        Log.d(TAG, "messageArray: " + messageArray);
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
        Log.d(TAG, "message: " + message);
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
        if (formatteddate != null) {
            eventDate.setText(targetFormat.format(formatteddate));
        }
    }
}
