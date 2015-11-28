package com.example.amit.faasos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
* Created by amit on 7/20/2015.
*/
public class FaasosDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "faasos.db";

    public FaasosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_FAASOS_TABLE = "CREATE TABLE " + FaasosDBContract.FaasosEntry.TABLE_NAME + " (" +
                FaasosDBContract.FaasosEntry._ID + " INTEGER PRIMARY KEY," +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_IMAGE + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_CATEGORY + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_SPICEMETER + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_DESCRIPTION + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_PRICE + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_RATING + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_ISVEG + " TEXT NOT NULL, " +
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_LIKED + " TEXT NOT NULL DEFAULT '0', " +

                " UNIQUE (" + FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_FAASOS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FaasosDBContract.FaasosEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
