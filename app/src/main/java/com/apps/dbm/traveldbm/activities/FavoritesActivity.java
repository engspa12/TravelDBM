package com.apps.dbm.traveldbm.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.core.app.NavUtils;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.adapter.FavoriteAdapter;
import com.apps.dbm.traveldbm.classes.Favorite;
import com.apps.dbm.traveldbm.data.CollectionContract;
import com.apps.dbm.traveldbm.widget.FavoriteAppWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks
        , FavoriteAdapter.UpdateWidgetsItemsListener
        , FavoriteAdapter.OnGridOnClickListener{

    private static final String LOG = FavoritesActivity.class.getSimpleName();

    private RecyclerView favoritesRecyclerView;

    private TextView favoritesEmptyTextView;

    private static final int LOADER_ID = 21;

    private List<Favorite> listFavorites;

    private FavoriteAdapter mAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.favorite_hotels_title));

        favoritesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_favorites);

        favoritesEmptyTextView = (TextView) findViewById(R.id.favorites_empty_text_view);
        favoritesEmptyTextView.setVisibility(View.GONE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        favoritesRecyclerView.setLayoutManager(gridLayoutManager);

        favoritesRecyclerView.setHasFixedSize(true);

        listFavorites = new ArrayList<>();

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

            String propertyCode;
            String name;
            String latitude;
            String longitude;
            String address;
            String city;
            String country;
            String phone;
            String url;
            String amenities;

            String location;

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
                listFavorites.add(new Favorite(propertyCode,name,latitude,longitude, address,city,country, phone,url,amenities, location));
            }

            favoritesRecyclerView.setVisibility(View.VISIBLE);
            favoritesEmptyTextView.setVisibility(View.GONE);
            mAdapter = new FavoriteAdapter(listFavorites.size(),listFavorites,this,this,this);
            favoritesRecyclerView.setAdapter(mAdapter);
        }

        if(cursor.getCount() == 0){
            favoritesRecyclerView.setVisibility(View.GONE);
            favoritesEmptyTextView.setVisibility(View.VISIBLE);
            favoritesEmptyTextView.setText(getString(R.string.no_favorites_message));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        listFavorites.clear();
    }

    @Override
    public void updateWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, FavoriteAppWidgetProvider.class));

        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

        FavoriteAppWidgetProvider.updateWidget(this,manager,appWidgetIds);
    }

    @Override
    public void onGridClick(int positionItem) {
        //Start SearchActivity from favorites section
        Intent searchIntent = new Intent(this,SearchActivity.class);
        searchIntent.putExtra("favorite_property_code",listFavorites.get(positionItem).getFavoritePropertyCode());
        searchIntent.putExtra("favorite_name",listFavorites.get(positionItem).getFavoriteName());
        searchIntent.putExtra("favorite_latitude",listFavorites.get(positionItem).getFavoriteLatitude());
        searchIntent.putExtra("favorite_longitude",listFavorites.get(positionItem).getFavoriteLongitude());
        searchIntent.putExtra("favorite_address",listFavorites.get(positionItem).getFavoriteAddress());
        searchIntent.putExtra("favorite_city",listFavorites.get(positionItem).getFavoriteCity());
        searchIntent.putExtra("favorite_country",listFavorites.get(positionItem).getFavoriteCountry());
        searchIntent.putExtra("favorite_phone",listFavorites.get(positionItem).getFavoritePhone());
        searchIntent.putExtra("favorite_url",listFavorites.get(positionItem).getFavoriteUrl());
        searchIntent.putExtra("favorite_amenities",listFavorites.get(positionItem).getFavoriteAmenities());
        startActivity(searchIntent);
    }
}
