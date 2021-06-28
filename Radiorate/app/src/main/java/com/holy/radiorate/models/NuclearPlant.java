package com.holy.radiorate.models;

public class NuclearPlant {

    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String address;

    public NuclearPlant(String id, String name, double latitude, double longitude, String address) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
