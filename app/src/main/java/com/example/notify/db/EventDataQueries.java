package com.example.notify.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notify.model.EventModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class EventDataQueries {

    private final Context mContext;
    private SQLiteDatabase database;
    private EventDbReaderHelper dbHelper;

    public EventDataQueries(Context context) {
        mContext = context;
        dbHelper = new EventDbReaderHelper(context);
    }

    private String[] eventColumns = {EventDbReaderHelper.EventEntry._ID,
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_NAME,
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_DATE,
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_LOCATION,
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_POSTER,
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_PRIORITY
    };


    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public EventModel create(EventModel event) {
        ContentValues values = new ContentValues();
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_NAME, event.getName());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_LOCATION, event.getLocation());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_DATE, event.getDate().toString());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_POSTER, event.getposter());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_PRIORITY, (event.getIsPrior() ? 1 :0));

        Cursor dbCursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();

        long insertId = database.insert(EventDbReaderHelper.EventEntry.TABLE_NAME, null, values);
        Log.i(getClass().getName(), String.valueOf(insertId));
        Cursor cursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME,
                eventColumns, EventDbReaderHelper.EventEntry._ID, null, null, null, null);
        cursor.moveToFirst();
        EventModel createdEvent = getEventDataFromCursor(cursor);
        cursor.close();

        return createdEvent;
    }

    public EventModel update(EventModel event) {
        ContentValues values = new ContentValues();
        values.put(EventDbReaderHelper.EventEntry._ID, event.getId());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_NAME, event.getName());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_LOCATION, event.getLocation());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_DATE, event.getDate().toString());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_POSTER, event.getposter());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_PRIORITY, (event.getIsPrior() ? 1 :0));

        if(database.update(EventDbReaderHelper.EventEntry.TABLE_NAME, values,
                EventDbReaderHelper.EventEntry._ID + "=" + event.getId(),
                null) > 0) {
            return  event;
        }

        return null;
    }

    public List<EventModel> getEventsList() {
        List<EventModel> eventList = new ArrayList<EventModel>();
        Cursor cursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME,
                eventColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EventModel event = getEventDataFromCursor(cursor);
            eventList.add(event);
            cursor.moveToNext();
        }
        cursor.close();
        return eventList;
    }

    public boolean delete(EventModel event) {
        long id = event.getId();
        String[] args = {String.valueOf(id)};

        return database.delete(EventDbReaderHelper.EventEntry.TABLE_NAME,
                EventDbReaderHelper.EventEntry._ID
                        + "=?" , args) > 0;
    }

    public EventModel getEvent(long id) {
        String[] args = {String.valueOf(id)};
        Cursor cursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME,
                eventColumns, EventDbReaderHelper.EventEntry._ID + "=?", args , null, null, null);
        cursor.moveToFirst();
        EventModel event = getEventDataFromCursor(cursor);
        cursor.close();
        return  event;
    }

    public List<EventModel> getUpcomingEvents()  {
        List<EventModel> events = getEventsList();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Date todaysDate = null;
        try {
            String datestr= dateFormat.format(date);
            todaysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(todaysDate == null) {
            return  new ArrayList<>();
        }

        List<EventModel> filteredevents = new ArrayList<>();


        for (EventModel e: events) {
            if(e.getDate().compareTo(todaysDate) == 1) {
                filteredevents.add(e);
            }
        }

        if(filteredevents.size() > 0) {
            Collections.sort(filteredevents, new Comparator<EventModel>() {

                @Override
                public int compare(EventModel o1, EventModel o2) {

                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            List<EventModel> subItems;
            if(filteredevents.size() > 1) {
                subItems = new ArrayList<EventModel>(filteredevents.subList(0, 2));
            } else {
                subItems = new ArrayList<EventModel>(filteredevents.subList(0, 1));
            }
            return subItems;
        }

        return  new ArrayList<EventModel>();
    }

    private EventModel getEventDataFromCursor(Cursor cursor) {
        return new EventModel(cursor.getLong(cursor.getColumnIndex("_id")), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), (cursor.getInt(5) == 0 ? false : true));
    }
}
