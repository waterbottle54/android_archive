package com.holy.exercise;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.holy.exercise.helpers.HospitalXmlParser;
import com.holy.exercise.models.Hospital;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HospitalFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HospitalFragment";

    public static final int REQUEST_LOCATION_PERMISSION = 100;

    // API URL
    public static final String URL_HOSPITAL = "http://apis.data.go.kr/B551182/hospInfoService/getHospBasisList?serviceKey=[KEY]&numOfRows=[NUM]&xPos=[XPOS]&yPos=[YPOS]&radius=[RAD]&dgsbjtCd=[PART]";
    public static final String PLACEHOLDER_KEY = "[KEY]";
    public static final String PLACEHOLDER_NUM = "[NUM]";
    public static final String PLACEHOLDER_XPOS = "[XPOS]";
    public static final String PLACEHOLDER_YPOS = "[YPOS]";
    public static final String PLACEHOLDER_RADIUS = "[RAD]";
    public static final String PLACEHOLDER_PART = "[PART]";
    public static final String KEY = "3U5FAOrbKwtTBXYzH54eZ0jeVY0FeCjK4xcoXMOBgH%2BOJq2omZYKSRYMqTjQTg5ytsAbkcmF3gDycB2zjPMprA%3D%3D";
    public static final String PART_ORTHOPEDICS = "08";

    private NaverMap mNaverMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private Marker mPositionMarker;
    private List<Marker> mHospitalMarkerList;
    private boolean mPositionTracked;

    private ProgressBar mLoadingBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHospitalMarkerList = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(20000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                onLocationUpdated(location);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);

        mLoadingBar = view.findViewById(R.id.progressBar);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    private boolean checkLocationPermission() {
        assert getContext() != null;
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkLocationPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    @Override
    public void onPause() {
        super.onPause();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    // 퍼미션 결과 처리

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkLocationPermission()) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
                }
            }
        }
    }

    // 네이버맵 준비 완료

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        mNaverMap = naverMap;

        if (checkLocationPermission()) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this::onLocationUpdated);
        }
    }

    // 위치 변경시 UI 업데이트

    private void onLocationUpdated(Location location) {

        if (location == null || mNaverMap == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // 카메라 이동 (최초 1회만)
        if (!mPositionTracked) {
            CameraPosition cameraPosition = new CameraPosition(latLng, 13);
            mNaverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition));
            mPositionTracked = true;
        }

        // 마커 업데이트
        if (mPositionMarker == null) {
            mPositionMarker = new Marker(latLng, OverlayImage.fromResource(R.drawable.ic_position));
            mPositionMarker.setMap(mNaverMap);
            mPositionMarker.setCaptionText("현위치");
        }
        mPositionMarker.setPosition(latLng);

        // 병원 데이터 업데이트
        loadHospitalData(URL_HOSPITAL, latitude, longitude);
    }

    // 병원 정보 로드

    private void loadHospitalData(String url, double latitude, double longitude) {

        String urlHospital = url
                .replace(PLACEHOLDER_KEY, KEY)
                .replace(PLACEHOLDER_NUM, String.valueOf(100))
                .replace(PLACEHOLDER_XPOS, String.valueOf(longitude))
                .replace(PLACEHOLDER_YPOS, String.valueOf(latitude))
                .replace(PLACEHOLDER_RADIUS, String.valueOf(2000))
                .replace(PLACEHOLDER_PART, PART_ORTHOPEDICS);

        new DownloadXmlTask(this).execute(urlHospital);
    }

    // 병원 UI 업데이트

    private void updateHospitalUI(List<Hospital> hospitalList) {

        // 마커 업데이트
        // - 모든 마커 지우기
        for (Marker marker : mHospitalMarkerList) {
            marker.setVisible(false);
        }
        mHospitalMarkerList.clear();

        // - 새 마커 표시하기
        for (Hospital hospital : hospitalList) {
            LatLng latLng = new LatLng(hospital.getLatitude(), hospital.getLongitude());
            Marker marker = new Marker(latLng, OverlayImage.fromResource(R.drawable.ic_orthopedics));
            marker.setCaptionText(hospital.getName());
            marker.setMap(mNaverMap);
            marker.setOnClickListener(overlay -> {
                showHospitalDialog(hospital);
                return true;
            });
            mHospitalMarkerList.add(marker);

            if (mHospitalMarkerList.size() >= 10) {
                break;
            }
        }

    }

    // 병원 정보 대화상자

    private void showHospitalDialog(Hospital hospital) {

        String tel = hospital.getTel();
        String address = hospital.getAddress();
        int distance = (int) Math.round(hospital.getDistance());
        String strHospital = String.format(Locale.getDefault(),
                "%s, %dm\n\n☎ %s", address, distance, tel);

        new AlertDialog.Builder(getContext())
                .setTitle(hospital.getName())
                .setMessage(strHospital)
                .setPositiveButton("확인", null)
                .setNeutralButton("통화", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                    startActivity(intent);
                })
                .show();
    }

    // XML 로부터 정보를 불러오기 위한 AsyncTask

    @SuppressWarnings("deprecation")
    static class DownloadXmlTask extends AsyncTask<String, Void, List<Hospital>> {

        // 액티비티 레퍼런스
        private final WeakReference<HospitalFragment> reference;

        // 생성자
        public DownloadXmlTask(HospitalFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HospitalFragment fragment = reference.get();
            if (fragment == null) {
                return;
            }

            fragment.mLoadingBar.setVisibility(View.VISIBLE);
        }

        // 백그라운드 태스크 (병원 정보 로드)
        @Override
        protected List<Hospital> doInBackground(String... strings) {

            // 주어진 URL 링크 확인
            String strUrl = strings[0];

            try {
                // URL 을 다운로드받아 InputStream 획득
                InputStream inputStream = downloadUrl(strUrl);

                // InputStream 을 parse 하여 리스트 구성
                return new HospitalXmlParser().parse(inputStream);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Hospital> hospitalList) {

            HospitalFragment fragment = reference.get();
            if (fragment == null) {
                return;
            }

            fragment.mLoadingBar.setVisibility(View.INVISIBLE);

            if (hospitalList == null) {
                Toast.makeText(reference.get().getContext(),
                        "불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 병원 UI 업데이트
            fragment.updateHospitalUI(hospitalList);
        }

        // URL 로부터 InputStream 을 생성하기

        private InputStream downloadUrl(String strUrl) throws IOException {

            java.net.URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* 밀리초 */);
            conn.setConnectTimeout(60000 /* 밀리초 */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // 쿼리 시작
            conn.connect();
            return conn.getInputStream();
        }
    }

}




