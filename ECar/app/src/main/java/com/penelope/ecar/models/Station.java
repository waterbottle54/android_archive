package com.penelope.ecar.models;

public class Station {

    private final String stationId;                 // 충전소 ID <statId>
    private final String name;                      // 충전소명 <statNm>
    private final String address;                   // 주소 <addr>
    private final double latitude;                  // 위도 <lat>
    private final double longitude;                 // 경도 <lng>
    private final int output;                       // 충전용량 (kW) <output>
    private final String locationDescription;       // 상세위치 설명 <location>
    private final String timeDescription;           // 이용시간 설명 <useTime>

    private final int chargerStatus;                // 충전기 상태 <stat>
    // 1: 통신이상, 2: 충전대기, 3: 충전중, 4: 운영중지, 5: 점검중, 9: 상태미확인

    public Station(String stationId, String name, String address, double latitude, double longitude,
                   int output, String locationDescription, String timeDescription, int chargerStatus) {
        this.stationId = stationId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.output = output;
        this.locationDescription = locationDescription;
        this.timeDescription = timeDescription;
        this.chargerStatus = chargerStatus;
    }

    public String getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
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

    public int getOutput() {
        return output;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getTimeDescription() {
        return timeDescription;
    }

    public int getChargerStatus() {
        return chargerStatus;
    }
}
