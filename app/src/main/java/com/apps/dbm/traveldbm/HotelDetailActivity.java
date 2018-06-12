package com.apps.dbm.traveldbm;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apps.dbm.traveldbm.adapter.RoomAdapter;
import com.apps.dbm.traveldbm.classes.Hotel;
import com.apps.dbm.traveldbm.classes.Room;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class HotelDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<Room> listRooms;
    private Hotel hotel;

    private double latitudeHotel;
    private double longitudeHotel;

    private NestedScrollView scrollView;

    private TextView hotelLocationTV;
    private TextView hotelAddressTV;
    private TextView hotelPhoneTV;
    private TextView hotelServicesTV;
    private TextView hotelWebsiteTV;

    private TextView hotelNameTV;

    private RoomAdapter roomAdapter;

    private Button hotelWebsiteButton;

    private TextView emptyTextView;

    private FloatingActionButton detailFabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent.hasExtra("room_list")){
            listRooms = intent.getParcelableArrayListExtra("room_list");
            hotel = intent.getParcelableExtra("hotel_selected");
            latitudeHotel = Double.valueOf(hotel.getHotelLatitude());
            longitudeHotel = Double.valueOf(hotel.getHotelLongitude());
        }

        //setTitle(hotel.getHotelName());
        setTitle(hotel.getHotelName());

        scrollView = (NestedScrollView) findViewById(R.id.scroll_view_details);

        emptyTextView = (TextView) findViewById(R.id.rooms_empty_text_view);

        detailFabButton = (FloatingActionButton) findViewById(R.id.detail_fab_button);
        detailFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mimeType = "text/plain";
                String title = "Share Content";
                String textToShare =
                        "Name: " + hotel.getHotelName() + "\n" +
                                "Address: " + hotel.getHotelAddress() + "\n" +
                                "Phone: " + hotel.getHotelPhone() + "\n" +
                                "Location: " + hotel.getHotelCity() + " - " + hotel.getHotelCountry();

                ShareCompat.IntentBuilder.from(HotelDetailActivity.this)
                        .setChooserTitle(title)
                        .setType(mimeType)
                        .setText(textToShare)
                        .startChooser();
            }
        });

        hotelLocationTV = (TextView) findViewById(R.id.hotel_location_text_view);
        hotelAddressTV = (TextView) findViewById(R.id.hotel_address_text_view);
        hotelPhoneTV = (TextView) findViewById(R.id.hotel_phone_text_view);
        hotelServicesTV = (TextView) findViewById(R.id.hotel_services_text_view);
        hotelWebsiteTV = (TextView) findViewById(R.id.hotel_website_text_view);
        hotelNameTV = (TextView) findViewById(R.id.hotel_name_text_view);


        hotelWebsiteButton = (Button) findViewById(R.id.hotel_website_button);

        hotelWebsiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(hotel.getHotelURL());
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitudeHotel, longitudeHotel);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        populateUI();
    }

    public void openWebPage(String url) {

        Uri webpage;
        if(url.contains("http")){
            webpage = Uri.parse(url);
        } else{
            webpage = Uri.parse("http://" + url);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void populateUI(){

            hotelLocationTV.setText("Location: " + hotel.getHotelCity() + " - " + hotel.getHotelCountry());
            hotelAddressTV.setText("Address: " + hotel.getHotelAddress());
            hotelPhoneTV.setText("Phone: " + hotel.getHotelPhone());
            hotelServicesTV.setText(hotel.getHotelAmenities());
            hotelNameTV.setText(hotel.getHotelName());
        if(hotel.getHotelURL() != null) {
            if (!(hotel.getHotelURL().isEmpty())) {
                String website = hotel.getHotelURL();
                if (website.contains("http")) {
                    hotelWebsiteTV.setText(hotel.getHotelURL());
                } else {
                    hotelWebsiteTV.setText("http://" + hotel.getHotelURL());
                }
                hotelWebsiteButton.setVisibility(View.VISIBLE);
            } else {
                hotelWebsiteTV.setText("This hotel does not have a website yet.");
                hotelWebsiteButton.setVisibility(View.GONE);
            }
        }


        RecyclerView recyclerViewRooms = (RecyclerView) findViewById(R.id.recycler_view_rooms);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerViewRooms.setLayoutManager(linearLayout);

        recyclerViewRooms.setHasFixedSize(true);
        recyclerViewRooms.setNestedScrollingEnabled(false);

        roomAdapter = new RoomAdapter(listRooms.size(),listRooms);
        recyclerViewRooms.setAdapter(roomAdapter);

        if(listRooms.size() == 0) {
            recyclerViewRooms.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_rooms_message));
        } else{
            recyclerViewRooms.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
