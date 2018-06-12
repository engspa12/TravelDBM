package com.apps.dbm.traveldbm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectionDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "favorites_collection.db";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + CollectionContract.CollectionEntry.TABLE_NAME + " (" +
            CollectionContract.CollectionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_LATITUDE + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_LONGITUDE + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_URL + " TEXT NOT NULL," +
            CollectionContract.CollectionEntry.COLUMN_HOTEL_AMENITIES + " TEXT NOT NULL)";
    //+
    //CollectionContract.CollectionEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL," +
    //CollectionContract.CollectionEntry.COLUMN_SAVED_MOVIE_IMAGE + " BLOB)";


    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CollectionContract.CollectionEntry.TABLE_NAME;

    public CollectionDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
