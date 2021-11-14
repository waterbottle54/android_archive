package com.penelope.weatherteacher.utils;

import android.content.res.Resources;

import com.penelope.weatherteacher.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class TimeUtils {

    // 시간, 시간 문자열 메소드

    public static LocalDateTime getLocalDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String getBaseTimeString(LocalDateTime ldt) {
        int hour = ldt.getHour();
        int minute = ldt.getMinute();
        return String.format(Locale.getDefault(), "%02d%02d", hour, minute);
    }

    public static String getBaseDateString(LocalDateTime ldt) {
        int year = ldt.getYear();
        int month = ldt.getMonthValue();
        int dayOfMonth = ldt.getDayOfMonth();
        return String.format(Locale.getDefault(), "%d%02d%02d", year, month, dayOfMonth);
    }

    public static String getDateTimeString(Resources res, LocalDateTime ldt) {
        return String.format(Locale.getDefault(), res.getString(R.string.ldt_format),
                ldt.getMonthValue(),
                ldt.getDayOfMonth(),
                ldt.getHour() >= 12 ? "PM" : "AM",
                ldt.getHour() % 12 == 0 ? 12 : ldt.getHour() % 12,
                ldt.getMinute()
                );
    }

}
