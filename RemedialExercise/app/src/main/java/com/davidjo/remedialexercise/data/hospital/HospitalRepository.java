package com.davidjo.remedialexercise.data.hospital;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.davidjo.remedialexercise.api.hospital.HospitalApi;
import com.davidjo.remedialexercise.api.hospital.HospitalMapper;
import com.davidjo.remedialexercise.api.hospital.HospitalNetworkEntity;
import com.davidjo.remedialexercise.api.hospital.HospitalXmlParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HospitalRepository {

    private final HospitalApi hospitalApi;
    private final HospitalMapper hospitalMapper;
    private final MutableLiveData<List<Hospital>> hospitals;


    @Inject
    public HospitalRepository(HospitalApi hospitalApi, HospitalMapper hospitalMapper) {
        this.hospitalApi = hospitalApi;
        this.hospitalMapper = hospitalMapper;
        this.hospitals = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<Hospital>> getHospitals() {
        return hospitals;
    }

    public void updateHospitals(double latitude, double longitude, double radius) {

        hospitalApi.setLatitude(latitude)
                .setLongitude(longitude)
                .setRadius(radius);

        new Thread(() -> {
            List<HospitalNetworkEntity> entities = hospitalApi.getHospitals();
            if (entities != null) {
                hospitals.postValue(hospitalMapper.mapFromEntityList(entities));
            } else {
                hospitals.postValue(null);
            }
        }).start();
    }

}
