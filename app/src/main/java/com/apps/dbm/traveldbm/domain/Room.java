package com.apps.dbm.traveldbm.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {

    private String roomDescription;
    private String roomPrice;
    private String roomCode;


    public Room(String description,
                String price,
                String code){
        roomDescription = description;
        roomPrice = price;
        roomCode = code;
    }

    private Room(Parcel in) {
        roomDescription = in.readString();
        roomPrice = in.readString();
        roomCode = in.readString();
    }


    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(roomDescription);
        out.writeString(roomPrice);
        out.writeString(roomCode);
    }

    public String getRoomDescription(){
        return roomDescription;
    }

    public String getRoomPrice(){return roomPrice;}

    public String getRoomCode(){return roomCode;}
}
