package com.apps.dbm.traveldbm.classes;

public class City {

    private String cityName;
    private String cityId;

    public City(String name, String id){
        cityName = name;
        cityId = id;
    }

    public String getCityName(){
        return cityName;
    }

    public String getCityId(){
        return cityId;
    }
}
