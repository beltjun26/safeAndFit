package com.upv.rosiebelt.safefit.sql;

import android.content.Context;
import android.provider.BaseColumns;

public class DBSteps {
    public DBManager stepsDbHelper;

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + StepsEntry.TABLE_NAME + " ("
            + StepsEntry._ID + " INTEGER PRIMARY KEY, "
            + StepsEntry.COLUMN_NO_STEPS + " INTEGER,"
            + StepsEntry.COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + StepsEntry.TABLE_NAME;


    public DBSteps(Context context) {
        this.stepsDbHelper = new DBManager(context);
    }

    public static class  StepsEntry implements BaseColumns{
        public static final String TABLE_NAME = "steps_table";
        public static final String COLUMN_NO_STEPS = "no_steps";
        public static final String COLUMN_DATE = "date";
    }

}
