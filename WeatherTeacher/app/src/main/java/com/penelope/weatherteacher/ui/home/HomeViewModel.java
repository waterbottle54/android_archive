package com.penelope.weatherteacher.ui.home;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.naver.maps.geometry.LatLng;
import com.penelope.weatherteacher.api.geocoding.GeocodingApi;
import com.penelope.weatherteacher.api.reversegeocoding.ReverseGeocodingApi;
import com.penelope.weatherteacher.data.WeatherTheme;
import com.penelope.weatherteacher.data.address.Address;
import com.penelope.weatherteacher.data.measurement.Measurement;
import com.penelope.weatherteacher.data.measurement.MeasurementRepository;

import java.time.LocalDateTime;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<LatLng> location = new MutableLiveData<>();   // 날씨 검색 기준이 되는 GPS 좌표
    private final LiveData<Measurement> measurement;                            // location 위치의 기상 실황
    private final LiveData<Address> address;                                    // location 위치의 주소명

    private final LiveData<WeatherTheme> weatherTheme;                          // 날씨, 시간에 따른 기상 테마

    private final GeocodingApi geocodingApi;                                    // Geocoding API


    @Inject
    public HomeViewModel(MeasurementRepository measurementRepository, ReverseGeocodingApi reverseGeocodingApi, GeocodingApi geocodingApi) {

        // measurement, address 를 location 에 연동한다
        measurement = Transformations.switchMap(location, measurementRepository::getMeasurements);
        address = Transformations.switchMap(location, loc -> reverseGeocodingApi.get(loc.latitude, loc.longitude));

        // 기상 테마를 measurement 에 연동한다
        weatherTheme = Transformations.map(measurement, m -> {
            // 저녁 7시 이후, 새벽 6시 전 이면 밤 테마
            LocalDateTime ldt = LocalDateTime.now();
            if (ldt.getHour() >= 19 || ldt.getHour() < 6) {
                return WeatherTheme.NIGHT;
            }
            // 강수형태에 따라 테마 결정
            switch (m.getPrecipitationType()) {
                case NONE:
                    return WeatherTheme.SUNNY;
                case RAIN:
                case DROP:
                    return WeatherTheme.RAINY;
                case SNOW:
                case RAIN_SNOW:
                case WINDY_DROP_SNOW:
                case WINDY_SNOW:
                    return WeatherTheme.SNOWY;
            }
            return WeatherTheme.SUNNY;
        });

        this.geocodingApi = geocodingApi;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Measurement> getMeasurement() {
        return measurement;
    }

    public LiveData<Address> getAddress() {
        return address;
    }

    public LiveData<WeatherTheme> getWeatherTheme() {
        return weatherTheme;
    }


    public void onGpsLocationChange(Location loc) {
        LatLng locationValue = location.getValue();
        // GPS 좌표가 첫 감지될 때만 GPS 좌표를 초기화한다
        if (locationValue == null) {
            location.setValue(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }
    }

    public void onSearchLocation(String query) {

        if (query.isEmpty()) {
            return;
        }

        // Geocoding API 로 주어진 주소의 GPS 좌표를 얻고 업데이트한다
        geocodingApi.get(query, latLng -> {
            if (latLng == null) {
                event.setValue(new Event.ShowGeneralMessage("올바른 주소를 입력해주세요"));
                return;
            }
            location.setValue(latLng);
        }, e -> {
        });
    }


    public static class Event {

        // 일반 메세지 출력 이벤트
        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }
    }

}