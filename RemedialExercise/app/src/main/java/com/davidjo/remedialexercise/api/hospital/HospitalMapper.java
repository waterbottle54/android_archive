package com.davidjo.remedialexercise.api.hospital;

import com.davidjo.remedialexercise.data.hospital.Hospital;
import com.davidjo.remedialexercise.util.EntityMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class HospitalMapper implements EntityMapper<HospitalNetworkEntity, Hospital> {

    @Inject
    public HospitalMapper() {
    }

    @Override
    public Hospital mapFromEntity(HospitalNetworkEntity hospitalNetworkEntity) {
        return new Hospital(
                hospitalNetworkEntity.getYadmNm(),
                hospitalNetworkEntity.getXPos(),
                hospitalNetworkEntity.getYPos(),
                hospitalNetworkEntity.getTelno(),
                hospitalNetworkEntity.getAddr()
        );
    }

    @Override
    public HospitalNetworkEntity mapToEntity(Hospital hospital) {
        return new HospitalNetworkEntity(
                hospital.getName(),
                hospital.getLongitude(),
                hospital.getLatitude(),
                hospital.getTel(),
                hospital.getAddress()
        );
    }

    public List<Hospital> mapFromEntityList(List<HospitalNetworkEntity> entities) {
        return entities.stream().map(this::mapFromEntity).collect(Collectors.toList());
    }

}
