package com.example.notify;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.notify.adapter.EventAdapter;
import com.example.notify.db.EventDataQueries;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsPage extends Fragment {


    public EventsPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_events_page, container, false);

        final ListView events = v.findViewById(R.id.listview_events);
        EventDataQueries database = new EventDataQueries(getContext());
        database.open();
        events.setAdapter(new EventAdapter(getActivity(), database.getEventsList()));
        database.close();
        return v;
    }

}
