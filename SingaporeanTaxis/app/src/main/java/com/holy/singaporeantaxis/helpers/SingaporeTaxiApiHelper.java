package com.holy.singaporeantaxis.helpers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.holy.singaporeantaxis.models.Taxi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SingaporeTaxiApiHelper {

    // URL of API
    public static final String URL = "https://api.data.gov.sg/v1/transport/taxi-availability";

    // KEY of JSON objects and arrays
    public static final String KEY_FEATURES = "features";
    public static final String KEY_GEOMETRY = "geometry";
    public static final String KEY_PROPERTIES = "properties";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COORDINATES = "coordinates";

    // Callback when called loading is finished
    public interface OnTaxiDataReadyListener {
        void onTaxiDataReady(List<Taxi> taxiList, LocalDateTime dateTime);
    }

    // Request queue
    private final RequestQueue requestQueue;


    public SingaporeTaxiApiHelper(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void loadTaxiData(OnTaxiDataReadyListener callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL,
                null,
                response -> {
                    try {
                        List<Taxi> taxiList = new ArrayList<>();

                        // Get "features" array from the root (response) object
                        JSONArray features = response.getJSONArray(KEY_FEATURES);

                        // Get feature object (the only element of the "feature" array)
                        JSONObject feature = features.getJSONObject(0);

                        // Get "geometry" and "properties" objects from feature object
                        JSONObject geometry = feature.getJSONObject(KEY_GEOMETRY);
                        JSONObject properties = feature.getJSONObject(KEY_PROPERTIES);

                        // Get timestamp of data from "properties" object
                        String timestamp = properties.getString(KEY_TIMESTAMP);
                        LocalDateTime dateTime = LocalDateTime.parse(
                                timestamp.substring(0, timestamp.length() - 6));

                        // Get "coordinates" array from "geometry" object
                        JSONArray coordinates = geometry.getJSONArray(KEY_COORDINATES);

                        for (int i = 0; i < coordinates.length(); i++) {

                            // Get each coordinate array from "coordinates" array
                            JSONArray coord = coordinates.getJSONArray(i);

                            // Get longitude and latitude from each coordinate array
                            double longitude = coord.getDouble(0);
                            double latitude = coord.getDouble(1);

                            // Make taxi object and insert it into the list
                            Taxi taxi = new Taxi(latitude, longitude);
                            taxiList.add(taxi);
                        }


                        // Loading is finished, invoke the callback
                        callback.onTaxiDataReady(taxiList, dateTime);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

}
