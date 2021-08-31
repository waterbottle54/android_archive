package com.davidjo.greenworld.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.davidjo.greenworld.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Utils {

    public static String getEmailFromUserId(String userId) {
        return String.format(Locale.getDefault(), "%s@greenworld.com", userId);
    }

    public static LocalDate getLocalDate(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String formatDate(Resources res, LocalDate date) {
        return String.format(Locale.getDefault(),
                res.getString(R.string.date_format),
                date.getYear(),
                date.getMonth().getValue(),
                date.getDayOfMonth()
        );
    }

    public static String formatDate(Resources res, long epochDays) {
        LocalDate date = LocalDate.ofEpochDay(epochDays);
        return formatDate(res, date);
    }

    public static int getMonthlyScoreImageResource(Resources res, int monthlyScore) {
        if (monthlyScore < 20) {
            return R.drawable.dissatified;
        } else if (monthlyScore < 100) {
            return R.drawable.soso;
        } else {
            return R.drawable.satisfied;
        }
    }

    public static String getDayOfWeekString(Resources res, int dayOfWeekFromZero) {
        switch (dayOfWeekFromZero) {
            case 0: return res.getString(R.string.monday);
            case 1: return res.getString(R.string.tuesday);
            case 2: return res.getString(R.string.wednesday);
            case 3: return res.getString(R.string.thursday);
            case 4: return res.getString(R.string.friday);
            case 5: return res.getString(R.string.saturday);
            case 6: return res.getString(R.string.sunday);
        }
        return "";
    }

}
