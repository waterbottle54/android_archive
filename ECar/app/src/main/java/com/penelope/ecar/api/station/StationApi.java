package com.penelope.ecar.api.station;

import androidx.annotation.WorkerThread;

import com.penelope.ecar.models.Station;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StationApi {

    public static final String URL_FORMAT = "http://apis.data.go.kr/B552584/EvCharger/getChargerInfo?serviceKey={SERVICE_KEY}&pageNo=1&numOfRows=10&zcode={CITY_CODE}";
    public static final String ARG_SERVICE_KEY = "{SERVICE_KEY}";
    public static final String ARG_CITY_CODE = "{CITY_CODE}";
    public static final String SERVICE_KEY = "MtVBdtw16Ez1YFEXjvMpB0Zws4eaThA35edKG44NT6A0P3gsproUZBkpaFUUVrHmaPUzkazSncH9iukFY%2BdQfw%3D%3D";


    @WorkerThread
    public static List<Station> get(String cityCode) {

        try {
            // 요청 URL 구성
            String strUrl = URL_FORMAT
                    .replace(ARG_SERVICE_KEY, SERVICE_KEY)
                    .replace(ARG_CITY_CODE, cityCode);

            // Http 연결
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 결과 (Xml) 를 파싱하여 충전소 리스트 리턴
            return StationXmlParser.parse(conn.getInputStream());

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
