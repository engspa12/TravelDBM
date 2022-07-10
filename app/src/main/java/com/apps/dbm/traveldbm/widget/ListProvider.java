package com.apps.dbm.traveldbm.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.domain.Favorite;
import com.apps.dbm.traveldbm.data.CollectionContract;

import java.util.ArrayList;
import java.util.List;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private String[] projection = {
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_LATITUDE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_LONGITUDE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_URL,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_AMENITIES};

    private List<Favorite> favoritesList;
    private Context context;
    private int appWidgetId;


    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public List<Favorite> getListItems() {

        List<Favorite> list = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(CollectionContract.CollectionEntry.CONTENT_URI,projection,null,null,null);

        String propertyCode;
        String name;
        String latitude;
        String longitude;
        String country;
        String city;
        String address;
        String phone;
        String url;
        String amenities;
        String location;

        if (cursor != null) {
            if (cursor.getCount() != 0) {

                while (cursor.moveToNext()) {
                    propertyCode = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME));
                    latitude = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_LATITUDE));
                    longitude = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_LONGITUDE));
                    address = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS));
                    city = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY));
                    country = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY));
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE));
                    url = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_URL));
                    amenities = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_AMENITIES));

                    location = city + " - " + country;

                    list.add(new Favorite(propertyCode,name,latitude,longitude, address,city,country, phone,url,amenities, location));
                }

            }
        }

        cursor.close();

        return list;
    }

    public void updateList(){
        if(favoritesList != null) {
            favoritesList.clear();
        }
        favoritesList = getListItems();
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        updateList();

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return favoritesList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Favorite favoriteListItem = favoritesList.get(position);

        RemoteViews remoteViews;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);

        remoteViews.setTextViewText(R.id.widget_favorite_name, favoriteListItem.getFavoriteName());
        remoteViews.setTextViewText(R.id.widget_favorite_address, context.getString(R.string.address_placeholder,favoriteListItem.getFavoriteAddress()));
        remoteViews.setTextViewText(R.id.widget_favorite_phone, context.getString(R.string.phone_placeholder,favoriteListItem.getFavoritePhone()));
        remoteViews.setTextViewText(R.id.widget_favorite_location, context.getString(R.string.favorite_location_placeholder,favoriteListItem.getFavoriteLocation()));


        Intent fillInIntent = new Intent();

        fillInIntent.putExtra("favorite_property_code",favoriteListItem.getFavoritePropertyCode());
        fillInIntent.putExtra("favorite_name",favoriteListItem.getFavoriteName());
        fillInIntent.putExtra("favorite_latitude",favoriteListItem.getFavoriteLatitude());
        fillInIntent.putExtra("favorite_longitude",favoriteListItem.getFavoriteLongitude());
        fillInIntent.putExtra("favorite_address",favoriteListItem.getFavoriteAddress());
        fillInIntent.putExtra("favorite_city",favoriteListItem.getFavoriteCity());
        fillInIntent.putExtra("favorite_country",favoriteListItem.getFavoriteCountry());
        fillInIntent.putExtra("favorite_phone",favoriteListItem.getFavoritePhone());
        fillInIntent.putExtra("favorite_url",favoriteListItem.getFavoriteUrl());
        fillInIntent.putExtra("favorite_amenities",favoriteListItem.getFavoriteAmenities());

        remoteViews.setOnClickFillInIntent(R.id.list_item,fillInIntent);


        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}