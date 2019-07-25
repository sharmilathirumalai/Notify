/*
 * The settings is used to set the user preferences
 * The user can specify the alarm preferences such as if they want a notification half an hour before
 * the event or one hour before the event.
 * fragment_settings.xml contains the UI for this fragment
 * */

package com.example.notify;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

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
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("notify", MODE_PRIVATE).edit();
                if(position == 0) {
                    editor.putString("notify_before", "30");

                } else {
                    editor.putString("notify_before", "60");
                }
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(TAG, "onItemSelected: ");
                Log.d(TAG, "id: ");
            }
        });

        TextView aboutbtn = view.findViewById(R.id.about_us);

        aboutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainActivity = new Intent(getContext(), About.class);
                startActivity(intentMainActivity);
            }
        });

        return view;
    }
}
