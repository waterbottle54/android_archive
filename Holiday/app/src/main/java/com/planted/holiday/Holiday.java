package com.planted.holiday;

import java.util.Objects;

// 공휴일 클래스
public class Holiday {

    private final String name;      // 공휴일 이름
    private final int year;         // 년
    private final int month;        // 월
    private final int dayOfMonth;   // 일
    private final int dayOfWeek;    // 요일 (1:월 ~ 7:일)

    public Holiday(String name, int year, int month, int dayOfMonth, int dayOfWeek) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
    }

    public String getName() {
        return name;
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

    public int getDayOfWeek() {
        return dayOfWeek;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holiday holiday = (Holiday) o;
        return year == holiday.year && month == holiday.month && dayOfMonth == holiday.dayOfMonth && dayOfWeek == holiday.dayOfWeek && name.equals(holiday.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, month, dayOfMonth, dayOfWeek);
    }
}
