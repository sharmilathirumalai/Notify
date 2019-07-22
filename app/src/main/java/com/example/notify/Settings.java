package com.example.notify;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    private static final String TAG = "Settings";
    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // NotificationReceiver
        Log.d(TAG, "onCreateView: ");
        
        NotificationReceiver notification = new NotificationReceiver();
        notification.setAlarm(getContext());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
