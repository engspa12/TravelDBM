package com.apps.dbm.traveldbm.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apps.dbm.traveldbm.BuildConfig;
import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.domain.Hotel;
import com.apps.dbm.traveldbm.domain.Room;

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

    private List<Room> listOfRooms;

    public static final String TAG = "MyTag";

    private static final String LOG = HotelRequestService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "com.apps.dbm.travel.BROADCAST";

    private static final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/hotels/search-circle";

    private static final String BASE_URL_ROOMS = "https://api.sandbox.amadeus.com/v1.2/hotels/";


    private static final String API_KEY = BuildConfig.API_KEY;
    private static final int RADIUS = 80;
    private static final int NUMBER_OF_RESULTS = 50;


    private static final String API_KEY_PARAM = "apikey";
    private static final String LATITUDE_PARAM = "latitude";
    private static final String LONGITUDE_PARAM = "longitude";
    private static final String RADIUS_PARAM = "radius";
    private static final String CHECK_IN_PARAM = "check_in";
    private static final String CHECK_OUT_PARAM = "check_out";
    private static final String NUMBER_OF_RESULTS_PARAM = "number_of_results";

    private String checkInDate;
    private String checkOutDate;

    private String tempUrl;

    private boolean isHotelGeneralInformation;

    private int counterTry;

    public HotelRequestService() {
        super("HotelRequestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Counter to retry requests before showing the user an error message
        counterTry = 0;
        mRequestQueue = Volley.newRequestQueue(this);
        if (intent != null){
            String queryAction = intent.getAction();
            checkInDate = intent.getStringExtra("check_in_date");
            checkOutDate = intent.getStringExtra("check_out_date");
            if(queryAction.equals("hotel_general_data")) {
                isHotelGeneralInformation = true;
                String cityLatitude = intent.getStringExtra("city_latitude");
                String cityLongitude = intent.getStringExtra("city_longitude");
                listOfHotels = new ArrayList<>();
                URL url = buildUrl(cityLatitude, cityLongitude, checkInDate, checkOutDate);
                getDataFromHttpUrlUsingJSON(url.toString());
            } else if(queryAction.equals("rooms_data")){
                isHotelGeneralInformation = false;
                String hotelPropertyCode = intent.getStringExtra("hotel_property_code");
                listOfRooms = new ArrayList<>();
                URL urlAdditional = buildUrlForRooms(hotelPropertyCode, checkInDate, checkOutDate);
                getDataFromHttpUrlUsingJSON(urlAdditional.toString());
            } else{
                Log.e(LOG,getString(R.string.action_not_found));
            }

        }
    }

    public void getDataFromHttpUrlUsingJSON(String url){

        tempUrl = url;

        if(listOfHotels != null) {
            listOfHotels.clear();
        }

        if(listOfRooms != null){
            listOfRooms.clear();
        }

        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if(isHotelGeneralInformation) {
                            try {
                                JSONArray results = response.getJSONArray("results");

                                for (int i = 0; i < results.length(); i++) {
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
                                    } catch (JSONException e) {
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
                                    for (int k = 0; k < contacts.length(); k++) {
                                        JSONObject Object = contacts.getJSONObject(k);
                                        String contactType = Object.getString("type");

                                        if (contactType.equals("PHONE")) {
                                            phone = Object.getString("detail");
                                        }

                                        if (contactType.equals("URL")) {
                                            url = Object.getString("detail");
                                        }
                                    }

                                    JSONArray amenitiesArray = hotel.getJSONArray("amenities");

                                    StringBuilder builder = new StringBuilder();
                                    for (int j = 0; j < amenitiesArray.length(); j++) {
                                        JSONObject amenityObject = amenitiesArray.getJSONObject(j);
                                        String amenity = amenityObject.getString("description");
                                        builder.append("* ");
                                        builder.append(amenity);
                                        builder.append("\n");
                                    }

                                    if (builder.length() != 0) {
                                        builder.deleteCharAt(builder.length() - 1);
                                    }
                                    String amenities = builder.toString();

                                    listOfHotels.add(new Hotel(propertyCode, name, latitude, longitude, address,
                                            minPrice, city, country, phone, url, amenities, checkInDate, checkOutDate));
                                }

                            } catch (JSONException e) {
                                Log.e(LOG, e.getMessage());
                            }
                        } else{
                            try{
                                JSONArray rooms = response.getJSONArray("rooms");
                                for(int i=0;i<rooms.length();i++) {
                                    JSONObject room = rooms.getJSONObject(i);
                                    JSONArray roomDescriptionArray = room.getJSONArray("descriptions");
                                    if(roomDescriptionArray.length() > 1) {
                                        String roomCode = room.getString("booking_code");

                                        JSONObject roomPriceObject = room.getJSONObject("total_amount");
                                        String roomPrice = roomPriceObject.getString("amount")
                                                + " " + roomPriceObject.getString("currency");


                                        StringBuilder descriptionBuilder = new StringBuilder();
                                        for (int j = 0; j < roomDescriptionArray.length(); j++) {
                                            String descriptionItem = roomDescriptionArray.getString(j);
                                            descriptionBuilder.append("* ");
                                            descriptionBuilder.append(descriptionItem);
                                            descriptionBuilder.append("\n\n");
                                        }

                                        if (descriptionBuilder.length() != 0) {
                                            descriptionBuilder.deleteCharAt(descriptionBuilder.length() - 1);
                                            descriptionBuilder.deleteCharAt(descriptionBuilder.length() - 1);
                                        }

                                        String roomDescription = descriptionBuilder.toString();

                                        listOfRooms.add(new Room(roomDescription, roomPrice, roomCode));
                                    }
                                }
                            } catch (JSONException e){
                                Log.e(LOG, e.getMessage());
                            }
                        }
                        sendHotelDataToActivity();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG,getString(R.string.error_retrieving_data_message));
                        counterTry++;
                        if(counterTry <= 3) {
                            getDataFromHttpUrlUsingJSON(tempUrl);
                        } else{
                            sendErrorMessage();
                        }

                    }
                });

        jsonObjectRequest.setTag(TAG);

        mRequestQueue.add(jsonObjectRequest);
    }

    //Use LocalBroadcastManager to send data to activities
    public void sendHotelDataToActivity(){

        Intent intent = new Intent(BROADCAST_ACTION);

        if(isHotelGeneralInformation) {
            intent.putExtra("hotel_list", (ArrayList<Hotel>) listOfHotels);
        } else {
            intent.putExtra("room_list", (ArrayList<Room>) listOfRooms);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Error in the server response
    public void sendErrorMessage(){
        Intent errorIntent = new Intent(BROADCAST_ACTION);
        errorIntent.putExtra("error_server", getString(R.string.error_message_broadcast));
        LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
    }

    //Create URL for an api request
    public URL buildUrl(String latitude, String longitude, String checkIn, String checkOut){
        Uri hotelQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LATITUDE_PARAM, latitude)
                .appendQueryParameter(LONGITUDE_PARAM,longitude)
                .appendQueryParameter(RADIUS_PARAM,String.valueOf(RADIUS))
                .appendQueryParameter(CHECK_IN_PARAM,checkIn)
                .appendQueryParameter(CHECK_OUT_PARAM,checkOut)
                .appendQueryParameter(NUMBER_OF_RESULTS_PARAM,String.valueOf(NUMBER_OF_RESULTS))
                .build();
        try {
            URL hotelQueryUrl = new URL(hotelQueryUri.toString());
            return hotelQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Create URL for the rooms of an specified hotel
    public URL buildUrlForRooms(String propertyCode, String checkIn, String checkOut){
        Uri roomQueryUri = Uri.parse(BASE_URL_ROOMS + propertyCode).buildUpon()
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(CHECK_IN_PARAM,checkIn)
                .appendQueryParameter(CHECK_OUT_PARAM,checkOut)
                .build();
        try {
            URL roomQueryUrl = new URL(roomQueryUri.toString());
            return roomQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
