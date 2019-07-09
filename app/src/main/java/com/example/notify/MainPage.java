package com.example.notify;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

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
    private ImageView posterImg;
    private String mCurrentPhotoPath;
    public static final String actionType = "OCR";

    public MainPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);


        Button launchCamera = view.findViewById(R.id.launch_camera);
        posterImg = view.findViewById(R.id.poster_view);

        Button launchQR = (Button) view.findViewById(R.id.launch_qrscanner);

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


        return view;
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
//            Bitmap picture =  (Bitmap) data.getExtras().get("data");
            File file = new File(mCurrentPhotoPath);
            Bitmap picture = null;
            try {
                picture = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap modifiedPicture = null;
            Uri tempuri = data.getData();
            posterImg.setImageBitmap(picture);

            try {
                modifiedPicture = modifyOrientation(picture, getRealImgPath(tempuri));
                try {
                    extractText(picture);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "exception: " + e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public Bitmap modifyOrientation(Bitmap bitmap, String imgPath) throws IOException {

        ExifInterface ei = new ExifInterface(imgPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public String getRealImgPath(Uri uri) {
        String[] largeFileProjection = {MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA};
        String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
        Cursor myCursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                largeFileProjection, null, null, largeFileSort);
        String largeImagePath = "";
        try {
            myCursor.moveToFirst();
            largeImagePath = myCursor
                    .getString(myCursor
                            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
        } finally {
            try {
                if (!myCursor.isClosed()) {
                    myCursor.close();
                }
                myCursor = null;
            } catch (Exception e) {
                Log.e("While closing cursor", e.getMessage());
            }
        }
        return largeImagePath;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void extractText(Bitmap bitmap) {
        Context context = getActivity().getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            IntentFilter isNoSpace = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().registerReceiver(null, isNoSpace) != null;

            if (hasLowStorage) {
                Toast.makeText(getActivity(), "Insufficient space", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Insufficient space");
            }
            return;
        }

        SparseArray<TextBlock> items = textRecognizer.detect(frame);
        if (items.size() <= 0) {
            return;
        }

        setExtractedText(textRecognizer, frame);

    }

    private void setExtractedText(TextRecognizer textRecognizer, Frame imgFrame) {
        SparseArray<TextBlock> items = textRecognizer.detect(imgFrame);
        if (items.size() <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.get(items.keyAt(i));
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
                sb.append(item.getValue());
                sb.append(" ");
            }
        }
        TextView textView = getActivity().findViewById(R.id.text_result);
        textView.setText(sb);

        // calling new activity to save the extracted information
        Intent myIntent = new Intent(getActivity().getApplicationContext(), SaveEvent.class);
        myIntent.putExtra(SaveEvent.actionType, actionType);
        myIntent.putExtra(SaveEvent.message, sb+"");
        startActivity(myIntent);
    }

}
