package com.apps.dbm.traveldbm;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.apps.dbm.traveldbm.classes.Hotel;

import java.util.List;

public class HotelResultsActivity extends AppCompatActivity implements HotelAdapter.ListItemClickListener{

    private static final String LOG = HotelResultsActivity.class.getSimpleName();

    private List<Hotel> listHotels;

    private RecyclerView recyclerView;
    private HotelAdapter mAdapter;

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
        Toast.makeText(this,
                "action: " + typeAction + " and " + "position is: " + clickedItemIndex,
                Toast.LENGTH_SHORT).show();
    }
}
