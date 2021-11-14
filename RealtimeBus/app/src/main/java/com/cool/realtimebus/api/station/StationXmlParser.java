package com.cool.realtimebus.api.station;

import android.util.Xml;

import com.cool.realtimebus.data.station.Station;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationXmlParser {

    private static final String ns = null;

    // 주어진 XML 파싱하기
    public List<Station> parse(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResponse(parser);
        } finally {
            in.close();
        }
    }

    // response 태그 읽기
    private List<Station> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private List<Station> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private List<Station> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<Station> stations = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "items");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // item 태그 찾기
            if (name.equals("item")) {
                Station station = readItem(parser);
                if (station != null) {
                    stations.add(station);
                }
            } else {
                skip(parser);
            }
        }
        return stations;
    }

    // item 태그 읽기
    private Station readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

       String id = null;
       String title = null;
       double latitude = -1;
       double longitude = -1;
       int cityCode = -1;

        parser.require(XmlPullParser.START_TAG, ns, "item");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            switch (tag) {
                case "citycode":
                    cityCode = readInteger(parser);
                    break;
                case "gpslati":
                    latitude = readDouble(parser);
                    break;
                case "gpslong":
                    longitude = readDouble(parser);
                    break;
                case "nodeid":
                    id = readText(parser);
                    break;
                case "nodenm":
                    title = readText(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        if (id == null || title == null || latitude < 0 || longitude < 0 || cityCode < 0 || cityCode == 12) {
            return null;
        }

        return new Station(id, title, latitude, longitude, cityCode);
    }


    private double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        if (result.isEmpty()) {
            return -1;
        }
        return Double.parseDouble(result);
    }

    private int readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        if (result.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(result);
    }

    // 태그 안의 텍스트 읽기
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // 태그 건너뛰기
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

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
