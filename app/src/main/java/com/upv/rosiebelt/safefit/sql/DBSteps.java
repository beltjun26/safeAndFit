package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
// This method is for setting the data to the steps_table will overwrite the data.
    public void setSteps(int steps){
        SQLiteDatabase db = stepsDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StepsEntry.COLUMN_NO_STEPS, steps);
        long newRowID = db.update(StepsEntry.TABLE_NAME, contentValues,StepsEntry.COLUMN_DATE+"= ?", new String[]{getDate()});
    }

    public int[] dataWeek(){
        SQLiteDatabase db = stepsDbHelper.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Calendar date = Calendar.getInstance();
        String dayOfWeek = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String dateIterator, query, dbDate;

        int[] data = new int[7];
        int toAdd = 0;
        switch (dayOfWeek) {
            case "Monday":
                toAdd = -1;
                break;
            case "Tuesday":
                toAdd = -2;
                break;
            case "Wednesday":
                toAdd = -3;
                break;
            case "Thursday":
                toAdd = -4;
                break;
            case "Friday":
                toAdd = -5;
                break;
            case "Saturday":
                toAdd = -6;
                break;
        }
        query = "SELECT * FROM " + StepsEntry.TABLE_NAME+ " WHERE date BETWEEN datetime('now', '"+toAdd+" days') AND datetime('now', 'localtime') ORDER BY date ASC";
        date.add(Calendar.DATE, toAdd);
        Cursor cursor = db.rawQuery(query, null);
        int iterator = 0;
        while(cursor.moveToNext()){
            dbDate = cursor.getString(cursor.getColumnIndex(StepsEntry.COLUMN_DATE));
            dateIterator = dateFormat.format(date.getTime());
            while(!dbDate.equals(dateIterator)){
                date.add(Calendar.DATE, 1);
                dateIterator = dateFormat.format(date.getTime());
                iterator++;
            }
            if(dbDate.equals(dateIterator)){
                data[iterator] = cursor.getInt(cursor.getColumnIndex(StepsEntry.COLUMN_NO_STEPS));
                iterator++;
                date.add(Calendar.DATE, 1);
            }
        }
        cursor.close();
        return data;
    }

    public int[] getDataWholeMonth(){
        SQLiteDatabase db = stepsDbHelper.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Calendar date = Calendar.getInstance();
        int days = date.getActualMaximum(Calendar.DAY_OF_MONTH);
        int[] monthSteps = new int[days];
        String dbDate, dateIterator;
        String query = "select * from "+StepsEntry.TABLE_NAME+" where strftime('%Y', date) = strftime('%Y', date('now')) and strftime('%m', date) = strftime('%m', date('now'))";
        Cursor cursor = db.rawQuery(query, null);
        date.set(Calendar.DAY_OF_MONTH, 1);
        int iterator = 0;
        while(cursor.moveToNext()){
            dbDate = cursor.getString(cursor.getColumnIndex(StepsEntry.COLUMN_DATE));
            dateIterator = dateFormat.format(date.getTime());
            while(!dbDate.equals(dateIterator)){
                date.add(Calendar.DATE, 1);
                dateIterator = dateFormat.format(date.getTime());
                iterator++;
            }
            if(dbDate.equals(dateIterator)){
                monthSteps[iterator] = cursor.getInt(cursor.getColumnIndex(StepsEntry.COLUMN_NO_STEPS));
                iterator++;
                date.add(Calendar.DATE, 1);
            }
        }
        return monthSteps;

    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
