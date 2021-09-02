package com.davidjo.remedialexercise.ui.diagnosis.hospital;

import android.Manifest;
import android.content.Context;
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

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.hospital.Hospital;
import com.davidjo.remedialexercise.databinding.FragmentHospitalsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HospitalsFragment extends Fragment implements OnMapReadyCallback, NaverMap.OnLocationChangeListener {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private Context context;
    private HospitalsViewModel viewModel;
    FragmentHospitalsBinding binding;

    private NaverMap map;
    private final List<Marker> markers = new ArrayList<>();

    private FusedLocationSource fusedLocationSource;
    private ActivityResultLauncher<String> locationPermissionLauncher;

    private Location currentLocation;


    public HospitalsFragment() {
        super(R.layout.fragment_hospitals);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (fusedLocationSource.onRequestPermissionsResult(
                            LOCATION_PERMISSION_REQUEST_CODE,
                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                            new int[] { result ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED }
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHospitalsBinding.bind(view);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        viewModel = new ViewModelProvider(this).get(HospitalsViewModel.class);

        viewModel.getHospitals().observe(getViewLifecycleOwner(), hospitals -> {
            if (hospitals != null) {
                updateMarkers(hospitals);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof HospitalsViewModel.Event.ShowHospitalLoadingUI) {

                binding.progressBar.setVisibility(View.VISIBLE);

            } else if (event instanceof HospitalsViewModel.Event.ShowHospitalUpdateFailureMessage) {

                HospitalsViewModel.Event.ShowHospitalUpdateFailureMessage showHospitalUpdateFailureMessage =
                        (HospitalsViewModel.Event.ShowHospitalUpdateFailureMessage) event;
                Snackbar.make(requireView(), showHospitalUpdateFailureMessage.message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("다시 시도", v -> viewModel.onRetryUpdateHospitalsClick())
                        .show();

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        map = naverMap;

        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    }

    @Override
    public void onLocationChange(@NonNull Location location) {

        viewModel.onLocationChange(location);
    }

    private void updateMarkers(List<Hospital> hospitals) {

        for (Marker marker : markers) {
            marker.setMap(null);
        }
        markers.clear();

        for (Hospital hospital : hospitals) {
            LatLng latLng = new LatLng(hospital.getLatitude(), hospital.getLongitude());
            OverlayImage icon = OverlayImage.fromResource(R.drawable.ic_hospital_red);
            Marker marker = new Marker(latLng, icon);
            marker.setCaptionText(hospital.getName());
            marker.setMap(map);
            markers.add(marker);
        }
    }

}
