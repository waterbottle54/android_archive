package com.lotus.vaccinecenters;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lotus.vaccinecenters.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private double latitude;
    private double longitude;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 전달된 접종센터 정보 확인
        if (getIntent() != null) {
            latitude = getIntent().getDoubleExtra("latitude", 0);
            longitude = getIntent().getDoubleExtra("longitude", 0);
            name = getIntent().getStringExtra("name");
        } else {
            return;
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 구글맵 획득
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // 줌 버튼 활성화
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // 예방접종센터 정보를 마커로 표시한다
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(name);

        googleMap.addMarker(marker);

        // 마커로 카메라를 이동한다
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f));
    }
}