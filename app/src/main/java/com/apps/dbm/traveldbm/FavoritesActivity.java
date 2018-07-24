package com.apps.dbm.traveldbm;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.apps.dbm.traveldbm.adapter.FavoriteAdapter;
import com.apps.dbm.traveldbm.classes.Favorite;
import com.apps.dbm.traveldbm.data.CollectionContract;
import com.apps.dbm.traveldbm.widget.FavoriteAppWidgetProvider;
import com.apps.dbm.traveldbm.widget.WidgetService;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks
        , FavoriteAdapter.UpdateWidgetsItemsListener {


    private static final String LOG = FavoritesActivity.class.getSimpleName();

    private RecyclerView favoritesRecyclerView;

    private TextView favoritesEmptyTextView;

    private static final int LOADER_ID = 21;

    private List<Favorite> listFavorites;

    private FavoriteAdapter mAdapter;

    private String[] projection = {
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Favorite Hotels");

        favoritesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_favorites);

        favoritesEmptyTextView = (TextView) findViewById(R.id.favorites_empty_text_view);
        favoritesEmptyTextView.setVisibility(View.GONE);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //favoritesRecyclerView.setLayoutManager(linearLayoutManager);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        favoritesRecyclerView.setLayoutManager(gridLayoutManager);

        favoritesRecyclerView.setHasFixedSize(true);

        listFavorites = new ArrayList<>();

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, CollectionContract.CollectionEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

        if(listFavorites != null){
            listFavorites.clear();
        }
        Cursor cursor = (Cursor) data;
        if(cursor != null && cursor.getCount() != 0) {

            cursor.moveToFirst();

            String propertyCode = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE));

            String location = city + " - " + country;

            listFavorites.add(new Favorite(propertyCode,name, address, phone, location));

            while (cursor.moveToNext()) {
                propertyCode = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE));
                name = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME));
                country = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY));
                city = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY));
                address = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS));
                phone = cursor.getString(cursor.getColumnIndexOrThrow(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE));

                location = city + " - " + country;

                listFavorites.add(new Favorite(propertyCode,name, address, phone, location));
            }

            favoritesRecyclerView.setVisibility(View.VISIBLE);
            favoritesEmptyTextView.setVisibility(View.GONE);
            mAdapter = new FavoriteAdapter(listFavorites.size(),listFavorites,this,this);
            favoritesRecyclerView.setAdapter(mAdapter);
        }

        if(cursor.getCount() == 0){
            favoritesRecyclerView.setVisibility(View.GONE);
            favoritesEmptyTextView.setVisibility(View.VISIBLE);
            favoritesEmptyTextView.setText(getString(R.string.no_favorites_message));
        }


        //getSupportLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        listFavorites.clear();
    }

    @Override
    public void updateWidgets() {
        //Log.v(LOG,"Update Widgets");
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, FavoriteAppWidgetProvider.class));

        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

        FavoriteAppWidgetProvider.updateWidget(this,manager,appWidgetIds);
    }
}
