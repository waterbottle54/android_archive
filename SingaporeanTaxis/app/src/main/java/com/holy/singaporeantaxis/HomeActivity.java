package com.holy.singaporeantaxis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.holy.singaporeantaxis.helpers.UtilHelper;
import com.holy.singaporeantaxis.helpers.SQLiteHelper;
import com.holy.singaporeantaxis.helpers.SingaporeTaxiApiHelper;
import com.holy.singaporeantaxis.models.Taxi;
import com.holy.singaporeantaxis.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String EXTRA_SEARCHED_USER_ID = "com.holy.singaporeantaxis.searchedUserId";

    public static final int REQUEST_LOCATION_PERMISSION = 100;
    public static final int REQUEST_CALL_PHONE_PERMISSION = 101;
    public static final int REQUEST_SETTINGS_ACTIVITY = 100;
    public static final float DEFAULT_ZOOM_LEVEL = 16.5f;

    private Toolbar mToolbar;
    private String mCurrentId;

    private GoogleMap mGoogleMap;
    private List<Marker> mTaxiMarkerList;
    private List<Marker> mUserMarkerList;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserLocation;

    private View mUserInfoView;
    private User mSelectedUser;
    private Polyline mConnectingLine;

    private CountDownTimer mCountDownTimer;
    private TextView mCountDownText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Check current id
        mCurrentId = ((App) getApplication()).getCurrentId();
        if (mCurrentId == null) {
            Toast.makeText(this,
                    "Sorry, you are not signed in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize google map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Check location permission
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED) {

            // Request location permission
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        // Get views
        mUserInfoView = findViewById(R.id.user_info_view);
        mCountDownText = findViewById(R.id.txt_count_down);

        // Create marker lists
        mTaxiMarkerList = new ArrayList<>();
        mUserMarkerList = new ArrayList<>();

        // Initialize toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && mGoogleMap != null) {

            // Start location updates
            startLocationUpdates();

            // Start loading taxi data
            mLoadTaxiDataThread = new Thread(mLoadTaxiData);
            mLoadTaxiDataThread.start();

        }
    }

    @Override
    public void onPause() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && mGoogleMap != null) {

            // Stop location updates
            stopLocationUpdates();

            // Stop loading taxi data
            if (mLoadTaxiDataThread != null && mLoadTaxiDataThread.isAlive()) {
                try {
                    mLoadTaxiDataThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
        }

        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (mSelectedUser != null) {
            // Un-select the currently selected user
            mSelectedUser = null;
            hideUserInformation();
        } else {
            // Ask whether to sign out and finish activity
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to sign out?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                        // Sign out
                        signOut();

                        // Finish activity
                        super.onBackPressed();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSubmitButtonEnabled(true);

        // Set searchable info of search view
        ComponentName searchableActivityCompName = ComponentName.createRelative(
                getPackageName(), SearchableActivity.class.getName());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(searchableActivityCompName));
        searchView.setIconifiedByDefault(true);

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_calculate:
                // Start fare activity
                startFareActivity();
                break;
            case R.id.item_settings:
                // Start settings activity
                if (mUserLocation != null) {
                    startSettingsActivity();
                } else {
                    Toast.makeText(this,
                            "Cannot detect your location", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Location permission Granted:
                    if (mGoogleMap != null) {

                        // Start location updates
                        startLocationUpdates();

                        // Start loading taxi data
                        mLoadTaxiDataThread = new Thread(mLoadTaxiData);
                        mLoadTaxiDataThread.start();
                    }

                } else {
                    // Location permission Denied: just move camera to default location
                    Toast.makeText(this, "Location permission denied",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
                break;
            case REQUEST_CALL_PHONE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Call Phone permission Granted: Start phone call activity
                    if (mSelectedUser != null) {
                        String tel = String.format(Locale.getDefault(),
                                "tel:%s", mSelectedUser.getPhone());
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                        startActivity(intent);
                    }
                } else {
                    // Call phone permission Denied: Show a toast message
                    Toast.makeText(this,
                            "Permission required to make a phone call", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // Process signing out : Must called before activity is paused

    private void signOut() {

        // - Update the signed state
        SQLiteHelper.getInstance(this).updateUserSignedState(mCurrentId, false);
        // - Set current id of application to null
        ((App) getApplication()).setCurrentId(null);

    }

    // Start location updates

    private void startLocationUpdates() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            mFusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(this);

            LocationRequest locationRequest = new LocationRequest();
            mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, mLocationCallback, Looper.getMainLooper());

            Log.d("TAG", "startLocationUpdates: ");
        }
    }

    // Stop location updates

    private void stopLocationUpdates() {

        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    // Google map ready

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Initialize google map
        mGoogleMap = googleMap;
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));

        // - Add zoom controls and My Location button
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        // - Set Marker click listener
        mGoogleMap.setOnMarkerClickListener(this);

        // - Set Map Click listener : Hide User Info View if showing
        mGoogleMap.setOnMapClickListener(latLng -> {
            if (mUserInfoView.getVisibility() == View.VISIBLE) {
                mSelectedUser = null;
                hideUserInformation();
            }
        });

        // If it's not loading taxi data yet, start loading taxi data
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (mUserLocation == null) {
                startLocationUpdates();
            }

            if (mLoadTaxiDataThread == null) {
                mLoadTaxiDataThread = new Thread(mLoadTaxiData);
                mLoadTaxiDataThread.start();
            }

            // Move camera to the searched user, if exists
            if (getIntent().hasExtra(EXTRA_SEARCHED_USER_ID)) {
                String searchedUserId = getIntent().getStringExtra(EXTRA_SEARCHED_USER_ID);
                mSelectedUser = SQLiteHelper.getInstance(this).getUser(searchedUserId);
                if (mSelectedUser != null) {
                    displayUserInformation();
                }
            }
        }
    }

    // Update taxi UI

    private void onUpdateTaxiData(List<Taxi> taxiList, LocalDateTime dateTime) {

        if (mUserLocation != null) {
            updateTaxiMarkers(taxiList);
            updateTaxiCount(taxiList);
        }

        // Calculate how many seconds are remaining until the next update
        int nowSecond = LocalDateTime.now().getSecond();
        int nextSecond = dateTime.getSecond();
        int remainingSeconds = nextSecond - nowSecond;
        if (remainingSeconds <= 0) {
            remainingSeconds = 60 + remainingSeconds;
        } else if (remainingSeconds < 5) {
            remainingSeconds = 60;
        }

        // Start countdown timer for the next update
        mCountDownTimer = new CountDownTimer(remainingSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String strCountDown = String.format(Locale.getDefault(),
                        "Updated in %d sec", millisUntilFinished / 1000);
                mCountDownText.setText(strCountDown);
            }

            @Override
            public void onFinish() {
                // Start background thread for loading taxi data
                mLoadTaxiDataThread = new Thread(mLoadTaxiData);
                mLoadTaxiDataThread.start();
            }
        };
        mCountDownTimer.start();
    }

    // Update taxi markers

    private void updateTaxiMarkers(List<Taxi> taxiList) {

        if (mGoogleMap == null) {
            return;
        }

        // Remove all existing markers
        for (int i = mTaxiMarkerList.size() - 1; i >= 0; i--) {
            mTaxiMarkerList.get(i).remove();
            mTaxiMarkerList.remove(i);
        }

        // Display taxis within range as markers
        for (Taxi taxi : taxiList) {

            if (!isLocationWithinRange(taxi.getLocation())) {
                continue;
            }

            // Create marker for each taxi
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(taxi.getLocation())
                    .icon(UtilHelper.bitmapDescriptorFromVector(this, R.drawable.marker_taxi))
                    .visible(true);

            Marker marker = mGoogleMap.addMarker(markerOptions);
            mTaxiMarkerList.add(marker);
        }
    }

    // Update taxi count

    private void updateTaxiCount(List<Taxi> taxiList) {

        if (mGoogleMap == null) {
            return;
        }

        int taxiCount = 0;

        for (Taxi taxi : taxiList) {

            if (isLocationWithinRange(taxi.getLocation())) {
                taxiCount++;
            }
        }

        String strTaxiCount = String.format(Locale.getDefault(),
                "%d taxis available", taxiCount);

        mToolbar.setTitle(strTaxiCount);
    }

    // Check if given taxi is within range from the user

    private boolean isLocationWithinRange(LatLng location) {

        float[] results = new float[3];
        Location.distanceBetween(
                mUserLocation.getLatitude(), mUserLocation.getLongitude(),
                location.latitude, location.longitude,
                results
        );

        // Get range of taxis preference
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int taxisRange = pref.getInt("range", 500);

        return (results[0] <= taxisRange);
    }

    // Update person markers

    private void updateUserMarkers() {

        if (mGoogleMap == null) {
            return;
        }

        // Remove all existing markers
        for (int i = mUserMarkerList.size() - 1; i >= 0; i--) {
            mUserMarkerList.get(i).remove();
            mUserMarkerList.remove(i);
        }

        // Get locations of users currently signed in
        List<User> userList = SQLiteHelper.getInstance(this).getUsersSignedIn();

        // Display all users signed in as markers
        for (User user : userList) {

            if (user.getLastLocation() == null) {
                continue;
            }

            // Marker shows different color if it's users
            int bitmapRes = (user.getId().equals(mCurrentId) ?
                    R.drawable.ic_marker_person_highlighted :
                    R.drawable.ic_marker_person);

            // Create marker for each user
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(user.getLastLocation())
                    .icon(UtilHelper.bitmapDescriptorFromVector(this, bitmapRes))
                    .visible(true)
                    .title(user.getId());

            Marker marker = mGoogleMap.addMarker(markerOptions);
            mUserMarkerList.add(marker);
        }
    }

    // Process user marker click

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (mSelectedUser != null) {
            hideUserInformation();
        }

        String clickedUserId = marker.getTitle();
        if (clickedUserId == null) {
            return false;
        }

        // Clicked myself
        if (clickedUserId.equals(mCurrentId)) {
            return false;
        }

        // Clicked marker that is not user's
        mSelectedUser = SQLiteHelper.getInstance(this).getUser(clickedUserId);
        if (mSelectedUser == null) {
            return false;
        }

        // Display user information
        displayUserInformation();

        return false;
    }

    // Display user information

    private void displayUserInformation() {

        // No user to show information
        if (mSelectedUser == null) {
            return;
        }

        // Display user information on user info view
        TextView userIdText = mUserInfoView.findViewById(R.id.txt_user_id);
        TextView userPhoneText = mUserInfoView.findViewById(R.id.txt_user_phone);

        userIdText.setText(mSelectedUser.getId());
        userPhoneText.setText(mSelectedUser.getPhone());

        // Set call button click listener
        Button callButton = mUserInfoView.findViewById(R.id.button_call);
        callButton.setOnClickListener(v -> {

            // Check Call Phone permission before starting phone call activity
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {

                // Granted: Start phone call activity
                String tel = String.format(Locale.getDefault(), "tel:%s", mSelectedUser.getPhone());
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                startActivity(intent);
            } else {

                // Denied: Request the permission
                requestPermissions(
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL_PHONE_PERMISSION);
            }
        });

        // Make the view (currently gone) visible with an animation
        mUserInfoView.setVisibility(View.VISIBLE);
        mUserInfoView.startAnimation(
                AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));

        // Draw a polyline connecting the user and me on the map
        if (mGoogleMap != null) {
            User me = SQLiteHelper.getInstance(this).getUser(mCurrentId);

            // Define pattern of polyline

            PolylineOptions polylineOptions = UtilHelper.getStandardPolylineOptions()
                    .add(me.getLastLocation())
                    .add(mSelectedUser.getLastLocation());

            mConnectingLine = mGoogleMap.addPolyline(polylineOptions);

            // Move camera to the position of selected user
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLng(mSelectedUser.getLastLocation());
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    // Hide user information

    private void hideUserInformation() {

        // Apply slide-out animation
        Animation slideOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // Make the view gone after the animation is finished
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mUserInfoView.setVisibility(View.GONE);
                mSelectedUser = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mUserInfoView.startAnimation(slideOut);

        // Hide the polyline from the map
        if (mConnectingLine != null) {
            mConnectingLine.remove();
            mConnectingLine = null;
        }
    }

    // Start settings activity

    private void startSettingsActivity() {

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_SETTINGS_ACTIVITY);
    }

    // Start fare activity

    private void startFareActivity() {

        Intent intent = new Intent(this, FareActivity.class);
        intent.putExtra(FareActivity.EXTRA_MY_LATITUDE, mUserLocation.getLatitude());
        intent.putExtra(FareActivity.EXTRA_MY_LONGITUDE, mUserLocation.getLongitude());
        startActivity(intent);
    }

    // Background Thread for Loading Taxi Data

    private Thread mLoadTaxiDataThread;

    private final Runnable mLoadTaxiData = () -> {

        // Load taxi data
        new SingaporeTaxiApiHelper(HomeActivity.this)
                .loadTaxiData((taxiList, dateTime) -> {

                    // When taxi data is ready, update taxi markers on the map
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> onUpdateTaxiData(taxiList, dateTime));
                });
    };

    // Location Callback

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (locationResult == null) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Failed to get location")
                        .setMessage("This app needs High accuracy location mode " +
                                "to get device location. " +
                                "Please go Settings -> Location mode and set " +
                                "\"High accuracy (GPS and networks)\" mode")
                        .show();
                return;
            }

            // Get last location
            mUserLocation = locationResult.getLastLocation();
            if (mUserLocation == null) {
                return;
            }

            LatLng latLng = new LatLng(
                    mUserLocation.getLatitude(), mUserLocation.getLongitude());

            // Update my location in DB
            SQLiteHelper.getInstance(HomeActivity.this)
                    .updateUserLastLocation(mCurrentId, latLng);

            // Update person markers on the map
            if (mGoogleMap != null) {
                updateUserMarkers();

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                mGoogleMap.moveCamera(cameraUpdate);

                Log.d("TAG", "onLocationResult1: ");
            } else {
                Log.d("TAG", "onLocationResult2: ");
            }
        }
    };

}