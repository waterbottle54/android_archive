package com.holy.deliveryapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.deliveryapp.adapters.LocalAdapter;
import com.holy.deliveryapp.helpers.LocalConnectionTask;
import com.holy.deliveryapp.models.Local;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    public interface OnLocalClickListener {
        void onLocalClick(Local local);
    }

    private OnLocalClickListener mHost;
    private Context mContext;

    private FusedLocationSource mLocationSource;
    private Location mLocation;

    private final List<Local> mLocalList = new ArrayList<>();
    private final List<Local> mFilteredLocalList = new ArrayList<>();
    private LocalAdapter mLocalAdapter;
    private RecyclerView mLocalRecycler;
    private ProgressBar mLocalRecyclerProgress;
    private int mCurrentPage;

    private String mCategory = "한식";

    private View mKoreanFoodView;
    private View mWesternFoodView;
    private View mJapaneseFoodView;
    private View mChineseFoodView;
    private View mFlourFoodView;
    private View mSnackView;
    private View mChickenView;

    private TextView mKoreanFoodText;
    private TextView mWesternFoodText;
    private TextView mJapaneseFoodText;
    private TextView mChineseFoodText;
    private TextView mFlourFoodText;
    private TextView mSnackText;
    private TextView mChickenText;

    private NaverMap mMap;


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            mHost = (OnLocalClickListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        mContext = context;
        mLocationSource = new FusedLocationSource(this, 100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mLocalRecycler = view.findViewById(R.id.recyclerLocal);
        mLocalRecyclerProgress = view.findViewById(R.id.progressRecyclerLocal);

        buildLocalRecycler();

        mKoreanFoodView = view.findViewById(R.id.viewKoreanFood);
        mWesternFoodView = view.findViewById(R.id.viewWesternFood);
        mJapaneseFoodView = view.findViewById(R.id.viewJapaneseFood);
        mChineseFoodView = view.findViewById(R.id.viewChineseFood);
        mFlourFoodView = view.findViewById(R.id.viewFlourFood);
        mSnackView = view.findViewById(R.id.viewSnack);
        mChickenView = view.findViewById(R.id.viewChicken);
        mKoreanFoodText = view.findViewById(R.id.txtKoreanFood);
        mWesternFoodText = view.findViewById(R.id.txtWesternFood);
        mJapaneseFoodText = view.findViewById(R.id.txtJapaneseFood);
        mChineseFoodText = view.findViewById(R.id.txtChineseFood);
        mFlourFoodText = view.findViewById(R.id.txtFlourFood);
        mSnackText = view.findViewById(R.id.txtSnack);
        mChickenText = view.findViewById(R.id.txtChicken);

        mKoreanFoodView.setOnClickListener(v -> selectCategory("한식"));
        mWesternFoodView.setOnClickListener(v -> selectCategory("양식"));
        mJapaneseFoodView.setOnClickListener(v -> selectCategory("일식"));
        mChineseFoodView.setOnClickListener(v -> selectCategory("중식"));
        mFlourFoodView.setOnClickListener(v -> selectCategory("분식"));
        mSnackView.setOnClickListener(v -> selectCategory("간식"));
        mChickenView.setOnClickListener(v -> selectCategory("치킨"));

        updateCategoryUI();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull @NotNull NaverMap naverMap) {

        Log.d("TAG", "onMapReady: ");

        mMap = naverMap;

        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            configureMap();
        } else {
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Boolean locationPermission = permissions.get(Manifest.permission.ACCESS_FINE_LOCATION);
                if (locationPermission != null && locationPermission) {
                    if (!mLocationSource.isActivated()) { // Permission denied
                        mMap.setLocationTrackingMode(LocationTrackingMode.None);
                        return;
                    }
                    configureMap();
                }
            }).launch(new String[] { Manifest.permission.ACCESS_FINE_LOCATION });
        }
    }

    private void configureMap() {

        mMap.setLocationSource(mLocationSource);
        mMap.setLocationTrackingMode(LocationTrackingMode.Face);
        mMap.addOnLocationChangeListener(location -> {
            if (mLocation == null) {
                mLocation = location;
                updateLocals(location);

                Log.d("TAG", "configureMap: ");
            }
        });
    }

    private void updateLocals(Location location) {

        mCurrentPage = 1;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        new LocalConnectionTask(mCurrentPage, latitude, longitude, 10000, mOnLocalParsingCompleteListener)
                .execute();
        showLocalRecycler(false);
    }

    private void buildLocalRecycler() {

        mLocalRecycler.setHasFixedSize(true);
        mLocalRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        mLocalAdapter = new LocalAdapter(mFilteredLocalList);
        mLocalAdapter.setOnItemClickListener(position -> {
            Local local = mFilteredLocalList.get(position);
            mHost.onLocalClick(local);
        });

        mLocalRecycler.setAdapter(mLocalAdapter);
    }

    private void showLocalRecycler(boolean show) {

        if (show) {
            mLocalRecyclerProgress.setVisibility(View.VISIBLE);
            mLocalRecyclerProgress.setVisibility(View.INVISIBLE);
        } else {
            mLocalRecyclerProgress.setVisibility(View.INVISIBLE);
            mLocalRecyclerProgress.setVisibility(View.VISIBLE);
        }
    }

    private void selectCategory(String category) {

        mCategory = category;

        mFilteredLocalList.clear();
        mFilteredLocalList.addAll(mLocalList.stream()
                .filter(local -> local.getCategoryName().equals(category))
                .collect(Collectors.toList())
        );

        mLocalAdapter.notifyDataSetChanged();

        updateCategoryUI();
    }

    private void updateCategoryUI() {

        int color = getResources().getColor(R.color.colorCategory, null);
        int colorHighlighted = getResources().getColor(R.color.colorCategoryHighlighted, null);

        mKoreanFoodText.setBackgroundColor(color);
        mWesternFoodText.setBackgroundColor(color);
        mJapaneseFoodText.setBackgroundColor(color);
        mChineseFoodText.setBackgroundColor(color);
        mFlourFoodText.setBackgroundColor(color);
        mSnackText.setBackgroundColor(color);
        mChickenText.setBackgroundColor(color);

        switch (mCategory) {
            case "한식": mKoreanFoodText.setBackgroundColor(colorHighlighted); break;
            case "양식": mWesternFoodText.setBackgroundColor(colorHighlighted); break;
            case "일식": mJapaneseFoodText.setBackgroundColor(colorHighlighted); break;
            case "중식": mChineseFoodText.setBackgroundColor(colorHighlighted); break;
            case "분식": mFlourFoodText.setBackgroundColor(colorHighlighted); break;
            case "간식": mSnackText.setBackgroundColor(colorHighlighted); break;
            case "치킨": mChickenText.setBackgroundColor(colorHighlighted); break;
        }

        mKoreanFoodView.setScaleX(0.95f);
        mKoreanFoodView.setScaleY(0.95f);
        mWesternFoodView.setScaleX(0.95f);
        mWesternFoodView.setScaleY(0.95f);
        mJapaneseFoodView.setScaleX(0.95f);
        mJapaneseFoodView.setScaleY(0.95f);
        mChineseFoodView.setScaleX(0.95f);
        mChineseFoodView.setScaleY(0.95f);
        mFlourFoodView.setScaleX(0.95f);
        mFlourFoodView.setScaleY(0.95f);
        mSnackView.setScaleX(0.95f);
        mSnackView.setScaleY(0.95f);
        mChickenView.setScaleX(0.95f);
        mChickenView.setScaleY(0.95f);

        switch (mCategory) {
            case "한식":
                mKoreanFoodView.setScaleX(1);
                mKoreanFoodView.setScaleY(1);
                break;
            case "양식":
                mWesternFoodView.setScaleX(1);
                mWesternFoodView.setScaleY(1);
                break;
            case "일식":
                mJapaneseFoodView.setScaleX(1);
                mJapaneseFoodView.setScaleY(1);
                break;
            case "중식":
                mChineseFoodView.setScaleX(1);
                mChineseFoodView.setScaleY(1);
                break;
            case "분식":
                mFlourFoodView.setScaleX(1);
                mFlourFoodView.setScaleY(1);
                break;
            case "간식":
                mSnackView.setScaleX(1);
                mSnackView.setScaleY(1);
                break;
            case "치킨":
                mChickenView.setScaleX(1);
                mChickenView.setScaleY(1);
                break;
        }
    }


    private final LocalConnectionTask.OnParsingCompleteListener mOnLocalParsingCompleteListener = new LocalConnectionTask.OnParsingCompleteListener() {
        @Override
        public void onParsingSuccess(List<Local> localList, boolean end) {
            mLocalList.addAll(localList);
            showLocalRecycler(true);
            selectCategory(mCategory);
            mCurrentPage++;

            if (!end && mLocation != null) {
                double latitude = mLocation.getLatitude();
                double longitude = mLocation.getLongitude();
                new LocalConnectionTask(mCurrentPage, latitude, longitude, 10000, mOnLocalParsingCompleteListener)
                        .execute();
            }
        }

        @Override
        public void onParsingFailure() {
            Toast.makeText(mContext, "음식점 정보를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            showLocalRecycler(true);
        }
    };

}