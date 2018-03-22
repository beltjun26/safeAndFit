package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Rosiebelt on 14/03/2018.
 */

public class DBUser{
    public static final String DATABASE_NAME = "safefile.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +UserEntry.TABLE_NAME+ " (" + UserEntry._ID + " INTEGER PRIMARY KEY, "
            + UserEntry.COLUMN_NAME_FULLNAME + " TEXT," + UserEntry.COLUMN_NAME_EMAIL + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +  UserEntry.TABLE_NAME;

    public UserDbHelper userdbhelper;
    public DBUser(Context context){
        userdbhelper = new UserDbHelper(context);
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_table";
        public static final String COLUMN_NAME_FULLNAME = "fullname";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

    public boolean exist(){
        SQLiteDatabase db = userdbhelper.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void setUser(String name){
        SQLiteDatabase db = userdbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_NAME_FULLNAME, name);
        long newRowID = db.insert(UserEntry.TABLE_NAME,null, contentValues);
    }

    public void setEmail(String email){
        SQLiteDatabase db = userdbhelper.getWritableDatabase();

    }
    public String[] getData(String[] projection){
        String[] result = {};
        SQLiteDatabase db = userdbhelper.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, projection, null, null, null, null, null);
        cursor.close();
        return result;
    }

    public String getEmail(){
        SQLiteDatabase db = userdbhelper.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME,new String[]{UserEntry.COLUMN_NAME_EMAIL}, null, null, null, null, null);
        cursor.moveToFirst();
        String email = cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_NAME_EMAIL));
        cursor.close();
        if(email == null){
            return "";
        }
        return email;
    }
    public String getUser(){
        SQLiteDatabase db = userdbhelper.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME,new String[]{UserEntry.COLUMN_NAME_FULLNAME}, null, null, null, null, null);
        cursor.moveToFirst();
        String user = cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_NAME_FULLNAME));
        cursor.close();
        return user;
    }

    public class UserDbHelper extends SQLiteOpenHelper{

        public UserDbHelper(Context context){
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
            onUpgrade(db, oldVersion, newVersion);
        }
    }




}
