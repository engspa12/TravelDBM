package com.apps.dbm.traveldbm.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Hotel implements Parcelable {

    private String hotelPropertyCode;
    private String hotelName;
    private String hotelLatitude;
    private String hotelLongitude;
    private String hotelAddress;
    private String hotelMinPrice;
    //private List<Bitmap> hotelPhotos;
    //private List<Room> hotelRooms;
    private String hotelCity;
    private String hotelCountry;
    //private List<Review> hotelReviews;
    private String hotelPhone;
    private String hotelURL;
    private String hotelAmenities;


    public Hotel(String propertyCode,String name,String latitude,String longitude,String address,String minPrice,
                 //List<Bitmap> photos,
                 //List<Room> rooms,
                 String city, String country,
                 //List<Review> reviews,
                 String phone, String url, String amenities){
        hotelPropertyCode = propertyCode;
        hotelName = name;
        hotelLatitude = latitude;
        hotelLongitude = longitude;
        hotelAddress = address;
        hotelMinPrice = minPrice;
        //hotelPhotos = photos;
        //hotelRooms = rooms;
        hotelCity = city;
        hotelCountry = country;
        //hotelReviews = reviews;
        hotelPhone = phone;
        hotelURL = url;
        hotelAmenities = amenities;
    }

    private Hotel(Parcel in) {
        hotelPropertyCode = in.readString();
        hotelName = in.readString();
        hotelLatitude = in.readString();
        hotelLongitude = in.readString();
        hotelAddress = in.readString();
        hotelMinPrice = in.readString();
        hotelCity = in.readString();
        hotelCountry = in.readString();
        hotelPhone = in.readString();
        hotelURL = in.readString();
        hotelAmenities = in.readString();
    }


    public static final Parcelable.Creator<Hotel> CREATOR = new Parcelable.Creator<Hotel>() {
        public Hotel createFromParcel(Parcel in) {
            return new Hotel(in);
        }

        public Hotel[] newArray(int size) {
            return new Hotel[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(hotelPropertyCode);
        out.writeString(hotelName);
        out.writeString(hotelLatitude);
        out.writeString(hotelLongitude);
        out.writeString(hotelAddress);
        out.writeString(hotelMinPrice);
        out.writeString(hotelCity);
        out.writeString(hotelCountry);
        out.writeString(hotelPhone);
        out.writeString(hotelURL);
        out.writeString(hotelAmenities);
    }

    public String getHotelPropertyCode(){
        return hotelPropertyCode;
    }

    public String getHotelName(){
        return hotelName;
    }

    public String getHotelLatitude(){return hotelLatitude;}

    public String getHotelLongitude(){return hotelLongitude;}

    public String getHotelAddress(){
        return hotelAddress;
    }

    public String getHotelMinPrice(){ return hotelMinPrice;}

    //public List<Bitmap> getHotelPhotos(){
    //   return hotelPhotos;
    //}

    //public List<Room> getHotelRooms(){
    //  return  hotelRooms;
    //}

    public String getHotelCity(){
        return hotelCity;
    }

    public String getHotelCountry(){return hotelCountry;}

    //public List<Review> getHotelReviews(){
    //   return hotelReviews;
    //}

    public String getHotelPhone(){return hotelPhone;}

    public String getHotelURL(){
        return hotelURL;
    }

    public String getHotelAmenities(){return hotelAmenities;}
}