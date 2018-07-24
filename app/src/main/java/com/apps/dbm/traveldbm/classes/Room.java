package com.apps.dbm.traveldbm.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Room implements Parcelable {

    private String roomDescription;
    //private List<Bitmap> roomPhotos;
    private String roomPrice;
    //private String roomType;
    //private String roomNumberOfBeds;
    //private String roomBedType;
    private String roomCode;


    public Room(String description,
                //List<Bitmap> photos,
                String price,
            //, String typeOfRoom
            //, String numberOfBeds
            //, String bedType
                String code){
        roomDescription = description;
        //roomPhotos = photos;
        roomPrice = price;
        //roomType = typeOfRoom;
        //roomNumberOfBeds = numberOfBeds;
        //roomBedType = bedType;
        roomCode = code;
    }

    private Room(Parcel in) {
        roomDescription = in.readString();
        roomPrice = in.readString();
        //roomType = in.readString();
        //roomNumberOfBeds = in.readString();
        //roomBedType = in.readString();
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
        //out.writeString(roomType);
        //out.writeString(roomNumberOfBeds);
        //out.writeString(roomBedType);
        out.writeString(roomCode);
    }

    public String getRoomDescription(){
        return roomDescription;
    }

    //public List<Bitmap> getRoomPhotos(){return roomPhotos;}

    public String getRoomPrice(){return roomPrice;}

    //public String getRoomType(){return roomType;}

    //public String getRoomNumberOfBeds(){return roomNumberOfBeds; }

    //public String getRoomBedType(){return roomBedType;}

    public String getRoomCode(){return roomCode;}
}
