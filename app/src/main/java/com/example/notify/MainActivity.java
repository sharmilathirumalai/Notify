package com.example.notify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int PERMISSION_REQUEST_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startCamera();
    }

    private void setTextProcessor(TextRecognizer textRecognizer) {
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() <= 0) {
                    return;
                }
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    if (item != null && item.getValue() != null) {
                        Log.d("Processor", "Text detected! " + item.getValue());
                        sb.append(item.getValue());
                        sb.append("\n");
                    }
                }
                 TextView textView = findViewById(R.id.text_result);
                textView.setText(sb);
            }
        });
    }

    private void startCamera() {
        final Context context = getApplicationContext();
        final Activity activity =  this;
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            IntentFilter isNoSpace = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, isNoSpace) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Insufficient space", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Insufficient space");
            }
        }

        final CameraSource cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setAutoFocusEnabled(true)
                        .build();

        final SurfaceView cameraView = findViewById(R.id.camera_view);
        SurfaceHolder holder = cameraView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                Manifest.permission.CAMERA)) {
                            return;

                        } else {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Insufficient space", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        setTextProcessor(textRecognizer);

    }
}
