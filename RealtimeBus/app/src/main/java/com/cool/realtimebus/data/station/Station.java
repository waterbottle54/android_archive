package com.cool.realtimebus.data.station;

import java.io.Serializable;

public class Station implements Serializable {

    private final String id;
    private final String title;
    private final double latitude;
    private final double longitude;
    private final int cityCode;

    public Station(String id, String title, double latitude, double longitude, int cityCode) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityCode = cityCode;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCityCode() {
        return cityCode;
    }
}
