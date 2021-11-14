package com.penelope.weatherteacher.api.measurement;

import com.penelope.weatherteacher.data.measurement.Measurement;

import java.time.LocalDateTime;
import java.util.List;

public class MeasurementMapper {

    // API 호출로부터 얻은 MeasurementResponse 를 Measurement 객체로 맵핑한다

    public static Measurement getMeasurement(List<MeasurementResponse.Value> values) {

        double temperature = -1;
        double precipitation = -1;
        double horizontalWind = 0;
        double verticalWind = 0;
        int humidity = -1;
        int windDegree = -1;
        double windVelocity = -1;
        Measurement.PrecipitationType type = null;

        boolean validHorizontalWind = false;
        boolean validVerticalWind = false;

        String baseDate = null;
        String baseTime = null;

        for (MeasurementResponse.Value value : values) {
            baseDate = value.getBaseDate();
            baseTime = value.getBaseTime();
            switch (value.getCategory()) {
                case "T1H":
                    try {
                        temperature = Double.parseDouble(value.getObsrValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "RN1":
                    try {
                        precipitation = Double.parseDouble(value.getObsrValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "UUU":
                    try {
                        horizontalWind = Double.parseDouble(value.getObsrValue());
                        validHorizontalWind = true;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "VVV":
                    try {
                        verticalWind = Double.parseDouble(value.getObsrValue());
                        validVerticalWind = true;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "REH":
                    try {
                        humidity = Integer.parseInt(value.getObsrValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "PTY":
                    try {
                        int ordinal = Integer.parseInt(value.getObsrValue());
                        if (ordinal == 0) {
                            type = Measurement.PrecipitationType.NONE;
                        } else if (ordinal == 1) {
                            type = Measurement.PrecipitationType.RAIN;
                        } else if (ordinal == 2) {
                            type = Measurement.PrecipitationType.RAIN_SNOW;
                        } else if (ordinal == 3) {
                            type = Measurement.PrecipitationType.SNOW;
                        } else if (ordinal == 5) {
                            type = Measurement.PrecipitationType.DROP;
                        } else if (ordinal == 6) {
                            type = Measurement.PrecipitationType.WINDY_DROP_SNOW;
                        } else if (ordinal == 7) {
                            type = Measurement.PrecipitationType.WINDY_SNOW;
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "VEC":
                    try {
                        windDegree = Integer.parseInt(value.getObsrValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                case "WSD":
                    try {
                        windVelocity = Double.parseDouble(value.getObsrValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
            }
        }

        if (temperature < 0 || precipitation < 0 || !validHorizontalWind
                || !validVerticalWind || humidity < 0 || windDegree < 0
                || windVelocity < 0 || type == null || baseTime == null || baseDate == null) {
            return null;
        }

        if (baseDate.length() != 8 || baseTime.length() != 4) {
            return null;
        }

        int year = Integer.parseInt(baseDate.substring(0, 4));
        int month = Integer.parseInt(baseDate.substring(4, 6));
        int dayOfMonth = Integer.parseInt(baseDate.substring(6, 8));
        int hour = Integer.parseInt(baseTime.substring(0, 2));
        int minute = Integer.parseInt(baseTime.substring(2, 4));
        LocalDateTime ldt = LocalDateTime.of(year, month, dayOfMonth, hour, minute);

        return new Measurement(temperature, precipitation, horizontalWind, verticalWind, humidity, windDegree, windVelocity, type, ldt);
    }

}
