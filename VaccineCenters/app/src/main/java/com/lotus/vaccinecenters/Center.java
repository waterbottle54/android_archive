package com.lotus.vaccinecenters;

public class Center {       // 코로나 백신 접종센터

    private String name;        // 센터명
    private String facility;    // 기관명
    private String address;     // 주소
    private String phone;       // 전화번호
    private double latitude;    // 위도
    private double longitude;   // 경도

    // 생성자
    public Center(String name, String facility, String address, String phone, double latitude, double longitude) {
        this.name = name;
        this.facility = facility;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter 와 Setter


    public String getName() {
        return name;
    }

    public String getFacility() {
        return facility;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
