package com.myapp.workcalendar.models;


public class Work {

    // 아이디
    private final int id;
    // 일 이름
    private final String title;
    // 일한 시간
    private final int hours;
    // 시급
    private final int hourlyWage;
    // 날짜
    private final int year;
    private final int month;
    private final int date;

    public Work(String title, int hours, int hourlyWage, int year, int month, int date) {
        this.id = -1;
        this.title = title;
        this.hours = hours;
        this.hourlyWage = hourlyWage;
        this.year = year;
        this.month = month;
        this.date = date;
    }

    public Work(int id, String title, int hours, int hourlyWage, int year, int month, int date) {
        this.id = id;
        this.title = title;
        this.hours = hours;
        this.hourlyWage = hourlyWage;
        this.year = year;
        this.month = month;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getHours() {
        return hours;
    }

    public int getHourlyWage() {
        return hourlyWage;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }
}
