package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public String dataWeek(){
        SQLiteDatabase db = activityDbHelper.getReadableDatabase();
        String[] projection = new String[]{ActivitiesEntry.COLUMN_ACTIVITY, ActivitiesEntry.COLUMN_TIME_START, ActivitiesEntry.COLUMN_TIME_END};
        String selection = ActivitiesEntry.COLUMN_TIME_START + "> ? " + " AND " + ActivitiesEntry.COLUMN_TIME_START + "< ? ";

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Calendar date = Calendar.getInstance();
        String dayOfWeek = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String startDate, endDate;
        int toAdd = 0;
        if(dayOfWeek.equals("Monday")){
            toAdd = -1;
        }else if(dayOfWeek.equals("Tuesday")){
            toAdd = -2;
        }else if(dayOfWeek.equals("Wednesday")){
            toAdd = -3;
        }else if(dayOfWeek.equals("Thursday")){
            toAdd = -4;
        }else if(dayOfWeek.equals("Friday")){
            toAdd = -5;
        }else if(dayOfWeek.equals("Saturday")){
            toAdd = -6;
        }
        date.add(Calendar.DATE, toAdd);
        startDate = dateFormat.format(date.getTime());
        date.add(Calendar.DATE, 7);
        endDate = dateFormat.format(date.getTime());
        String[] selectionArgs = new String[]{startDate, endDate};
        Cursor cursor = db.query(ActivitiesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, ActivitiesEntry.COLUMN_TIME_START + " ASC");

        return Integer.toString(cursor.getCount());

//        return cursor;

    }

    public Cursor dataToday(){
        SQLiteDatabase db = activityDbHelper.getReadableDatabase();
        String[] projection = new String[]{ActivitiesEntry.COLUMN_ACTIVITY, ActivitiesEntry.COLUMN_TIME_START, ActivitiesEntry.COLUMN_TIME_END};
        String selection = ActivitiesEntry.COLUMN_TIME_START + "> ? " + " AND " + ActivitiesEntry.COLUMN_TIME_START + "< ? ";

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String startDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        String endDate = dateFormat.format(calendar.getTime());

        String[] selectionArgs = new String[]{startDate, endDate};
        Cursor cursor = db.query(ActivitiesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, ActivitiesEntry.COLUMN_TIME_START + " ASC");
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

    // get current date
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long[] getTodayActivityTime(){
        long[] activityLength = new long[5];
        Cursor todayActivity = dataToday();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        if(todayActivity.getCount() !=0 ){
            String start, end, activityType;
            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            while(todayActivity.moveToNext()){
/**
 *           calculate the percentage for each activity
 */
                end = todayActivity.getString(todayActivity.getColumnIndex(DBActivities.ActivitiesEntry.COLUMN_TIME_END));
                start = todayActivity.getString(todayActivity.getColumnIndex(DBActivities.ActivitiesEntry.COLUMN_TIME_START));
                activityType = todayActivity.getString(todayActivity.getColumnIndex(DBActivities.ActivitiesEntry.COLUMN_ACTIVITY));
                long difference = 0;
                try{
                    calendarEnd.setTime(dateFormat.parse(end));
                    calendarStart.setTime(dateFormat.parse(start));
                    difference = calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis();;
                    switch (activityType) {
                        case "Walking":
                            activityLength[0] = activityLength[0] + difference;
                            break;
                        case "Bicycle":
                            activityLength[1] = activityLength[1] + difference;
                            break;
                        case "Still":
                            activityLength[2] = activityLength[2] + difference;
                            break;
                        case "Vehicle":
                            activityLength[3] = activityLength[3] + difference;
                            break;
                        case "Running":
                            activityLength[4] = activityLength[4] + difference;
                            break;
                    }
                }catch (Exception e){
                    Log.e("DataBase Errro", "Error in parsing");
                }
            }
            return activityLength;
        }else{
            return null;
        }
    }
}
