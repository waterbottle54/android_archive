package com.holy.batterystation.helpers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naver.maps.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionsJsonParser {

    private static final String URL_FORMAT = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?X-NCP-APIGW-API-KEY-ID=[API_KEY_ID]&X-NCP-APIGW-API-KEY=[API_KEY]&start=[START_LONGITUDE],[START_LATITUDE]&goal=[GOAL_LONGITUDE],[GOAL_LATITUDE]&option=trafast";
    private static final String PLACEHOLDER_API_KEY_ID = "[API_KEY_ID]";
    private static final String PLACEHOLDER_API_KEY = "[API_KEY]";
    private static final String PLACEHOLDER_START_LONGITUDE = "[START_LONGITUDE]";
    private static final String PLACEHOLDER_START_LATITUDE = "[START_LATITUDE]";
    private static final String PLACEHOLDER_GOAL_LONGITUDE = "[GOAL_LONGITUDE]";
    private static final String PLACEHOLDER_GOAL_LATITUDE = "[GOAL_LATITUDE]";

    private static final String API_KEY_ID = "da26ky7d4e";
    private static final String API_KEY = "r0vM5QYRcrcjVf5e9jIhMelw2HU3cqTq6y9GJlLJ";


    public interface OnParsingCompleteListener {
        void onParsingSucceed(int distance, int time, List<LatLng> waypointList);
        void onParsingFailed();
    }


    private final RequestQueue requestQueue;


    public DirectionsJsonParser(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void parse(double startLongitude, double startLatitude,
                      double goalLongitude, double goalLatitude,
                      OnParsingCompleteListener listener) {

        String url = URL_FORMAT
                .replace(PLACEHOLDER_API_KEY_ID, API_KEY_ID)
                .replace(PLACEHOLDER_API_KEY, API_KEY)
                .replace(PLACEHOLDER_START_LONGITUDE, String.valueOf(startLongitude))
                .replace(PLACEHOLDER_START_LATITUDE, String.valueOf(startLatitude))
                .replace(PLACEHOLDER_GOAL_LONGITUDE, String.valueOf(goalLongitude))
                .replace(PLACEHOLDER_GOAL_LATITUDE, String.valueOf(goalLatitude));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                response -> {
                    try {

                        int code = response.getInt("code");
                        if (code != 0) {
                            listener.onParsingFailed();
                        }

                        JSONObject route = response.getJSONObject("route");
                        JSONArray trafast = route.getJSONArray("trafast");
                        JSONObject data = trafast.getJSONObject(0);

                        JSONObject summary = data.getJSONObject("summary");
                        int distance = summary.getInt("distance");
                        int duration = summary.getInt("duration");

                        JSONArray path = data.getJSONArray("path");
                        List<LatLng> waypointList = new ArrayList<>();
                        for (int i = 0; i < path.length(); i++) {
                            JSONArray waypoint = path.getJSONArray(i);
                            double longitude = waypoint.getDouble(0);
                            double latitude = waypoint.getDouble(1);
                            waypointList.add(new LatLng(latitude, longitude));
                        }

                        listener.onParsingSucceed(distance, duration, waypointList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> listener.onParsingFailed());

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

}
