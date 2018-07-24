package com.apps.dbm.traveldbm.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite implements Parcelable {

    private String favoritePropertyCode;
    private String favoriteName;
    private String favoriteAddress;
    private String favoritePhone;
    private String favoriteLocation;

    public Favorite(String propertyCode,String name, String address, String phone, String location){
        favoritePropertyCode = propertyCode;
        favoriteName = name;
        favoriteAddress = address;
        favoritePhone = phone;
        favoriteLocation = location;
    }

    public String getFavoritePropertyCode(){return favoritePropertyCode;}

    public String getFavoriteName(){ return favoriteName;}

    public String getFavoriteAddress(){return favoriteAddress;}

    public String getFavoritePhone(){return favoritePhone;}

    public String getFavoriteLocation(){return favoriteLocation;}


    private Favorite(Parcel in) {
        favoritePropertyCode = in.readString();
        favoriteName = in.readString();
        favoriteAddress = in.readString();
        favoritePhone = in.readString();
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
        out.writeString(favoriteAddress);
        out.writeString(favoritePhone);
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
