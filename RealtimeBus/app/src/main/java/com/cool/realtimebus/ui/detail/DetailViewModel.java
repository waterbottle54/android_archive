package com.cool.realtimebus.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.cool.realtimebus.api.arrival.ArrivalApi;
import com.cool.realtimebus.data.arrival.Arrival;
import com.cool.realtimebus.data.station.Station;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final Station station;
    private final LiveData<List<Arrival>> arrivals;


    public DetailViewModel(SavedStateHandle savedStateHandle) {

        // StationsFragment 로부터 인수로 전달된 Station 객체를 가져온다
        station = savedStateHandle.get("station");
        assert station != null;

        // 해당 Station 의 도시코드와 아이디로 도착정보를 조회한다
        ArrivalApi arrivalApi = new ArrivalApi();
        arrivals = arrivalApi.get(station.getCityCode(), station.getId());
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Arrival>> getArrivals() {
        return arrivals;
    }

    public String getStationTitle() {
        return station.getTitle();
    }


    public void onCloseClick() {
        event.setValue(new Event.NavigateBack());
    }


    public static class Event {

        public static class NavigateBack extends Event {}
    }

}