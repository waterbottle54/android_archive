package com.holy.radiorate.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.holy.radiorate.R;
import com.holy.radiorate.models.RadioactivityData;
import com.holy.radiorate.models.RiskLevel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class UtilHelper {

    public static double getAverageRadioactivity(List<RadioactivityData> radioactivityDataList) {

        if (radioactivityDataList == null || radioactivityDataList.isEmpty()) {
            return 0;
        }

        double sum = 0;

        for (RadioactivityData radioactivityData : radioactivityDataList) {
            sum += radioactivityData.getValue();
        }

        return (sum / radioactivityDataList.size());
    }

    public static String getRiskLevelName(Context context, RiskLevel riskLevel) {

        Resources res = context.getResources();

        switch (riskLevel.getLevel()) {
            case RiskLevel.SafeLongTerm:
                return res.getString(R.string.safe_long_term);
            case RiskLevel.SafeMediumTerm:
                return res.getString(R.string.safe_medium_term);
            case RiskLevel.SafeShortTerm:
                return res.getString(R.string.safe_short_term);
            case RiskLevel.Risky:
                return res.getString(R.string.risky);
            case RiskLevel.Danger:
                return res.getString(R.string.danger);
            case RiskLevel.HighDanger:
                return res.getString(R.string.high_danger);
            case RiskLevel.Severe:
                return res.getString(R.string.severe);
            case RiskLevel.VerySevere:
                return res.getString(R.string.very_severe);
            case RiskLevel.Fatal:
                return res.getString(R.string.fatal);
        }
        return "";
    }

    public static int getRiskLevelColor(Context context, RiskLevel riskLevel) {

        Resources res = context.getResources();

        switch (riskLevel.getLevel()) {
            case RiskLevel.SafeLongTerm:
                return res.getColor(R.color.colorSafeLongTerm, null);
            case RiskLevel.SafeMediumTerm:
                return res.getColor(R.color.colorSafeMediumTerm, null);
            case RiskLevel.SafeShortTerm:
                return res.getColor(R.color.colorSafeShortTerm, null);
            case RiskLevel.Risky:
                return res.getColor(R.color.colorRisky, null);
            case RiskLevel.Danger:
                return res.getColor(R.color.colorDanger, null);
            case RiskLevel.HighDanger:
                return res.getColor(R.color.colorHighDanger, null);
            case RiskLevel.Severe:
                return res.getColor(R.color.colorSevere, null);
            case RiskLevel.VerySevere:
                return res.getColor(R.color.colorVerySevere, null);
            case RiskLevel.Fatal:
                return res.getColor(R.color.colorFatal, null);
        }

        return Color.WHITE;
    }

    public static String getDateString(Date date) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return String.format(Locale.getDefault(),
                "%d-%02d-%02d %02d:%02d",
                year, month, dayOfMonth, hour, minute);
    }

}
