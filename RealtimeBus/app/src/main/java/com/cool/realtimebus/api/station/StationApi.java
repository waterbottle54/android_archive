package com.cool.realtimebus.api.station;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cool.realtimebus.data.station.Station;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StationApi {

    public static final String URL_FORMAT = "http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey=[SERVICE_KEY]&gpsLati=[LATITUDE]&gpsLong=[LONGITUDE]";
    public static final String PLACEHOLDER_SERVICE_KEY = "[SERVICE_KEY]";
    public static final String PLACEHOLDER_LATITUDE = "[LATITUDE]";
    public static final String PLACEHOLDER_LONGITUDE = "[LONGITUDE]";
    public static final String SERVICE_KEY = "xg9o0%2BtaSHcj9cuhS%2Ff6KtSuvLA2Xq7AEDZl7HtWrcGUpnbC38kPJmvDXBg1kPRaIL%2B1DTz1JYhXcUIlVovttA%3D%3D";

    private final StationXmlParser xmlParser = new StationXmlParser();

    public LiveData<List<Station>> get(double latitude, double longitude) {

        String url = URL_FORMAT
                .replace(PLACEHOLDER_SERVICE_KEY, SERVICE_KEY)
                .replace(PLACEHOLDER_LATITUDE, String.valueOf(latitude))
                .replace(PLACEHOLDER_LONGITUDE, String.valueOf(longitude));

        Log.d("TAG", "get: " + url);

        MutableLiveData<List<Station>> stations = new MutableLiveData<>();

        new Thread(() -> {
            try {
                // URL 을 다운로드받아 InputStream 획득
                InputStream inputStream = downloadUrl(url);

                // InputStream 을 parse 하여 리스트 구성
                List<Station> stationList = xmlParser.parse(inputStream);
                if (stationList.size() > 5) {
                    stationList = stationList.subList(0, 5);
                }

                stations.postValue(stationList);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
                stations.setValue(null);
            }
        }).start();

        return stations;
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
