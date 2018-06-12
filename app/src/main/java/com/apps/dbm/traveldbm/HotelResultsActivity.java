package com.apps.dbm.traveldbm;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.dbm.traveldbm.adapter.HotelAdapter;
import com.apps.dbm.traveldbm.classes.Hotel;
import com.apps.dbm.traveldbm.classes.Room;
import com.apps.dbm.traveldbm.data.CollectionContract;
import com.apps.dbm.traveldbm.service.HotelRequestService;
import com.apps.dbm.traveldbm.widget.FavoriteAppWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class HotelResultsActivity extends AppCompatActivity implements HotelAdapter.ListItemClickListener{

    private static final String LOG = HotelResultsActivity.class.getSimpleName();

    private String[] projection = {
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS,
            CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE};

    private List<Hotel> listHotels;

    public static final String BROADCAST_ACTION = "com.apps.dbm.travel.BROADCAST";

    private RecyclerView recyclerView;
    private HotelAdapter mAdapter;

    private ResponseReceiver responseReceiver;

    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private int indexSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_results);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setTitle("Hotel Results");

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle("Hotel Results");

        Intent intent = getIntent();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_results);
        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView = (TextView) findViewById(R.id.results_empty_text_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_results);

        if(intent.hasExtra("hotel_list")){
            listHotels = intent.getParcelableArrayListExtra("hotel_list");
            if(listHotels.size() != 0) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);

                mEmptyTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);


                mAdapter = new HotelAdapter(listHotels,listHotels.size(),this);
                recyclerView.setAdapter(mAdapter);

            } else{
                //Log.v(LOG,"No hotels could be found for this city");
                mEmptyTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mEmptyTextView.setText("No hotels could be found for this city.");
            }
        }
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

    @Override
    public void onListItemClick(int clickedItemIndex, String typeAction) {
        indexSelected = clickedItemIndex;
        if(typeAction.equals("details")){
            mProgressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Intent serviceIntent = new Intent(HotelResultsActivity.this,HotelRequestService.class);
            serviceIntent.setAction("rooms_data");
            serviceIntent.putExtra("hotel_property_code",listHotels.get(clickedItemIndex).getHotelPropertyCode());
            serviceIntent.putExtra("check_in_date",listHotels.get(clickedItemIndex).getHotelCheckInDate());
            serviceIntent.putExtra("check_out_date",listHotels.get(clickedItemIndex).getHotelCheckOutDate());
            startService(serviceIntent);
        } else if(typeAction.equals("mark_as_favorite")){
            //Toast.makeText(this, "save hotel in favorites",Toast.LENGTH_SHORT).show();

            if(isNewFavorite(listHotels.get(clickedItemIndex))) {

                ContentValues values = new ContentValues();
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE, listHotels.get(clickedItemIndex).getHotelPropertyCode());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME, listHotels.get(clickedItemIndex).getHotelName());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_LATITUDE, listHotels.get(clickedItemIndex).getHotelLatitude());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_LONGITUDE, listHotels.get(clickedItemIndex).getHotelLongitude());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS, listHotels.get(clickedItemIndex).getHotelAddress());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY, listHotels.get(clickedItemIndex).getHotelCity());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY, listHotels.get(clickedItemIndex).getHotelCountry());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE, listHotels.get(clickedItemIndex).getHotelPhone());
                if(listHotels.get(clickedItemIndex).getHotelURL() != null) {
                    values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_URL, listHotels.get(clickedItemIndex).getHotelURL());
                } else{
                    values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_URL,"");
                }
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_AMENITIES, listHotels.get(clickedItemIndex).getHotelAmenities());

                getContentResolver().insert(CollectionContract.CollectionEntry.CONTENT_URI, values);

                updateWidgets();
            } else{
                Toast.makeText(this,"This hotel is already in your favorites",Toast.LENGTH_SHORT).show();
            }

        } else if(typeAction.equals("share_general_data")){
            //Toast.makeText(this, "share general data",Toast.LENGTH_SHORT).show();
            shareContent(clickedItemIndex);
        } else {
            Toast.makeText(this, "error, type action not recognized",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNewFavorite(Hotel hotel){

        Uri wantedUri = Uri.withAppendedPath(CollectionContract.CollectionEntry.CONTENT_URI, hotel.getHotelPropertyCode());
        boolean newFavorite;
        Cursor cursor = getContentResolver().query(wantedUri,projection,null,null,null);

        if (cursor.getCount() == 0) {
            newFavorite = true;
        } else{
            newFavorite = false;
        }

        cursor.close();

        return newFavorite;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
        super.onStop();
    }

    public void updateWidgets(){
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, FavoriteAppWidgetProvider.class));

        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

        FavoriteAppWidgetProvider.updateWidget(this,manager,appWidgetIds);
    }

    public void shareContent(int index){
        String mimeType = "text/plain";
        String title = "Share Content";
        String textToShare =
                "Name: " + listHotels.get(index).getHotelName() + "\n" +
                "Address: " + listHotels.get(index).getHotelAddress() + "\n" +
                "Phone: " + listHotels.get(index).getHotelPhone() + "\n" +
                "Location: " + listHotels.get(index).getHotelCity() + " - " + listHotels.get(index).getHotelCountry();

        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .startChooser();
    }


    private class ResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            mProgressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            List<Room> roomList = intent.getParcelableArrayListExtra("room_list");
            //progressBar.setVisibility(View.GONE);
            //linearLayout.setVisibility(View.VISIBLE);
            Intent intentDetail = new Intent(HotelResultsActivity.this, HotelDetailActivity.class);
            intentDetail.putExtra("hotel_selected",listHotels.get(indexSelected));
            intentDetail.putExtra("room_list",(ArrayList<Room>) roomList);
            startActivity(intentDetail);
        }
    }
}
