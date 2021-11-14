package com.penelope.weatherteacher.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;
import com.penelope.weatherteacher.R;
import com.penelope.weatherteacher.data.measurement.Measurement;
import com.penelope.weatherteacher.databinding.FragmentHomeBinding;
import com.penelope.weatherteacher.utils.TimeUtils;
import com.penelope.weatherteacher.utils.WeatherUtils;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnMapReadyCallback, NaverMap.OnLocationChangeListener {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private NaverMap map;                                   // 네이버 맵
    private FusedLocationSource fusedLocationSource;        // 구글 로케이션 소스
    private ActivityResultLauncher<String> locationPermissionLauncher;


    public HomeFragment() {
        super(R.layout.fragment_home);
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
                            // 퍼미션 거부
                            map.setLocationTrackingMode(LocationTrackingMode.None);
                            return;
                        }
                        // 네이버맵 초기 설정하기
                        configureMap();
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHomeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 서치 뷰 리스너 구현
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // 주소 검색 시 뷰모델에 검색 문자열 전달
                viewModel.onSearchLocation(s);
                hideKeyboard(requireView());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        // 네이버 맵 획득
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        assert mapFragment != null;

        viewModel.getMeasurement().observe(getViewLifecycleOwner(), measurement -> {

            binding.progressBar.setVisibility(View.INVISIBLE);

            if (measurement == null) {
                return;
            }

            // 기온 표시
            String strTemperature = String.format(Locale.getDefault(),
                    getString(R.string.temperature_format), measurement.getTemperature());
            binding.textViewTemperature.setText(strTemperature);

            // 강수형태 표시
            String strPrecipitationType = WeatherUtils.getPrecipitationTypeName(measurement.getPrecipitationType());
            binding.textViewPrecipitationType.setText(strPrecipitationType);

            // 습도 표시
            String strHumidity = String.format(Locale.getDefault(),
                    getString(R.string.humidity_format), measurement.getHumidity());
            binding.textViewHumidity.setText(strHumidity);

            // 강수량 표시
            if (measurement.getPrecipitationType() == Measurement.PrecipitationType.NONE) {
                binding.textViewPrecipitation.setVisibility(View.INVISIBLE);
            } else {
                binding.textViewPrecipitation.setVisibility(View.VISIBLE);
                String strPrecipitation = String.format(Locale.getDefault(),
                        getString(R.string.precipitation_format), measurement.getPrecipitation());
                binding.textViewHumidity.setText(strPrecipitation);
            }

            // 풍향/풍속 표시
            String strWindDirectionVelocity = String.format(Locale.getDefault(),
                    getString(R.string.wind_direction_velocity_format),
                    WeatherUtils.getWindDirectionName(measurement.getWindDegree()),
                    measurement.getWindVelocity());
            binding.textViewWindDirectionVelocity.setText(strWindDirectionVelocity);

            String strLdt = TimeUtils.getDateTimeString(getResources(), measurement.getDateTime());
            binding.textViewDateTime.setText(strLdt);
        });

        // 주소 업데이트
        viewModel.getAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                binding.textViewLocation.setText(address.getFullAddress());
            }
        });

        // 기상 테마 업데이트
        viewModel.getWeatherTheme().observe(getViewLifecycleOwner(), theme -> {
            switch (theme) {
                case SUNNY:
                    binding.imageViewBackground.setImageResource(R.drawable.sunny);
                    break;
                case RAINY:
                    binding.imageViewBackground.setImageResource(R.drawable.rainy);
                    break;
                case SNOWY:
                    binding.imageViewBackground.setImageResource(R.drawable.snowy);
                    break;
                case NIGHT:
                    binding.imageViewBackground.setImageResource(R.drawable.night);
                    break;
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            // 메세지 표시
            if (event instanceof HomeViewModel.Event.ShowGeneralMessage) {
                String message = ((HomeViewModel.Event.ShowGeneralMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
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
            // 네이버맵 초기 설정하기
            configureMap();
        } else {
            // 위치 퍼미션 요청하기
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void configureMap() {
        // 네이버맵 초기 설정
        map.addOnLocationChangeListener(this);
        map.setLocationSource(fusedLocationSource);
        map.setLocationTrackingMode(LocationTrackingMode.Follow);
    }

    @Override
    public void onLocationChange(@NonNull Location location) {
        // GPS 위치 감지 시 뷰모델에 통보
        viewModel.onGpsLocationChange(location);
    }

    // 키보드 감추기

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}