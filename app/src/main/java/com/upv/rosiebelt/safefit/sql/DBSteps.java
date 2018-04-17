package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBSteps {
    public DBManager stepsDbHelper;

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + StepsEntry.TABLE_NAME + " ("
            + StepsEntry._ID + " INTEGER PRIMARY KEY, "
            + StepsEntry.COLUMN_NO_STEPS + " INTEGER,"
            + StepsEntry.COLUMN_DATE + " DATETIME DEFAULT CURRENT_DATE)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + StepsEntry.TABLE_NAME;


    public DBSteps(Context context) {
        this.stepsDbHelper = new DBManager(context);
    }

    public static class  StepsEntry implements BaseColumns{
        public static final String TABLE_NAME = "steps_table";
        public static final String COLUMN_NO_STEPS = "no_steps";
        public static final String COLUMN_DATE = "date";
    }

    public int getSteps(){
        SQLiteDatabase db = stepsDbHelper.getReadableDatabase();
        String[] projection = new String[]{StepsEntry.COLUMN_DATE, StepsEntry.COLUMN_NO_STEPS};
        Cursor cursor = db.query(StepsEntry.TABLE_NAME, projection, StepsEntry.COLUMN_DATE+" = ?", new String[]{getDate()}, null, null, null);
        if(cursor.getCount() == 0){
            db = stepsDbHelper.getWritableDatabase();
            ContentValues contentValues= new ContentValues();
            contentValues.put(StepsEntry.COLUMN_DATE, getDate());
            contentValues.put(StepsEntry.COLUMN_NO_STEPS, 0);
            db.insert(StepsEntry.TABLE_NAME, null, contentValues);
            return 0;
        }
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(StepsEntry.COLUMN_NO_STEPS));
    }

    public void setSteps(int steps){
        SQLiteDatabase db = stepsDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StepsEntry.COLUMN_NO_STEPS, steps);
        long newRowID = db.update(StepsEntry.TABLE_NAME, contentValues,StepsEntry.COLUMN_DATE+"= ?", new String[]{getDate()});
    }
    public void setData(String column, String data, int id){
        SQLiteDatabase db = stepsDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, data);
        long newRowID = db.update(StepsEntry.TABLE_NAME, contentValues,"_id="+id, null);
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
