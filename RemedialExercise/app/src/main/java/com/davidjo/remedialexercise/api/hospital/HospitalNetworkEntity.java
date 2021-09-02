package com.davidjo.remedialexercise.api.hospital;

public class HospitalNetworkEntity {

    private final String yadmNm;      // 병원명
    private final double XPos;        // 위도
    private final double YPos;        // 경도
    private final String telno;       // 전화번호
    private final String addr;        // 주소

    public HospitalNetworkEntity(String yadmNm, double XPos, double YPos, String telno, String addr) {
        this.yadmNm = yadmNm;
        this.XPos = XPos;
        this.YPos = YPos;
        this.telno = telno;
        this.addr = addr;
    }

    public String getYadmNm() {
        return yadmNm;
    }

    public double getXPos() {
        return XPos;
    }

    public double getYPos() {
        return YPos;
    }

    public String getTelno() {
        return telno;
    }

    public String getAddr() {
        return addr;
    }
}
