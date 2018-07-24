package com.apps.dbm.traveldbm.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite implements Parcelable {

    private String favoritePropertyCode;
    private String favoriteName;
    private String favoriteLatitude;
    private String favoriteLongitude;
    private String favoriteAddress;
    private String favoriteCity;
    private String favoriteCountry;
    private String favoritePhone;
    private String favoriteUrl;
    private String favoriteAmenities;
    private String favoriteLocation;

    public Favorite(String propertyCode,String name,String latitude,String longitude, String address, String city,
                    String country, String phone, String url, String amenities, String location){

        favoritePropertyCode = propertyCode;
        favoriteName = name;
        favoriteLatitude = latitude;
        favoriteLongitude = longitude;
        favoriteAddress = address;
        favoriteCity = city;
        favoriteCountry = country;
        favoritePhone = phone;
        favoriteUrl = url;
        favoriteAmenities = amenities;
        favoriteLocation = location;
    }

    public String getFavoritePropertyCode(){return favoritePropertyCode;}

    public String getFavoriteName(){ return favoriteName;}

    public String getFavoriteLatitude(){return favoriteLatitude;}

    public String getFavoriteLongitude(){return favoriteLongitude;}

    public String getFavoriteAddress(){return favoriteAddress;}

    public String getFavoriteCity(){return favoriteCity;}

    public String getFavoriteCountry(){return favoriteCountry;}

    public String getFavoritePhone(){return favoritePhone;}

    public String getFavoriteUrl(){return favoriteUrl;}

    public String getFavoriteAmenities(){return favoriteAmenities;}

    public String getFavoriteLocation(){return favoriteLocation;}


    private Favorite(Parcel in) {
        favoritePropertyCode = in.readString();
        favoriteName = in.readString();
        favoriteLatitude = in.readString();
        favoriteLongitude = in.readString();
        favoriteAddress = in.readString();
        favoriteCity = in.readString();
        favoriteCountry = in.readString();
        favoritePhone = in.readString();
        favoriteUrl = in.readString();
        favoriteAmenities = in.readString();
        favoriteLocation = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(favoritePropertyCode);
        out.writeString(favoriteName);
        out.writeString(favoriteLatitude);
        out.writeString(favoriteLongitude);
        out.writeString(favoriteAddress);
        out.writeString(favoriteCity);
        out.writeString(favoriteCountry);
        out.writeString(favoritePhone);
        out.writeString(favoriteUrl);
        out.writeString(favoriteAmenities);
        out.writeString(favoriteLocation);
    }

    public static final Parcelable.Creator<Favorite> CREATOR = new Parcelable.Creator<Favorite>() {
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };
}
