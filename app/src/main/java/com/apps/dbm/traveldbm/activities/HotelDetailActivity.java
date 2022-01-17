package com.apps.dbm.traveldbm.activities;

import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ShareCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apps.dbm.traveldbm.R;
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
    private TextView scrollViewEmptyTextView;

    private TextView hotelLocationTV;
    private TextView hotelAddressTV;
    private TextView hotelPhoneTV;
    private TextView hotelServicesTV;
    private TextView hotelWebsiteTV;
    private TextView hotelCheckInTV;
    private TextView hotelCheckOutTV;

    private TextView hotelNameTV;

    private RoomAdapter roomAdapter;

    private Button hotelWebsiteButton;

    private TextView emptyTextView;

    private FloatingActionButton detailFabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);


        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        scrollView = (NestedScrollView) findViewById(R.id.scroll_view_details);
        scrollViewEmptyTextView = (TextView) findViewById(R.id.scroll_view_empty_text_view);
        emptyTextView = (TextView) findViewById(R.id.rooms_empty_text_view);
        detailFabButton = (FloatingActionButton) findViewById(R.id.detail_fab_button);

        Intent intent = getIntent();

        if (intent.hasExtra("room_list")) {
            listRooms = intent.getParcelableArrayListExtra("room_list");
            hotel = intent.getParcelableExtra("hotel_selected");
            latitudeHotel = Double.valueOf(hotel.getHotelLatitude());
            longitudeHotel = Double.valueOf(hotel.getHotelLongitude());

            setTitle(hotel.getHotelName());

            scrollViewEmptyTextView.setVisibility(View.VISIBLE);

            //Share content
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
            hotelCheckInTV = (TextView) findViewById(R.id.hotel_check_in_text_view);
            hotelCheckOutTV = (TextView) findViewById(R.id.hotel_check_out_text_view);

            hotelWebsiteButton = (Button) findViewById(R.id.hotel_website_button);

            hotelWebsiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebPage(hotel.getHotelURL());
                }
            });

            //Google maps fragment setup
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } else if (intent.hasExtra("error_hotel_id")) {
            detailFabButton.setVisibility(View.GONE);
            scrollViewEmptyTextView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            scrollViewEmptyTextView.setText(intent.getStringExtra("error_hotel_id"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hotelPosition = new LatLng(latitudeHotel, longitudeHotel);
        mMap.addMarker(new MarkerOptions().position(hotelPosition).title(hotel.getHotelName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hotelPosition));

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
        if (url.contains("http")) {
            webpage = Uri.parse(url);
        } else {
            webpage = Uri.parse("http://" + url);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void populateUI() {
        hotelLocationTV.setText(getString(R.string.location_placeholder, hotel.getHotelCity(), hotel.getHotelCountry()));
        hotelAddressTV.setText(getString(R.string.address_placeholder, hotel.getHotelAddress()));
        hotelPhoneTV.setText(getString(R.string.phone_placeholder, hotel.getHotelPhone()));
        hotelServicesTV.setText(hotel.getHotelAmenities());
        hotelNameTV.setText(hotel.getHotelName());
        hotelCheckInTV.setText(getString(R.string.check_in_placeholder, hotel.getHotelCheckInDate()));
        hotelCheckOutTV.setText(getString(R.string.check_out_placeholder, hotel.getHotelCheckOutDate()));
        if (hotel.getHotelURL() != null) {
            if (!(hotel.getHotelURL().isEmpty())) {
                String website = hotel.getHotelURL();
                if (website.contains("http")) {
                    hotelWebsiteTV.setText(hotel.getHotelURL());
                } else {
                    hotelWebsiteTV.setText(getString(R.string.website_placeholder, hotel.getHotelURL()));
                }
                hotelWebsiteButton.setVisibility(View.VISIBLE);
            } else {
                hotelWebsiteTV.setText(getString(R.string.no_website_message));
                hotelWebsiteButton.setVisibility(View.GONE);
            }
        } else {
            hotelWebsiteTV.setText(getString(R.string.no_website_message));
            hotelWebsiteButton.setVisibility(View.GONE);
        }


        RecyclerView recyclerViewRooms = (RecyclerView) findViewById(R.id.recycler_view_rooms);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerViewRooms.setLayoutManager(linearLayout);

        recyclerViewRooms.setHasFixedSize(true);
        recyclerViewRooms.setNestedScrollingEnabled(false);

        roomAdapter = new RoomAdapter(listRooms.size(), listRooms, this);
        recyclerViewRooms.setAdapter(roomAdapter);

        if (listRooms.size() == 0) {
            recyclerViewRooms.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_rooms_message));
        } else {
            recyclerViewRooms.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
