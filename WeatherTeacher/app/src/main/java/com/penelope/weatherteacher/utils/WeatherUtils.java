package com.penelope.weatherteacher.utils;

import androidx.core.util.Pair;

import com.penelope.weatherteacher.data.measurement.Measurement;

public class WeatherUtils {

    // 풍향 각도로부터 풍향 이름 리턴
    public static String getWindDirectionName(int degrees) {

        if (degrees >= 315) {
            return "북서풍";
        } else if (degrees >= 270) {
            return "북서풍";
        } else if (degrees >= 225) {
            return "남서풍";
        } else if (degrees >= 180) {
            return "남서풍";
        } else if (degrees >= 135) {
            return "남동풍";
        } else if (degrees >= 90) {
            return "남동풍";
        } else if (degrees >= 45) {
            return "북동풍";
        } else if (degrees >= 0) {
            return "북동풍";
        }

        return "";
    }

    // 강수 형태로부터 기상 이름 리턴
    public static String getPrecipitationTypeName(Measurement.PrecipitationType type) {
        switch (type) {
            case NONE: return "맑음";
            case RAIN: return "비";
            case RAIN_SNOW: return "눈비";
            case SNOW: return "눈";
            case DROP: return "빗방울 떨어짐";
            case WINDY_DROP_SNOW: return "빗방울 날림";
            case WINDY_SNOW: return "눈발 날림";
        }
        return "";
    }

    public static Pair<Integer, Integer> getWeatherCoord(double lat, double lon) {
        int x = (int) Math.round(18.5 * lon - 2290);
        int y = (int) Math.round(21.2 * lat - 670);
        return new Pair<>(x, y);
    }


}
