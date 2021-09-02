package com.davidjo.remedialexercise.ui.diagnosis.hospital;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.hospital.Hospital;
import com.davidjo.remedialexercise.data.hospital.HospitalRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HospitalsViewModel extends ViewModel {

    private final HospitalRepository repository;

    private Location currentLocation;

    private final MutableLiveData<Event> event = new MutableLiveData<>(null);

    private final Observer<List<Hospital>> hospitalsObserver = hospitals -> {
        if (hospitals == null) {
            event.setValue(new Event.ShowHospitalUpdateFailureMessage("병원 정보를 불러오지 못했습니다"));
        }
    };


    @Inject
    public HospitalsViewModel(HospitalRepository repository) {
        this.repository = repository;

        getHospitals().observeForever(hospitalsObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        getHospitals().removeObserver(hospitalsObserver);
    }

    public LiveData<List<Hospital>> getHospitals() {
        return repository.getHospitals();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public void onLocationChange(Location location) {

        if (currentLocation == null) {
            currentLocation = location;
            updateHospitals();
        } else {
            currentLocation = location;
        }
    }

    public void onRetryUpdateHospitalsClick() {

        if (currentLocation != null) {
            updateHospitals();
        }
    }

    private void updateHospitals() {
        if (currentLocation != null) {
            repository.updateHospitals(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    5000);

            event.setValue(new Event.ShowHospitalLoadingUI());
        }
    }


    public static class Event {
        public static class ShowHospitalLoadingUI extends Event {

        }
        public static class ShowHospitalUpdateFailureMessage extends Event {
            public final String message;

            public ShowHospitalUpdateFailureMessage(String message) {
                this.message = message;
            }
        }

    }

}




