/*
* This fragment has a list of events.
* This list includes the completed and upcoming events
* The user will be able to share or delete the events by swiping the list items to the left.
* The empty state of this fragments shows a No Events tag when there are no events to display.
* fragment_events_page.xml has the UI for this fragment
* */



package com.example.notify;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.notify.adapter.EventAdapter;
import com.example.notify.db.EventDataQueries;
import com.example.notify.model.EventModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsPage extends Fragment {
    private List<EventModel> eventsList = new ArrayList<>();
    private SwipeMenuListView events;

    public EventsPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_events_page, container, false);

        SwipeMenuListView listView=v.findViewById(R.id.listview_events);
        listView.setEmptyView(v.findViewById(R.id.emptyview));

        events = v.findViewById(R.id.listview_events);
        EventDataQueries database = new EventDataQueries(getContext());
        database.open();
        eventsList = database.getEventsList();
        events.setAdapter(new EventAdapter(getActivity(), eventsList));
        database.close();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem shareItem = new SwipeMenuItem(
                        getContext());
                shareItem.setWidth(250);
                shareItem.setBackground(R.color.grey);
                shareItem.setIcon(R.drawable.ic_share_white_24dp);
                menu.addMenuItem(shareItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                deleteItem.setBackground(R.color.colorPrimary);
                deleteItem.setWidth(250);
                deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
                menu.addMenuItem(deleteItem);
            }
        };

        events.setMenuCreator(creator);

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

        events.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        shareEvent(position);
                        break;
                    case 1:
                        deleteEvent(position);
                        break;
                }
                return false;
            }
        });
        return v;
    }

    private void deleteEvent(int position) {
        EventModel event = eventsList.get(position);
        EventDataQueries database = new EventDataQueries(getContext());
        database.open();
        if(database.delete(event)) {
            eventsList.remove(event);
            events.invalidateViews();
            Toast.makeText(getContext(), getString(R.string.delete_sucess), Toast.LENGTH_LONG).show();
        }
        database.close();
    }

    private void shareEvent(int position) {
        EventModel event = eventsList.get(position);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String message = event.getName() + "\n" + "Date: " + event.getDate() + "\n" + "Location: " + event.getLocation();
        intent.putExtra(Intent.EXTRA_SUBJECT, event.getName());
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        if(event.getposter() != null && !event.getposter().trim().isEmpty()) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(event.getposter()));
            intent.setType("image/jpeg");
        }
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_using)));
    }

}
