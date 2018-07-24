package com.apps.dbm.traveldbm.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.classes.Favorite;
import com.apps.dbm.traveldbm.data.CollectionContract;

import java.util.ArrayList;
import java.util.List;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private String[] projection = {
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE};

    private List<Favorite> favoritesList;
    private Context context = null;
    private int appWidgetId;

    private boolean noFavorites = false;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

       //favoritesList = getListItems();
    }

    public List<Favorite> getListItems() {

        List<Favorite> list = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(CollectionContract.CollectionEntry.CONTENT_URI,projection,null,null,null);

        String propertyCode;
        String name;
        String country;
        String city;
        String address;
        String phone;
        String location;

        //List<Favorite> favoriteList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                //cursor.moveToFirst();
                noFavorites = false;

                while (cursor.moveToNext()) {
                    propertyCode = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME));
                    country = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY));
                    city = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY));
                    address = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS));
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE));

                    location = city + " - " + country;

                    list.add(new Favorite(propertyCode,name, address, phone, location));
                }

            } else{
                noFavorites = true;
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

        RemoteViews remoteView;

        if(!noFavorites) {
            remoteView = new RemoteViews(
                    context.getPackageName(), R.layout.widget_list_item);
            Favorite favoriteListItem = favoritesList.get(position);

            remoteView.setTextViewText(R.id.widget_favorite_name, favoriteListItem.getFavoriteName());
            remoteView.setTextViewText(R.id.widget_favorite_address, "Address: " + favoriteListItem.getFavoriteAddress());
            remoteView.setTextViewText(R.id.widget_favorite_phone, "Phone: " + favoriteListItem.getFavoritePhone());
            remoteView.setTextViewText(R.id.widget_favorite_location, "Location: " + favoriteListItem.getFavoriteLocation());
        } else{
            remoteView = new RemoteViews(
                    context.getPackageName(), R.layout.widget_empty_view);
        }

        return remoteView;
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