package com.apps.dbm.traveldbm.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class CollectionContract {

    public static final String CONTENT_AUTHORITY = "com.apps.dbm.traveldbm";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HOTELS = "hotels";

    private CollectionContract(){}

    public static final class CollectionEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HOTELS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_HOTELS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_HOTELS;

        public final static String TABLE_NAME = "hotels";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_HOTEL_ID = "hotel_id";
        public final static String COLUMN_HOTEL_NAME = "hotel_name";
        public final static String COLUMN_HOTEL_CITY = "hotel_city";
        public final static String COLUMN_HOTEL_COUNTRY = "hotel_country";
        public final static String COLUMN_HOTEL_RATING = "hotel_rating";
    }
}
