package com.holy.radiorate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.radiorate.adapters.NuclearPlantAdapter;
import com.holy.radiorate.helpers.RadioactivityDataXmlTask;
import com.holy.radiorate.helpers.SQLiteHelper;
import com.holy.radiorate.helpers.UtilHelper;
import com.holy.radiorate.models.NuclearPlant;
import com.holy.radiorate.models.RadioactivityData;
import com.holy.radiorate.models.RiskLevel;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, RadioactivityDataXmlTask.OnParsingCompleteListener {

    private NaverMap mMap;
    private final List<Marker> mNuclearPlantMarkerList = new ArrayList<>();

    private final List<NuclearPlant> mNuclearPlantList = SQLiteHelper.getInstance(this).getAllNuclearPlants();
    private final List<Double> mRadioactivityList = new ArrayList<>();
    private final List<RiskLevel> mRiskLevelList = new ArrayList<>();

    private boolean mIsLoadingRadioactivity;

    private View mNuclearPlantRecyclerLayout;
    private ProgressBar mNuclearPlantProgress;

    private TextView mAverageRadioactivityText;
    private ProgressBar mAverageRadioactivityProgress;

    private View mRiskInfoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNuclearPlantRecyclerLayout = findViewById(R.id.layoutNuclearPlantsRecycler);
        mNuclearPlantProgress = findViewById(R.id.progressRecycler);

        mAverageRadioactivityText = findViewById(R.id.txtAverageRadioactivity);
        mAverageRadioactivityProgress = findViewById(R.id.progressAverageRadioactivity);

        mRiskInfoView = findViewById(R.id.viewRiskInfo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.plant);
        }

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        TextView totalNumberText = findViewById(R.id.txtTotalNumber);
        String strTotalNumber = String.format(Locale.getDefault(), "%d", mNuclearPlantList.size());
        totalNumberText.setText(strTotalNumber);

        ImageButton riskInfoIButton = findViewById(R.id.iBtnRiskInfo);
        riskInfoIButton.setOnClickListener(v -> mRiskInfoView.setVisibility(View.VISIBLE));

        ImageButton closeRiskInfoIButton = findViewById(R.id.iBtnCloseRiskInfo);
        closeRiskInfoIButton.setOnClickListener(v -> mRiskInfoView.setVisibility(View.GONE));

        View riskLevelLayout = findViewById(R.id.layoutRiskLevel);
        riskLevelLayout.setOnClickListener(v -> mRiskInfoView.setVisibility(View.VISIBLE));

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.scrollTo(0, 0));

         /*
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        */
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        mMap = naverMap;

        LatLng center = new LatLng(36.20000, 127.78665);
        naverMap.moveCamera(CameraUpdate.toCameraPosition(new CameraPosition(center, 5)));

        updateRadioactivity();
    }

    private void updateRadioactivity() {

        mRadioactivityList.clear();
        mRiskLevelList.clear();

        mIsLoadingRadioactivity = true;

        new RadioactivityDataXmlTask(this)
                .execute(mNuclearPlantList.get(0).getId());

        mNuclearPlantRecyclerLayout.setVisibility(View.INVISIBLE);
        mNuclearPlantProgress.setVisibility(View.VISIBLE);

        mAverageRadioactivityText.setVisibility(View.INVISIBLE);
        mAverageRadioactivityProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onParsingSucceed(List<RadioactivityData> radioactivityDataList) {

        if (radioactivityDataList.isEmpty()) {
            onParsingFailed();
            return;
        }

        double radioactivity = UtilHelper.getAverageRadioactivity(radioactivityDataList);

        mRadioactivityList.add(radioactivity);
        mRiskLevelList.add(new RiskLevel(radioactivity));

        if (mRadioactivityList.size() < mNuclearPlantList.size()) {
            new RadioactivityDataXmlTask(this)
                    .execute(mNuclearPlantList.get(mRadioactivityList.size()).getId());
        } else {
            mIsLoadingRadioactivity = false;
            updateRadioactivityUI(radioactivityDataList);
        }
    }

    @Override
    public void onParsingFailed() {

        mIsLoadingRadioactivity = false;
        Toast.makeText(this,
                "데이터를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
    }

    private void updateRadioactivityUI(List<RadioactivityData> radioactivityDataList) {

        updateRadioactivityMarkers();
        updateAverageRadioactivityUI();
        updateRiskLevelUI();
        updateLatestUpdateUI(radioactivityDataList);
        buildNuclearPlantRecycler();

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.scrollTo(0, 0));
    }

    private void updateRadioactivityMarkers() {

        if (!mNuclearPlantMarkerList.isEmpty()) {
            for (Marker marker : mNuclearPlantMarkerList) {
                marker.setMap(null);
            }
            mNuclearPlantMarkerList.clear();
        }

        for (int i = 0; i < mNuclearPlantList.size(); i++) {

            NuclearPlant nuclearPlant = mNuclearPlantList.get(i);
            RiskLevel riskLevel = mRiskLevelList.get(i);

            LatLng latLng = new LatLng(nuclearPlant.getLatitude(), nuclearPlant.getLongitude());
            OverlayImage icon = OverlayImage.fromResource(R.drawable.ic_circle);
            Marker marker = new Marker(latLng, icon);
            marker.setCaptionText(nuclearPlant.getName());

            marker.setIconTintColor(UtilHelper.getRiskLevelColor(this, riskLevel));
            marker.setMap(mMap);
            mNuclearPlantMarkerList.add(marker);

            Marker marker2 = new Marker(latLng, icon);
            marker2.setIconTintColor(UtilHelper.getRiskLevelColor(this, riskLevel));
            marker2.setMap(mMap);
            mNuclearPlantMarkerList.add(marker2);
        }
    }

    private void updateAverageRadioactivityUI() {

        double sum = 0;
        for (double radioactivity : mRadioactivityList) {
            sum += radioactivity;
        }

        double average = sum / mRadioactivityList.size();
        String strAverage = String.format(Locale.getDefault(), "%.3f", average);
        mAverageRadioactivityText.setText(strAverage);

        mAverageRadioactivityText.setVisibility(View.VISIBLE);
        mAverageRadioactivityProgress.setVisibility(View.INVISIBLE);
    }

    private void updateRiskLevelUI() {

        TextView safeLongTermText = findViewById(R.id.txtNumberSafeLongTerm);
        TextView safeMediumTermText = findViewById(R.id.txtNumberSafeMediumTerm);
        TextView safeShortTermText = findViewById(R.id.txtNumberSafeShortTerm);
        TextView riskyText = findViewById(R.id.txtNumberRisky);
        TextView dangerText = findViewById(R.id.txtNumberDanger);
        TextView highDangerText = findViewById(R.id.txtNumberHighDanger);
        TextView severeText = findViewById(R.id.txtNumberSevere);
        TextView verySevereText = findViewById(R.id.txtNumberVerySevere);
        TextView fatalText = findViewById(R.id.txtNumberFatal);
        TextView[] textViews = {
                safeLongTermText,
                safeMediumTermText,
                safeShortTermText,
                riskyText,
                dangerText,
                highDangerText,
                severeText,
                verySevereText,
                fatalText,
        };

        Map<RiskLevel, TextView> riskLevelTextViews = new HashMap<>();
        for (int i = 0; i < RiskLevel.getRiskLevels().length; i++) {
            RiskLevel riskLevel = RiskLevel.getRiskLevels()[i];
            riskLevelTextViews.put(riskLevel, textViews[i]);
        }

        Map<RiskLevel, Integer> riskLevelNumbers = new HashMap<>();
        for (RiskLevel riskLevel : RiskLevel.getRiskLevels()) {
            riskLevelNumbers.put(riskLevel, 0);
        }

        for (RiskLevel riskLevel : mRiskLevelList) {
            int number = riskLevelNumbers.get(riskLevel);
            riskLevelNumbers.replace(riskLevel, number + 1);
        }

        for (Map.Entry<RiskLevel, Integer> entry : riskLevelNumbers.entrySet()) {

            TextView textView = riskLevelTextViews.get(entry.getKey());

            String strNumber = String.format(Locale.getDefault(), "%d", entry.getValue());
            textView.setText(strNumber);
        }
    }

    private void buildNuclearPlantRecycler() {

        RecyclerView recycler = findViewById(R.id.recyclerNuclearPlants);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        NuclearPlantAdapter adapter = new NuclearPlantAdapter(
                this, mNuclearPlantList, mRadioactivityList);

        recycler.setAdapter(adapter);

        mNuclearPlantRecyclerLayout.setVisibility(View.VISIBLE);
        mNuclearPlantProgress.setVisibility(View.INVISIBLE);
    }

    private void updateLatestUpdateUI(List<RadioactivityData> radioactivityDataList) {

        TextView latestUpdateText = findViewById(R.id.txtLatestUpdate);

        if (radioactivityDataList.isEmpty()) {
            latestUpdateText.setText("-");
            return;
        }

        Date date = radioactivityDataList.get(0).getTime();
        latestUpdateText.setText(UtilHelper.getDateString(date));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.item_refresh) {
            if (!mIsLoadingRadioactivity) {
                updateRadioactivity();
            }
            return true;
        } else if (id == R.id.item_risk_info) {
            mRiskInfoView.setVisibility(View.VISIBLE);
        } else if (id == R.id.item_open_api_url) {
            startOpenApiURL();
        } else if (id == R.id.item_inquiry) {
            startPlayStoreURL();
        }

        return false;
    }

    private void startOpenApiURL() {

        String url = getResources().getString(R.string.open_api_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void startPlayStoreURL() {

        String url = getResources().getString(R.string.play_store_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}