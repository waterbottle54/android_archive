package com.holy.foodscanner.helpers;


import android.util.Xml;

import com.holy.foodscanner.models.Product;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


public class ProductXmlParser {

    private static final String ns = null;

    // 주어진 XML 파싱하기
    public Product parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readC005(parser);
        } finally {
            in.close();
        }
    }

    // C005 태그 읽기
    private Product readC005(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "C005");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // row 태그 찾기
            if (name.equals("row")) {
                return readRow(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    // row 태그 읽기
    private Product readRow(XmlPullParser parser) throws XmlPullParserException, IOException {

        String name = "";
        String type = "";
        String manufacturer = "";
        String shelfLife = "";

        parser.require(XmlPullParser.START_TAG, ns, "row");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            switch (tag) {
                case "PRDLST_NM":
                    name = readName(parser);
                    break;
                case "PRDLST_DCNM":
                    type = readType(parser);
                    break;
                case "BSSH_NM":
                    manufacturer = readManufacturer(parser);
                    break;
                case "POG_DAYCNT":
                    shelfLife = readShelfLife(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Product(name, type, manufacturer, shelfLife);
    }

    // PRDLST_NM 태그의 내용 읽기
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "PRDLST_NM");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "PRDLST_NM");
        return name;
    }

    // PRDLST_DCNM 태그의 내용 읽기
    private String readType(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "PRDLST_DCNM");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "PRDLST_DCNM");
        return type;
    }

    // BSSH_NM 태그의 내용 읽기
    private String readManufacturer(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "BSSH_NM");
        String manufacturer = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "BSSH_NM");
        return manufacturer;
    }

    // POG_DAYCNT 태그의 내용 읽기
    private String readShelfLife(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "POG_DAYCNT");
        String shelfLife = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "POG_DAYCNT");
        return shelfLife;
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




