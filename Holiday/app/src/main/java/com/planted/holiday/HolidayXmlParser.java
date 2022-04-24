package com.planted.holiday;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayXmlParser {

    private static final String ns = null;

    public static List<Holiday> parse(InputStream in) throws XmlPullParserException, IOException {
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
    private static List<Holiday> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private static List<Holiday> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private static List<Holiday> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<Holiday> holidays = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "items");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // item 태그 찾기
            if (name.equals("item")) {
                Holiday holiday = readItem(parser);
                if (holiday != null) {
                    holidays.add(holiday);
                }
            } else {
                skip(parser);
            }
        }
        return holidays;
    }

    private static Holiday readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

        /*
        private final String name;      // 공휴일 이름
        private final int year;         // 년
        private final int month;        // 월
        private final int dayOfMonth;   // 일
        private final int dayOfWeek;    // 요일 (0:월 ~ 6:일)
        * */

        String name = null;
        String strDate = null;

        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // 태그 찾기
            switch (tag) {
                case "dateName":
                    name = readText(parser);
                    break;
                case "locdate":
                    strDate = readText(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        if (name == null || strDate == null || strDate.length() != 8) {
            return null;
        }

        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(4, 6));
        int dayOfMonth = Integer.parseInt(strDate.substring(6, 8));
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        int dayOfWeek = date.getDayOfWeek().getValue();

        return new Holiday(name, year, month, dayOfMonth, dayOfWeek);
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