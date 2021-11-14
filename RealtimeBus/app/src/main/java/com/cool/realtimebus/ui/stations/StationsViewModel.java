package com.cool.realtimebus.ui.stations;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.cool.realtimebus.api.station.StationApi;
import com.cool.realtimebus.data.station.Station;

import java.util.List;

public class StationsViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<Location> lastLocation = new MutableLiveData<>();
    private final LiveData<List<Station>> stations;

    // 앵커 로케이션 : 정류소 데이터를 마지막으로 업데이트 한 로케이션
    // 현재 로케이션이 앵커로부터 일정 거리 이상 벗어나면 정류소 데이터를 다시 업데이트한다
    private Location anchorLocation;


    public StationsViewModel() {
        stations = Transformations.switchMap(lastLocation, location -> {
            StationApi stationApi = new StationApi();
            return stationApi.get(location.getLatitude(), location.getLongitude());
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Station>> getStations() {
        return stations;
    }


    public void onLocationChange(Location location) {

        // 위치가 100m 이상 변경되었거나 또는 위치가 처음으로 설정될 때, lastLocation 를 변경한다
        if (anchorLocation == null || location.distanceTo(anchorLocation) > 100) {
            anchorLocation = location;
            lastLocation.setValue(location);
            event.setValue(new Event.ShowProgressBar());
        }
    }

    public void onStationMarkerClick(Station station) {
        event.setValue(new Event.NavigateToDetailScreen(station));
    }


    public static class Event {

        public static class ShowProgressBar extends Event {}

        public static class NavigateToDetailScreen extends Event {
            public final Station station;

            public NavigateToDetailScreen(Station station) {
                this.station = station;
            }
        }
    }

}