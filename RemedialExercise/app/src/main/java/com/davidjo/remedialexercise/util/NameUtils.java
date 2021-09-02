package com.davidjo.remedialexercise.util;

import com.davidjo.remedialexercise.data.BodyPart;

public class NameUtils {

    public static String getBodyPartName(BodyPart bodyPart) {
        switch (bodyPart) {
            case NECK:
                return "목";
            case SHOULDER:
                return "어깨";
            case WRIST:
                return "손목";
            case BACK:
                return "허리";
            case KNEE:
                return "무릎";
            case ANKLE:
                return "발목";
        }
        return "";
    }

    public static String getChartFilterName(ChartFilter filter) {
        switch (filter) {
            case YEARLY:
                return "연도별";
            case MONTHLY:
                return "월별";
            case DAILY:
                return "일별";
       }
       return "";
    }

}
