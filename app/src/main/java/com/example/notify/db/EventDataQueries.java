package com.example.notify.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notify.model.EventModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_LOCATION};




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

        if(database.update(EventDbReaderHelper.EventEntry.TABLE_NAME, values,
                EventDbReaderHelper.EventEntry._ID + "=" + event.getId(),
                null) > 0) {
            return  event;
        }

        return null;
    }

    public List<EventModel> getUpcomingEvents() {
        List<EventModel> eventList = new ArrayList<EventModel>();
        Cursor cursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME,
                eventColumns, null, null, null, null, EventDbReaderHelper.EventEntry.COLUMN_EVENT_DATE, "2");

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
        return database.delete(EventDbReaderHelper.EventEntry.TABLE_NAME,
                EventDbReaderHelper.EventEntry._ID
                        + "=" + id, null) > 0;
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

    public List<EventModel> getEventList() {
        List<EventModel> events = getEventList();
        if(events.size() > 0) {
            Collections.sort(events, new Comparator<EventModel>() {

                @Override
                public int compare(EventModel o1, EventModel o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            List<EventModel> subItems;

            if(events.size() > 1) {
                subItems = new ArrayList<EventModel>(events.subList(0, 2));
            } else {
                subItems = new ArrayList<EventModel>(events.subList(0, 1));
            }
            return subItems;
        }

        return  null;
    }

    private EventModel getEventDataFromCursor(Cursor cursor) {
        return new EventModel(cursor.getLong(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
    }
}
