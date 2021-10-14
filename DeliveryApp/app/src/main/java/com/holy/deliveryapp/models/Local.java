package com.holy.deliveryapp.models;

public class Local {

    private final String id;
    private final String categoryName;
    private final String subcategoryName;
    private final double distance;      // in meters
    private final String phone;
    private final String name;
    private final String url;
    private final String address;
    private final double latitude;
    private final double longitude;

    public Local(String id, String categoryName, String subcategoryName, double distance, String phone, String name, String url, String address, double latitude, double longitude) {
        this.id = id;
        this.categoryName = categoryName;
        this.subcategoryName = subcategoryName;
        this.distance = distance;
        this.phone = phone;
        this.name = name;
        this.url = url;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public double getDistance() {
        return distance;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Local{" +
                "id='" + id + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", distance=" + distance +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
