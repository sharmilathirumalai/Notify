package com.example.notify;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

public class Settings extends Fragment {
    private static final String TAG = "Settings";
    Spinner spinner;


    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Inflate the layout for this fragment
        spinner = view.findViewById(R.id.alarm_before_spinner);
        Log.d(TAG, "onCreateView: ");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d(TAG, "onItemSelected: "+position);
                Log.d(TAG, "id: "+id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(TAG, "onItemSelected: ");
                Log.d(TAG, "id: ");
            }
        });

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
