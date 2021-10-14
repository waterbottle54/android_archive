package com.holy.deliveryapp.helpers;

import android.os.AsyncTask;

import com.holy.deliveryapp.models.Local;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class LocalConnectionTask extends AsyncTask<String, Void, List<Local>> {

    private static final String TAG = "LocalConnectionTask";

    public static final String URL_FORMAT = "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=FD6&page=[PAGE]&size=15&sort=distance&x=[LONGITUDE]&y=[LATITUDE]&radius=[RADIUS]";
    public static final String PLACEHOLDER_PAGE = "[PAGE]";
    public static final String PLACEHOLDER_LATITUDE = "[LATITUDE]";
    public static final String PLACEHOLDER_LONGITUDE = "[LONGITUDE]";
    public static final String PLACEHOLDER_RADIUS = "[RADIUS]";
    public static final String AUTHORIZATION_FORMAT = "KakaoAK [REST_API_KEY]";
    public static final String PLACEHOLDER_REST_API_KEY = "[REST_API_KEY]";
    public static final String REST_API_KEY = "a1adcbb8672a1bd0e6a2d2151a8b39fc";


    public interface OnParsingCompleteListener {
        void onParsingSuccess(List<Local> localList, boolean end);
        void onParsingFailure();
    }


    private final OnParsingCompleteListener onParsingCompleteListener;
    private final int page;
    private final double latitude;
    private final double longitude;
    private final double radius;
    private final boolean[] end = new boolean[1];


    public LocalConnectionTask(int page, double latitude, double longitude, double radius,
                               OnParsingCompleteListener onParsingCompleteListener) {
        this.page = page;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.onParsingCompleteListener = onParsingCompleteListener;
    }

    @Override
    protected List<Local> doInBackground(String... strings) {

        try {
            String strUrl = URL_FORMAT
                    .replace(PLACEHOLDER_PAGE, String.valueOf(page))
                    .replace(PLACEHOLDER_LATITUDE, String.valueOf(latitude))
                    .replace(PLACEHOLDER_LONGITUDE, String.valueOf(longitude))
                    .replace(PLACEHOLDER_RADIUS, String.valueOf(radius));

            String strAuthorization = AUTHORIZATION_FORMAT.replace(PLACEHOLDER_REST_API_KEY, REST_API_KEY);

            URL url = new URL(strUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", strAuthorization);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject response = new JSONObject(sb.toString());
            return LocalJsonParser.parse(response, end);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Local> localList) {
        super.onPostExecute(localList);

        if (localList == null) {
            onParsingCompleteListener.onParsingFailure();
            return;
        }

        onParsingCompleteListener.onParsingSuccess(localList , end[0]);
    }

}
