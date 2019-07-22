package com.example.notify;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainPage extends Fragment {

    private static final String TAG = "MainPage";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST = 1888;
    private String mCurrentPhotoPath;
    private ProgressBar loading;
    private RelativeLayout wrapper;

    public static final String actionType = "OCR";

    public MainPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);


        ImageButton launchCamera = view.findViewById(R.id.launch_camera);
        ImageButton launchQR = view.findViewById(R.id.launch_qrscanner);
        loading = view.findViewById(R.id.progress_circular);
        wrapper = view.findViewById(R.id.wrapper);


        final Activity mActivity = getActivity();

        launchQR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), QrScanner.class);
                startActivity(myIntent);
            }
        });

        launchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(mActivity,
                                "com.example.notify.provider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST);
                    }
                }
            }
        });


        ImageView image;

        image = view.findViewById(R.id.imageView2);
        setImage(image);

        TextView dateview = view.findViewById(R.id.textView_date);
        setDate(dateview);

        TextView info = view.findViewById(R.id.textView_info);
        setinfo(info);

        return view;
    }

    private void setDate(TextView view) {
        String str = String.format("%tc", new Date());
        view.setText(str);
    }

    private void setImage(ImageView image) {
        image.setImageResource(R.drawable.ic_photo_frame);
    }

    private void setinfo(TextView info) {
        String str_info = "Halifax Data Science\n" + "Social Meetup - Foggy Google\n" + "6pm";
        info.setText(str_info);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.camera_permission_granted), Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.camera_permission_deined), Toast.LENGTH_LONG).show();
            }
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
                    if(isParserCompleted[0] == true) {
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

            new OCRParser(getActivity(),  new AsyncResponse() {
                @Override
                public void processFinish(Object... objects) {
                    // calling Edit page activity with the save image
                    myIntent.putExtra(SaveEvent.message, (String) objects[0]);
                    isParserCompleted[0] = true;

                    if(isSaveimgCompleted[0] == true) {
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
