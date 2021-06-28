package com.holy.batterystation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.batterystation.helpers.SQLiteHelper;
import com.holy.batterystation.models.BatteryStation;
import com.holy.batterystation.helpers.BatteryStationXmlTask;
import com.holy.batterystation.helpers.ReverseGeocodingJsonParser;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        NaverMap.OnLocationChangeListener {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    private NaverMap mMap;
    private FusedLocationSource mLocationSource;
    private Location mAnchorLocation;
    private String mAddress1;

    private TextView mLocationText;

    private ReverseGeocodingJsonParser mReverseGeocodingParser;

    private ActivityResultLauncher<Intent> mVoiceRecognitionLauncher;

    private boolean mIsLoadingBatteryStations;
    private int mNumberOfBatteryStationParsingFailure;

    private boolean mIsSearchMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationText = findViewById(R.id.txtCurrentLocation);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mReverseGeocodingParser = new ReverseGeocodingJsonParser(this);

        ImageButton voiceSearchIButton = findViewById(R.id.iBtnVoiceSearch);
        voiceSearchIButton.setOnClickListener(v -> showVoiceRecognitionDialog());

        mVoiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), mVoiceRecognitionResultCallback);

        configureSearchView();

        SearchView pseudoSearchView = findViewById(R.id.pseudoSearchView);
        pseudoSearchView.setOnClickListener(v -> {
            mIsSearchMode = true;
            updateSearchUI(true);
        });

        ImageButton closeSearchIButton = findViewById(R.id.iBtnCloseSearch);
        closeSearchIButton.setOnClickListener(v -> {
            mIsSearchMode = false;
            updateSearchUI(false);
        });
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
    }

    @Override
    public void onLocationChange(@NonNull Location location) {

        if (mAnchorLocation == null || location.distanceTo(mAnchorLocation) > 1000) {
            mAnchorLocation = location;

            mReverseGeocodingParser.parse(
                    location.getLongitude(),
                    location.getLatitude(),
                    mOnReverseGeocodingParsingCompleteListener);
        }

    }

    private void hideProgressBar() {

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        mLocationText.setVisibility(View.VISIBLE);
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

    private void updateSearchUI(boolean showOrHide) {

        View locationLayout = findViewById(R.id.layoutLocation);
        View pseudoSearchLayout = findViewById(R.id.layoutPseudoSearch);
        View searchLayout = findViewById(R.id.layoutSearch);
        View searchView = findViewById(R.id.searchView);

        if (showOrHide) {
            locationLayout.setVisibility(View.GONE);
            pseudoSearchLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            searchView.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        } else {
            locationLayout.setVisibility(View.VISIBLE);
            pseudoSearchLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            searchLayout.clearFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
        }
    }

    private void configureSearchView() {

        SearchView searchView = findViewById(R.id.searchView);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName cname = ComponentName.createRelative(getPackageName(), SearchableActivity.class.getName());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cname));
        searchView.setIconifiedByDefault(false);
    }

    @Override
    public void onBackPressed() {

        if (mIsSearchMode) {
            mIsSearchMode = false;
            updateSearchUI(false);
            return;
        }

        super.onBackPressed();
    }

    private final ReverseGeocodingJsonParser.OnParsingCompleteListener mOnReverseGeocodingParsingCompleteListener
            = new ReverseGeocodingJsonParser.OnParsingCompleteListener() {

        @SuppressWarnings("deprecation")
        @Override
        public void onParsingSucceed(String address, String address1, String address2, String address3) {

            mAddress1 = address1;

            hideProgressBar();
            mLocationText.setText(address);

            if (!mIsLoadingBatteryStations) {
                new BatteryStationXmlTask(MainActivity.this, mOnBatteryStationParsingCompleteListener)
                        .execute(address1);
                mIsLoadingBatteryStations = true;
            }
        }

        @Override
        public void onParsingFailed() {

            hideProgressBar();
            mLocationText.setText("인식할 수 없는 위치입니다");
        }
    };

    private final BatteryStationXmlTask.OnParsingCompleteListener mOnBatteryStationParsingCompleteListener
            = new BatteryStationXmlTask.OnParsingCompleteListener() {
        @Override
        public void onParsingSucceed(List<BatteryStation> batteryStationList) {

            mIsLoadingBatteryStations = false;

            SQLiteHelper.getInstance(MainActivity.this).clearBatteryStations();
            for (BatteryStation batteryStation : batteryStationList) {
                SQLiteHelper.getInstance(MainActivity.this).addBatteryStation(batteryStation);
            }
        }

        @Override
        public void onParsingFailed() {

            mNumberOfBatteryStationParsingFailure++;

            if (mNumberOfBatteryStationParsingFailure < 10) {
                new BatteryStationXmlTask(MainActivity.this, mOnBatteryStationParsingCompleteListener)
                        .execute(mAddress1);
            } else {
                mIsLoadingBatteryStations = false;
                Toast.makeText(MainActivity.this,
                        "공공 API 이용 횟수가 초과되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final ActivityResultCallback<ActivityResult> mVoiceRecognitionResultCallback = result -> {

        Intent data = result.getData();

        if (result.getResultCode() == Activity.RESULT_OK && data != null) {

            // 인식된 스펠링 확인
            ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String strSpelling = resultList.get(0);

            triggerSearch(strSpelling, null);
        }
    };

}



