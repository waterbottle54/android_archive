package com.holy.caloriecalendar.models;

// 칼로리 섭취 정보

public class Intake {

    private final int id;             // 섭취 정보의 고유키 (ex. 1, 2, 3, ...)
    private final int kcals;          // 섭취량 (ex. 350mL)
    private final String title;       // 섭취식품 이름
    private final int year;           // 섭취한 연도 (ex. 2020)
    private final int month;          // 섭취한 달 (0 부터 11 까지)
    private final int dayOfMonth;     // 섭취한 일자 (1부터 31까지)
    private final int hour;           // 섭취한 시각
    private final int minute;         // 섭취한 분

    public Intake(int id, String title, int kcals, int year, int month, int dayOfMonth, int hour, int minute) {
        this.id = id;
        this.kcals = kcals;
        this.title = title;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
    }

    public Intake(int kcals, String title, int year, int month, int dayOfMonth, int hour, int minute) {
        this.id = -1;
        this.kcals = kcals;
        this.title = title;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
    }

    public int getId() {
        return id;
    }

    public int getKcals() {
        return kcals;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
