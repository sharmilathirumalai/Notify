/*
* This class saves the images captured by the camera in the users local storage under a folder named notify.
*
* */


package com.example.notify;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImage extends AsyncTask<Object, Void, String> {

    public static final String TAG = "SaveImage";
    private final Context context;
    private final AsyncResponse listener;

    public SaveImage(Context c, AsyncResponse listener) {
        this.context = c;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... objects) {
        Bitmap img = (Bitmap) objects[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Event_" + date + ".jpg";
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(TAG, "Can't create directory to save image.");
            return null;

        }

        String filename = pictureFileDir.getPath() + File.separator + photoFile;
        File pictureFile = new File(filename);

        try {
            FileOutputStream fout = new FileOutputStream(pictureFile);
            img.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.close();


        } catch (Exception error) {
            Log.d(TAG, "File" + filename + "not saved: " + error.getMessage());
        }

        return filename;
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, context.getString(R.string.app_name));
    }

    @Override
    protected void onPostExecute(String filename) {
        listener.processFinish(filename);
    }

}
