package com.apps.dbm.traveldbm.activities;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.dbm.traveldbm.AmadeusService;
import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.adapter.HotelAdapter;
import com.apps.dbm.traveldbm.classes.Hotel;
import com.apps.dbm.traveldbm.classes.Room;
import com.apps.dbm.traveldbm.data.CollectionContract;
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


        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.hotel_results_title));

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


                mAdapter = new HotelAdapter(listHotels,listHotels.size(),this,this);
                recyclerView.setAdapter(mAdapter);

            } else{
                //If the list if empty, inform the user that there are no hotels available in the city entered
                mEmptyTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mEmptyTextView.setText(getString(R.string.no_hotels_found_message));
            }
        } else if (intent.hasExtra("error_hotel_id")) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            mEmptyTextView.setText(intent.getStringExtra("error_hotel_id"));
        }
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

    @Override
    public void onListItemClick(int clickedItemIndex, String typeAction) {

        indexSelected = clickedItemIndex;

        if(typeAction.equals("details")){
            mProgressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Intent serviceIntent = new Intent(HotelResultsActivity.this, AmadeusService.class);
            serviceIntent.setAction("rooms_data");
            serviceIntent.putExtra("hotel_property_code",listHotels.get(clickedItemIndex).getHotelPropertyCode());
            serviceIntent.putExtra("check_in_date",listHotels.get(clickedItemIndex).getHotelCheckInDate());
            serviceIntent.putExtra("check_out_date",listHotels.get(clickedItemIndex).getHotelCheckOutDate());
            startService(serviceIntent);
        } else if(typeAction.equals("mark_as_favorite")){

            if(isNewFavorite(listHotels.get(clickedItemIndex))) {

                ContentValues values = new ContentValues();
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_PROPERTY_CODE, listHotels.get(clickedItemIndex).getHotelPropertyCode());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_NAME, listHotels.get(clickedItemIndex).getHotelName());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_LATITUDE, listHotels.get(clickedItemIndex).getHotelLatitude());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_LONGITUDE, listHotels.get(clickedItemIndex).getHotelLongitude());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_ADDRESS, listHotels.get(clickedItemIndex).getHotelAddress());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_CITY, listHotels.get(clickedItemIndex).getHotelCity());
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_COUNTRY, listHotels.get(clickedItemIndex).getHotelCountry());
                if(listHotels.get(clickedItemIndex).getHotelPhone() != null){
                    values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE, listHotels.get(clickedItemIndex).getHotelPhone());
                } else {
                    values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_PHONE, "");
                }

                if(listHotels.get(clickedItemIndex).getHotelURL() != null) {
                    values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_URL, listHotels.get(clickedItemIndex).getHotelURL());
                } else{
                    values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_URL,"");
                }
                values.put(CollectionContract.CollectionEntry.COLUMN_HOTEL_AMENITIES, listHotels.get(clickedItemIndex).getHotelAmenities());

                getContentResolver().insert(CollectionContract.CollectionEntry.CONTENT_URI, values);

                updateWidgets();
            } else{
                Toast.makeText(this, getString(R.string.already_in_favorites_message),Toast.LENGTH_SHORT).show();
            }

        } else if(typeAction.equals("share_general_data")){
            shareContent(clickedItemIndex);
        } else {
            Toast.makeText(this, getString(R.string.error_type_not_recognized_message),Toast.LENGTH_SHORT).show();
        }
    }

    //Find out if the hotel is already in your favorites list
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
        //Register Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        //Unregister Broadcast Receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
        super.onStop();
    }

    //Update widgets once a hotel is added to the favorites list
    public void updateWidgets(){
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, FavoriteAppWidgetProvider.class));

        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

        FavoriteAppWidgetProvider.updateWidget(this,manager,appWidgetIds);
    }


    //Share content of a particular hotel
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

        //Get rooms data of a particular hotel once the service HotelRequestService has finished
        //and start HotelDetailActivity passing in that data
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("room_list")) {
                mProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                //Get room list
                List<Room> roomList = intent.getParcelableArrayListExtra("room_list");
                Intent intentDetail = new Intent(HotelResultsActivity.this, HotelDetailActivity.class);
                intentDetail.putExtra("hotel_selected", listHotels.get(indexSelected));
                intentDetail.putExtra("room_list", (ArrayList<Room>) roomList);
                startActivity(intentDetail);
            } else if (intent.hasExtra("error_server_broadcast")){
                mProgressBar.setVisibility(View.GONE);
                mEmptyTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mEmptyTextView.setText(intent.getStringExtra("error_server_broadcast"));
            } else if(intent.hasExtra("error_server_hotel_id_broadcast")){
                //Error in the response in the hotel id
                mProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Intent errorIntent = new Intent(HotelResultsActivity.this, HotelDetailActivity.class);
                errorIntent.putExtra("error_hotel_id",intent.getStringExtra("error_server_hotel_id_broadcast"));
                startActivity(errorIntent);
            }
        }
    }
}
