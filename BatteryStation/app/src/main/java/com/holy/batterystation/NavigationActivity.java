package com.holy.batterystation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.batterystation.helpers.DirectionsJsonParser;
import com.holy.batterystation.helpers.ReverseGeocodingJsonParser;
import com.holy.batterystation.helpers.SQLiteHelper;
import com.holy.batterystation.models.BatteryStation;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class NavigationActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        NaverMap.OnLocationChangeListener {

    public static final String EXTRA_BATTERY_STATION_ID = "com.holy.batterystation.battery_station_id";

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    private NaverMap mMap;
    private FusedLocationSource mLocationSource;
    private PolylineOverlay mPathPolyline;
    private Marker mGoalMarker;
    private Location mAnchorLocation;

    private TextView mLocationText;
    private TextView mDestinationTimeText;
    private TextView mDistanceText;

    private ReverseGeocodingJsonParser mReverseGeocodingParser;
    private DirectionsJsonParser mDirectionsParser;

    private BatteryStation mBatteryStation;

    // TTS (Text to Speech) 객체
    private TextToSpeech mTTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        TextView goalText = findViewById(R.id.txtGoal);
        mLocationText = findViewById(R.id.txtCurrentLocation);
        mDestinationTimeText = findViewById(R.id.txtDestinationTime);
        mDistanceText = findViewById(R.id.txtDistance);

        int stationId = getIntent().getIntExtra(EXTRA_BATTERY_STATION_ID, 0);
        mBatteryStation = SQLiteHelper.getInstance(this).getBatteryStation(stationId);
        if (mBatteryStation == null) {
            Toast.makeText(this, "충전소 정보가 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        goalText.setText(mBatteryStation.getName());

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mReverseGeocodingParser = new ReverseGeocodingJsonParser(this);
        mDirectionsParser = new DirectionsJsonParser(this);

        // TTS 객체를 초기화한다.
        mTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                mTTS.setLanguage(Locale.KOREAN);
            } else {
                Toast.makeText(this,
                        "TTS 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {

        // TTS 종료
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        mMap = naverMap;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            configureMap();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {

            if (!mLocationSource.isActivated()) { // Permission denied
                mMap.setLocationTrackingMode(LocationTrackingMode.None);
                return;
            }

            configureMap();
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void configureMap() {

        mMap.addOnLocationChangeListener(this);
        mMap.setLocationSource(mLocationSource);
        mMap.setLocationTrackingMode(LocationTrackingMode.Face);
        mMap.getUiSettings().setLocationButtonEnabled(true);
        mMap.getLocationOverlay().setIcon(OverlayImage.fromResource(R.drawable.ic_navigation));
    }

    @Override
    public void onLocationChange(@NonNull Location location) {

        if (mAnchorLocation == null || location.distanceTo(mAnchorLocation) > 1000) {

            mAnchorLocation = location;

            mReverseGeocodingParser.parse(
                    location.getLongitude(),
                    location.getLatitude(),
                    mOnReverseGeocodingParsingCompleteListener);

            mDirectionsParser.parse(
                    location.getLongitude(), location.getLatitude(),
                    mBatteryStation.getLongitude(), mBatteryStation.getLatitude(),
                    mOnDirectionsParsingCompleteListener);
        }
    }

    private void hideProgressBar() {

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        mLocationText.setVisibility(View.VISIBLE);
    }


    private final ReverseGeocodingJsonParser.OnParsingCompleteListener mOnReverseGeocodingParsingCompleteListener
            = new ReverseGeocodingJsonParser.OnParsingCompleteListener() {
        @Override
        public void onParsingSucceed(String address, String address1, String address2, String address3) {

            hideProgressBar();
            mLocationText.setText(address);
        }

        @Override
        public void onParsingFailed() {

            hideProgressBar();
            mLocationText.setText("인식할 수 없는 위치입니다");
        }
    };

    private final DirectionsJsonParser.OnParsingCompleteListener mOnDirectionsParsingCompleteListener
            = new DirectionsJsonParser.OnParsingCompleteListener() {
        @Override
        public void onParsingSucceed(int distance, int time, List<LatLng> waypointList) {

            double distanceInKm = distance / 1000.0;
            int timeInMin = (int)Math.ceil(time / 60000.0);

            String strDistance = String.format(Locale.getDefault(), "%.0fkm", distanceInKm);
            mDistanceText.setText(strDistance);

            LocalDateTime dateTime = LocalDateTime.now();
            dateTime = dateTime.plusMinutes(timeInMin);
            String strDestinationTime = String.format(Locale.getDefault(),
                    "%s %02d시 %02d분",
                    (dateTime.getHour() < 12) ? "오전" : "오후",
                    dateTime.getHour() % 12,
                    dateTime.getMinute());
            mDestinationTimeText.setText(strDestinationTime);

            if (waypointList.size() > 1) {
                if (mPathPolyline != null) {
                    mPathPolyline.setMap(null);
                }
                mPathPolyline = new PolylineOverlay(waypointList);
                mPathPolyline.setColor(getColor(R.color.colorPath));
                mPathPolyline.setWidth(14);
                mPathPolyline.setMap(mMap);

                if (mGoalMarker != null) {
                    mGoalMarker.setMap(null);
                }
                LatLng goalPosition = waypointList.get(waypointList.size() - 1);
                mGoalMarker = new Marker(goalPosition, OverlayImage.fromResource(R.drawable.ic_charge));
                mGoalMarker.setCaptionText(mBatteryStation.getName());
                mGoalMarker.setMap(mMap);
            }

            // 음성합성 안내

            int hours = timeInMin / 60;
            int minutes = timeInMin % 60;
            StringBuilder strGuide = new StringBuilder("목적지까지 남은 시간은 ");
            if (hours > 0) {
                strGuide.append(hours);
                strGuide.append("시간");
            }
            if (minutes > 0) {
                strGuide.append(minutes);
                strGuide.append("분");
            }
            strGuide.append("이며, 목적지까지의 거리는 ");
            strGuide.append(strDistance);
            strGuide.append("입니다");

            Bundle params = new Bundle();
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1);

            mTTS.speak(strGuide, TextToSpeech.QUEUE_FLUSH, params, null);
        }

        @Override
        public void onParsingFailed() {
            Toast.makeText(NavigationActivity.this,
                    "경로를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    };

}



