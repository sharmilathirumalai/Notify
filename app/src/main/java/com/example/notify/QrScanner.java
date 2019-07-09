package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private static final String TAG = "scanner";
    public static final String message = "event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }


    @Override
    public void handleResult(Result result) {
        Log.d(TAG, "handleResult: " + result.getText());
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();

        Intent myIntent = new Intent(getApplicationContext(), SaveEventQR.class);
        myIntent.putExtra("event", result.getText());
        startActivity(myIntent);
    }
}