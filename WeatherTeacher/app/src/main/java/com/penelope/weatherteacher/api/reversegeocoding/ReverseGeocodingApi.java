package com.penelope.weatherteacher.api.reversegeocoding;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.penelope.weatherteacher.data.address.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import javax.inject.Inject;

public class ReverseGeocodingApi {

    private static final String URL_FORMAT = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?X-NCP-APIGW-API-KEY-ID=[API_KEY_ID]&X-NCP-APIGW-API-KEY=[API_KEY]&output=json&coords=[LONGITUDE],[LATITUDE]";
    private static final String PLACEHOLDER_API_KEY_ID = "[API_KEY_ID]";
    private static final String PLACEHOLDER_API_KEY = "[API_KEY]";
    private static final String PLACEHOLDER_LONGITUDE = "[LONGITUDE]";
    private static final String PLACEHOLDER_LATITUDE = "[LATITUDE]";
    private static final String API_KEY_ID = "wjt59f8v8d";
    private static final String API_KEY = "0YdHdPEHO8QYFvDo80cmwfJdDtwY6yKZ0EhDfu4R";


    private final RequestQueue requestQueue;

    @Inject
    public ReverseGeocodingApi(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<Address> get(double lat, double lon) {

        MutableLiveData<Address> address = new MutableLiveData<>();

        // API 호출을 위한 URL 을 구성한다
        String url = URL_FORMAT
                .replace(PLACEHOLDER_API_KEY_ID, API_KEY_ID)
                .replace(PLACEHOLDER_API_KEY, API_KEY)
                .replace(PLACEHOLDER_LONGITUDE, String.valueOf(lon))
                .replace(PLACEHOLDER_LATITUDE, String.valueOf(lat));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                response -> {
                    try {
                        // result 배열
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() == 0) {
                            address.setValue(null);
                            return;
                        }

                        // 첫 번째 result 를 취한다
                        JSONObject result = results.getJSONObject(0);
                        // result 로부터 주소명을 추출한다
                        JSONObject region = result.getJSONObject("region");
                        JSONObject area1 = region.getJSONObject("area1");
                        JSONObject area2 = region.getJSONObject("area2");
                        JSONObject area3 = region.getJSONObject("area3");
                        String name1 = area1.getString("name");
                        String name2 = area2.getString("name");
                        String name3 = area3.getString("name");

                        String fullAddress = String.format(Locale.getDefault(),
                                "%s %s %s", name1, name2, name3);

                        address.setValue(new Address(fullAddress, name1, name2, name3));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> address.setValue(null));

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);

        return address;
    }
}


