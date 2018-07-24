package com.apps.dbm.traveldbm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.apps.dbm.traveldbm.adapter.HotelAdapter;
import com.apps.dbm.traveldbm.classes.Hotel;
import com.apps.dbm.traveldbm.classes.Room;
import com.apps.dbm.traveldbm.service.HotelRequestService;

import java.util.ArrayList;
import java.util.List;

public class HotelResultsActivity extends AppCompatActivity implements HotelAdapter.ListItemClickListener{

    private static final String LOG = HotelResultsActivity.class.getSimpleName();

    private List<Hotel> listHotels;

    public static final String BROADCAST_ACTION = "com.apps.dbm.travel.BROADCAST";

    private RecyclerView recyclerView;
    private HotelAdapter mAdapter;

    private ResponseReceiver responseReceiver;

    private int indexSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if(intent.hasExtra("hotel_list")){
            listHotels = intent.getParcelableArrayListExtra("hotel_list");
            if(listHotels.size() != 0) {
                recyclerView = (RecyclerView) findViewById(R.id.recycler_view_results);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);

                mAdapter = new HotelAdapter(listHotels,listHotels.size(),this);
                recyclerView.setAdapter(mAdapter);

            } else{
                Log.v(LOG,"No hotels could be found for this city");
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
            Intent serviceIntent = new Intent(HotelResultsActivity.this,HotelRequestService.class);
            serviceIntent.setAction("rooms_data");
            serviceIntent.putExtra("hotel_property_code",listHotels.get(clickedItemIndex).getHotelPropertyCode());
            serviceIntent.putExtra("check_in_date",listHotels.get(clickedItemIndex).getHotelCheckInDate());
            serviceIntent.putExtra("check_out_date",listHotels.get(clickedItemIndex).getHotelCheckOutDate());
            startService(serviceIntent);
        } else if(typeAction.equals("mark_as_favorite")){
            Toast.makeText(this, "save hotel in favorites",Toast.LENGTH_SHORT).show();
        } else if(typeAction.equals("share_general_data")){
            Toast.makeText(this, "share general data",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "error, type action not recognized",Toast.LENGTH_SHORT).show();
        }
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


    private class ResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
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
