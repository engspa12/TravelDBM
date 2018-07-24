package com.apps.dbm.traveldbm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class CollectionProvider extends ContentProvider {

    private static final int HOTELS = 100;
    private static final int HOTEL_PROPERTY_CODE = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{

        sUriMatcher.addURI(
                CollectionContract.CONTENT_AUTHORITY,CollectionContract.PATH_HOTELS,HOTELS);

        sUriMatcher.addURI(
                CollectionContract.CONTENT_AUTHORITY,CollectionContract.PATH_HOTELS + "/*",HOTEL_PROPERTY_CODE);

    }

    private CollectionDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new CollectionDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case HOTELS:
                cursor=database.query(CollectionContract.CollectionEntry.TABLE_NAME, projection, selection, selectionArgs,null,null,sortOrder);
                break;
            case HOTEL_PROPERTY_CODE:

                selection = CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE + "=?";
                selectionArgs = new String[] { String.valueOf(uri.getPath().substring(8,uri.getPath().length())) };

                cursor = database.query(CollectionContract.CollectionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HOTELS:
                return CollectionContract.CollectionEntry.CONTENT_LIST_TYPE;
            case HOTEL_PROPERTY_CODE:
                return CollectionContract.CollectionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HOTELS:
                return insertHotel(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertHotel(Uri uri, ContentValues values) {

        String hotelPropertyCode = values.getAsString(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE);

        if (hotelPropertyCode == null) {
            throw new IllegalArgumentException("Hotel requires valid id");
        }

        String hotelName = values.getAsString(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME);

        if (hotelName == null) {
            throw new IllegalArgumentException("Hotel requires valid name");
        }

        String hotelCity = values.getAsString(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY);

        if (hotelCity == null) {
            throw new IllegalArgumentException("Hotel requires valid city");
        }

        String hotelCountry = values.getAsString(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY);

        if (hotelCountry == null) {
            throw new IllegalArgumentException("Hotel requires valid rating");
        }

        //If everything is correct, we proceed to write into the database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long newRowId = database.insert(CollectionContract.CollectionEntry.TABLE_NAME,null,values);

        if (newRowId != -1){
            Toast.makeText(getContext(), "The hotel was added successfully",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"The hotel couldn't be added as favorite",Toast.LENGTH_SHORT).show();
        }

        getContext().getContentResolver().notifyChange(uri,null);

        //return
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HOTELS:
                rowsDeleted = database.delete(CollectionContract.CollectionEntry.TABLE_NAME, selection, selectionArgs);
                database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + CollectionContract.CollectionEntry.TABLE_NAME + "'");
                break;
            case HOTEL_PROPERTY_CODE:
                selection = CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE + "=?";
                selectionArgs = new String[] { String.valueOf(uri.getPath().substring(8,uri.getPath().length())) };
                rowsDeleted = database.delete(CollectionContract.CollectionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


        if (rowsDeleted != 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }

        if (rowsDeleted != 0) {
            Toast.makeText(getContext(), "Hotel deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Hotel deletion failed", Toast.LENGTH_SHORT).show();
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HOTELS:
                return updateHotels(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updateHotels(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsUpdated = db.update(CollectionContract.CollectionEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0){
            Toast.makeText(getContext(),"Hotel was updated successfully",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"There was a problem during update",Toast.LENGTH_SHORT).show();
        }

        if (rowsUpdated != 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
