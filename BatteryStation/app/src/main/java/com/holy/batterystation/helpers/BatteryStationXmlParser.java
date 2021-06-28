package com.holy.batterystation.helpers;

import android.util.Xml;

import com.holy.batterystation.models.BatteryStation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BatteryStationXmlParser {

    public static final String TAG_STATION_ID = "csId";
    public static final String TAG_ADDRESS = "addr";
    public static final String TAG_CHARGE_TYPE = "chargeTp";
    public static final String TAG_CP_STATUS = "cpStat";
    public static final String TAG_CP_TYPE = "cpTp";
    public static final String TAG_STATION_NAME = "csNm";
    public static final String TAG_LATITUDE = "lat";
    public static final String TAG_LONGITUDE = "longi";

    private static final String ns = null;


    public List<BatteryStation> parse(InputStream in) throws XmlPullParserException, IOException {
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
    private List<BatteryStation> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private List<BatteryStation> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {

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
    private List<BatteryStation> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<BatteryStation> batteryStationList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "items");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // item 태그 찾기
            if (name.equals("item")) {
                BatteryStation batteryStation = readItem(parser);
                if (batteryStation != null) {
                    batteryStationList.add(batteryStation);
                }
            } else {
                skip(parser);
            }
        }
        return batteryStationList;
    }

    private BatteryStation readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

        int stationId = -1;
        String address = null;
        int chargeType = -1;
        int cpStatus = -1;
        int cpType = -1;
        String stationName = null;
        double latitude = -1;
        double longitude = -1;

        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // 태그 찾기
            switch (name) {
                case TAG_STATION_ID:
                    stationId = readInteger(parser);
                    break;
                case TAG_ADDRESS:
                    address = readText(parser);
                    break;
                case TAG_CHARGE_TYPE:
                    chargeType = readInteger(parser);
                    break;
                case TAG_CP_STATUS:
                    cpStatus = readInteger(parser);
                    break;
                case TAG_CP_TYPE:
                    cpType = readInteger(parser);
                    break;
                case TAG_STATION_NAME:
                    stationName = readText(parser);
                    break;
                case TAG_LATITUDE:
                    latitude = readDouble(parser);
                    break;
                case TAG_LONGITUDE:
                    longitude = readDouble(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        if (stationId == -1 || address == null || chargeType == -1 || cpStatus == -1 || cpType == -1
                || stationName == null || latitude == -1 || longitude == -1) {
            return null;
        }

        return new BatteryStation(
                stationId,
                address,
                chargeType,
                cpStatus,
                cpType,
                stationName,
                latitude,
                longitude
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
