package com.holy.batterystation.models;

public class BatteryStation {

    private final int stationId;
    private final String address;
    private final int chargeType;
    private final int cpStatus;
    private final int cpType;
    private final String name;
    private final double latitude;
    private final double longitude;

    public BatteryStation(int stationId, String address, int chargeType, int cpStatus, int cpType, String name, double latitude, double longitude) {
        this.stationId = stationId;
        this.address = address;
        this.chargeType = chargeType;
        this.cpStatus = cpStatus;
        this.cpType = cpType;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getStationId() {
        return stationId;
    }

    public String getAddress() {
        return address;
    }

    public int getChargeType() {
        return chargeType;
    }

    public int getCpStatus() {
        return cpStatus;
    }

    public int getCpType() {
        return cpType;
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

    @Override
    public String toString() {
        return "BatteryStation{" +
                "stationId=" + stationId +
                ", address='" + address + '\'' +
                ", chargeType=" + chargeType +
                ", cpStatus=" + cpStatus +
                ", cpType=" + cpType +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
