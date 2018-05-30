package com.apps.dbm.traveldbm.classes;

import android.graphics.Bitmap;

import java.util.List;

public class Room {
    private String roomDescription;
    private List<Bitmap> roomPhotos;
    private String roomType;
    private List<String> roomServices;

    public Room(String description, List<Bitmap> photos,String type, List<String> services){
        roomDescription = description;
        roomPhotos = photos;
        roomType = type;
        roomServices = services;
    }

    public String getRoomDescription(){
        return roomDescription;
    }

    public List<Bitmap> getRoomPhotos(){
        return roomPhotos;
    }

    public String getRoomType(){
        return roomType;
    }

    public List<String> getRoomServices(){
        return roomServices;
    }
}
