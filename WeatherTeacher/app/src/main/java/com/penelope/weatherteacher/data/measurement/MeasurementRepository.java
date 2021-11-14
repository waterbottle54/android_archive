package com.penelope.weatherteacher.data.measurement;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.naver.maps.geometry.LatLng;
import com.penelope.weatherteacher.api.measurement.MeasurementApi;
import com.penelope.weatherteacher.api.measurement.MeasurementMapper;
import com.penelope.weatherteacher.api.measurement.MeasurementResponse;
import com.penelope.weatherteacher.utils.TimeUtils;
import com.penelope.weatherteacher.utils.WeatherUtils;

import java.time.LocalDateTime;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeasurementRepository {

    private final MeasurementApi api;

    @Inject
    public MeasurementRepository(MeasurementApi api) {
        this.api = api;
    }

    // GPS 좌표 위치의 기상 실황을 얻는다

    public LiveData<Measurement> getMeasurements(LatLng location) {

        MutableLiveData<Measurement> measurements = new MutableLiveData<>();

        Pair<Integer, Integer> coord = WeatherUtils.getWeatherCoord(location.latitude, location.longitude);
        LocalDateTime ldt = TimeUtils.getLocalDateTime(System.currentTimeMillis());
        if (ldt.getMinute() <= 40) {
            ldt = ldt.minusHours(1);
        }
        String baseTime = TimeUtils.getBaseTimeString(ldt);
        String baseDate = TimeUtils.getBaseDateString(ldt);

        Call<MeasurementResponse> responseCall = api.getResponse(
                baseTime, baseDate, coord.first, coord.second);

        responseCall.enqueue(new Callback<MeasurementResponse>() {
            @Override
            public void onResponse(@NonNull Call<MeasurementResponse> call, @NonNull Response<MeasurementResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    measurements.setValue(null);
                    return;
                }

                MeasurementResponse.Response res = response.body().getResponse();

                MeasurementResponse.Header header = res.getHeader();
                if (!header.getResultCode().equals("00")) {
                    measurements.setValue(null);
                    return;
                }

                MeasurementResponse.Items items = res.getBody().getItems();
                if (items == null || items.getItem() == null) {
                    measurements.setValue(null);
                    return;
                }

                Measurement measurement = MeasurementMapper.getMeasurement(items.getItem());
                measurements.setValue(measurement);
            }

            @Override
            public void onFailure(@NonNull Call<MeasurementResponse> call, @NonNull Throwable t) {
                measurements.setValue(null);
            }
        });

        return measurements;
    }


}
