package com.upv.rosiebelt.safefit.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "safefile.db";
    public static final int DATABASE_VERSION = 1;

    public DBManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBMedicalRecord.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(DBUser.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(DBActivities.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(DBSteps.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DBMedicalRecord.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(DBUser.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(DBActivities.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(DBSteps.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
