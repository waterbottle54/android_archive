package com.penelope.ecar.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.penelope.ecar.R;
import com.penelope.ecar.api.place.PlaceApi;
import com.penelope.ecar.api.station.StationApi;
import com.penelope.ecar.databinding.ActivityMainBinding;
import com.penelope.ecar.databinding.DialogStationBinding;
import com.penelope.ecar.models.Place;
import com.penelope.ecar.models.Station;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnLocationChangeListener {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private NaverMap map;                                               // 네이버 맵
    private final List<Marker> markers = new ArrayList<>();             // 충전소 마커 리스트

    private FusedLocationSource fusedLocationSource;                    // GPS 위치 소스
    private ActivityResultLauncher<String> locationPermissionLauncher;  // 퍼미션 런처

    private Location anchorLocation = null;                                 // 마지막 업데이트 위치
    private final MutableLiveData<Place> place = new MutableLiveData<>();   // 자세한 위치 정보 (지역명, 지역코드)

    // 위치정보가 업데이트 될 때 충전소(Station) 리스트가 업데이트 되도록 정의함
    private final LiveData<List<Station>> stations = Transformations.switchMap(place, placeValue -> {
       if (placeValue != null) {
           // 위치정보로부터 시도코드 획득
           String cityCode = placeValue.getCode().substring(0, 2);
           MutableLiveData<List<Station>> stations = new MutableLiveData<>();
           // 시도코드를 전달해 백그라운드에서 충전소 목록 획득
           new Thread(() -> stations.postValue(StationApi.get(cityCode))).start();
           return stations;
       }
       return null;
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 바인딩
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // GPS 소스 획득
        fusedLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // 위치 퍼미션 런처 정의
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (fusedLocationSource.onRequestPermissionsResult(
                            LOCATION_PERMISSION_REQUEST_CODE,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            new int[]{result ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED}
                    )) {
                        if (!fusedLocationSource.isActivated()) {
                            // 퍼미션 거부됨
                            map.setLocationTrackingMode(LocationTrackingMode.None);
                            return;
                        }
                        // 퍼미션 승인 시 네이버맵 초기설정 진행
                        configureMap();
                    }
                }
        );

        // 네이버 맵 획득
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        assert mapFragment != null;

        // 충전소 UI 업데이트
        stations.observe(this, stationList -> {
            if (stationList != null) {
                // 네이버맵 상의 충전소 마커 업데이트
                updateStationUI(stationList);
                // 충전소 개수 텍스트뷰 업데이트
                String strNumber = "이 지역에 " + stationList.size() + "개의 충전소가 있습니다";
                binding.textViewNumberStations.setText(strNumber);
            } else {
                Toast.makeText(this, "충전소를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
            binding.progressBar.setVisibility(View.INVISIBLE);
        });

        // 현위치 UI 업데이트
        place.observe(this, placeValue -> {
            if (placeValue != null) {
                // 현위치 텍스트뷰 업데이트
                binding.textViewLocation.setText(placeValue.getName());
            } else {
                binding.textViewLocation.setText("");
            }
        });

        // 안내 메세지 5초후 삭제
        binding.cardGuide.postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.cardGuide.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            binding.cardGuide.setAnimation(fadeOut);
        }, 5000);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        map = naverMap;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 퍼미션 승인 상태이면 네이버맵 초기설정 진행
            configureMap();
        } else {
            // 위치 퍼미션 요구
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void configureMap() {

        // 네이버맵 초기설정 진행
        map.addOnLocationChangeListener(this);
        map.setLocationSource(fusedLocationSource);
        map.setLocationTrackingMode(LocationTrackingMode.Follow);
        map.getUiSettings().setLocationButtonEnabled(true);
        map.moveCamera(CameraUpdate.zoomTo(10));
    }

    @Override
    public void onLocationChange(@NonNull Location location) {

        // 위치 최초 변경 시, 또는 마지막 업데이트 위치에서 100m 이상 이격 시, 현 위치 변경
        if (anchorLocation == null || location.distanceTo(anchorLocation) > 100) {
            anchorLocation = location;
            new Thread(() -> {
                // 백그라운드에서 현위치 전달하여 상세 위치정보 획득
                Place placeValue = PlaceApi.get(location.getLatitude(), location.getLongitude());
                place.postValue(placeValue);
            }).start();
        }
    }

    private void updateStationUI(List<Station> stationList) {

        // 기존 마커 제거
        for (Marker marker : markers) {
            marker.setMap(null);
        }
        markers.clear();

        // 충전소 마커 추가
        for (Station station : stationList) {
            LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
            OverlayImage icon = OverlayImage.fromResource(R.drawable.icon_charger);
            Marker marker = new Marker(latLng, icon);
            marker.setCaptionText(station.getName());
            marker.setOnClickListener(overlay -> {
                // 충전소 클릭 시, 상세정보 대화상자 띄우기
                showStationDialog(station);
                return true;
            });
            marker.setMap(map);
            markers.add(marker);
        }
    }

    // 충전소 상세정보 대화상자

    private void showStationDialog(Station station) {

        DialogStationBinding dialogBinding = DialogStationBinding.inflate(getLayoutInflater());

        // 대화상자에 충전소 정보 입력
        dialogBinding.textViewStationId.setText(station.getStationId());
        dialogBinding.textViewName.setText(station.getName());
        dialogBinding.textViewAddress.setText(station.getAddress());

        String strOutput = "충전용량 " + station.getOutput() + "kW";
        dialogBinding.textViewOutput.setText(strOutput);

        // 1: 통신이상, 2: 충전대기, 3: 충전중, 4: 운영중지, 5: 점검중, 9: 상태미확인
        String strChargerStatus = "";
        switch (station.getChargerStatus()) {
            case 1: strChargerStatus = "통신이상"; break;
            case 2: strChargerStatus = "충전대기"; break;
            case 3: strChargerStatus = "충전중"; break;
            case 4: strChargerStatus = "운영중지"; break;
            case 5: strChargerStatus = "점검중"; break;
            case 9: strChargerStatus = "상태미확인"; break;
        }
        dialogBinding.textViewChargerStatus.setText(strChargerStatus);

        dialogBinding.textViewTimeDescription.setText(station.getTimeDescription());

        // 대화상자 띄우기
        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("확인", null)
                .show();
    }

}





