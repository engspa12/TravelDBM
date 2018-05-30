package com.apps.dbm.traveldbm.classes;

import android.graphics.Bitmap;

import java.util.List;

public class Hotel {

    private String hotelId;
    private String hotelName;
    private String hotelAddress;
    private int hotelRating;
    private List<Bitmap> hotelPhotos;
    private List<Room> hotelRooms;
    private String hotelLocation;
    private List<Review> hotelReviews;
    private String hotelURL;


    public Hotel(String id,String name,String address,int rating, List<Bitmap> photos, List<Room> rooms, String location, List<Review> reviews, String url){
        hotelId = id;
        hotelName = name;
        hotelAddress = address;
        hotelRating = rating;
        hotelPhotos = photos;
        hotelRooms = rooms;
        hotelLocation = location;
        hotelReviews = reviews;
        hotelURL = url;
    }

    public String getHotelId(){
        return hotelId;
    }

    public String getHotelName(){
        return hotelName;
    }

    public String getHotelAddress(){
        return hotelAddress;
    }

    public int getHotelRating(){
        return hotelRating;
    }

    public List<Bitmap> getHotelPhotos(){
        return hotelPhotos;
    }

    public List<Room> getHotelRooms(){
        return  hotelRooms;
    }

    public String getHotelLocation(){
        return hotelLocation;
    }

    public List<Review> getHotelReviews(){
        return hotelReviews;
    }

    public String getHotelURL(){
        return hotelURL;
    }

}