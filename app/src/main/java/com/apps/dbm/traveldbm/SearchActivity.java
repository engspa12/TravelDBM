package com.apps.dbm.traveldbm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.dbm.traveldbm.classes.City;
import com.apps.dbm.traveldbm.classes.Hotel;
import com.apps.dbm.traveldbm.datehelper.DatePickerFragment;
import com.apps.dbm.traveldbm.service.HotelRequestService;
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

    public static final String EXTENDED_DATA_STATUS =
            "com.apps.dbm.travel.STATUS";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.search_hotels_title));

        cityEditText = (EditText) findViewById(R.id.edit_text_city);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_container);
        linearLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        validCityEntered = false;

        getCitiesList();

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String city = cityEditText.getText().toString().trim();
                city = cityEditText.getText().toString().trim();
                if (checkInput(city)) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
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
                // Code to be executed when when the interstitial ad is closed.
                Intent intent = new Intent(SearchActivity.this, HotelRequestService.class);
                intent.setAction("hotel_general_data");
                intent.putExtra("city_name",city);
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


    public boolean checkInput(String cityInput) {
        if (!cityInput.isEmpty()) {
                if (cityInput.contains("0123456789") || cityInput.equals("")) {
                    Toast.makeText(this, "Enter a valid city", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    validCity(cityInput);
                    return checkAdditionalData();
                }
        } else {
            Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

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
                    Toast.makeText(SearchActivity.this, "Please, specify country", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    }

    public void validCity(String cityName) {

        //validCityEntered = false;
        int count =0;

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
                                // The 'which' argument contains the index position
                                // of the selected item
                                validCityEntered = true;
                                cityLat = listCitiesDuplicated.get(which).getCityLatitude();
                                cityLong = listCitiesDuplicated.get(which).getCityLongitude();
                                String city = cityEditText.getText().toString().trim();
                                if (checkAdditionalData()) {
                                    if (mInterstitialAd.isLoaded()) {
                                        mInterstitialAd.show();
                                    } else {
                                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                                    }
                                    //Intent intent = new Intent(SearchActivity.this, HotelRequestService.class);
                                    //intent.setAction("hotel_general_data");
                                    //intent.putExtra("city_latitude", cityLat);
                                    //intent.putExtra("city_longitude", cityLong);
                                    //intent.putExtra("check_in_date", checkInDateInput);
                                    //intent.putExtra("check_out_date", checkOutDateInput);
                                    //startService(intent);
                                    //progressBar.setVisibility(View.VISIBLE);
                                    //linearLayout.setVisibility(View.GONE);
                                }

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

        if(count == 0){
            Toast.makeText(SearchActivity.this, "The city called " + cityName
                    + " couldn't be found in the database ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        responseReceiver = new MyResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
        super.onStop();
    }

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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    public boolean checkDates() {
        if (checkInDate != null && checkOutDate != null) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                Date checkOut = sdf.parse(checkOutDate);
                Date checkIn = sdf.parse(checkInDate);
                if (checkOut.compareTo(checkIn) <= 0) {
                        if(isCheckIn){
                            checkInTextView.setText(getString(R.string.no_date_selected));
                            checkInDate = null;
                            Toast.makeText(this, "Check-in date must be before Check-out date. Choose Check-in date before " + sdf.format(checkOut), Toast.LENGTH_LONG).show();
                            DialogFragment newFragment = DatePickerFragment.newInstance(isCheckIn);
                            newFragment.show(getSupportFragmentManager(), "datePickerCheckOut");
                        } else {
                            checkOutTextView.setText(getString(R.string.no_date_selected));
                            checkOutDate = null;
                            Toast.makeText(this, "Check-out date must be after Check-in date. Choose Check-out date after " + sdf.format(checkIn), Toast.LENGTH_LONG).show();
                            DialogFragment newFragment = DatePickerFragment.newInstance(isCheckIn);
                            newFragment.show(getSupportFragmentManager(), "datePickerCheckOut");
                        }
                        return false;
                } else{
                    //Toast.makeText(this,"Dates entered are valid",Toast.LENGTH_LONG).show();
                    return true;
                }
            } catch (ParseException e) {
                Log.e(LOG, e.getMessage());
                return false;
            }
        } else {
            if(checkInDate == null && checkOutDate == null){
                Toast.makeText(this, "Select Check-in date and Check-out date", Toast.LENGTH_LONG).show();
            } else {
                if (checkInDate == null) {
                    Toast.makeText(this, "Select Check-in date", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Select Check-out date", Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }
    }


    private class MyResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            List<Hotel> hotelResultsList = intent.getParcelableArrayListExtra("hotel_list");
            progressBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            Intent intentResults = new Intent(SearchActivity.this,HotelResultsActivity.class);
            intentResults.putExtra("hotel_list",(ArrayList<Hotel>) hotelResultsList);
            startActivity(intentResults);
        }
    }
}
