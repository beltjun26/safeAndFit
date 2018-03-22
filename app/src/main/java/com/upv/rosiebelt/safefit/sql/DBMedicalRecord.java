package com.upv.rosiebelt.safefit.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Rosiebelt on 18/03/2018.
 */

public class DBMedicalRecord {
    public static final String DATABASE_NAME = "safefile.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +MdRecordEntry.TABLE_NAME+ " (" + MdRecordEntry._ID + " INTEGER PRIMARY KEY, "
            + MdRecordEntry.COLUMN_NAME_ADDRESS + " TEXT," + MdRecordEntry.COLUMN_NAME_DATE_OF_BIRTH + " DATETIME, "
            +MdRecordEntry.COLUMN_NAME_MARITAL_STATUS + " Text," + MdRecordEntry.COLUMN_NAME_SEX+ " TEXT,"+ MdRecordEntry.COLUMN_NAME_ALLERGIES +" TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +  MdRecordEntry.TABLE_NAME;

    public MdRecordDBHelper mdRecordDBHelper;

    public DBMedicalRecord(Context context) {
        mdRecordDBHelper = new MdRecordDBHelper(context);
    }

    public static class MdRecordEntry implements BaseColumns{
        public static final String TABLE_NAME = "medical_record_table";
        public static final String COLUMN_NAME_DATE_OF_BIRTH = "birthday";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_MARITAL_STATUS = "marital_status";
        public static final String COLUMN_NAME_SEX = "sex";
        public static final String COLUMN_NAME_ALLERGIES = "allergies";
    }

    public boolean exist(){
        SQLiteDatabase db = mdRecordDBHelper.getReadableDatabase();
        Cursor cursor = db.query(MdRecordEntry.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public class MdRecordDBHelper extends SQLiteOpenHelper{

        public MdRecordDBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
