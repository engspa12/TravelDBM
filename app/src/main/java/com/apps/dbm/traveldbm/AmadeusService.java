package com.apps.dbm.traveldbm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.HotelOffer;

import com.apps.dbm.traveldbm.classes.Hotel;
import com.apps.dbm.traveldbm.classes.Room;

import java.util.ArrayList;

import java.util.List;

public class AmadeusService extends IntentService {

    public static final String LOG = AmadeusService.class.getSimpleName();

    private static final String API_KEY_AMADEUS = BuildConfig.API_KEY_AMADEUS;

    private static final String API_SECRET = BuildConfig.API_SECRET;

    private List<Hotel> listOfHotels;

    private List<Room> listOfRooms;

    public static final String BROADCAST_ACTION = "com.apps.dbm.travel.BROADCAST";

    private static final int RADIUS = 100;

    private static final String LATITUDE_PARAM = "latitude";
    private static final String LONGITUDE_PARAM = "longitude";
    private static final String CHECK_IN_PARAM = "checkInDate";
    private static final String CHECK_OUT_PARAM = "checkOutDate";
    private static final String RADIUS_PARAM = "radius";
    private static final String RADIUS_UNITS_PARAM = "radiusUnit";
    private static final String BEST_RATE_ONLY_PARAM = "bestRateOnly";

    private static final String HOTEL_ID_PARAM = "hotelId";

    private String checkInDate;
    private String checkOutDate;

    private boolean isHotelGeneralInformation;

    //private Amadeus amadeus;

    public AmadeusService() {
        super("AmadeusService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null){
            String queryAction = intent.getAction();
            checkInDate = intent.getStringExtra("check_in_date");
            checkOutDate = intent.getStringExtra("check_out_date");
            if(queryAction.equals("hotel_general_data")) {
                isHotelGeneralInformation = true;
                String cityLatitude = intent.getStringExtra("city_latitude");
                String cityLongitude = intent.getStringExtra("city_longitude");
                listOfHotels = new ArrayList<>();
                retrieveHotelsData(cityLatitude, cityLongitude);
            } else if(queryAction.equals("rooms_data")){
                isHotelGeneralInformation = false;
                String hotelPropertyCode = intent.getStringExtra("hotel_property_code");
                listOfRooms = new ArrayList<>();
                retrieveRoomsData(hotelPropertyCode);
            } else{
                Log.e(LOG,getString(R.string.action_not_found));
            }

        }

    }

    public void retrieveHotelsData(String cityLatitude, String cityLongitude){

        Amadeus amadeus = Amadeus
                .builder(API_KEY_AMADEUS, API_SECRET)
                .build();

       try {
            HotelOffer[] hotels = amadeus.shopping.hotelOffers
                    .get(Params.with(LATITUDE_PARAM, cityLatitude)
                            .and(LONGITUDE_PARAM, cityLongitude)
                            .and(CHECK_IN_PARAM, checkInDate)
                            .and(CHECK_OUT_PARAM, checkOutDate)
                            .and(RADIUS_PARAM, RADIUS)
                            .and(RADIUS_UNITS_PARAM, "KM")
                            .and(BEST_RATE_ONLY_PARAM, false));


            if(hotels != null && hotels.length > 0) {

                for (int i = 0; i < hotels.length; i++) {

                    String hotelPropertyCode = hotels[i].getHotel().getHotelId();
                    String hotelName = hotels[i].getHotel().getName();
                    String hotelLatitude = String.valueOf(hotels[i].getHotel().getLatitude());
                    String hotelLongitude = String.valueOf(hotels[i].getHotel().getLongitude());

                    String hotelCompleteAddress;

                    String hotelCity = " " + hotels[i].getHotel().getAddress().getCityName();
                    String hotelCountry = " " + hotels[i].getHotel().getAddress().getCountryCode();
                    String[] address = hotels[i].getHotel().getAddress().getLines();

                    String hotelPostalCode;
                    String hotelStateCode;

                    try {
                        hotelPostalCode = " " + hotels[i].getHotel().getAddress().getPostalCode();
                        if(hotels[i].getHotel().getAddress().getPostalCode() == null){
                            hotelPostalCode = "";
                        }
                    } catch (Exception e) {
                        hotelPostalCode = "";
                    }

                    try {
                        hotelStateCode = " " + hotels[i].getHotel().getAddress().getStateCode();
                        if(hotels[i].getHotel().getAddress().getStateCode() == null){
                            hotelStateCode = "";
                        }
                    } catch (Exception e) {
                        hotelStateCode = "";
                    }


                    hotelCompleteAddress = address[0]
                            + hotelCity
                            + hotelStateCode
                            + hotelPostalCode
                            + hotelCountry;

                    HotelOffer.Offer[] offers = hotels[i].getOffers();

                    String hotelMinPrice;

                    if (offers.length > 0) {

                        double price = 10000000;

                        for (HotelOffer.Offer offer : offers) {
                            try {
                                String total = offer.getPrice().getTotal();
                                double priceOffer = Double.parseDouble(total);
                                if (priceOffer < price) {
                                    price = priceOffer;
                                }
                            } catch (Exception e){
                                Log.e(LOG, e.getMessage());
                            }
                        }

                        if (price == 10000000){
                            hotelMinPrice = "** Contact hotel for price details.";
                        } else{
                            hotelMinPrice = price
                                    + " " + offers[0].getPrice().getCurrency();
                        }

                    } else {
                        hotelMinPrice = "";
                    }

                    String hotelPhone;

                    try {
                        hotelPhone = hotels[i].getHotel().getContact().getPhone();
                        if(hotelPhone != null){
                            hotelPhone = hotels[i].getHotel().getContact().getPhone();
                        } else {
                            hotelPhone = "";
                        }
                    } catch(Exception e){
                        hotelPhone = "";
                    }


                    String amenitiesArray[] = hotels[i].getHotel().getAmenities();

                    StringBuilder builder = new StringBuilder();

                    String hotelAmenities;

                    if(amenitiesArray != null){
                        for (int j = 0; j < amenitiesArray.length; j++) {
                            String amenity = amenitiesArray[j];
                            builder.append("* ");
                            builder.append(amenity);
                            builder.append("\n");
                        }

                        if (builder.length() != 0) {
                            builder.deleteCharAt(builder.length() - 1);
                        }

                        hotelAmenities = builder.toString();
                    } else{
                        hotelAmenities = "There is not amenities details available.";
                    }

                    listOfHotels.add(new Hotel(hotelPropertyCode, hotelName, hotelLatitude, hotelLongitude, hotelCompleteAddress,
                            hotelMinPrice, hotelCity, hotelCountry, hotelPhone, null, hotelAmenities, checkInDate, checkOutDate));
                }

                sendHotelDataToActivity();

            } else{
                sendErrorMessage();
            }

        } catch (Exception e){
            e.printStackTrace();
            sendErrorMessage();
        }

    }

    public void retrieveRoomsData(String hotelCode){

        Amadeus amadeus = Amadeus
                .builder(API_KEY_AMADEUS, API_SECRET)
                .build();

        try {
            HotelOffer hotelOffer = amadeus.shopping.hotelOffersByHotel
                    .get(Params.with(HOTEL_ID_PARAM, hotelCode)
                    .and(CHECK_IN_PARAM, checkInDate)
                    .and(CHECK_OUT_PARAM, checkOutDate));

            HotelOffer.Offer[] rooms = hotelOffer.getOffers();

            if(rooms != null && rooms.length > 0) {


                for (int i = 0; i < rooms.length; i++) {

                    String roomDescription = rooms[i].getRoom().getDescription().getText();

                    String total = rooms[i].getPrice().getTotal();
                    String currency = rooms[i].getPrice().getCurrency();

                    String roomPrice;

                    if(total == null && currency == null){
                        roomPrice = "** Contact hotel for room price.";
                    } else if(total == null){
                        roomPrice = "** Contact hotel for room price.";
                    } else if(currency == null){
                        roomPrice = "** Contact hotel for room price.";
                    } else {
                         roomPrice = total
                                + " " + currency;
                    }

                    String roomCode = rooms[i].getRateCode() + rooms[i].getRoom().getType();

                    listOfRooms.add(new Room(roomDescription, roomPrice, roomCode));
                }

                sendHotelDataToActivity();

            } else {

                sendHotelIdErrorMessage();
            }

        } catch (Exception e){
                e.printStackTrace();
                sendHotelIdErrorMessage();
        }


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
        errorIntent.putExtra("error_server_broadcast", getString(R.string.error_message_broadcast));
        LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
    }

    //Error in the server response
    public void sendHotelIdErrorMessage(){
        Intent errorIntent = new Intent(BROADCAST_ACTION);
        errorIntent.putExtra("error_server_hotel_id_broadcast", getString(R.string.error_message_hotel_id_broadcast));
        LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
    }
}
