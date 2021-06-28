package com.holy.caloriecalendar.models;

public class Food {

    private final int id;           // 음식 정보의 고유키 (ex. 1, 2, 3, ...)
    private final String title;     // 음식의 이름 (ex. 바나나, 피자, ...)
    private final int kcals;        // 음식의 칼로리 (ex. 150kcal)

    public Food(int id, String title, int kcals) {
        this.id = id;
        this.title = title;
        this.kcals = kcals;
    }

    public Food(String title, int kcals) {
        this.id = -1;
        this.title = title;
        this.kcals = kcals;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getKcals() {
        return kcals;
    }

}
