package com.penelope.ecar.api.place;

import androidx.annotation.WorkerThread;

import com.penelope.ecar.models.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// 네이버 Geocoding API 를 이용하여 GPS 좌표로부터 상세한 위치정보 (Place) 를 획득

public class PlaceApi {

    public static final String URL_FORMAT = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?output=json&coords={LONGITUDE},{LATITUDE}";
    public static final String ARG_LATITUDE = "{LATITUDE}";
    public static final String ARG_LONGITUDE = "{LONGITUDE}";
    public static final String API_ID = "tn4a341aja";
    public static final String API_KEY = "LfZIfgkjwPsxJFNrZUJafQx5Nr7KE3y3N3uQofB7";


    @WorkerThread
    public static Place get(double latitude, double longitude) {

        try {
            // 요청 URL 구성
            String strUrl = URL_FORMAT
                    .replace(ARG_LATITUDE, String.valueOf(latitude))
                    .replace(ARG_LONGITUDE, String.valueOf(longitude));

            // Http 연결 및 Header 에 API ID, KEY 추가
            URL url = new URL(strUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", API_ID);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", API_KEY);

            // 결과 (Json) 받기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // Json 을 파싱하여 Place 리턴
            JSONObject response = new JSONObject(sb.toString());
            return PlaceJsonParser.parse(response);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
