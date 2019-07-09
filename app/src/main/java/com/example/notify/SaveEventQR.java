package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class SaveEventQR extends AppCompatActivity {
    private static final String TAG = "SaveEventQR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_event_qr);

        Intent intent = getIntent();
        String message = intent.getStringExtra(QrScanner.message);

        try {
            Log.d(TAG, "message: "+message);
            JSONObject messageObject = new JSONObject(message);
            Toast.makeText(this, messageObject+"", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "messageObject: "+messageObject);
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
            e.printStackTrace();
        }


    }
}
