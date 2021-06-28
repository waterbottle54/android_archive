package com.myapp.mapnplayers;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.IOException;
import java.util.List;


public class MyMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TARGET_ADDRESS = "경기도 연천군 전곡읍 온골로 11번길 국민주택";


    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 네이버맵 초기화
        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.naver_map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    // 네이버맵 준비 완료

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        // Geocoder 를 이용하여 주어진 주소를 GPS 좌표로 바꾼다
        try {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addressList = geocoder.getFromLocationName(TARGET_ADDRESS, 1);

            for (Address address : addressList) {
                // 주소의 GPS 좌표를 얻는다.
                LatLng gps = new LatLng(address.getLatitude(), address.getLongitude());
                double zoom = naverMap.getMaxZoom() / 1.3;

                // 네이버 맵의 카메라 위치를 해당 GPS 위치로 설정한다.
                naverMap.setCameraPosition(new CameraPosition(gps, zoom));

                // 해당 GPS 위치에 마커 표시
                Marker marker = new Marker(gps);
                marker.setMap(naverMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}