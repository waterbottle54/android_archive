package com.davidjo.remedialexercise.util;

import android.content.res.Resources;

import com.davidjo.remedialexercise.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeUtils {

    public static String getDateString(Resources resources, long time) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(Locale.getDefault(),
                resources.getString(R.string.date_format),
                year, month, dayOfMonth
        );
    }

    public static LocalDate getLocalDate(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int getDifferenceInDays(long start, long end) {
        return (int)(getLocalDate(end).toEpochDay() - getLocalDate(start).toEpochDay());
    }

    public static String formatMinutesSeconds(int seconds) {
        return String.format(Locale.getDefault(),
                "%02d:%02d",
                seconds / 60, seconds % 60);
    }

    public static String formatMonthDay(LocalDate localDate) {

        int month = localDate.getMonth().getValue();
        int dayOfMonth = localDate.getDayOfMonth();

        return String.format(Locale.getDefault(), "%02d-%02d", month, dayOfMonth);
    }

    public static String formatYearMonth(LocalDate localDate) {

        int month = localDate.getMonth().getValue();
        int year = localDate.getYear();

        return String.format(Locale.getDefault(), "%02d.%02d", year % 100, month);
    }

    public static boolean isYearMonthTheSame(LocalDate a, LocalDate b) {
        return a.withDayOfMonth(1).equals(b.withDayOfMonth(1));
    }


}
