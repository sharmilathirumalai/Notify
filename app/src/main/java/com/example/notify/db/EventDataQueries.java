package com.example.notify.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notify.model.EventModel;

import java.util.ArrayList;
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
            EventDbReaderHelper.EventEntry.COLUMN_EVENT_LOCATION,
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
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_DATE, event.getDate());

        Cursor dbCursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();

        long insertId = database.insert(EventDbReaderHelper.EventEntry.TABLE_NAME, null, values);
        Log.i(getClass().getName(), String.valueOf(insertId));
        Cursor cursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME,
                eventColumns, EventDbReaderHelper.EventEntry._ID, null, null, null, null);
        cursor.moveToFirst();
        EventModel studentDataFromCursor = getStudentDataFromCursor(cursor);
        cursor.close();

        return studentDataFromCursor;
    }

    public boolean update(EventModel event) {
        ContentValues values = new ContentValues();
        values.put(EventDbReaderHelper.EventEntry._ID, event.getId());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_NAME, event.getName());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_LOCATION, event.getLocation());
        values.put(EventDbReaderHelper.EventEntry.COLUMN_EVENT_DATE, event.getDate());

        return database.update(EventDbReaderHelper.EventEntry.TABLE_NAME, values,
                EventDbReaderHelper.EventEntry._ID + "=" + event.getId(),
                null) > 0;

    }

    public boolean delete(EventModel studentModel) {
        long id = studentModel.getId();
        return database.delete(EventDbReaderHelper.EventEntry.TABLE_NAME,
                EventDbReaderHelper.EventEntry._ID
                        + "=" + id, null) > 0;
    }

    public List<EventModel> getEventList() {
        List<EventModel> studentModelList = new ArrayList<EventModel>();
        Cursor cursor = database.query(EventDbReaderHelper.EventEntry.TABLE_NAME,
                eventColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EventModel studentModel = getStudentDataFromCursor(cursor);
            studentModelList.add(studentModel);
            cursor.moveToNext();

        }
        cursor.close();
        return studentModelList;
    }

    private EventModel getStudentDataFromCursor(Cursor cursor) {
        return new EventModel(cursor.getLong(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
    }
}
