package com.cool.realtimebus.api.arrival;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cool.realtimebus.data.arrival.Arrival;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class ArrivalApi {

    public static final String URL_FORMAT = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList?serviceKey=[SERVICE_KEY]&cityCode=[CITY_CODE]&nodeId=[STATION_ID]";
    public static final String PLACEHOLDER_SERVICE_KEY = "[SERVICE_KEY]";
    public static final String PLACEHOLDER_CITY_CODE = "[CITY_CODE]";
    public static final String PLACEHOLDER_STATION_ID = "[STATION_ID]";
    public static final String SERVICE_KEY = "xg9o0%2BtaSHcj9cuhS%2Ff6KtSuvLA2Xq7AEDZl7HtWrcGUpnbC38kPJmvDXBg1kPRaIL%2B1DTz1JYhXcUIlVovttA%3D%3D";

    private final ArrivalXmlParser xmlParser = new ArrivalXmlParser();

    public LiveData<List<Arrival>> get(int cityCode, String stationId) {

        String url = URL_FORMAT
                .replace(PLACEHOLDER_SERVICE_KEY, SERVICE_KEY)
                .replace(PLACEHOLDER_CITY_CODE, String.valueOf(cityCode))
                .replace(PLACEHOLDER_STATION_ID, stationId);

        Log.d("TAG", "get: " + url);

        MutableLiveData<List<Arrival>> arrivals = new MutableLiveData<>();

        new Thread(() -> {
            try {
                // URL 을 다운로드받아 InputStream 획득
                InputStream inputStream = downloadUrl(url);

                // InputStream 을 parse 하여 리스트 구성
                List<Arrival> arrivalList = xmlParser.parse(inputStream);
                arrivalList.sort(Comparator.comparingInt(Arrival::getSecondsLeft));

                arrivals.postValue(arrivalList);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
                arrivals.setValue(null);
            }
        }).start();

        return arrivals;
    }

    private InputStream downloadUrl(String strUrl) throws IOException {

        java.net.URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(60000 /* 밀리초 */);
        conn.setConnectTimeout(60000 /* 밀리초 */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // 쿼리 시작
        conn.connect();
        return conn.getInputStream();
    }

}
