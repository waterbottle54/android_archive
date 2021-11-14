package com.penelope.weatherteacher.api.geocoding;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class GeocodingApi {

    private static final String URL_FORMAT = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?X-NCP-APIGW-API-KEY-ID=[API_KEY_ID]&X-NCP-APIGW-API-KEY=[API_KEY]&output=json&query=[ADDRESS]";
    private static final String PLACEHOLDER_API_KEY_ID = "[API_KEY_ID]";
    private static final String PLACEHOLDER_API_KEY = "[API_KEY]";
    private static final String PLACEHOLDER_ADDRESS = "[ADDRESS]";
    private static final String API_KEY_ID = "wjt59f8v8d";
    private static final String API_KEY = "0YdHdPEHO8QYFvDo80cmwfJdDtwY6yKZ0EhDfu4R";


    private final RequestQueue requestQueue;

    @Inject
    public GeocodingApi(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void get(String addressName, OnSuccessListener<LatLng> onSuccessListener, OnFailureListener onFailureListener) {

        // API 호출을 위한 URL 구성하기
        String url = URL_FORMAT
                .replace(PLACEHOLDER_API_KEY_ID, API_KEY_ID)
                .replace(PLACEHOLDER_API_KEY, API_KEY)
                .replace(PLACEHOLDER_ADDRESS, addressName);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                response -> {
                    try {
                        // address 배열
                        JSONArray addresses = response.getJSONArray("addresses");
                        if (addresses.length() == 0) {
                            onSuccessListener.onSuccess(null);
                            return;
                        }

                        // 첫 번째 address 객체를 취함
                        JSONObject address = addresses.getJSONObject(0);
                        // address 로부터 GPS 좌표를 추출한다
                        String strLatitude = address.getString("y");
                        String strLongitude = address.getString("x");
                        double latitude;
                        double longitude;
                        try {
                            latitude = Double.parseDouble(strLatitude);
                            longitude = Double.parseDouble(strLongitude);
                        } catch (NumberFormatException e) {
                            onFailureListener.onFailure(e);
                            return;
                        }

                        onSuccessListener.onSuccess(new LatLng(latitude, longitude));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                onFailureListener::onFailure);

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

}
