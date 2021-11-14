package com.cool.realtimebus.ui.stations;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.cool.realtimebus.R;
import com.cool.realtimebus.data.station.Station;
import com.cool.realtimebus.databinding.FragmentStationsBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

public class StationsFragment extends Fragment implements OnMapReadyCallback, NaverMap.OnLocationChangeListener {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FragmentStationsBinding binding;
    private StationsViewModel viewModel;

    private NaverMap map;
    private final List<Marker> markers = new ArrayList<>();

    private FusedLocationSource fusedLocationSource;
    private ActivityResultLauncher<String> locationPermissionLauncher;


    public StationsFragment() {
        super(R.layout.fragment_stations);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                            map.setLocationTrackingMode(LocationTrackingMode.None);
                            return;
                        }
                        configureMap();
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentStationsBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(StationsViewModel.class);

        // 네이버 맵 획득
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        assert mapFragment != null;

        // 주위 정류소가 업데이트되면 마커를 업데이트한다
        viewModel.getStations().observe(getViewLifecycleOwner(), stations -> {
            updateMarkers(stations);
            binding.progressBar.setVisibility(View.INVISIBLE);
        });

        // 뷰모델에서 전송한 이벤트 처리
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof StationsViewModel.Event.ShowProgressBar) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (event instanceof StationsViewModel.Event.NavigateToDetailScreen) {
                // 디테일 스크린 띄우기
                Station station = ((StationsViewModel.Event.NavigateToDetailScreen) event).station;
                NavDirections action = StationsFragmentDirections.actionStationsFragmentToDetailFragment(station);
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        map = naverMap;

        if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            configureMap();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void configureMap() {

        map.addOnLocationChangeListener(this);
        map.setLocationSource(fusedLocationSource);
        map.setLocationTrackingMode(LocationTrackingMode.Follow);
        map.getUiSettings().setLocationButtonEnabled(true);

        map.moveCamera(CameraUpdate.zoomTo(17));
    }

    @Override
    public void onLocationChange(@NonNull Location location) {
        viewModel.onLocationChange(location);
    }

    private void updateMarkers(List<Station> stations) {

        // 기존 마커 제거
        for (Marker marker : markers) {
            marker.setMap(null);
        }
        markers.clear();

        // 각 정류소에 대응되는 마커 설치
        for (Station station : stations) {
            LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
            Marker marker = new Marker(latLng);
            marker.setIcon(OverlayImage.fromResource(R.drawable.bus_icon));
            marker.setMap(map);

            // 마커가 클릭될 시 뷰모델에 통보
            marker.setOnClickListener(overlay -> {
                viewModel.onStationMarkerClick(station);
                return true;
            });

            markers.add(marker);
        }
    }

}






