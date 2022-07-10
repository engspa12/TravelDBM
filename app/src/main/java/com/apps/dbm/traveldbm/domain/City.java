package com.apps.dbm.traveldbm.domain;

public class City {

    private String cityName;
    private String cityLatitude;
    private String cityLongitude;
    private String cityCountry;
    private String cityProvince;

    public City(String name, String latitude, String longitude, String country, String province){
        cityName = name;
        cityLatitude = latitude;
        cityLongitude = longitude;
        cityCountry = country;
        cityProvince = province;
    }

    public String getCityName(){
        return cityName;
    }

    public String getCityLatitude(){
        return cityLatitude;
    }

    public String getCityLongitude(){
        return cityLongitude;
    }

    public String getCityCountry(){
        return cityCountry;
    }

    public String getCityProvince(){return cityProvince;}
}
