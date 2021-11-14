package com.cool.realtimebus.api.arrival;

import android.util.Xml;

import com.cool.realtimebus.data.arrival.Arrival;
import com.cool.realtimebus.data.station.Station;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ArrivalXmlParser {

    private static final String ns = null;

    // 주어진 XML 파싱하기
    public List<Arrival> parse(InputStream in) throws XmlPullParserException, IOException {

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
    private List<Arrival> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private List<Arrival> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private List<Arrival> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<Arrival> arrivals = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "items");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // item 태그 찾기
            if (name.equals("item")) {
                Arrival arrival = readItem(parser);
                if (arrival != null) {
                    arrivals.add(arrival);
                }
            } else {
                skip(parser);
            }
        }
        return arrivals;
    }

    // item 태그 읽기
    private Arrival readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

        String routeId = null;
        String routeTitle = null;
        int secondsLeft = -1;
        int stationsLeft = -1;

        parser.require(XmlPullParser.START_TAG, ns, "item");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            switch (tag) {
                case "arrprevstationcnt":
                    stationsLeft = readInteger(parser);
                    break;
                case "arrtime":
                    secondsLeft = readInteger(parser);
                    break;
                case "routeid":
                    routeId = readText(parser);
                    break;
                case "routeno":
                    routeTitle = readText(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Arrival(routeId, routeTitle, secondsLeft, stationsLeft);
    }


    private double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        if (result.isEmpty()) {
            return 0;
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
