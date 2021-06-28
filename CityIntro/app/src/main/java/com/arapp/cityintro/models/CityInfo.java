package com.arapp.cityintro.models;

/* Simple class that contains information about city */

public class CityInfo {

    private String name;
    private String desc1;
    private String desc2;
    private double lat;
    private double lon;

    public CityInfo(String name, String desc1, String desc2, double lat, double lon) {
        this.name = name;
        this.desc1 = desc1;
        this.desc2 = desc2;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getDesc1() {
        return desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
