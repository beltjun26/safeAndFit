package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBActivities {
    public static final String DATABASE_NAME = "safefile.db";
    public static final int DATABASE_VERSION = 1;
    public ActivityDbHelper activityDbHelper;

    public DBActivities(Context context) {
        activityDbHelper = new ActivityDbHelper(context);
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ActivitiesEntry.TABLE_NAME + " ("
            + ActivitiesEntry._ID + " INTEGER PRIMARY KEY, "
            + ActivitiesEntry.COLUMN_TIME_START + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + ActivitiesEntry.COLUNN_TINE_END + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + ActivitiesEntry.COLUMN_ACTIVITY + " TEXT)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+ ActivitiesEntry.TABLE_NAME;


    public static class ActivitiesEntry implements BaseColumns {
        public static final String TABLE_NAME = "activities_table";
        public static final String COLUMN_TIME_START = "datetime_start";
        public static final String COLUNN_TINE_END = "datetime_end";
        public static final String COLUMN_ACTIVITY = "activity";
    }

    public class ActivityDbHelper extends SQLiteOpenHelper {

        public ActivityDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
            onCreate(sqLiteDatabase);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }
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
        cursor.moveToFirst();
        return cursor;
    }

}
