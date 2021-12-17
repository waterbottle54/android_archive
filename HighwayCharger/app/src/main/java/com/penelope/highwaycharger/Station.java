package com.penelope.highwaycharger;

import com.google.gson.annotations.SerializedName;

public class Station {  // 주유소 정보

    @SerializedName("수소차 충전소")  // SerializedName = json 에서의 명칭
    private final String supportHydrogen; // 수소차 충전소 여부 (O, X)

    @SerializedName("전기차 충전소")
    private final String supportElectric; // 전기차 충전소 여부 (O, X)

    @SerializedName("전화번호")
    private final String phone;

    @SerializedName("주유소명")
    private final String name;

    // 생성자
    public Station(String supportHydrogen, String supportElectric, String phone, String name) {
        this.supportHydrogen = supportHydrogen;
        this.supportElectric = supportElectric;
        this.phone = phone;
        this.name = name;
    }

    // Setter / Getter
    public String getSupportHydrogen() {
        return supportHydrogen;
    }

    public String getSupportElectric() {
        return supportElectric;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

}
