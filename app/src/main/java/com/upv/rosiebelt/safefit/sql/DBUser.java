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

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +UserEntry.TABLE_NAME+ " (" + UserEntry._ID + " INTEGER PRIMARY KEY, "
            + UserEntry.COLUMN_NAME_FULLNAME + " TEXT," + UserEntry.COLUMN_NAME_EMAIL +" TEXT,"+ UserEntry.COLUMN_NAME_SEX + " TEXT)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +  UserEntry.TABLE_NAME;

    public UserDbHelper userdbhelper;
    public DBUser(Context context){
        userdbhelper = new UserDbHelper(context);
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_table";
        public static final String COLUMN_NAME_FULLNAME = "fullname";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SEX = "sex";
    }

    public int exist(){
        SQLiteDatabase db = userdbhelper.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return -1;
        }
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(UserEntry._ID));
        cursor.close();
        return id;
    }

    public long setUser(String name){
        SQLiteDatabase db = userdbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_NAME_FULLNAME, name);
        long newRowID = db.insert(UserEntry.TABLE_NAME,null, contentValues);
        return newRowID;

    }

    public void setData(String column, String data, int id){
        SQLiteDatabase db = userdbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, data);
        long newRowID = db.update(UserEntry.TABLE_NAME, contentValues, "_id="+id, null);
    }

    public Cursor getData(String[] projection){
        SQLiteDatabase db = userdbhelper.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, projection, null, null, null, null, null);
        cursor.moveToFirst();
        return cursor;
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
