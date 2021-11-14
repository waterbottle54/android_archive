package com.penelope.weatherteacher.data.measurement;

import java.time.LocalDateTime;

// 초단기 기상실황 데이터

public class Measurement {

    public enum PrecipitationType {
        NONE, RAIN, RAIN_SNOW, SNOW, DROP, WINDY_DROP_SNOW, WINDY_SNOW
    }

    private final double temperature;       // 기온 (C)
    private final double precipitation;     // 시간당 강수량 (mm)
    private final double horizontalWind;    // 바람 수평성분 속도 (m/s)
    private final double verticalWind;      // 바람 수직성분 속도 (m/s)
    private final int humidity;             // 습도 (%)
    private final int windDegree;           // 풍향 (0~360도)
    private final double windVelocity;      // 풍속 (m/s)
    private final PrecipitationType precipitationType;  // 강수형태
    private final LocalDateTime dateTime;               // 기준시각

    public Measurement(double temperature, double precipitation, double horizontalWind, double verticalWind, int humidity, int windDegree, double windVelocity, PrecipitationType precipitationType, LocalDateTime dateTime) {
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.horizontalWind = horizontalWind;
        this.verticalWind = verticalWind;
        this.humidity = humidity;
        this.windDegree = windDegree;
        this.windVelocity = windVelocity;
        this.precipitationType = precipitationType;
        this.dateTime = dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public double getHorizontalWind() {
        return horizontalWind;
    }

    public double getVerticalWind() {
        return verticalWind;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public double getWindVelocity() {
        return windVelocity;
    }

    public PrecipitationType getPrecipitationType() {
        return precipitationType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
