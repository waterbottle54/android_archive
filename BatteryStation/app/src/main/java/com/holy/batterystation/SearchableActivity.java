package com.holy.batterystation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.Toast;

import com.holy.batterystation.adapters.BatteryStationAdapter;
import com.holy.batterystation.helpers.SQLiteHelper;
import com.holy.batterystation.models.BatteryStation;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private NaverMap mMap;
    private FusedLocationSource mLocationSource;
    private List<BatteryStation> mBatteryStationList;

    private ActivityResultLauncher<Intent> mVoiceRecognitionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        // Get the intent, verify the action
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // User searched with query: Get the query and search & display the results
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // User selected suggestion: Start home activity to show the selected user
            int stationId = Integer.parseInt(intent.getDataString());
            startNavigationActivity(stationId);
        }

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mVoiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), mVoiceRecognitionResultCallback);

        ImageButton voiceRecognitionIButton = findViewById(R.id.iBtnVoiceRecognition);
        voiceRecognitionIButton.setOnClickListener(v -> showVoiceRecognitionDialog());
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

    private void configureMap() {

        mMap.setLocationSource(mLocationSource);
        mMap.setLocationTrackingMode(LocationTrackingMode.Face);
        mMap.getUiSettings().setZoomControlEnabled(false);
    }

    private void buildBatteryStationRecycler(List<BatteryStation> batteryStationList) {

        RecyclerView recycler = findViewById(R.id.recyclerBatteryStation);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mBatteryStationList = batteryStationList;
        BatteryStationAdapter adapter = new BatteryStationAdapter(mBatteryStationList);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            int stationId = mBatteryStationList.get(position).getStationId();
            startNavigationActivity(stationId);
        });
    }

    private void doMySearch(String query) {

        mBatteryStationList = SQLiteHelper.getInstance(this).getBatteryStationsIncludeAddress(query);
        buildBatteryStationRecycler(mBatteryStationList);
    }

    private void startNavigationActivity(int stationId) {

        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(NavigationActivity.EXTRA_BATTERY_STATION_ID, stationId);
        startActivity(intent);
        finish();
    }

    // 음성인식 다이얼로그를 보여준다.

    private void showVoiceRecognitionDialog() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.please_speak);

        try {
            mVoiceRecognitionLauncher.launch(intent);

        } catch (Exception e) {
            Toast.makeText(this,
                    "음성인식을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private final ActivityResultCallback<ActivityResult> mVoiceRecognitionResultCallback = result -> {

        Intent data = result.getData();

        if (result.getResultCode() == Activity.RESULT_OK && data != null) {

            // 인식된 스펠링 확인
            ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String strSpelling = resultList.get(0);

            for (BatteryStation batteryStation : mBatteryStationList) {
                if (batteryStation.getName().contains(strSpelling)) {
                    startNavigationActivity(batteryStation.getStationId());
                }
            }
        }
    };

}