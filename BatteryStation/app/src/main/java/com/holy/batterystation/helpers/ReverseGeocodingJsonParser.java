package com.holy.batterystation.helpers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ReverseGeocodingJsonParser {

    private static final String URL_FORMAT = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?X-NCP-APIGW-API-KEY-ID=[API_KEY_ID]&X-NCP-APIGW-API-KEY=[API_KEY]&output=json&coords=[LONGITUDE],[LATITUDE]";
    private static final String PLACEHOLDER_API_KEY_ID = "[API_KEY_ID]";
    private static final String PLACEHOLDER_API_KEY = "[API_KEY]";
    private static final String PLACEHOLDER_LONGITUDE = "[LONGITUDE]";
    private static final String PLACEHOLDER_LATITUDE = "[LATITUDE]";

    private static final String API_KEY_ID = "da26ky7d4e";
    private static final String API_KEY = "r0vM5QYRcrcjVf5e9jIhMelw2HU3cqTq6y9GJlLJ";


    public interface OnParsingCompleteListener {
        void onParsingSucceed(String address, String address1, String address2, String address3);
        void onParsingFailed();
    }


    private final RequestQueue requestQueue;


    public ReverseGeocodingJsonParser(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void parse(double longitude, double latitude, OnParsingCompleteListener listener) {

        String url = URL_FORMAT
                .replace(PLACEHOLDER_API_KEY_ID, API_KEY_ID)
                .replace(PLACEHOLDER_API_KEY, API_KEY)
                .replace(PLACEHOLDER_LONGITUDE, String.valueOf(longitude))
                .replace(PLACEHOLDER_LATITUDE, String.valueOf(latitude));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                response -> {
                    try {

                        JSONArray results = response.getJSONArray("results");
                        if (results.length() == 0) {
                            listener.onParsingFailed();
                            return;
                        }

                        JSONObject result = results.getJSONObject(0);
                        JSONObject region = result.getJSONObject("region");
                        JSONObject area1 = region.getJSONObject("area1");
                        JSONObject area2 = region.getJSONObject("area2");
                        JSONObject area3 = region.getJSONObject("area3");
                        String name1 = area1.getString("name");
                        String name2 = area2.getString("name");
                        String name3 = area3.getString("name");

                        String address = String.format(Locale.getDefault(),
                                "%s %s %s", name1, name2, name3);

                        listener.onParsingSucceed(address, name1, name2, name3);

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
