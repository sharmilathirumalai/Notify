/*
* This fragment is the first screen of the application where the user can take pictures of the posters
* or scan QR codes in the posters by clicking on the respective buttons.
* Once the user takes a picture or scans the QR code of the posters the event get generated and takes
* the user to the save event page where the user will be able to save the event.
* Once the event is being saved the events appears below the upcoming section in this fragment.
* */package com.example.notify;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.notify.adapter.EventAdapter;
import com.example.notify.db.EventDataQueries;
import com.example.notify.model.EventModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The home screen {@MainPage Fragment} subclass.
 */
public class MainPage extends Fragment {

    private static final String TAG = "MainPage";
    private static final int PERMISSION_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST = 1888;
    private static final int WRITE_PERMISSION_REQUEST = 1024;
    public static final String actionType = "OCR";


    private ProgressBar loading;
    private RelativeLayout wrapper;
    private  ListView events;

    private String mCurrentPhotoPath;
    List<String> permissionsList = new ArrayList<>();
    private  List<EventModel> eventsList = new ArrayList<>();


    public MainPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        //Creates a list view for upcoming events
        ListView listView=view.findViewById(R.id.listview_events);
        //Creating empty space when no events are available
        listView.setEmptyView(view.findViewById(R.id.emptyview));

        ImageButton launchCamera = view.findViewById(R.id.launch_camera);
        ImageButton launchQR = view.findViewById(R.id.launch_qrscanner);
        loading = view.findViewById(R.id.progress_circular);
        wrapper = view.findViewById(R.id.wrapper);

        //Opens the camera and scans the QR code of the image when the button is clicked
        launchQR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), QrScanner.class);
                myIntent.putExtra("FromActivityTAG", TAG);
                startActivity(myIntent);
            }
        });

        //Opens the camera to capture the image when the button is clicked
        launchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    launchCameraActivity();
                }

            }
        });

        if (!checkPermissions()) {
            requestPermissions();
        }

        EventDataQueries database = new EventDataQueries(getContext());
        events = view.findViewById(R.id.listview_events);
        database.open();
        eventsList = database.getUpcomingEvents();
        events.setAdapter(new EventAdapter(getActivity(), eventsList ));
        database.close();

        events.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

                EventModel event = eventsList.get(position);
                Intent intent = new Intent(getContext(), SaveEvent.class);

                Bundle detailsBundle = new Bundle();
                detailsBundle.putString(SaveEvent.EventId, Long.toString(event.getId()));
                detailsBundle.putString(SaveEvent.EventName, event.getName());
                detailsBundle.putString(SaveEvent.EventDate, event.getDate().toString());
                detailsBundle.putString(SaveEvent.EventLocation, event.getLocation());
                detailsBundle.putString(SaveEvent.posterThumbnail, event.getposter());
                detailsBundle.putBoolean(SaveEvent.EventPriority, event.getIsPrior());

                intent.putExtra("bundle", detailsBundle);
                String a = intent.getStringExtra(SaveEvent.EventId);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        EventDataQueries database = new EventDataQueries(getContext());
        database.open();
        eventsList = database.getUpcomingEvents();
        events.setAdapter(new EventAdapter(getActivity(), eventsList));
        database.close();
    }

    private void launchCameraActivity() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.example.notify.provider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST);
        }
    }


    private boolean checkPermissions() {
        /*
         * Request permissions, so that we can get the camera & storage
         * access. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        int str = getActivity().checkSelfPermission(Manifest.permission.CAMERA);
        int str1 = PackageManager.PERMISSION_GRANTED;
        if (getActivity().checkSelfPermission(android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if(!permissionsList.contains(Manifest.permission.CAMERA)) {
                permissionsList.add(Manifest.permission.CAMERA);
            }
        } else {
            permissionsList.remove(Manifest.permission.CAMERA);
        }
        if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if(!permissionsList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

        } else {
            permissionsList.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionsList.size() != 0) {
            return false;
        }

        return true;
    }

    private void requestPermissions() {
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]),
                    PERMISSION_CODE);
            checkPermissions();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PERMISSION_REQUEST && resultCode == Activity.RESULT_OK) {
            loading.setVisibility(View.VISIBLE);
            wrapper.setAlpha(0.1f);
            for (int i = 0; i < wrapper.getChildCount(); i++) {
                View child = wrapper.getChildAt(i);
                child.setEnabled(false);
            }

            File file = new File(mCurrentPhotoPath);
            Bitmap picture = null;

            try {
                picture = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Intent myIntent = new Intent(getActivity().getApplicationContext(), SaveEvent.class);
            myIntent.putExtra(SaveEvent.actionType, actionType);

            final Boolean[] isSaveimgCompleted = {false};
            final Boolean[] isParserCompleted = {false};

            new SaveImage(getContext(), new AsyncResponse() {
                @Override
                public void processFinish(Object... objects) {
                    // calling Edit page activity with the save image
                    myIntent.putExtra(SaveEvent.posterThumbnail, (String) objects[0]);
                    isSaveimgCompleted[0] = true;
                    if (isParserCompleted[0] == true) {
                        startActivity(myIntent);
                        loading.setVisibility(View.INVISIBLE);
                        for (int i = 0; i < wrapper.getChildCount(); i++) {
                            View child = wrapper.getChildAt(i);
                            child.setEnabled(true);
                        }
                        wrapper.setAlpha(1.0f);
                    }
                }
            }).execute(picture);

            new OCRParser(getActivity(), new AsyncResponse() {
                @Override
                public void processFinish(Object... objects) {
                    // calling Edit page activity with the save image
                    myIntent.putExtra(SaveEvent.message, (String) objects[0]);
                    isParserCompleted[0] = true;

                    if (isSaveimgCompleted[0] == true) {
                        startActivity(myIntent);
                        loading.setVisibility(View.INVISIBLE);
                        for (int i = 0; i < wrapper.getChildCount(); i++) {
                            View child = wrapper.getChildAt(i);
                            child.setEnabled(true);
                        }
                        wrapper.setAlpha(1.0f);
                    }
                }
            }).execute(picture, data.getData());

        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
