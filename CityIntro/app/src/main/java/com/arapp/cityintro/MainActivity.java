package com.arapp.cityintro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.arapp.cityintro.models.CityInfo;
import com.arapp.cityintro.services.UtilService;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        Scene.OnUpdateListener,
        OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int REQUEST_LOCATION_PERMISSION = 200;

    // SceneForm AR Fragment
    private ArFragment mARFragment;

    // Card View Renderables (each represents a city)
    private List<ViewRenderable> mCardRenderables = new ArrayList<>();
    // Nodes for Card Renderables
    private final List<TransformableNode> mCardNodes = new ArrayList<>();
    // Index of card currently being stared.
    private int mHotCardIndex = -1;
    private int mDirectionInDegrees = -1;
    private int mDistantInMeters = -1;
    // Control variables
    private boolean mHasFinishedLoading = false;
    private boolean mHasPlacedRenderables = false;

    // City information list
    private List<CityInfo> mCityInfoList = new ArrayList<>();
    // City logo list (resource ID)
    private final List<Integer> mCityLogoList = new ArrayList<>();
    // City position list
    private final List<PointF> mCityPositionList = new ArrayList<>();
    // GPS Location
    private FusedLocationSource mLocationSource;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This app requires OpenGL ES 3.0 or above.
        if (!checkIsSupportedDeviceOrFinish(this))
            return;

        // Check Naver Location permission.
        mLocationSource = new FusedLocationSource(this, REQUEST_LOCATION_PERMISSION);

        // Initialize Naver Map.
        initNaverMap();

        // initialize city data
        initCityData();

        // Initialize AR Fragment
        setContentView(R.layout.activity_main);
        mARFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        assert mARFragment != null;
        mARFragment.getArSceneView().getScene().addOnUpdateListener(this);
        mARFragment.setOnSessionInitializationListener(session -> {
            // build card renderables.
            buildCardRenderables(mCityInfoList, mCityLogoList);     // Takes a while.
        });
    }

    private void initCityData() {

        // City information
        mCityInfoList = UtilService.LoadCityInfoLFromJson(this, "cityinfo.json");

        // City logos
        TypedArray logos = getResources().obtainTypedArray(R.array.city_logos);
        for (int i = 0; i < logos.length(); i++) {
            mCityLogoList.add(logos.getResourceId(i, 0));
        }
        logos.recycle();

        // City position : give likely values
        mCityPositionList.add(new PointF(2, 4));
        mCityPositionList.add(new PointF(4, 3));
        mCityPositionList.add(new PointF(4, 0));
        mCityPositionList.add(new PointF(4, 1));
        mCityPositionList.add(new PointF(-3, -4));
        mCityPositionList.add(new PointF(-3, -2));
        mCityPositionList.add(new PointF(-1, -3));
        mCityPositionList.add(new PointF(3, -3));
        mCityPositionList.add(new PointF(2, -4));
    }

    @Override
    public void onUpdate(FrameTime frameTime) {

        // Can't do anything yet.
        if (!mHasFinishedLoading)
            return;

        ArSceneView sceneView = mARFragment.getArSceneView();
        Frame frame = sceneView.getArFrame();
        com.google.ar.sceneform.Camera sfCamera = sceneView.getScene().getCamera();

        if (frame == null)
            return;

        // Place card renderables if not been placed yet
        if (!mHasPlacedRenderables) {
            if (tryPlaceCardNodes(frame)) {
                mHasPlacedRenderables = true;
            }
        }

        else {
            // Rotate card renderables so that they face Camera directly.
            rotateCardNodes(sceneView);

            // Find which card is being stared by user.
            mHotCardIndex = findHotCardIndex(sceneView);

            // Calculate distance to the indicated city.
            if (mHotCardIndex != -1 && mHotCardIndex < mCityInfoList.size()) {
                CityInfo cityInfo = mCityInfoList.get(mHotCardIndex);
                Location locationGPS = mLocationSource.getLastLocation();
                if (locationGPS != null) {
                    float[] result = {0};
                    Location.distanceBetween(
                            locationGPS.getLatitude(), locationGPS.getLongitude(),
                            cityInfo.getLat(), cityInfo.getLon(), result);
                    mDistantInMeters = (int) result[0];
                } else {
                    Log.d(TAG, "onUpdate: GPS NOT WORKING.");
                }
            }
        }

        // Calculate direction of camera
        Vector3 forward = sfCamera.getForward();
        double direction = Math.atan2(forward.z, forward.x);
        mDirectionInDegrees = (int)(direction * 180 / Math.PI) + 180;   // 0~360

        // Update information UI to reflect those changes.
        updateInfoUI();
    }

    private void updateInfoUI() {

        TextView cityName = findViewById(R.id.text_city_name);
        TextView cityDesc1 = findViewById(R.id.text_city_desc_1);
        TextView cityDesc2 = findViewById(R.id.text_city_desc_2);
        TextView cityDistanceLocation = findViewById(R.id.text_distance_and_location);
        TextView directionText = findViewById(R.id.text_direction);

        // Update city description text
        if (mHotCardIndex != -1 && mHotCardIndex < mCityInfoList.size()) {
            CityInfo cityInfo = mCityInfoList.get(mHotCardIndex);
            cityName.setText(cityInfo.getName());
            cityDesc1.setText(cityInfo.getDesc1());
            cityDesc2.setText(cityInfo.getDesc2());

            String strDistance = mDistantInMeters != -1
                    ? mDistantInMeters + "m" : "NO GPS";
            String strDistanceLocation = String.format(
                    Locale.getDefault(),
                    "%s (%.3f, %.3f)",
                    strDistance, (float) cityInfo.getLat(), (float) cityInfo.getLon()
            );
            cityDistanceLocation.setText(strDistanceLocation);
        }

        // Update direction text
        directionText.setText(UtilService.getDirectionString(mDirectionInDegrees));
    }

    private int findHotCardIndex(ArSceneView sceneView) {

        com.google.ar.sceneform.Camera sfCamera = sceneView.getScene().getCamera();

        // get Camera's sight direction
        Vector3 forward = sfCamera.getForward();

        for (int i = 0; i < mCardNodes.size(); i++) {
            // For each node, calculate relative position of card about camera
            Vector3 card = mCardNodes.get(i).getWorldPosition();
            Vector3 me = sfCamera.getWorldPosition();
            Vector3 relative = Vector3.subtract(card, me);

            // Normalize relative, forward vectors.
            relative = relative.normalized();
            forward = forward.normalized();

            // Calculate the distance between them.
            float distance = Vector3.subtract(relative, forward).length();

            // Check if it's close enough.
            if (distance < 0.2) {
                return i;
            }
        }
        // No result.
        return -1;
    }

    private void rotateCardNodes(ArSceneView sceneView) {

        Frame frame = sceneView.getArFrame();
        if (frame == null)
            return;

        Camera camera = frame.getCamera();
        com.google.ar.sceneform.Camera sfCamera
                = sceneView.getScene().getCamera();

        if (camera.getTrackingState() == TrackingState.TRACKING) {

            // Get camera position
            Vector3 cameraPosition = sfCamera.getWorldPosition();

            for (int i = 0; i < mCardNodes.size(); i++) {
                // Align each card with the line connecting camera and card.
                // (ignore Y component so that cards don't face sky or ground)
                TransformableNode transNode = mCardNodes.get(i);
                Vector3 cardPosition = transNode.getWorldPosition();
                Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
                if (direction.length() > 1) {
                    transNode.setLookDirection(direction);
                }
            }
        }
    }

    private boolean tryPlaceCardNodes(Frame frame) {

        Session session = mARFragment.getArSceneView().getSession();
        if (session == null)
            return false;

        if (frame.getCamera().getTrackingState() == TrackingState.TRACKING) {

            for (int i = 0; i < mCardRenderables.size(); i++) {

                // Calculate position on which each card will be placed
                // CityInfo cityInfo = mCityInfoList.get(i);

                PointF point = mCityPositionList.get(i);
                Pose pose = new Pose(
                        new float[]{ point.x, 0, point.y },
                        new float[]{ 1, 1, 1, 1 });

                // Create the anchor.
                Anchor anchor = session.createAnchor(pose);
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(mARFragment.getArSceneView().getScene());

                // Create the transformable node and add it to the anchor.
                TransformableNode transNode = new TransformableNode(
                        mARFragment.getTransformationSystem());
                transNode.setParent(anchorNode);
                transNode.setRenderable(mCardRenderables.get(i));
                transNode.select();
                mCardNodes.add(transNode);
            }
            return true;
        }
        return false;
    }

    private void buildCardRenderables(List<CityInfo> cityInfoList, List<Integer> cityLogoList) {

        mCardRenderables = new ArrayList<>();

        for (int i = 0; i < cityInfoList.size(); i++) {
            // Inflate a city card view for each city.
            View cityCard = View.inflate(this, R.layout.city_card, null);

            // Give it a logo and name.
            ImageView cityLogo = cityCard.findViewById(R.id.image_city_logo);
            TextView cityName = cityCard.findViewById(R.id.text_city_name);
            if (i < cityLogoList.size()) {
                // Basically the number of logo is the same with the number of city info.
                cityLogo.setImageResource(cityLogoList.get(i));
            }
            cityName.setText(cityInfoList.get(i).getName());

            // Build a card renderable with the card view.
            ViewRenderable.builder()
                    .setView(this, cityCard)
                    .build()
                    .thenAccept(renderable -> {
                        mCardRenderables.add(renderable);
                        if (mCardRenderables.size() == cityInfoList.size()) {
                            mHasFinishedLoading = true;
                        }
                    });
        }
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, openGlVersionString, Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    // Naver Map

    private void initNaverMap() {
        // Initialize Naver Map.
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // Set Naver Map to track device position.
        naverMap.setLocationSource(mLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // Hide unnecessary controls.
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setScaleBarEnabled(false);
    }



}