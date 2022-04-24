package com.penelope.ecar.api.station;

import android.util.Xml;

import com.penelope.ecar.models.Station;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationXmlParser {

    private static final String ns = null;


    // StationApi 에서 전달된 Xml 파싱하여 충전소 리스트 리턴

    public static List<Station> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            String name = parser.getName();
            if (name.equals("response")) {
                return readResponse(parser);
            }
        } finally {
            in.close();
        }
        return null;
    }

    // response 태그 읽기
    private static List<Station> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // body 태그 찾기
            if (name.equals("body")) {
                return readBody(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    // body 태그 읽기
    private static List<Station> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "body");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // items 태그 찾기
            if (name.equals("items")) {
                return readItems(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    // items 태그 읽기
    private static List<Station> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<Station> StationList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "items");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // item 태그 찾기
            if (name.equals("item")) {
                Station Station = readItem(parser);
                if (Station != null) {
                    StationList.add(Station);
                }
            } else {
                skip(parser);
            }
        }
        return StationList;
    }

    private static Station readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

        String stationId = null;
        String stationName = null;
        String address = null;
        double latitude = -1;
        double longitude = -1;
        int output = -1;
        String locationDescription = "";
        String timeDescription = "";
        int chargerStatus = -1;

        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // 태그 찾기
            switch (name) {
                case "statId":
                    stationId = readText(parser);
                    break;
                case "statNm":
                    stationName = readText(parser);
                    break;
                case "addr":
                    address = readText(parser);
                    break;
                case "lat":
                    latitude = readDouble(parser);
                    break;
                case "lng":
                    longitude = readDouble(parser);
                    break;
                case "output":
                    output = readInteger(parser);
                    break;
                case "location":
                    locationDescription = readText(parser);
                    break;
                case "useTime":
                    timeDescription = readText(parser);
                    break;
                case "stat":
                    chargerStatus = readInteger(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        if (stationId == null || stationName == null || address == null ||
                latitude < 0 || longitude < 0 || output < 0 ||
                locationDescription == null || timeDescription == null || chargerStatus < 0) {
            return null;
        }

        return new Station(
                stationId, stationName, address, latitude, longitude,
                output, locationDescription, timeDescription, chargerStatus
        );
    }

    // 태그 안의 텍스트 읽기
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // 태그 안의 실수값 읽기
    private static double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        if (result.trim().isEmpty()) {
            return 0;
        }
        return Double.parseDouble(result);
    }

    // 태그 안의 정수값 읽기
    private static int readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        if (result.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(result);
    }

    // 태그 건너뛰기
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
