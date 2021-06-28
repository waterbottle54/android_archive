package com.holy.singaporeantaxis;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.holy.singaporeantaxis.helpers.GoogleDirectionApiHelper;
import com.holy.singaporeantaxis.helpers.UtilHelper;

import java.util.List;
import java.util.Locale;

public class FareActivity extends FragmentActivity implements
        OnMapReadyCallback, View.OnClickListener, GoogleDirectionApiHelper.OnDirectionDataReadyListener {

    public static final String EXTRA_MY_LATITUDE = "com.holy.my_latitude";
    public static final String EXTRA_MY_LONGITUDE = "com.holy.my_longitude";

    private GoogleMap mGoogleMap;
    private LatLng mMyLocation;
    private LatLng mDestLocation;
    private TextView mBottomText;
    private Polyline mPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get my location
        double myLatitude = getIntent().getDoubleExtra(EXTRA_MY_LATITUDE, 0);
        double myLongitude = getIntent().getDoubleExtra(EXTRA_MY_LONGITUDE, 0);
        mMyLocation = new LatLng(myLatitude, myLongitude);

        // Set up click listeners
        mBottomText = findViewById(R.id.txt_bottom);
        mBottomText.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Initialize google map
        mGoogleMap = googleMap;
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Move camera to my location
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMyLocation, 12);
        mGoogleMap.moveCamera(cameraUpdate);

        // Add market at my location
        MarkerOptions markerOptions = new MarkerOptions().position(mMyLocation);
        mGoogleMap.addMarker(markerOptions);
    }

    // Process click on views

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.txt_bottom) {

            // Select destination location
           selectDestination();
        }
    }

    // Select destination location

    private void selectDestination() {

        // Get target location
        mDestLocation = mGoogleMap.getCameraPosition().target;

        // Calculate the steps and distance between me and target, using google Direction API
        new GoogleDirectionApiHelper(this)
                .loadDirectionData(this, mMyLocation, mDestLocation);
    }

    // Steps and distance calculated

    @Override
    public void onDirectionDataReady(double distance, List<LatLng> endPointList) {

        // Calculate the fare based on the distance
        double fare = UtilHelper.fareInSingaporeanDollars(distance * 1.41);

        // Display the fare
        String strFare = String.format(Locale.getDefault(),
                "The fare is roughly S$%.2f", fare);
        mBottomText.setText(strFare);

        // Add polyline representing the endPoints
        PolylineOptions polylineOptions = UtilHelper.getStandardPolylineOptions()
                .add(mMyLocation);

        for (LatLng endPoint : endPointList) {
            polylineOptions.add(endPoint);
        }

        if (mPolyline != null) {
            mPolyline.remove();
        }
        mPolyline = mGoogleMap.addPolyline(polylineOptions);
    }

}