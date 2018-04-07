package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class DBActivities {
    public DBManager activityDbHelper;

    public DBActivities(Context context) {
        activityDbHelper = new DBManager(context);
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ActivitiesEntry.TABLE_NAME + " ("
            + ActivitiesEntry._ID + " INTEGER PRIMARY KEY, "
            + ActivitiesEntry.COLUMN_TIME_START + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + ActivitiesEntry.COLUMN_TIME_END + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + ActivitiesEntry.COLUMN_ACTIVITY + " TEXT)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+ ActivitiesEntry.TABLE_NAME;


    public static class ActivitiesEntry implements BaseColumns {
        public static final String TABLE_NAME = "activities_table";
        public static final String COLUMN_TIME_START = "datetime_start";
        public static final String COLUMN_TIME_END = "datetime_end";
        public static final String COLUMN_ACTIVITY = "activity";
    }


    public void setData(String column, String data, int id){
        SQLiteDatabase db = activityDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, data);
        long newRowID = db.update(ActivitiesEntry.TABLE_NAME, contentValues,"_id="+id, null);
    }

    public Cursor getData(String[] projection){
        SQLiteDatabase db = activityDbHelper.getReadableDatabase();
        Cursor cursor = db.query(ActivitiesEntry.TABLE_NAME, projection,null, null, null, null, null);
        return cursor;
    }

    public void addData(String activity, String start, String end){
        SQLiteDatabase db = activityDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ActivitiesEntry.COLUMN_ACTIVITY, activity);
        contentValues.put(ActivitiesEntry.COLUMN_TIME_START, start);
        contentValues.put(ActivitiesEntry.COLUMN_TIME_END, end);
        long newRowID = db.insert(ActivitiesEntry.TABLE_NAME,null, contentValues);
    }

}
