package com.holy.batterystation.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.holy.batterystation.MainActivity;
import com.holy.batterystation.models.BatteryStation;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("deprecation")
public class BatteryStationXmlTask extends AsyncTask<String, Void, List<BatteryStation>> {

    public static final String URL_FORMAT = "http://openapi.kepco.co.kr/service/EvInfoServiceV2/getEvSearchList?serviceKey=[SERVICE_KEY]&pageNo=1&numOfRows=100&addr=[ADDRESS]";
    public static final String PLACEHOLDER_SERVICE_KEY = "[SERVICE_KEY]";
    public static final String PLACEHOLDER_ADDRESS = "[ADDRESS]";

    public static final String SERVICE_KEY = "3U5FAOrbKwtTBXYzH54eZ0jeVY0FeCjK4xcoXMOBgH%2BOJq2omZYKSRYMqTjQTg5ytsAbkcmF3gDycB2zjPMprA%3D%3D";


    public interface OnParsingCompleteListener {
        void onParsingSucceed(List<BatteryStation> batteryStationList);
        void onParsingFailed();
    }


    private final WeakReference<MainActivity> reference;
    private final OnParsingCompleteListener onParsingCompleteListener;


    public BatteryStationXmlTask(MainActivity activity, OnParsingCompleteListener onParsingCompleteListener) {
        reference = new WeakReference<>(activity);
        this.onParsingCompleteListener = onParsingCompleteListener;
    }

    @Override
    protected List<BatteryStation> doInBackground(String... strings) {

        String address = strings[0];
        String url = URL_FORMAT
                .replace(PLACEHOLDER_SERVICE_KEY, SERVICE_KEY)
                .replace(PLACEHOLDER_ADDRESS, address);

        try {
            // URL 을 다운로드받아 InputStream 획득
            InputStream inputStream = downloadUrl(url);

            // InputStream 을 parse 하여 리스트 구성
            return new BatteryStationXmlParser().parse(inputStream);

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<BatteryStation> batteryStationList) {

        MainActivity activity = reference.get();
        if (activity == null) {
            return;
        }

        if (batteryStationList != null) {
            onParsingCompleteListener.onParsingSucceed(batteryStationList);
        } else {
            onParsingCompleteListener.onParsingFailed();
        }
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
