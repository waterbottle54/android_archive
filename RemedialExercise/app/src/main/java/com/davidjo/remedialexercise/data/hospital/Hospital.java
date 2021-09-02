package com.davidjo.remedialexercise.data.hospital;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hospital_table")
public class Hospital {

    @PrimaryKey
    @NonNull
    private final String name;             // 병원명
    private final double longitude;        // 위도
    private final double latitude;         // 경도
    private final String tel;              // 전화번호
    private final String address;          // 주소

    public Hospital(@NonNull String name, double longitude, double latitude, String tel, String address) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.tel = tel;
        this.address = address;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTel() {
        return tel;
    }

    public String getAddress() {
        return address;
    }
}
