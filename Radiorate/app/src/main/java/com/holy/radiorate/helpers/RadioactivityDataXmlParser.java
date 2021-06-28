package com.holy.radiorate.helpers;

import android.util.Xml;

import com.holy.radiorate.models.RadioactivityData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class RadioactivityDataXmlParser {

    public static final String TAG_EXPLANATION = "expl";
    public static final String TAG_NAME = "name";
    public static final String TAG_TIME = "time";
    public static final String TAG_VALUE = "value";

    private static final String ns = null;


    public List<RadioactivityData> parse(InputStream in) throws XmlPullParserException, IOException {

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
    private List<RadioactivityData> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("header")) {
                // header 태그 읽기
                if (!readHeader(parser)) {
                    return null;
                }
            } else if (name.equals("body")) {
                // body 태그 읽기
                return readBody(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    private boolean readHeader(XmlPullParser parser) throws XmlPullParserException, IOException {

        String resultCode = null;

        parser.require(XmlPullParser.START_TAG, ns, "header");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("resultCode")) {
                resultCode = readText(parser);
            } else {
                skip(parser);
            }
        }

        if (resultCode == null) {
            return false;
        }

        return resultCode.equals("00");
    }

    // body 태그 읽기
    private List<RadioactivityData> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {


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
    private List<RadioactivityData> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<RadioactivityData> radioactivityDataList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "items");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // item 태그 찾기
            if (name.equals("item")) {
                RadioactivityData radioactivityData = readItem(parser);
                if (radioactivityData != null) {
                    radioactivityDataList.add(radioactivityData);
                }
            } else {
                skip(parser);
            }
        }
        return radioactivityDataList;
    }

    private RadioactivityData readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

        String explanation = null;
        String name = null;
        String strTime = null;
        double value = -1;

        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // 태그 찾기
            switch (tagName) {
                case TAG_EXPLANATION:
                    explanation = readText(parser);
                    break;
                case TAG_NAME:
                    name = readText(parser);
                    break;
                case TAG_TIME:
                    strTime = readText(parser);
                    break;
                case TAG_VALUE:
                    value = readDouble(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        if (explanation == null || name == null || strTime == null || value == -1) {
            return null;
        }

        String[] tokens = strTime.split("-| |:");
        if (tokens.length < 5) {
            return null;
        }

        Calendar calendar = new GregorianCalendar(
                Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]) - 1,
                Integer.parseInt(tokens[2]),
                Integer.parseInt(tokens[3]),
                Integer.parseInt(tokens[4]),
                0
        );

        return new RadioactivityData(
                explanation,
                name,
                calendar.getTime(),
                value
        );
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

    // 태그 안의 실수값 읽기
    private double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {

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
    private int readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {

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
