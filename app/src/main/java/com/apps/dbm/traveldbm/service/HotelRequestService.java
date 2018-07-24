package com.apps.dbm.traveldbm.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apps.dbm.traveldbm.BuildConfig;
import com.apps.dbm.traveldbm.classes.Hotel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HotelRequestService extends IntentService {

    private RequestQueue mRequestQueue;

    private List<Hotel> listOfHotels;

    public static final String TAG = "MyTag";

    private static final String LOG = HotelRequestService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "com.apps.dbm.travel.BROADCAST";

    private static final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/hotels/search-circle";


    private static final String API_KEY = BuildConfig.API_KEY;
    private static final int RADIUS = 75;
    private static final int NUMBER_OF_RESULTS = 30;

    private static final String API_KEY_PARAM = "apikey";
    private static final String LATITUDE_PARAM = "latitude";
    private static final String LONGITUDE_PARAM = "longitude";
    private static final String RADIUS_PARAM = "radius";
    private static final String CHECK_IN_PARAM = "check_in";
    private static final String CHECK_OUT_PARAM = "check_out";
    private static final String NUMBER_OF_RESULTS_PARAM = "number_of_results";

    public HotelRequestService() {
        super("HotelRequestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            //String cityName = intent.getStringExtra("city_name");
            String cityLatitude = intent.getStringExtra("city_latitude");
            String cityLongitude = intent.getStringExtra("city_longitude");
            String checkInDate = intent.getStringExtra("check_in_date");
            String checkOutDate = intent.getStringExtra("check_out_date");
            listOfHotels = new ArrayList<>();
            mRequestQueue = Volley.newRequestQueue(this);
            URL url = buildUrl(cityLatitude,cityLongitude,checkInDate,checkOutDate);
            getDataFromHttpUrlUsingJSON(url.toString());
        }
    }

    public void getDataFromHttpUrlUsingJSON(String url){

        listOfHotels.clear();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");

                            for(int i=0;i<results.length();i++) {
                               JSONObject hotel = results.getJSONObject(i);

                               String propertyCode = hotel.getString("property_code");
                               String name = hotel.getString("property_name");

                               JSONObject location = hotel.getJSONObject("location");
                               String latitude = location.getString("latitude");
                               String longitude = location.getString("longitude");


                               JSONObject addressObject = hotel.getJSONObject("address");
                               String city = null;
                               String country = null;
                               String address;
                               try {
                                   city = addressObject.getString("city");
                                   country = addressObject.getString("country");
                                   String region = addressObject.getString("region");
                                   address = addressObject.getString("line1")
                                           + " " + addressObject.getString("city")
                                           + " " + region
                                           + " " + addressObject.getString("postal_code")
                                           + " " + addressObject.getString("country");
                               } catch(JSONException e){
                                   address = addressObject.getString("line1")
                                           + " " + addressObject.getString("city")
                                           + " " + addressObject.getString("postal_code")
                                           + " " + addressObject.getString("country");
                               }

                               JSONObject priceObject = hotel.getJSONObject("total_price");
                               String minPrice = priceObject.getString("amount")
                                       + " " + priceObject.getString("currency");

                               JSONArray contacts = hotel.getJSONArray("contacts");
                               String phone = null;
                               String url = null;
                               for(int k=0;k<contacts.length();k++) {
                                   JSONObject Object = contacts.getJSONObject(k);
                                   String contactType = Object.getString("type");

                                   if(contactType.equals("PHONE")) {
                                           phone = Object.getString("detail");
                                   }

                                   if(contactType.equals("URL")){
                                           url = Object.getString("detail");
                                   }
                               }

                               JSONArray amenitiesArray = hotel.getJSONArray("amenities");

                               StringBuilder builder = new StringBuilder();
                               for(int j=0;j<amenitiesArray.length();j++){
                                   JSONObject amenityObject = amenitiesArray.getJSONObject(j);
                                   String amenity = amenityObject.getString("description");
                                   builder.append("* ");
                                   builder.append(amenity);
                                   builder.append("\n");
                               }

                               if(builder.length() != 0) {
                                   builder.deleteCharAt(builder.length() - 1);
                               }
                               String amenities = builder.toString();

                            listOfHotels.add(new Hotel(propertyCode,name,latitude,longitude,address,
                                    minPrice,city,country,phone,url,amenities));
                            }

                        } catch( JSONException e){
                            Log.e(LOG,e.getMessage());
                        }
                        sendHotelDataToActivity();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG,error.getMessage());
                    }
                });

        jsonObjectRequest.setTag(TAG);

        mRequestQueue.add(jsonObjectRequest);
    }

    public void sendHotelDataToActivity(){
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("hotel_list",(ArrayList<Hotel>) listOfHotels);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public URL buildUrl(String latitude, String longitude, String checkIn, String checkOut){
        Uri movieQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LATITUDE_PARAM, latitude)
                .appendQueryParameter(LONGITUDE_PARAM,longitude)
                .appendQueryParameter(RADIUS_PARAM,String.valueOf(RADIUS))
                .appendQueryParameter(CHECK_IN_PARAM,checkIn)
                .appendQueryParameter(CHECK_OUT_PARAM,checkOut)
                .appendQueryParameter(NUMBER_OF_RESULTS_PARAM,String.valueOf(NUMBER_OF_RESULTS))
                .build();
        try {
            URL movieQueryUrl = new URL(movieQueryUri.toString());
            return movieQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
