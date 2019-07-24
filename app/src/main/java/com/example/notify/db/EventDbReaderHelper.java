package com.example.notify.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class EventDbReaderHelper extends SQLiteOpenHelper {


    public static abstract class EventEntry implements BaseColumns {
        static final String TABLE_NAME = "events";
        static final String COLUMN_EVENT_NAME = "event_name";
        static final String COLUMN_EVENT_DATE = "event_date";
        static final String COLUMN_EVENT_LOCATION = "event_location";
        static final String COLUMN_EVENT_POSTER = "event_poster";
        static final String COLUMN_EVENT_PRIORITY ="event_priority";
    }


    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE  = " INTEGER";
    public static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_EVENT =
            "CREATE TABLE IF NOT EXISTS " + EventEntry.TABLE_NAME + "(" +
                    EventEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    EventEntry.COLUMN_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_EVENT_DATE + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_EVENT_POSTER + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_EVENT_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_EVENT_LOCATION + TEXT_TYPE + ")";


    private static final String SQL_DELETE_EVENT = "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notify.db";

    public EventDbReaderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EVENT);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

