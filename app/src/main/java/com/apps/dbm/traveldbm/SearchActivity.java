package com.apps.dbm.traveldbm;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apps.dbm.traveldbm.classes.City;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText cityEditText;
    private EditText ratingEditText;

    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private String cityId;
    private List<City> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle(getString(R.string.search_hotels_title));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityEditText = (EditText) findViewById(R.id.edit_text_city);
        ratingEditText = (EditText) findViewById(R.id.edit_text_stars);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_container);
        linearLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        cityId = null;

        getCitiesList();

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEditText.getText().toString().trim();
                String rating = ratingEditText.getText().toString().trim();
                if(checkInput(city,rating)) {
                    if(validCity(city)) {
                        Intent intent = new Intent(SearchActivity.this, HotelRequestService.class);
                        intent.putExtra("city_name", city);
                        intent.putExtra("city_id",cityId);
                        intent.putExtra("rating", rating);
                        startService(intent);
                    } else{
                        Toast.makeText(SearchActivity.this,"No hotels could be found for city: " + city,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public boolean checkInput(String cityInput, String ratingInput){
        if(!cityInput.isEmpty()) {
            if(!ratingInput.isEmpty()) {
                if (cityInput.contains("0123456789") || cityInput.equals("")) {
                    Toast.makeText(this, "Enter a valid city", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (ratingInput.equals("-")) {
                        return true;
                    } else {
                        if (Integer.valueOf(ratingInput) < 6 && Integer.valueOf(ratingInput) > 1) {
                            return true;
                        } else {
                            Toast.makeText(this, "Enter a rating between 2 and 5 stars, or enter a \'-\'", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
            } else{
                Toast.makeText(this, "Enter a rating between 2 and 5 stars, or enter a \'-\'", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else{
            Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean validCity(String cityName){

        boolean validCityEntered = false;

        if(cities != null) {
            for (int i = 0; i < cities.size(); i++) {
                if (cityName.equals(cities.get(i).getCityName())) {
                    validCityEntered = true;
                    cityId = cities.get(i).getCityId();
                }
            }
        }

        return validCityEntered;
    }


    public void getCitiesList(){
        cities = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("city_list.csv")));
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                cities.add(new City(nextLine[0],nextLine[1]));
            }
        } catch (Exception e){
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
}
