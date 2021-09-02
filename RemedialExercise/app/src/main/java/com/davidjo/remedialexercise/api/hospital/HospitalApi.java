package com.davidjo.remedialexercise.api.hospital;

import android.util.Log;

import androidx.annotation.WorkerThread;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

public class HospitalApi {

    public static final String URL_FORMAT = "http://apis.data.go.kr/B551182/hospInfoService/getHospBasisList?serviceKey=[SERVICE_KEY]&numOfRows=[NUM]&xPos=[XPOS]&yPos=[YPOS]&radius=[RAD]";
    public static final String PLACEHOLDER_SERVICE_KEY = "[SERVICE_KEY]";
    public static final String PLACEHOLDER_NUM = "[NUM]";
    public static final String PLACEHOLDER_XPOS = "[XPOS]";
    public static final String PLACEHOLDER_YPOS = "[YPOS]";
    public static final String PLACEHOLDER_RADIUS = "[RAD]";
    public static final String SERVICE_KEY = "3U5FAOrbKwtTBXYzH54eZ0jeVY0FeCjK4xcoXMOBgH%2BOJq2omZYKSRYMqTjQTg5ytsAbkcmF3gDycB2zjPMprA%3D%3D";

    private double latitude;
    private double longitude;
    private double radius;

    @Inject
    public HospitalXmlParser xmlParser;

    @Inject
    public HospitalApi(HospitalXmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public HospitalApi setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public HospitalApi setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public HospitalApi setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @WorkerThread
    public List<HospitalNetworkEntity> getHospitals() {

        String url = URL_FORMAT
                .replace(PLACEHOLDER_SERVICE_KEY, SERVICE_KEY)
                .replace(PLACEHOLDER_NUM, String.valueOf(10))
                .replace(PLACEHOLDER_XPOS, String.valueOf(longitude))
                .replace(PLACEHOLDER_YPOS, String.valueOf(latitude))
                .replace(PLACEHOLDER_RADIUS, String.valueOf(radius));

        try {
            // URL 을 다운로드받아 InputStream 획득
            InputStream inputStream = downloadUrl(url);

            // InputStream 을 parse 하여 리스트 구성
            return xmlParser.parse(inputStream);

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
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
