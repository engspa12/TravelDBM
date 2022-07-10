package com.apps.dbm.traveldbm.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.dbm.traveldbm.service.AmadeusService;
import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.domain.City;
import com.apps.dbm.traveldbm.domain.Hotel;
import com.apps.dbm.traveldbm.domain.Room;
import com.apps.dbm.traveldbm.datehelper.DatePickerFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements DatePickerFragment.DateListener {

    private static final String LOG = SearchActivity.class.getSimpleName();

    public static final String BROADCAST_ACTION = "com.apps.dbm.travel.BROADCAST";

    private EditText cityEditText;

    private MyResponseReceiver responseReceiver;

    private boolean validCityEntered;

    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private String cityLat;
    private String cityLong;
    private List<City> cities;

    private List<City> listCitiesDuplicated;

    private int checkInDay;
    private int checkInMonth;
    private int checkInYear;

    private int checkOutDay;
    private int checkOutMonth;
    private int checkOutYear;

    private String checkInDateInput;
    private String checkOutDateInput;

    private TextView checkInTextView;
    private TextView checkOutTextView;

    private String checkInDate;
    private String checkOutDate;

    private boolean isCheckIn;

    private String city;

    private InterstitialAd mInterstitialAd;

    private TextView doublePurposeTextView;

    private boolean isNewSearch;

    private String propertyCodeFav;

    private Hotel hotelFav;

    private TextView errorServerTextView;

    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        cityEditText = (EditText) findViewById(R.id.edit_text_city);
        doublePurposeTextView = (TextView) findViewById(R.id.double_purpose_text_view);
        errorServerTextView = (TextView) findViewById(R.id.error_server_text_view);
        errorServerTextView.setVisibility(View.GONE);
        searchButton = (Button) findViewById(R.id.search_button);

        validCityEntered = false;

        if(intent.hasExtra("favorite_property_code")){
            propertyCodeFav = intent.getStringExtra("favorite_property_code");
            String favName = intent.getStringExtra("favorite_name");
            setTitle(favName);
            hotelFav = new Hotel(propertyCodeFav,
                    favName,
                    intent.getStringExtra("favorite_latitude"),
                    intent.getStringExtra("favorite_longitude"),
                    intent.getStringExtra("favorite_address"),
                    null,
                    intent.getStringExtra("favorite_city"),
                    intent.getStringExtra("favorite_country"),
                    intent.getStringExtra("favorite_phone"),
                    intent.getStringExtra("favorite_url"),
                    intent.getStringExtra("favorite_amenities"),null,null);

            city = intent.getStringExtra("favorite_city");
            validCityEntered = true;
            cityEditText.setVisibility(View.GONE);
            doublePurposeTextView.setText(getString(R.string.hotel_name_placeholder,favName));
            searchButton.setText(getString(R.string.check_hotel_details_button_text));
            isNewSearch = false;
        } else {
            setTitle(getString(R.string.search_hotels_title));
            cityEditText.setVisibility(View.VISIBLE);
            isNewSearch = true;
        }

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_container);
        linearLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        getCitiesList();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNewSearch) {
                    city = cityEditText.getText().toString().trim();
                    if (checkInput(city)) {
                        if (mInterstitialAd.isLoaded()) {
                            //Show ad and be ready to execute HotelRequestService
                            //This code executes in case of no duplication problem in city name
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", getString(R.string.ad_not_loaded_yet_message));
                        }
                    }
                } else{
                    if(checkAdditionalData()) {
                        hotelFav.setHotelCheckInDate(checkInDateInput);
                        hotelFav.setHotelCheckOutDate(checkOutDateInput);
                        Intent serviceIntent = new Intent(SearchActivity.this, AmadeusService.class);
                        serviceIntent.setAction("rooms_data");
                        serviceIntent.putExtra("hotel_property_code",propertyCodeFav);
                        serviceIntent.putExtra("check_in_date", checkInDateInput);
                        serviceIntent.putExtra("check_out_date", checkOutDateInput);
                        startService(serviceIntent);
                        progressBar.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                    }
                }
            }
        });


        Button checkInButton = (Button) findViewById(R.id.check_in_button);
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckIn = true;
                DialogFragment newFragment = DatePickerFragment.newInstance(isCheckIn);
                newFragment.show(getSupportFragmentManager(), "datePickerCheckIn");
            }
        });


        Button checkOutButton = (Button) findViewById(R.id.check_out_button);
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckIn = false;
                DialogFragment newFragment = DatePickerFragment.newInstance(isCheckIn);
                newFragment.show(getSupportFragmentManager(), "datePickerCheckOut");
            }
        });

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Intent intent = new Intent(SearchActivity.this, AmadeusService.class);
                intent.setAction("hotel_general_data");
                intent.putExtra("city_name", city);
                intent.putExtra("city_latitude", cityLat);
                intent.putExtra("city_longitude", cityLong);
                intent.putExtra("check_in_date", checkInDateInput);
                intent.putExtra("check_out_date", checkOutDateInput);
                startService(intent);
                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
        });
    }

    //Check all the input values are correct
    public boolean checkInput(String cityInput) {
        if (!cityInput.isEmpty()) {
                if (cityInput.contains("0123456789") || cityInput.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_valid_city_message), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    validateCity(cityInput);
                    return checkAdditionalData();
                }
        } else {
            Toast.makeText(this, getString(R.string.enter_city_name_message), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Validate city name as input
    public void validateCity(String cityName) {

        int count =0;

        //Check city names match
        if (cities != null) {
            for (int i = 0; i < cities.size(); i++) {
                if (cityName.equals(cities.get(i).getCityName())) {
                    validCityEntered = true;
                    cityLat = cities.get(i).getCityLatitude();
                    cityLong = cities.get(i).getCityLongitude();
                    count++;
                }
            }
        }

        if(count != 1 && count != 0) {
            //There are two or more cities with the same name
            validCityEntered = false;
            if (cities != null) {
                listCitiesDuplicated = new ArrayList<>();
                for (int j = 0; j < cities.size(); j++) {
                    if (cityName.equals(cities.get(j).getCityName())) {
                        String cityLatitude = cities.get(j).getCityLatitude();
                        String cityLongitude = cities.get(j).getCityLongitude();
                        String cityCountry = cities.get(j).getCityCountry();
                        String cityProvince = cities.get(j).getCityProvince();
                        listCitiesDuplicated.add(new City(cities.get(j).getCityName(),cityLatitude,cityLongitude,cityCountry,cityProvince));
                    }
                }

                List<String> listCitiesString = new ArrayList<>();

                for(int k = 0; k < listCitiesDuplicated.size(); k++){
                    String cityString = listCitiesDuplicated.get(k).getCityName()
                            + " - " + listCitiesDuplicated.get(k).getCityCountry()
                            + " - " + listCitiesDuplicated.get(k).getCityProvince();
                    listCitiesString.add(cityString);
                }

                String[] cities = listCitiesString.toArray(new String[listCitiesString.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.pick_city)
                        .setItems(cities, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //The 'which' argument contains the index position
                                //of the selected item.
                                //The city won't be valid unless the user selects and specify a country from
                                //the list of duplicate cities.
                                validCityEntered = true;
                                cityLat = listCitiesDuplicated.get(which).getCityLatitude();
                                cityLong = listCitiesDuplicated.get(which).getCityLongitude();
                                if (checkAdditionalData()) {
                                    if (mInterstitialAd.isLoaded()) {
                                        //Show ad and be ready to execute HotelRequestService
                                        //This code executes when there are 2 or more cities with the same name
                                        mInterstitialAd.show();
                                    } else {
                                        Log.d("TAG", getString(R.string.ad_not_loaded_yet_message));
                                    }
                                }

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

        if(count == 0){
            Toast.makeText(SearchActivity.this, getString(R.string.city_not_found_message,cityName), Toast.LENGTH_SHORT).show();
        }
    }

    //Check dates and show specify country message in case of duplication in the name of the city
    public boolean checkAdditionalData(){

        if (validCityEntered) {
            if (checkDates()) {
                if(checkInMonth < 10){
                    checkInDateInput = checkInYear + "-0" + checkInMonth + "-" + checkInDay;
                } else{
                    checkInDateInput = checkInYear + "-" + checkInMonth + "-" + checkInDay;
                }

                if(checkOutMonth < 10){
                    checkOutDateInput = checkOutYear + "-0" + checkOutMonth + "-" + checkOutDay;
                }
                else {
                    checkOutDateInput = checkOutYear + "-" + checkOutMonth + "-" + checkOutDay;
                }
                return true;
            } else {
                return false;
            }
        } else {
            if(listCitiesDuplicated != null) {
                if (listCitiesDuplicated.size() > 1) {
                    Toast.makeText(SearchActivity.this, getString(R.string.specify_country_message), Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Register Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        responseReceiver = new MyResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        //Unregister Broadcast Receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
        super.onStop();
    }

    //Get cities using CSV file
    public void getCitiesList() {
        cities = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("cities_lat_long.csv")));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                cities.add(new City(nextLine[0], nextLine[2], nextLine[3], nextLine[5], nextLine[8]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.csv_file_not_found), Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //Set dates in Activity via the DialogFragment
    @Override
    public void setDate(String textDate,int dayValue, int monthValue, int yearValue) {

        if (isCheckIn) {
            checkInDate = textDate;
            checkInDay = dayValue;
            checkInMonth = monthValue;
            checkInYear = yearValue;
            checkInTextView = (TextView) findViewById(R.id.check_in_text_view);
            checkInTextView.setText(textDate);
        } else {
            checkOutDate = textDate;
            checkOutDay = dayValue;
            checkOutMonth = monthValue;
            checkOutYear = yearValue;
            checkOutTextView = (TextView) findViewById(R.id.check_out_text_view);
            checkOutTextView.setText(textDate);
        }

        checkDates();

    }

    //Check if dates for check-in and check-out are correct
    public boolean checkDates() {
        if (checkInDate != null && checkOutDate != null) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                Date checkOut = sdf.parse(checkOutDate);
                Date checkIn = sdf.parse(checkInDate);
                //Check if check-out date is earlier than check-in and inform user about the issue
                if (checkOut.compareTo(checkIn) <= 0) {
                        if(isCheckIn){
                            checkInTextView.setText(getString(R.string.no_date_selected));
                            checkInDate = null;
                            Toast.makeText(this, getString(R.string.check_in_error_message) + " " + sdf.format(checkOut) + ".", Toast.LENGTH_LONG).show();
                            DialogFragment newFragment = DatePickerFragment.newInstance(isCheckIn);
                            newFragment.show(getSupportFragmentManager(), "datePicker");
                        } else {
                            checkOutTextView.setText(getString(R.string.no_date_selected));
                            checkOutDate = null;
                            Toast.makeText(this, getString(R.string.check_out_error_message) + " " + sdf.format(checkIn) + ".", Toast.LENGTH_LONG).show();
                            DialogFragment newFragment = DatePickerFragment.newInstance(isCheckIn);
                            newFragment.show(getSupportFragmentManager(), "datePicker");
                        }
                        return false;
                } else{
                    return true;
                }
            } catch (ParseException e) {
                Log.e(LOG, e.getMessage());
                return false;
            }
        } else {
            if(checkInDate == null && checkOutDate == null){
                Toast.makeText(this, getString(R.string.select_check_in_check_out), Toast.LENGTH_LONG).show();
            } else {
                if (checkInDate == null) {
                    Toast.makeText(this, getString(R.string.select_check_in), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.select_check_out), Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }
    }


    private class MyResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("hotel_list")) {
                //Go to HotelResultsActivity to show the list of hotels available
                List<Hotel> hotelResultsList = intent.getParcelableArrayListExtra("hotel_list");
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Intent intentResults = new Intent(SearchActivity.this, HotelResultsActivity.class);
                intentResults.putExtra("hotel_list", (ArrayList<Hotel>) hotelResultsList);
                startActivity(intentResults);
            } else if (intent.hasExtra("room_list") && !isNewSearch){
                //Go directly to HotelDetailActivity when searching from user favorites list
                List<Room> roomList = intent.getParcelableArrayListExtra("room_list");
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Intent intentDetail = new Intent(SearchActivity.this, HotelDetailActivity.class);
                intentDetail.putExtra("hotel_selected", hotelFav);
                intentDetail.putExtra("room_list",(ArrayList<Room>) roomList);
                startActivity(intentDetail);
            } else if(intent.hasExtra("error_server_broadcast")){
                //Error in the response in the hotels list
                progressBar.setVisibility(View.GONE);
                errorServerTextView.setVisibility(View.VISIBLE);
                errorServerTextView.setText(intent.getStringExtra("error_server_broadcast"));
            }
            else if(intent.hasExtra("error_server_hotel_id_broadcast")){
                //Error in the response in the hotel id
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Intent errorIntent = new Intent(SearchActivity.this, HotelDetailActivity.class);
                errorIntent.putExtra("error_hotel_id",intent.getStringExtra("error_server_hotel_id_broadcast"));
                startActivity(errorIntent);
            }
        }
    }
}
