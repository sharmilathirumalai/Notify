/*
* This class is used to read the text from the images of the posters.
* Once the image is captures and read, the information such as time, date and location of the event is
* obtained.
* */
package com.example.notify;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;

public class OCRParser extends AsyncTask<Object, Void, String> {
    private static final String TAG = "MainPage";

    private final FragmentActivity activity;
    private final AsyncResponse listener;

    public OCRParser(FragmentActivity activity,  AsyncResponse listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... params) {
        Bitmap picture = (Bitmap) params[0];
        Uri tempuri = (Uri) params[1];

        try {
            Bitmap modifiedPicture = modifyOrientation(picture, getRealImgPath(tempuri));
            try {
                String message =  extractText(modifiedPicture);
                Log.d(TAG, "message: "+message);
                return  message;
            } catch (Exception e) {
                Log.d(TAG, "exception: " + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        Cursor myCursor = this.activity.getContentResolver().query(
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

    public String extractText(Bitmap bitmap) {
        Context context = this.activity.getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this.activity).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            IntentFilter isNoSpace = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = this.activity.registerReceiver(null, isNoSpace) != null;

            if (hasLowStorage) {
                Toast.makeText(this.activity, "Insufficient space", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Insufficient space");
            }
            return "";
        }

        SparseArray<TextBlock> items = textRecognizer.detect(frame);
        if (items.size() <= 0) {
            return "";
        }


        return setExtractedText(textRecognizer, frame);
    }

    private String setExtractedText(TextRecognizer textRecognizer, Frame imgFrame) {
        SparseArray<TextBlock> items = textRecognizer.detect(imgFrame);
        if (items.size() <= 0) {
            return "";
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

        return sb.toString();
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "Notify");
    }

    @Override
    protected void onPostExecute(String message) {
        listener.processFinish(message);
    }
}
