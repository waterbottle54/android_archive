package com.penelope.ecar.models;

public class Place {

    private final String code;      // 지역코드 (앞 두자리는 시도코드)
    private final String area1;     // 광역 지역명
    private final String area2;     // 기초 지역명
    private final String area3;     // 최하위 지역명

    public Place(String code, String area1, String area2, String area3) {
        this.code = code;
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
    }

    public String getCode() {
        return code;
    }

    public String getArea1() {
        return area1;
    }

    public String getArea2() {
        return area2;
    }

    public String getArea3() {
        return area3;
    }

    public String getName() {
        return area1 + " " + area2 + " " + area3;
    }

    @Override
    public String toString() {
        return "Place{" +
                "code='" + code + '\'' +
                ", area1='" + area1 + '\'' +
                ", area2='" + area2 + '\'' +
                ", area3='" + area3 + '\'' +
                '}';
    }
}
