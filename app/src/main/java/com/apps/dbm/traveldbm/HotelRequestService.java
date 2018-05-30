package com.apps.dbm.traveldbm;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apps.dbm.traveldbm.classes.Hotel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HotelRequestService extends IntentService {

    private RequestQueue mRequestQueue;

    private List<Hotel> listOfHotels;

    public static final String TAG = "MyTag";

    private static final String LOG = HotelRequestService.class.getSimpleName();

    private static final String APPLICATION_ID = "XXXXXXX";

    private static final String APPLICATION_KEY = "XXXXXXXXXXXXXXXXXXXX";

    public HotelRequestService() {
        super("HotelRequestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            String cityName = intent.getStringExtra("city_name");
            String cityId = intent.getStringExtra("city_id");
            String rating = intent.getStringExtra("rating");
            Log.v(LOG,cityName + " and " + cityId + " and " + rating);
        }
    }

    public void getDataFromHttpUrlUsingJSON(String url){

        //if(page == 1) {
        listOfHotels.clear();
        // }
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");

                            // for(int i=0;i<NUM_GRID_ITEMS;i++) {
                            //   JSONObject movie = results.getJSONObject(i);
                            //   int id = movie.getInt(getString(R.string.movie_id));
                            //  String title = movie.getString(getString(R.string.movie_title));
                            //  String synopsis = movie.getString(getString(R.string.movie_synopsis));
                            //  double rating = movie.getDouble(getString(R.string.movie_rating));
                            //  String releaseDate = movie.getString(getString(R.string.movie_release_date));
                            // String posterPath = movie.getString(getString(R.string.movie_poster_path));

                            //   listOfHotels.add(new MovieItem(id,title,synopsis,rating,releaseDate,BASE_POSTER_URL + posterPath));
                            // }
                            //if(page==5) {
                            //  mAdapter = new MoviesAdapter(NUM_TOTAL, MainActivity.this, listOfHotels,MainActivity.this);
                            //  mMoviesGrid.setAdapter(mAdapter);
                            //  mMoviesGrid.setVisibility(View.VISIBLE);
                            //  emptyTextView.setVisibility(View.GONE);
                            // page=1;
                            //} else{
                            //    page++;
                            //   startArrangement(sortValue);
                            //}

                        } catch( JSONException e){
                            Log.e(LOG,e.getMessage());
                        }
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

    public URL buildUrl(String sortBy, int page){
        Uri movieQueryUri = Uri.parse("BASE_URL" + sortBy).buildUpon()
                .appendQueryParameter("API_KEY_PARAM","API_KEY" )
                .appendQueryParameter("LANGUAGE_PARAM", "LANGUAGE")
                .appendQueryParameter("PAGE_PARAM",String.valueOf(page))
                .build();
        try {
            URL movieQueryUrl = new URL(movieQueryUri.toString());
            return movieQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startArrangement(String sort){
        //URL url = buildUrl(sort, page);
        //getDataFromHttpUrlUsingJSON(url.toString());
    }
}
