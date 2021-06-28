package com.holy.radiorate.helpers;

import android.os.AsyncTask;

import com.holy.radiorate.models.RadioactivityData;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("deprecation")
public class RadioactivityDataXmlTask extends AsyncTask<String, Void, List<RadioactivityData>> {

    public static final String URL_FORMAT = "http://data.khnp.co.kr/environ/service/realtime/radiorate?serviceKey=[SERVICE_KEY]&genName=[GENERATOR_NAME]";
    public static final String PLACEHOLDER_SERVICE_KEY = "[SERVICE_KEY]";
    public static final String PLACEHOLDER_GENERATOR_NAME = "[GENERATOR_NAME]";

    public static final String SERVICE_KEY = "3U5FAOrbKwtTBXYzH54eZ0jeVY0FeCjK4xcoXMOBgH%2BOJq2omZYKSRYMqTjQTg5ytsAbkcmF3gDycB2zjPMprA%3D%3D";


    public interface OnParsingCompleteListener {
        void onParsingSucceed(List<RadioactivityData> radioactivityDataList);
        void onParsingFailed();
    }


    private final OnParsingCompleteListener onParsingCompleteListener;


    public RadioactivityDataXmlTask(OnParsingCompleteListener onParsingCompleteListener) {
        this.onParsingCompleteListener = onParsingCompleteListener;
    }

    @Override
    protected List<RadioactivityData> doInBackground(String... strings) {

        String generatorName = strings[0];
        String url = URL_FORMAT
                .replace(PLACEHOLDER_SERVICE_KEY, SERVICE_KEY)
                .replace(PLACEHOLDER_GENERATOR_NAME, generatorName);

        try {
            // URL 을 다운로드받아 InputStream 획득
            InputStream inputStream = downloadUrl(url);

            // InputStream 을 parse 하여 리스트 구성
            return new RadioactivityDataXmlParser().parse(inputStream);

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<RadioactivityData> radioactivityDataList) {

        if (radioactivityDataList != null) {
            onParsingCompleteListener.onParsingSucceed(radioactivityDataList);
        } else {
            onParsingCompleteListener.onParsingFailed();
        }
    }

    private InputStream downloadUrl(String strUrl) throws IOException {

        URL url = new URL(strUrl);
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
