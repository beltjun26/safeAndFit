package com.upv.rosiebelt.safefit.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Rosiebelt on 18/03/2018.
 */

public class DBMedicalRecord {
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +MdRecordEntry.TABLE_NAME+ " (" + MdRecordEntry._ID + " INTEGER PRIMARY KEY, "
            + MdRecordEntry.COLUMN_LABEL + " TEXT,"
            + MdRecordEntry.COLUMN_CONTENT + " TEXT)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +  MdRecordEntry.TABLE_NAME;

    public DBManager mdRecordDBHelper;

    public DBMedicalRecord(Context context) {
        mdRecordDBHelper = new DBManager(context);
    }

    public static class MdRecordEntry implements BaseColumns{
        public static final String TABLE_NAME = "medical_record_table";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_CONTENT = "content";

    }


    public void setData(String column, String data, int id){
        SQLiteDatabase db = mdRecordDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, data);
        long newRowID = db.update(MdRecordEntry.TABLE_NAME, contentValues,"_id="+id, null);
    }

    public void addData(String label, String content){
        SQLiteDatabase db = mdRecordDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MdRecordEntry.COLUMN_LABEL, label);
        contentValues.put(MdRecordEntry.COLUMN_CONTENT, content);
        long newRowID = db.insert(MdRecordEntry.TABLE_NAME,null, contentValues);
    }

    public Cursor getData(String[] projection){
        SQLiteDatabase db = mdRecordDBHelper.getReadableDatabase();
        Cursor cursor = db.query(MdRecordEntry.TABLE_NAME, projection,null, null, null, null, null);
        return cursor;
    }


}
