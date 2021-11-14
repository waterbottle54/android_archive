package com.penelope.weatherteacher.data.address;

public class Address {

    private final String fullAddress;   // 전체 주소
    private final String address1;      // 최상위 주소 (ex. 대전시)
    private final String address2;      // 2번째 레벨 주소 (ex. 유성구)
    private final String address3;      // 3번째 레벨 주소 (ex. --동)

    public Address(String fullAddress, String address1, String address2, String address3) {
        this.fullAddress = fullAddress;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }
}
